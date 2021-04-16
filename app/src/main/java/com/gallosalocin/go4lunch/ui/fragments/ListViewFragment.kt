package com.gallosalocin.go4lunch.ui.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gallosalocin.go4lunch.BuildConfig
import com.gallosalocin.go4lunch.R
import com.gallosalocin.go4lunch.adapters.RestaurantAdapter
import com.gallosalocin.go4lunch.databinding.FragmentListViewBinding
import com.gallosalocin.go4lunch.models.RestaurantResult
import com.gallosalocin.go4lunch.models.Workmate
import com.gallosalocin.go4lunch.util.Constants.WORKMATES_COLLECTION
import com.gallosalocin.go4lunch.viewmodels.RestaurantViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class ListViewFragment : Fragment(R.layout.fragment_list_view) {

    private var _binding: FragmentListViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val restaurantViewModel: RestaurantViewModel by viewModels()

    private val db = FirebaseFirestore.getInstance()
    private val workmatesCollectionRef = db.collection(WORKMATES_COLLECTION)
    private var workmateListener: ListenerRegistration? = null
    private lateinit var chosenRestaurantsList: MutableList<String>
    private lateinit var restaurantResultList: MutableList<RestaurantResult>
    private lateinit var restaurantResultPredictionsList: MutableList<RestaurantResult>
    private lateinit var placesClient: PlacesClient
    private lateinit var predictionsPlaceIdList: MutableList<String>

    private var workmate: Workmate? = null
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var radius: String
    private var latitude = 0.0
    private var longitude = 0.0
    private lateinit var currentLocation: String
    private var stateName = true
    private var stateDistance = true
    private var stateRating = true
    private var stateWorkmates = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        radius = sharedPreferences.getString("key_pref_radius_2", "500").toString()
        navController = Navigation.findNavController(view)
        placesClient = Places.createClient(requireContext())
        restaurantResultList = ArrayList()
        restaurantResultPredictionsList = ArrayList()
        Timber.d("restaurantResultPredictionsList NEW : %s", restaurantResultPredictionsList.toString())

        setupRecyclerView()
        getCurrentLocation()
    }

    // Setup RecyclerView
    private fun setupRecyclerView() {
        restaurantAdapter = RestaurantAdapter(
                onItemClickListener = {
                    val action = ListViewFragmentDirections.actionListViewFragmentToDetailsFragment(it, null)
                    navController.navigate(action)
                })

        binding.rvListView.apply {
            adapter = restaurantAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    // Setup Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        val menuAutoCompleteTextView = menu.findItem(R.id.tb_menu_search_btn)
        val actionAutoCompleteTextView = menuAutoCompleteTextView.actionView as AppCompatAutoCompleteTextView
        actionAutoCompleteTextView.hint = "Search restaurants..."
        actionAutoCompleteTextView.minimumWidth = 800
        actionAutoCompleteTextView.setDropDownBackgroundResource(R.color.colorWhite)
        actionAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                predictionsPlaceIdList = ArrayList()
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val token = AutocompleteSessionToken.newInstance()
                val request = FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(actionAutoCompleteTextView.text.toString().trim { it <= ' ' })
                        .build()
                placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                            for (prediction in response.autocompletePredictions) {
                                predictionsPlaceIdList.add(prediction.placeId)
                            }
                            if (predictionsPlaceIdList.isNotEmpty()) {
                                getRestaurantDetails(predictionsPlaceIdList[0])
                            }
                        }
                        .addOnFailureListener { exception: Exception? ->
                            if (exception is ApiException) {
                                Timber.e("Place not found: %s", exception.statusCode)
                            }
                        }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.tb_menu_search_btn -> return true
            R.id.toolbar_sort_name -> {
                val restaurantResultListFiltered = restaurantResultList.sortedBy { it.name?.toLowerCase(Locale.ROOT) }

                if (stateName) {
                    restaurantAdapter.submitList(restaurantResultListFiltered, kotlinx.coroutines.Runnable {
                        binding.rvListView.scrollToPosition(0)
                    })
                } else {
                    restaurantAdapter.submitList(restaurantResultListFiltered.reversed(), kotlinx.coroutines.Runnable {
                        binding.rvListView.scrollToPosition(0)
                    })
                }
                stateName = !stateName
            }
            R.id.toolbar_sort_distance -> {
                val restaurantResultListFiltered = restaurantResultList.sortedBy { it.distance }

                if (stateDistance) {
                    restaurantAdapter.submitList(restaurantResultListFiltered, kotlinx.coroutines.Runnable {
                        binding.rvListView.scrollToPosition(0)
                    })
                } else {
                    restaurantAdapter.submitList(restaurantResultListFiltered.reversed(), kotlinx.coroutines.Runnable {
                        binding.rvListView.scrollToPosition(0)
                    })
                }
                stateDistance = !stateDistance
            }
            R.id.toolbar_sort_rating -> {
                val restaurantResultListFiltered = restaurantResultList.sortedBy { it.rating }

                if (stateRating) {
                    restaurantAdapter.submitList(restaurantResultListFiltered.reversed(), kotlinx.coroutines.Runnable {
                        binding.rvListView.scrollToPosition(0)
                    })
                } else {
                    restaurantAdapter.submitList(restaurantResultListFiltered, kotlinx.coroutines.Runnable {
                        binding.rvListView.scrollToPosition(0)
                    })
                }
                stateRating = !stateRating
            }
            R.id.toolbar_sort_workmates -> {
                val restaurantResultListFiltered = restaurantResultList.sortedBy { it.workmates }

                if (stateWorkmates) {
                    restaurantAdapter.submitList(restaurantResultListFiltered.reversed(), kotlinx.coroutines.Runnable {
                        binding.rvListView.scrollToPosition(0)
                    })
                } else {
                    restaurantAdapter.submitList(restaurantResultListFiltered, kotlinx.coroutines.Runnable {
                        binding.rvListView.scrollToPosition(0)
                    })
                }
                stateWorkmates = !stateWorkmates
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Get details restaurant from api
    private fun getRestaurantDetails(placeId: String) {
        restaurantViewModel.getDetailsRestaurant(placeId, BuildConfig.ApiKey).observe(viewLifecycleOwner) { detailsResult ->
            val currentLocation = Location("")
            currentLocation.latitude = latitude
            currentLocation.longitude = longitude
            val restaurantLocation = Location("")
            if (detailsResult != null) {
                restaurantLocation.latitude = detailsResult.detailsGeometry!!.location!!.lat.toDouble()
                restaurantLocation.longitude = detailsResult.detailsGeometry!!.location!!.lng.toDouble()
                val occurrences = Collections.frequency(chosenRestaurantsList, placeId)
                if (restaurantResultPredictionsList.isNotEmpty()) {
                    if (placeId != restaurantResultPredictionsList[0].placeId) {
                        restaurantResultPredictionsList.add(0, RestaurantResult(
                                name = detailsResult.name,
                                restaurantOpeningHours = detailsResult.restaurantOpeningHours,
                                address = detailsResult.address,
                                placeId = placeId,
                                restaurantPhotos = detailsResult.detailsPhotos,
                                rating = detailsResult.rating * 3 / 5,
                                workmates = occurrences.toFloat(),
                                distance = currentLocation.distanceTo(restaurantLocation).toInt())
                        )
                    }
                } else {
                    restaurantResultPredictionsList.add(0, RestaurantResult(
                            name = detailsResult.name,
                            restaurantOpeningHours = detailsResult.restaurantOpeningHours,
                            address = detailsResult.address,
                            placeId = placeId,
                            restaurantPhotos = detailsResult.detailsPhotos,
                            rating = detailsResult.rating * 3 / 5,
                            workmates = occurrences.toFloat(),
                            distance = currentLocation.distanceTo(restaurantLocation).toInt())
                    )
                }
                restaurantAdapter.submitList(restaurantResultPredictionsList)
                binding.rvListView.adapter = restaurantAdapter
            }
        }
    }

    // Get nearby restaurants from api
    private fun getAllRestaurants() {
        val type = "restaurant"
        restaurantViewModel.getNearbyRestaurantList(currentLocation, radius.toInt(), type, BuildConfig.ApiKey)
                .observe(viewLifecycleOwner) { restaurants ->
                    val currentLocation = Location("")
                    currentLocation.latitude = latitude
                    currentLocation.longitude = longitude
                    for (i in restaurants.indices) {
                        val occurrences = Collections.frequency(chosenRestaurantsList, restaurants[i].placeId)
                        val restaurantLocation = Location("")
                        restaurantLocation.latitude = restaurants[i].geometry!!.location!!.lat.toDouble()
                        restaurantLocation.longitude = restaurants[i].geometry!!.location!!.lng.toDouble()
                        restaurantResultList.add(RestaurantResult(
                                name = restaurants[i].name,
                                restaurantOpeningHours = restaurants[i].restaurantOpeningHours,
                                address = restaurants[i].address,
                                placeId = restaurants[i].placeId,
                                restaurantPhotos = restaurants[i].restaurantPhotos,
                                rating = restaurants[i].rating * 3 / 5,
                                workmates = occurrences.toFloat(),
                                distance = currentLocation.distanceTo(restaurantLocation).toInt()))
                    }
                    val restaurantResultListFiltered = restaurantResultList.sortedBy { it.distance }
                    restaurantAdapter.submitList(restaurantResultListFiltered)
                }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        Timber.d("getCurrentLocation")
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        try {
            val taskLocation = fusedLocationProviderClient.lastLocation
            taskLocation.addOnCompleteListener { task: Task<Location?> ->
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    latitude = location!!.latitude
                    longitude = location.longitude
                    currentLocation = "$latitude, $longitude"
                    getAllRestaurants()
                }
            }
        } catch (e: SecurityException) {
            Timber.e("Exception: %s", e.message)
        }
    }

    // Setup Snapshot Listener Workmates Collection
    private fun setupSnapshotListener() {
        Timber.d("setupSnapshotListener")
        chosenRestaurantsList = mutableListOf()
        workmateListener = workmatesCollectionRef.addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (error != null) {
                return@addSnapshotListener
            }
            for (queryDocumentSnapshot in queryDocumentSnapshots!!) {
                workmate = queryDocumentSnapshot.toObject(Workmate::class.java)
                workmate!!.chosenRestaurantId?.let { chosenRestaurantsList.add(it) }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
        setupSnapshotListener()
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop")
        //        workmateListener.remove();
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("onDestroy")
        _binding = null
    }
}