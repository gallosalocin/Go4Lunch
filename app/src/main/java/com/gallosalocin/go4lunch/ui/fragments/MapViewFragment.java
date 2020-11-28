package com.gallosalocin.go4lunch.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.gallosalocin.go4lunch.BuildConfig;
import com.gallosalocin.go4lunch.R;
import com.gallosalocin.go4lunch.adapters.PlaceAutoCompleteAdapter;
import com.gallosalocin.go4lunch.databinding.FragmentMapViewBinding;
import com.gallosalocin.go4lunch.models.Workmate;
import com.gallosalocin.go4lunch.viewmodels.RestaurantViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

import static com.gallosalocin.go4lunch.util.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static com.gallosalocin.go4lunch.util.Constants.MAP_VIEW_BUNDLE_KEY;
import static com.gallosalocin.go4lunch.util.Constants.WORKMATES_COLLECTION;

public class MapViewFragment extends Fragment implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference workmatesCollectionRef = db.collection(WORKMATES_COLLECTION);
    private ListenerRegistration workmateListener;
    private List<String> chosenRestaurants;

    private FragmentMapViewBinding binding;
    private GoogleMap mMap;
    private float defaultZoom;
    private double latitude;
    private double longitude;
    private String radius;
    private RestaurantViewModel restaurantViewModel;
    private LatLng currentLatLng;
    private String currentLocation;
    private NavController navController;
    private Workmate workmate;
    private MarkerOptions markerOptions;

    private PlacesClient placesClient;
    private PlaceAutoCompleteAdapter placeAutocompleteAdapter;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        Timber.d("onCreateView: called");

        if (mAuth != null) {
            requestPermissions();
        }

        AutocompleteSessionToken autocompleteSessionToken = AutocompleteSessionToken.newInstance();
        placesClient = Places.createClient(requireContext());
        placeAutocompleteAdapter = new PlaceAutoCompleteAdapter(requireContext(), placesClient, autocompleteSessionToken);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        defaultZoom = sharedPreferences.getInt("key_pref_zoom", 18);
        radius = sharedPreferences.getString("key_pref_radius_2", "500");


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("onViewCreated: called");

        navController = Navigation.findNavController(view);
        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        binding.mapView.onCreate(mapViewBundle);

    }

    // Request all permissions
    private void requestPermissions() {
        Timber.d("requestPermissions: called");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_message),
                    LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_message),
                    LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Timber.d("onPermissionsGranted");
        initMap();
        binding.fabMapView.setVisibility(View.VISIBLE);
        binding.fabMapView.setImageResource(isGPSEnabled(requireContext()) ? R.drawable.ic_location : R.drawable.ic_location_disabled);
        initFabLocation();

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // Initialise Map
    private void initMap() {
        Timber.d("initMap: called");
        binding.mapView.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        Timber.d("onMapReady");
        mMap = map;

        if (mAuth != null) {
            Timber.d("onMapReady after verification");

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            getDeviceLocation();

            mMap.setOnMarkerClickListener(marker -> {
                String markerPlaceId = marker.getTitle();
                MapViewFragmentDirections.ActionMapViewFragmentToDetailsFragment action =
                        MapViewFragmentDirections.actionMapViewFragmentToDetailsFragment(null, markerPlaceId);
                navController.navigate(action);
                return false;
            });
        }
    }

    // Find device location
    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        Timber.d("getDeviceLocation: called");
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        try {
            Task<Location> taskLocation = fusedLocationProviderClient.getLastLocation();
            taskLocation.addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    currentLatLng = new LatLng(latitude, longitude);
                    currentLocation = latitude + ", " + longitude;
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, defaultZoom);
                    getAllRestaurants();
                    mMap.moveCamera(cameraUpdate);
                }
            });
        } catch (SecurityException e) {
            Timber.e("Exception: %s", e.getMessage());
        }
    }

    // Get nearby restaurants from api
    private void getAllRestaurants() {
        String type = "restaurant";
        restaurantViewModel.getRestaurants(currentLocation, Integer.parseInt(radius), type, BuildConfig.ApiKey).observe(getViewLifecycleOwner(),
                restaurants -> {

                    for (int i = 0; i < restaurants.size(); i++) {

                        LatLng latLng = new LatLng(restaurants.get(i).getGeometry().getLocation().getLat(),
                                restaurants.get(i).getGeometry().getLocation().getLng());

                        markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(restaurants.get(i).getPlaceId());

                        for (int j = 0; j < chosenRestaurants.size(); j++) {

                            if (restaurants.get(i).getPlaceId().equals(chosenRestaurants.get(j))) {
                                markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_pin_restaurant_green));
                                break;
                            } else {
                                markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_pin_restaurant_orange));
                            }
                        }
                        mMap.addMarker(markerOptions);
                    }
                });
    }


    // Bitmap Descriptor
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // Check if GPS enabled
    private boolean isGPSEnabled(Context context) {
        Timber.d("isGPSEnabled: called");
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        assert lm != null;
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // Initialize Location Fab
    private void initFabLocation() {
        binding.fabMapView.setOnClickListener(v -> {
            binding.fabMapView.setImageResource(isGPSEnabled(requireContext()) ? R.drawable.ic_location : R.drawable.ic_location_disabled);
            if (isGPSEnabled(requireContext())) {
                getDeviceLocation();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(R.string.dialog_title_gps).setMessage(R.string.dialog_message_gps)
                        .setPositiveButton(R.string.dialog_positive,
                                (dialog, id) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                        .setNegativeButton(R.string.dialog_negative, (dialog, id) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    // Setup Snapshot Listener Workmates Collection
    private void setupSnapshotListener() {
        chosenRestaurants = new ArrayList<>();
        workmateListener = workmatesCollectionRef.addSnapshotListener((queryDocumentSnapshots, error) -> {
            Timber.d("Snapshot Listener");
            if (error != null) {
                return;
            }
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                if (mAuth != null) {
                    workmate = queryDocumentSnapshot.toObject(Workmate.class);

                    if (!workmate.getChosenRestaurantId().isEmpty()) {
                        chosenRestaurants.add(workmate.getChosenRestaurantId());
                    } else {
                        chosenRestaurants.add("");
                    }
                }
            }
        });
    }

    // Setup Toolbar
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        menu.findItem(R.id.tb_menu_sort_btn).setVisible(false);

        MenuItem menuAutoCompleteTextView = menu.findItem(R.id.tb_menu_search_btn);
        AppCompatAutoCompleteTextView actionAutoCompleteTextView = (AppCompatAutoCompleteTextView) menuAutoCompleteTextView.getActionView();
        actionAutoCompleteTextView.setHint(R.string.tv_toolbar_hint);
        actionAutoCompleteTextView.setMinimumWidth(800);
        actionAutoCompleteTextView.setDropDownBackgroundResource(R.color.colorWhite);
        actionAutoCompleteTextView.setAdapter(placeAutocompleteAdapter);
        actionAutoCompleteTextView.setOnItemClickListener(autoCompleteClickListener);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.tb_menu_search_btn) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Setup click event on AutoCompleteTextView
    private AdapterView.OnItemClickListener autoCompleteClickListener = (adapterView, view, i, l) -> {

        try {
            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
            String placeId = null;
            if (item != null) {
                placeId = item.getPlaceId();
            }

            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME);

            FetchPlaceRequest request = null;
            if (placeId != null) {
                request = FetchPlaceRequest.builder(placeId, placeFields).build();
            }

            if (request != null) {
                placesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(fetchPlaceResponse.getPlace().getLatLng(), defaultZoom);

                    markerOptions = new MarkerOptions();
                    markerOptions.position(Objects.requireNonNull(fetchPlaceResponse.getPlace().getLatLng()));
                    markerOptions.title(fetchPlaceResponse.getPlace().getId());

                    for (int j = 0; j < chosenRestaurants.size(); j++) {

                        if (fetchPlaceResponse.getPlace().getId().equals(chosenRestaurants.get(j))) {
                            markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_pin_restaurant_green));
                            break;
                        } else {
                            markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_pin_restaurant_orange));
                        }
                    }
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(cameraUpdate);
                    Timber.d("New Place Clicked : %s, %s", fetchPlaceResponse.getPlace().getName(), fetchPlaceResponse.getPlace().getLatLng());
                }).addOnFailureListener(Throwable::printStackTrace);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    // LifeCycle for MapView
    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        binding.mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        Timber.d("onResume: called");
        super.onResume();
        binding.mapView.onResume();
        binding.fabMapView.setImageResource(isGPSEnabled(requireContext()) ? R.drawable.ic_location : R.drawable.ic_location_disabled);
    }

    @Override
    public void onStart() {
        Timber.d("onStart: called");
        super.onStart();
        binding.mapView.onStart();
        if (mAuth != null) {
            setupSnapshotListener();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.d("onStop: called");

        binding.mapView.onStop();
        if (mAuth != null) {
            workmateListener.remove();
        }
    }

    @Override
    public void onPause() {
        Timber.d("onPause: called");
        super.onPause();
        binding.mapView.onPause();
        binding.fabMapView.setImageResource(isGPSEnabled(requireContext()) ? R.drawable.ic_location : R.drawable.ic_location_disabled);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }
}