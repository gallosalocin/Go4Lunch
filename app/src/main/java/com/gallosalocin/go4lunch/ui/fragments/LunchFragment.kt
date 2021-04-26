package com.gallosalocin.go4lunch.ui.fragments

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
import com.gallosalocin.go4lunch.databinding.FragmentLunchBinding
import com.gallosalocin.go4lunch.models.Workmate
import com.gallosalocin.go4lunch.util.Constants.CHOSEN_RESTAURANT_ID_FIELD
import com.gallosalocin.go4lunch.util.Constants.CHOSEN_RESTAURANT_NAME_FIELD
import com.gallosalocin.go4lunch.util.Constants.FAVORITE_FIELD
import com.gallosalocin.go4lunch.util.Constants.PLACE_ID_SHARED_PREFS
import com.gallosalocin.go4lunch.util.Constants.SHARED_PREFS
import com.gallosalocin.go4lunch.util.Constants.WORKMATES_COLLECTION
import com.gallosalocin.go4lunch.viewmodels.RestaurantViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class LunchFragment : Fragment(R.layout.fragment_lunch) {

    private var _binding: FragmentLunchBinding? = null
    private val binding get() = _binding!!
    private val restaurantViewModel: RestaurantViewModel by viewModels()

    private var workmateListener: ListenerRegistration? = null
    private val mAuth = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val workmatesCollectionRef = db.collection(WORKMATES_COLLECTION)
    private lateinit var mAdapter: WorkmateAdapter
    private var currentWorkmate: Workmate? = null
    private var isRestaurantCheck = false
    private var isFavoriteCheck = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLunchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val placeId = sharedPreferences.getString(PLACE_ID_SHARED_PREFS, "")

        setupRecyclerView(placeId)
    }

    private fun getRestaurantDetails(placeId: String?) {
        restaurantViewModel.getDetailsRestaurant(placeId!!, BuildConfig.ApiKey).subscribe { detailsResult ->
            Timber.d("getRestaurantDetails")
            if (isRestaurantCheck) {
                val imageUrl: String
                if (detailsResult != null) {
                    binding.tvMyLunchNameRestaurant.text = detailsResult.name
                    binding.tvMyLunchInformation.text = detailsResult.address
                    binding.ratingMyLunch.rating = detailsResult.rating * 3 / 5
                    if (detailsResult.detailsPhotos != null) {
                        imageUrl = getPhoto(detailsResult.detailsPhotos!![0].photoReference, detailsResult.detailsPhotos!![0].width)
                        Picasso.get().load(imageUrl).fit().centerCrop().into(binding.ivMyLunchPicture)
                    } else {
                        binding.ivMyLunchPicture.setImageResource(R.drawable.ic_broken_image)
                    }
                    binding.btnMyLunchCall.setOnClickListener {
                        if (detailsResult.formattedPhoneNumber != null) {
                            val callRestaurant = Intent(Intent.ACTION_DIAL)
                            callRestaurant.data = Uri.parse("tel:${detailsResult.formattedPhoneNumber}")
                            startActivity(callRestaurant)
                        } else {
                            Toast.makeText(requireContext(), R.string.alert_details_no_phone_number, Toast.LENGTH_SHORT).show()
                        }
                    }

                    binding.btnMyLunchLike.setOnClickListener {
                        isFavoriteCheck = if (isFavoriteCheck) {
                            workmatesCollectionRef.document(Objects.requireNonNull(mAuth.uid)).update(FAVORITE_FIELD,
                                    FieldValue.arrayRemove(placeId))
                            binding.btnMyLunchLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_red, 0, 0)
                            false
                        } else {
                            workmatesCollectionRef.document(Objects.requireNonNull(mAuth.uid)).update(FAVORITE_FIELD,
                                    FieldValue.arrayUnion(placeId))
                            binding.btnMyLunchLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_yellow, 0, 0)
                            true
                        }
                    }

                    binding.btnMyLunchWebsite.setOnClickListener {
                        if (detailsResult.website != null) {
                            val linkRestaurant = Intent(Intent.ACTION_VIEW)
                            linkRestaurant.data = Uri.parse(detailsResult.website)
                            startActivity(linkRestaurant)
                        } else {
                            Toast.makeText(requireContext(), R.string.alert_details_no_website, Toast.LENGTH_SHORT).show()
                        }
                    }

                    binding.fabMyLunchChoice.setOnClickListener {
                        if (isRestaurantCheck) {
                            workmatesCollectionRef.document(mAuth.uid).update(CHOSEN_RESTAURANT_ID_FIELD, "")
                            workmatesCollectionRef.document(mAuth.uid).update(CHOSEN_RESTAURANT_NAME_FIELD, "")
                            binding.fabMyLunchChoice.setImageResource(R.drawable.ic_check_empty)
                            isRestaurantCheck = false
                            viewVisibility()
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(placeID: String?) {
        Timber.d("setupRecyclerView")
        val query: Query = workmatesCollectionRef.whereEqualTo(CHOSEN_RESTAURANT_ID_FIELD, placeID)
        val options = FirestoreRecyclerOptions.Builder<Workmate>().setQuery(query, Workmate::class.java).build()

        mAdapter = WorkmateAdapter(
                options,
                onItemClickListener = {})

        binding.rvMyLunch.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun getPhoto(photoReference: String?, maxWidth: Int): String {
        return "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=" + maxWidth +
                "&photoreference=" + photoReference +
                "&key=" + BuildConfig.ApiKey
    }

    private fun setupDocumentSnapshot() {
        Timber.d("setupDocumentSnapshot")
        workmateListener = workmatesCollectionRef.document(mAuth.uid).addSnapshotListener { documentSnapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
            viewVisibility()
            if (error != null) {
                Timber.d(error.toString())
                return@addSnapshotListener
            }
            if (documentSnapshot!!.exists()) {
                currentWorkmate = documentSnapshot.toObject(Workmate::class.java)
                if (currentWorkmate!!.chosenRestaurantId!!.isNotEmpty()) {
                    restaurantIsFavorite(currentWorkmate!!.chosenRestaurantId)
                    isRestaurantCheck = true
                    binding.fabMyLunchChoice.setImageResource(R.drawable.ic_check_ok)
                    getRestaurantDetails(currentWorkmate!!.chosenRestaurantId)
                    viewVisibility()
                    Timber.d("setupDocumentSnapshot placeId: %s", currentWorkmate!!.chosenRestaurantId)
                }
            }
        }
    }

    // Check if restaurant is favorite
    private fun restaurantIsFavorite(placeId: String?) {
        if (currentWorkmate!!.favorite != null) {
            for (i in currentWorkmate!!.favorite!!.indices) {
                if (currentWorkmate!!.favorite!![i] == placeId) {
                    binding.btnMyLunchLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_yellow, 0, 0)
                    isFavoriteCheck = true
                }
            }
        }
    }

    private fun viewVisibility() {
        if (isRestaurantCheck) {
            binding.apply {
                llMyLunch.visibility = View.INVISIBLE
                fabMyLunchChoice.visibility = View.VISIBLE
                btnMyLunchCall.visibility = View.VISIBLE
                btnMyLunchLike.visibility = View.VISIBLE
                btnMyLunchWebsite.visibility = View.VISIBLE
                cvMyLunchAddress.visibility = View.VISIBLE
                ivMyLunchPicture.visibility = View.VISIBLE
                ratingMyLunch.visibility = View.VISIBLE
                tvMyLunchInformation.visibility = View.VISIBLE
                tvMyLunchNameRestaurant.visibility = View.VISIBLE
                rvMyLunch.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                llMyLunch.visibility = View.VISIBLE
                fabMyLunchChoice.visibility = View.INVISIBLE
                btnMyLunchCall.visibility = View.INVISIBLE
                btnMyLunchLike.visibility = View.INVISIBLE
                btnMyLunchWebsite.visibility = View.INVISIBLE
                cvMyLunchAddress.visibility = View.INVISIBLE
                ivMyLunchPicture.visibility = View.INVISIBLE
                ratingMyLunch.visibility = View.INVISIBLE
                tvMyLunchInformation.visibility = View.INVISIBLE
                tvMyLunchNameRestaurant.visibility = View.INVISIBLE
                rvMyLunch.visibility = View.INVISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
        mAdapter.startListening()
        setupDocumentSnapshot()
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop")
        mAdapter.stopListening()
        workmateListener!!.remove()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}