package com.gallosalocin.go4lunch.ui.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.gallosalocin.go4lunch.BuildConfig
import com.gallosalocin.go4lunch.R
import com.gallosalocin.go4lunch.adapters.WorkmateAdapter
import com.gallosalocin.go4lunch.databinding.FragmentDetailsBinding
import com.gallosalocin.go4lunch.models.RestaurantResult
import com.gallosalocin.go4lunch.models.Workmate
import com.gallosalocin.go4lunch.notifications.AlertReceiver
import com.gallosalocin.go4lunch.util.Constants
import com.gallosalocin.go4lunch.viewmodels.RestaurantViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val restaurantViewModel: RestaurantViewModel by viewModels()

    private var workmateListener: ListenerRegistration? = null
    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val workmatesCollectionRef = db.collection(Constants.WORKMATES_COLLECTION)
    private var currentWorkmate: Workmate? = null
    private var mAdapter: WorkmateAdapter? = null
    private var restaurantResult: RestaurantResult? = null
    private lateinit var placeId: String
    private var args: DetailsFragmentArgs? = null
    private var isRestaurantCheck = false
    private var isFavoriteCheck = false
    private var fragmentChoice = 0
    private var workmatesToday: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButton()
        getRestaurantArguments()
    }

    // Setup notification time per day
    private fun setupCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 9
        calendar[Calendar.MINUTE] = 15
        calendar[Calendar.SECOND] = 0
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        Timber.d("Calendar : %s", calendar.time)
        return calendar
    }

    // Create alarm notification
    private fun alarmNotification(name: String?, address: String?, workmatesToday: String?) {
        if (isRestaurantCheck) {
            Timber.d("START alarmNotification")
            val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(requireContext(), AlertReceiver::class.java)
            alarmIntent.putExtra(Constants.NAME_EXTRA, name)
            alarmIntent.putExtra(Constants.ADDRESS_EXTRA, address)
            alarmIntent.putExtra(Constants.WORKMATES_TODAY_EXTRA, workmatesToday)
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), Constants.NOTIFICATION_LUNCH_RESTAURANT_ID, alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, setupCalendar().timeInMillis, pendingIntent)
        } else {
            Timber.d("CANCEL alarmNotification")
            val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(requireContext(), AlertReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), Constants.NOTIFICATION_LUNCH_RESTAURANT_ID, alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
        }
    }

    // Get arguments
    private fun getRestaurantArguments() {
        if (arguments != null) {
            args = DetailsFragmentArgs.fromBundle(requireArguments())
        }
        if (args!!.placeId != null) {
            placeId = args!!.placeId.toString()
            fragmentChoice = 1
            getRestaurantDetails(placeId)
            setupRecyclerView(placeId)
        } else if (args!!.restaurantResult != null) {
            restaurantResult = args!!.restaurantResult
            fragmentChoice = 2
            restaurantResult!!.placeId?.let { getRestaurantDetails(it) }
            setupRecyclerView(restaurantResult!!.placeId)
        }
    }

    // Save Chosen Restaurant
    private fun saveRestaurantForLunch(placeId: String?, name: String?) {
        isRestaurantCheck = if (isRestaurantCheck) {
            Objects.requireNonNull(mAuth.uid)?.let {
                workmatesCollectionRef.document(it).update(Constants.CHOSEN_RESTAURANT_ID_FIELD, "")
            }
            Objects.requireNonNull(mAuth.uid)?.let {
                workmatesCollectionRef.document(it).update(Constants.CHOSEN_RESTAURANT_NAME_FIELD, "")
            }
            binding.fabDetailsChoice.setImageResource(R.drawable.ic_check_empty)
            false
        } else {
            Objects.requireNonNull(mAuth.uid)?.let {
                workmatesCollectionRef.document(it).update(Constants.CHOSEN_RESTAURANT_ID_FIELD, placeId)
            }
            Objects.requireNonNull(mAuth.uid)?.let {
                workmatesCollectionRef.document(it).update(Constants.CHOSEN_RESTAURANT_NAME_FIELD, name)
            }
            binding.fabDetailsChoice.setImageResource(R.drawable.ic_check_ok)
            true
        }
    }

    private fun getRestaurantDetails(placeId: String) {
        restaurantViewModel!!.getDetailsRestaurant(placeId, BuildConfig.ApiKey).observe(viewLifecycleOwner) { detailsResult ->
            val imageUrl: String
            if (detailsResult != null) {
                binding.tvDetailsNameRestaurant.text = detailsResult.name
                binding.tvDetailsInformation.text = detailsResult.address
                binding.ratingDetails.rating = detailsResult.rating * 3 / 5
                if (detailsResult.detailsPhotos != null) {
                    imageUrl = getPhoto(detailsResult.detailsPhotos!![0].photoReference, detailsResult.detailsPhotos!![0].width)
                    Picasso.get().load(imageUrl).fit().centerCrop().into(binding.ivDetailsPicture)
                } else {
                    binding.ivDetailsPicture.setImageResource(R.drawable.ic_broken_image)
                }

                binding.btnDetailsCall.setOnClickListener {
                    if (detailsResult.formattedPhoneNumber != null) {
                        val callRestaurant = Intent(Intent.ACTION_DIAL)
                        callRestaurant.data = Uri.parse("tel:${detailsResult.formattedPhoneNumber}")
                        startActivity(callRestaurant)
                    } else {
                        Toast.makeText(requireContext(), R.string.alert_details_no_phone_number, Toast.LENGTH_SHORT).show()
                    }
                }

                binding.btnDetailsLike.setOnClickListener {
                    isFavoriteCheck = if (isFavoriteCheck) {
                        Objects.requireNonNull(mAuth.uid)?.let {
                            workmatesCollectionRef.document(it).update(Constants.FAVORITE_FIELD, FieldValue.arrayRemove(placeId))
                        }
                        binding.btnDetailsLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_red, 0, 0)
                        false
                    } else {
                        Objects.requireNonNull(mAuth.uid)?.let {
                            workmatesCollectionRef.document(it).update(Constants.FAVORITE_FIELD, FieldValue.arrayUnion(placeId))
                        }
                        binding.btnDetailsLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_yellow, 0, 0)
                        true
                    }
                }

                binding.btnDetailsWebsite.setOnClickListener {
                    if (detailsResult.website != null) {
                        val linkRestaurant = Intent(Intent.ACTION_VIEW)
                        linkRestaurant.data = Uri.parse(detailsResult.website)
                        startActivity(linkRestaurant)
                    } else {
                        Toast.makeText(requireContext(), R.string.alert_details_no_website, Toast.LENGTH_SHORT).show()
                    }
                }

                binding.fabDetailsChoice.setOnClickListener {
                    if (fragmentChoice == 1) {
                        saveRestaurantForLunch(this.placeId, detailsResult.name)
                        saveData(this.placeId)
                        alarmNotification(detailsResult.name, detailsResult.address, workmatesToday)
                    } else {
                        saveRestaurantForLunch(restaurantResult!!.placeId, restaurantResult!!.name)
                        saveData(restaurantResult!!.placeId)
                        alarmNotification(restaurantResult!!.name, restaurantResult!!.address, workmatesToday)
                    }
                }
            }
        }
    }

    private fun saveData(placeId: String?) {
        val sharedPreferences = requireContext().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.PLACE_ID_SHARED_PREFS, placeId)
        editor.apply()
    }

    private fun getPhoto(photoReference: String?, maxWidth: Int): String {
        val url = StringBuilder()
        url.append("https://maps.googleapis.com/maps/api/place/photo?")
        url.append("maxwidth=")
        url.append(maxWidth)
        url.append("&photoreference=")
        url.append(photoReference)
        url.append("&key=")
        url.append(BuildConfig.ApiKey)
        return url.toString()
    }

    private fun setupRecyclerView(placeId: String?) {
        val query = workmatesCollectionRef.whereEqualTo(Constants.CHOSEN_RESTAURANT_ID_FIELD, placeId)
        val options = FirestoreRecyclerOptions.Builder<Workmate>().setQuery(query, Workmate::class.java).build()

        mAdapter = WorkmateAdapter(
                options,
                onItemClickListener = {})

        binding.rvDetailsWorkmates.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun retrieveWorkmatesForLunch(placeId: String?) {
        workmatesCollectionRef.whereEqualTo(Constants.CHOSEN_RESTAURANT_ID_FIELD, placeId).addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, error: FirebaseFirestoreException? ->
            Timber.d("retrieveWorkmatesForLunch")
            if (error != null) {
                return@addSnapshotListener
            }
            workmatesToday = ""
            for (queryDocumentSnapshot in queryDocumentSnapshots!!) {
                val (name) = queryDocumentSnapshot.toObject(Workmate::class.java)
                if (name != currentWorkmate!!.name) {
                    workmatesToday += "$name, "
                }
            }
            if (workmatesToday!!.length > 2) {
                workmatesToday = workmatesToday!!.substring(0, workmatesToday!!.length - 2)
            }
        }
    }

    private fun setupDocumentSnapshot() {
        workmateListener = Objects.requireNonNull(mAuth.uid)?.let {
            workmatesCollectionRef.document(it).addSnapshotListener { documentSnapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                Timber.d("setupDocumentSnapshot")
                if (error != null) {
                    Timber.d(error.toString())
                    return@addSnapshotListener
                }
                if (documentSnapshot!!.exists()) {
                    currentWorkmate = documentSnapshot.toObject(Workmate::class.java)
                    when (fragmentChoice) {
                        1 -> {
                            restaurantIsForLunch(placeId)
                            restaurantIsFavorite(placeId)
                            retrieveWorkmatesForLunch(placeId)
                        }
                        2 -> {
                            restaurantIsForLunch(restaurantResult!!.placeId)
                            restaurantIsFavorite(restaurantResult!!.placeId)
                            retrieveWorkmatesForLunch(restaurantResult!!.placeId)
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    // Check if restaurant is for lunch
    private fun restaurantIsForLunch(placeId: String?) {
        Timber.d("restaurantIsForLunch")
        if (currentWorkmate!!.chosenRestaurantId == placeId) {
            binding.fabDetailsChoice.setImageResource(R.drawable.ic_check_ok)
            isRestaurantCheck = true
        }
    }

    // Check if restaurant is favorite
    private fun restaurantIsFavorite(placeId: String?) {
        if (currentWorkmate!!.favorite != null) {
            for (i in currentWorkmate!!.favorite!!.indices) {
                if (currentWorkmate!!.favorite!![i] == placeId) {
                    binding.btnDetailsLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_yellow, 0, 0)
                    isFavoriteCheck = true
                }
            }
        }
    }

    // Setup back button
    private fun setupBackButton() {
        binding.ivBackBtn.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
        mAdapter!!.startListening()
        setupDocumentSnapshot()
    }

    override fun onStop() {
        super.onStop()
        mAdapter!!.stopListening()
        workmateListener!!.remove()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}