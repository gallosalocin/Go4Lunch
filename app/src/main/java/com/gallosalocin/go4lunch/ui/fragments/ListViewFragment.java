package com.gallosalocin.go4lunch.ui.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gallosalocin.go4lunch.BuildConfig;
import com.gallosalocin.go4lunch.R;
import com.gallosalocin.go4lunch.adapters.RestaurantAdapter;
import com.gallosalocin.go4lunch.databinding.FragmentListViewBinding;
import com.gallosalocin.go4lunch.models.RestaurantResult;
import com.gallosalocin.go4lunch.models.Workmate;
import com.gallosalocin.go4lunch.viewmodels.RestaurantViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static com.gallosalocin.go4lunch.util.Constants.WORKMATES_COLLECTION;
import static java.util.Comparator.comparing;

public class ListViewFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference workmatesCollectionRef = db.collection(WORKMATES_COLLECTION);
    private ListenerRegistration workmateListener;
    private List<String> chosenRestaurantsList;

    private Workmate workmate;
    private FragmentListViewBinding binding;
    private RestaurantAdapter restaurantAdapter;
    private RestaurantViewModel restaurantViewModel;
    private String radius;
    private double latitude;
    private double longitude;
    private String currentLocation;
    private List<RestaurantResult> restaurantResultList;
    private List<RestaurantResult> restaurantResultPredictionsList;
    private boolean stateName = true;
    private boolean stateDistance = true;
    private boolean stateRating = true;
    private boolean stateWorkmates = true;

    private PlacesClient placesClient;
    private List<String> predictionsPlaceIdList;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        radius = sharedPreferences.getString("key_pref_radius_2", "500");

        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);

        placesClient = Places.createClient(requireContext());

        binding.rvListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        restaurantAdapter = new RestaurantAdapter();
        restaurantResultList = new ArrayList<>();

        restaurantResultPredictionsList = new ArrayList<>();
        Timber.d("restaurantResultPredictionsList NEW : %s", restaurantResultPredictionsList.toString());

        getCurrentLocation();

        return binding.getRoot();
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

        MenuItem menuAutoCompleteTextView = menu.findItem(R.id.tb_menu_search_btn);
        AppCompatAutoCompleteTextView actionAutoCompleteTextView = (AppCompatAutoCompleteTextView) menuAutoCompleteTextView.getActionView();
        actionAutoCompleteTextView.setHint("Search restaurants...");
        actionAutoCompleteTextView.setMinimumWidth(800);
        actionAutoCompleteTextView.setDropDownBackgroundResource(R.color.colorWhite);

        actionAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                predictionsPlaceIdList = new ArrayList<>();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(actionAutoCompleteTextView.getText().toString().trim())
                        .build();

                placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener((response) -> {
                            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                                predictionsPlaceIdList.add(prediction.getPlaceId());
                            }

                            if (!predictionsPlaceIdList.isEmpty()) {
                                getRestaurantDetails(predictionsPlaceIdList.get(0));
                            }
                        })
                        .addOnFailureListener((exception) -> {
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                Timber.e("Place not found: %s", apiException.getStatusCode());
                            }
                        });
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tb_menu_search_btn:
                return true;
            case R.id.toolbar_sort_name:
                restaurantResultList.sort(stateName ? comparing(RestaurantResult::getName) :
                        comparing(RestaurantResult::getName).reversed());
                stateName = !stateName;
                restaurantAdapter.notifyDataSetChanged();
                return true;
            case R.id.toolbar_sort_distance:
                restaurantResultList.sort(stateDistance ? comparing(RestaurantResult::getDistance) :
                        comparing(RestaurantResult::getDistance).reversed());
                stateDistance = !stateDistance;
                restaurantAdapter.notifyDataSetChanged();
                return true;
            case R.id.toolbar_sort_rating:
                restaurantResultList.sort(stateRating ? comparing(RestaurantResult::getRating).reversed() :
                        comparing(RestaurantResult::getRating));
                stateRating = !stateRating;
                restaurantAdapter.notifyDataSetChanged();
                return true;
            case R.id.toolbar_sort_workmates:
                restaurantResultList.sort(stateWorkmates ? comparing(RestaurantResult::getWorkmates).reversed() :
                        comparing(RestaurantResult::getWorkmates));
                stateWorkmates = !stateWorkmates;
                restaurantAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        restaurantAdapter.setOnItemClickListener((restaurant) -> {
            ListViewFragmentDirections.ActionListViewFragmentToDetailsFragment action =
                    ListViewFragmentDirections.actionListViewFragmentToDetailsFragment(restaurant, null);
            navController.navigate(action);
        });
    }

    // Get details restaurant from api
    private void getRestaurantDetails(String placeId) {
        restaurantViewModel.getDetailsRestaurant(placeId, BuildConfig.ApiKey).observe(getViewLifecycleOwner(), detailsResult -> {

            Location currentLocation = new Location("");
            currentLocation.setLatitude(latitude);
            currentLocation.setLongitude(longitude);

            Location restaurantLocation = new Location("");
            restaurantLocation.setLatitude(detailsResult.getDetailsGeometry().getLocation().getLat());
            restaurantLocation.setLongitude(detailsResult.getDetailsGeometry().getLocation().getLng());

            int occurrences = Collections.frequency(chosenRestaurantsList, placeId);

            if (!restaurantResultPredictionsList.isEmpty()) {
                if (!placeId.equals(restaurantResultPredictionsList.get(0).getPlaceId())) {
                    restaurantResultPredictionsList.add(0, new RestaurantResult(detailsResult.getName(), detailsResult.getRestaurantOpeningHours(),
                            detailsResult.getAddress(), placeId, detailsResult.getDetailsPhotos(), detailsResult.getRating() * 3 / 5,
                            occurrences, (int) currentLocation.distanceTo(restaurantLocation)));
                }
            } else {
                restaurantResultPredictionsList.add(0, new RestaurantResult(detailsResult.getName(), detailsResult.getRestaurantOpeningHours(),
                        detailsResult.getAddress(), placeId, detailsResult.getDetailsPhotos(), detailsResult.getRating() * 3 / 5, occurrences,
                        (int) currentLocation.distanceTo(restaurantLocation)));
            }

            restaurantAdapter.setData(restaurantResultPredictionsList);
            binding.rvListView.setAdapter(restaurantAdapter);
        });
    }

    // Get nearby restaurants from api
    private void getAllRestaurants() {
        String type = "restaurant";
        restaurantViewModel.getRestaurants(currentLocation, Integer.parseInt(radius), type, BuildConfig.ApiKey).observe(getViewLifecycleOwner(),
                restaurants -> {

                    Location currentLocation = new Location("");
                    currentLocation.setLatitude(latitude);
                    currentLocation.setLongitude(longitude);

                    for (int i = 0; i < restaurants.size(); i++) {

                        int occurrences = Collections.frequency(chosenRestaurantsList, restaurants.get(i).getPlaceId());

                        Location restaurantLocation = new Location("");
                        restaurantLocation.setLatitude(restaurants.get(i).getGeometry().getLocation().getLat());
                        restaurantLocation.setLongitude(restaurants.get(i).getGeometry().getLocation().getLng());

                        restaurantResultList.add(new RestaurantResult(restaurants.get(i).getName(), restaurants.get(i).getRestaurantOpeningHours(),
                                restaurants.get(i).getAddress(), restaurants.get(i).getPlaceId(), restaurants.get(i).getRestaurantPhotos(),
                                restaurants.get(i).getRating() * 3 / 5, occurrences, (int) currentLocation.distanceTo(restaurantLocation)));
                    }

                    restaurantAdapter.setData(restaurantResultList);
                    binding.rvListView.setAdapter(restaurantAdapter);
                });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        Timber.d("getCurrentLocation");
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        try {
            Task<Location> taskLocation = fusedLocationProviderClient.getLastLocation();
            taskLocation.addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    currentLocation = latitude + ", " + longitude;
                    getAllRestaurants();
                }
            });
        } catch (SecurityException e) {
            Timber.e("Exception: %s", e.getMessage());
        }
    }

    // Setup Snapshot Listener Workmates Collection
    private void setupSnapshotListener() {
        Timber.d("setupSnapshotListener");
        chosenRestaurantsList = new ArrayList<>();
        workmateListener = workmatesCollectionRef.addSnapshotListener((queryDocumentSnapshots, error) -> {

            if (error != null) {
                return;
            }
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                workmate = queryDocumentSnapshot.toObject(Workmate.class);
                chosenRestaurantsList.add(workmate.getChosenRestaurantId());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("onStart");
        setupSnapshotListener();

    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.d("onStop");
//        workmateListener.remove();
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("onPause");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d("onDestroy");
        binding = null;
    }
}