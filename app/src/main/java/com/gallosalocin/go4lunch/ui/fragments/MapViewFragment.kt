package com.gallosalocin.go4lunch.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.gallosalocin.go4lunch.BuildConfig
import com.gallosalocin.go4lunch.R
import com.gallosalocin.go4lunch.adapters.PlaceAutoCompleteAdapter
import com.gallosalocin.go4lunch.databinding.FragmentMapViewBinding
import com.gallosalocin.go4lunch.models.Workmate
import com.gallosalocin.go4lunch.util.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.gallosalocin.go4lunch.util.Constants.MAP_VIEW_BUNDLE_KEY
import com.gallosalocin.go4lunch.util.Constants.WORKMATES_COLLECTION
import com.gallosalocin.go4lunch.viewmodels.RestaurantViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.Disposable
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class MapViewFragment : Fragment(R.layout.fragment_map_view), OnMapReadyCallback, PermissionCallbacks {

    private var _binding: FragmentMapViewBinding? = null
    private val binding get() = _binding!!
    private val restaurantViewModel: RestaurantViewModel by viewModels()

    private val mAuth = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val workmatesCollectionRef = db.collection(WORKMATES_COLLECTION)
    private var workmateListener: ListenerRegistration? = null
    private lateinit var chosenRestaurants: MutableList<String>
    private lateinit var mMap: GoogleMap
    private var defaultZoom = 0f
    private var latitude = 0.0
    private var longitude = 0.0
    private lateinit var radius: String
    private lateinit var currentLatLng: LatLng
    private lateinit var currentLocation: String
    private lateinit var navController: NavController
    private lateinit var workmate: Workmate
    private lateinit var markerOptions: MarkerOptions
    private lateinit var placesClient: PlacesClient
    private lateinit var placeAutocompleteAdapter: PlaceAutoCompleteAdapter

    private lateinit var disposable: Disposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mAuth != null) {
            requestPermissions()
        }
        val autocompleteSessionToken = AutocompleteSessionToken.newInstance()
        placesClient = Places.createClient(requireContext())
        placeAutocompleteAdapter = PlaceAutoCompleteAdapter(requireContext(), placesClient, autocompleteSessionToken)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        defaultZoom = sharedPreferences.getInt("key_pref_zoom", 18).toFloat()
        radius = sharedPreferences.getString("key_pref_radius_2", "500").toString()

        navController = Navigation.findNavController(view)
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
        binding.mapView.onCreate(mapViewBundle)

    }

    // Request all permissions
    private fun requestPermissions() {
        Timber.d("requestPermissions: called")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_message),
                    LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_message),
                    LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Timber.d("onPermissionsGranted")
        initMap()
        binding.fabMapView.visibility = View.VISIBLE
        binding.fabMapView.setImageResource(if (isGPSEnabled(requireContext())) R.drawable.ic_location else R.drawable.ic_location_disabled)
        initFabLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    // Initialise Map
    private fun initMap() {
        Timber.d("initMap: called")
        binding.mapView.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        Timber.d("onMapReady")
        mMap = map
        if (mAuth != null) {
            Timber.d("onMapReady after verification")
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = false
            getDeviceLocation()
            mMap.setOnMarkerClickListener { marker: Marker ->
                val markerPlaceId = marker.title
                val action = MapViewFragmentDirections.actionMapViewFragmentToDetailsFragment(null, markerPlaceId)
                navController.navigate(action)
                false
            }
        }
    }

    // Find device location
    private fun getDeviceLocation() {
        Timber.d("getDeviceLocation: called")
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        try {
            val taskLocation = fusedLocationProviderClient.lastLocation
            taskLocation.addOnCompleteListener { task: Task<Location?> ->
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    latitude = location!!.latitude
                    longitude = location.longitude
                    currentLatLng = LatLng(latitude, longitude)
                    currentLocation = "$latitude, $longitude"
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, defaultZoom)
                    getNearbyRestaurants()
                    mMap.moveCamera(cameraUpdate)
                }
            }
        } catch (e: SecurityException) {
            Timber.e("Exception: ${e.message}")
        }
    }

    // Get nearby restaurants from api
    private fun getNearbyRestaurants() {
        val type = "restaurant"

        disposable = restaurantViewModel.getNearbyRestaurantList(currentLocation, radius.toInt(), type, BuildConfig.ApiKey).subscribe {
            for (i in it.indices) {
                val latLng = LatLng(
                        it[i].geometry.location.lat.toDouble(),
                        it[i].geometry.location.lng.toDouble()
                )
                markerOptions = MarkerOptions()
                markerOptions.apply {
                    position(latLng)
                    title(it[i].placeId)
                    for (j in chosenRestaurants.indices) {
                        if (it[i].placeId == chosenRestaurants[j]) {
                            icon(bitmapDescriptorFromVector(R.drawable.ic_pin_restaurant_green))
                            break
                        } else {
                            icon(bitmapDescriptorFromVector(R.drawable.ic_pin_restaurant_orange))
                        }
                    }
                    mMap.addMarker(this)
                }
            }
        }
    }

    // Bitmap Descriptor
    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId)!!
        vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // Check if GPS enabled
    private fun isGPSEnabled(context: Context): Boolean {
        Timber.d("isGPSEnabled: called")
        val lm = (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // Initialize Location Fab
    private fun initFabLocation() {
        binding.fabMapView.setOnClickListener {
            binding.fabMapView.setImageResource(if (isGPSEnabled(requireContext())) R.drawable.ic_location else R.drawable.ic_location_disabled)
            if (isGPSEnabled(requireContext())) {
                getDeviceLocation()
            } else {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(R.string.dialog_title_gps).setMessage(R.string.dialog_message_gps)
                        .setPositiveButton(R.string.dialog_positive
                        ) { _: DialogInterface?, _: Int -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                        .setNegativeButton(R.string.dialog_negative) { dialog: DialogInterface, _: Int -> dialog.cancel() }
                val alert = builder.create()
                alert.show()
            }
        }
    }

    // Setup Snapshot Listener Workmates Collection
    private fun setupSnapshotListener() {
        chosenRestaurants = mutableListOf()
        workmateListener = workmatesCollectionRef.addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, error: FirebaseFirestoreException? ->
            Timber.d("Snapshot Listener")
            if (error != null) {
                return@addSnapshotListener
            }
            for (queryDocumentSnapshot in queryDocumentSnapshots!!) {
                if (mAuth != null) {
                    workmate = queryDocumentSnapshot.toObject(Workmate::class.java)
                    if (workmate.chosenRestaurantId!!.isNotEmpty()) {
                        workmate.chosenRestaurantId?.let { chosenRestaurants.add(it) }
                    } else {
                        chosenRestaurants.add("")
                    }
                }
            }
        }
    }

    // Setup Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        menu.findItem(R.id.tb_menu_sort_btn).isVisible = false
        val menuAutoCompleteTextView = menu.findItem(R.id.tb_menu_search_btn)
        val actionAutoCompleteTextView = menuAutoCompleteTextView.actionView as AppCompatAutoCompleteTextView
        actionAutoCompleteTextView.setHint(R.string.tv_toolbar_hint)
        actionAutoCompleteTextView.minimumWidth = 800
        actionAutoCompleteTextView.setDropDownBackgroundResource(R.color.colorWhite)
        actionAutoCompleteTextView.setAdapter(placeAutocompleteAdapter)
        actionAutoCompleteTextView.onItemClickListener = autoCompleteClickListener
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.tb_menu_search_btn) {
            true
        } else super.onOptionsItemSelected(item)
    }

    // Setup click event on AutoCompleteTextView
    private val autoCompleteClickListener = AdapterView.OnItemClickListener { _: AdapterView<*>?, _: View?, i: Int, _: Long ->
        try {
            val item = placeAutocompleteAdapter.getItem(i)
            val placeId: String?
            placeId = item.placeId
            val placeFields = listOf(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME)
            val request: FetchPlaceRequest?
            request = FetchPlaceRequest.builder(placeId, placeFields).build()
            placesClient.fetchPlace(request).addOnSuccessListener { fetchPlaceResponse: FetchPlaceResponse ->
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(fetchPlaceResponse.place.latLng, defaultZoom)
                markerOptions = MarkerOptions()
                markerOptions.position(Objects.requireNonNull(fetchPlaceResponse.place.latLng)!!)
                markerOptions.title(fetchPlaceResponse.place.id)
                for (j in chosenRestaurants.indices) {
                    if (fetchPlaceResponse.place.id == chosenRestaurants[j]) {
                        markerOptions.icon(bitmapDescriptorFromVector(R.drawable.ic_pin_restaurant_green))
                        break
                    } else {
                        markerOptions.icon(bitmapDescriptorFromVector(R.drawable.ic_pin_restaurant_orange))
                    }
                }
                mMap.addMarker(markerOptions)
                mMap.moveCamera(cameraUpdate)
                Timber.d("New Place Clicked : %s, %s", fetchPlaceResponse.place.name, fetchPlaceResponse.place.latLng)
            }.addOnFailureListener { obj: Exception -> obj.printStackTrace() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // LifeCycle for MapView
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        binding.mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        Timber.d("onResume: called")
        super.onResume()
        binding.mapView.onResume()
        binding.fabMapView.setImageResource(if (isGPSEnabled(requireContext())) R.drawable.ic_location else R.drawable.ic_location_disabled)
    }

    override fun onStart() {
        Timber.d("onStart: called")
        super.onStart()
        binding.mapView.onStart()
        if (mAuth != null) {
            setupSnapshotListener()
        }
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop: called")
        binding.mapView.onStop()
        if (mAuth != null) {
            workmateListener!!.remove()
        }
    }

    override fun onPause() {
        Timber.d("onPause: called")
        super.onPause()
        binding.mapView.onPause()
        binding.fabMapView.setImageResource(if (isGPSEnabled(requireContext())) R.drawable.ic_location else R.drawable.ic_location_disabled)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

}