package com.gallosalocin.go4lunch.ui.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gallosalocin.go4lunch.BuildConfig;
import com.gallosalocin.go4lunch.R;
import com.gallosalocin.go4lunch.adapters.WorkmateAdapter;
import com.gallosalocin.go4lunch.databinding.FragmentDetailsBinding;
import com.gallosalocin.go4lunch.models.RestaurantResult;
import com.gallosalocin.go4lunch.models.Workmate;
import com.gallosalocin.go4lunch.notifications.AlertReceiver;
import com.gallosalocin.go4lunch.viewmodels.RestaurantViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Objects;

import timber.log.Timber;

import static com.gallosalocin.go4lunch.util.Constants.ADDRESS_EXTRA;
import static com.gallosalocin.go4lunch.util.Constants.CHOSEN_RESTAURANT_ID_FIELD;
import static com.gallosalocin.go4lunch.util.Constants.CHOSEN_RESTAURANT_NAME_FIELD;
import static com.gallosalocin.go4lunch.util.Constants.FAVORITE_FIELD;
import static com.gallosalocin.go4lunch.util.Constants.NAME_EXTRA;
import static com.gallosalocin.go4lunch.util.Constants.NOTIFICATION_LUNCH_RESTAURANT_ID;
import static com.gallosalocin.go4lunch.util.Constants.PLACE_ID_SHARED_PREFS;
import static com.gallosalocin.go4lunch.util.Constants.SHARED_PREFS;
import static com.gallosalocin.go4lunch.util.Constants.WORKMATES_COLLECTION;
import static com.gallosalocin.go4lunch.util.Constants.WORKMATES_TODAY_EXTRA;

public class DetailsFragment extends Fragment {

    private ListenerRegistration workmateListener;

    private FragmentDetailsBinding binding;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference workmatesCollectionRef = db.collection(WORKMATES_COLLECTION);
    private Workmate currentWorkmate;
    private WorkmateAdapter mAdapter;
    private RestaurantViewModel restaurantViewModel;
    private RestaurantResult restaurantResult;
    private String placeId;
    private DetailsFragmentArgs args;
    private boolean isRestaurantCheck = false;
    private boolean isFavoriteCheck = false;
    private int fragmentChoice = 0;
    private String workmatesToday;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);

        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);

        setupBackButton();
        getRestaurantArguments();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    // Setup notification time per day
    private Calendar setupCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Timber.d("Calendar : %s", calendar.getTime());
        return calendar;
    }

    // Create alarm notification
    private void alarmNotification(String name, String address, String workmatesToday) {
        if (isRestaurantCheck) {
            Timber.d("START alarmNotification");
            AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(requireContext(), AlertReceiver.class);
            alarmIntent.putExtra(NAME_EXTRA, name);
            alarmIntent.putExtra(ADDRESS_EXTRA, address);
            alarmIntent.putExtra(WORKMATES_TODAY_EXTRA, workmatesToday);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), NOTIFICATION_LUNCH_RESTAURANT_ID, alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            assert alarmManager != null;
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, setupCalendar().getTimeInMillis(), pendingIntent);
        } else {
            Timber.d("CANCEL alarmNotification");
            AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(requireContext(), AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), NOTIFICATION_LUNCH_RESTAURANT_ID, alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            assert alarmManager != null;
            alarmManager.cancel(pendingIntent);
        }
    }

    // Get arguments
    private void getRestaurantArguments() {
        if (getArguments() != null) {
            args = DetailsFragmentArgs.fromBundle(getArguments());
        }
        if (args.getPlaceId() != null) {
            placeId = args.getPlaceId();
            fragmentChoice = 1;
            getRestaurantDetails(placeId);
            setupRecyclerView(placeId);
        } else if (args.getRestaurantResult() != null) {
            restaurantResult = args.getRestaurantResult();
            fragmentChoice = 2;
            getRestaurantDetails(restaurantResult.getPlaceId());
            setupRecyclerView(restaurantResult.getPlaceId());
        }
    }

    // Save Chosen Restaurant
    private void saveRestaurantForLunch(String placeId, String name) {
        if (isRestaurantCheck) {
            workmatesCollectionRef.document(Objects.requireNonNull(mAuth.getUid())).update(CHOSEN_RESTAURANT_ID_FIELD, "");
            workmatesCollectionRef.document(Objects.requireNonNull(mAuth.getUid())).update(CHOSEN_RESTAURANT_NAME_FIELD, "");
            binding.fabDetailsChoice.setImageResource(R.drawable.ic_check_empty);
            isRestaurantCheck = false;
        } else {
            workmatesCollectionRef.document(Objects.requireNonNull(mAuth.getUid())).update(CHOSEN_RESTAURANT_ID_FIELD, placeId);
            workmatesCollectionRef.document(Objects.requireNonNull(mAuth.getUid())).update(CHOSEN_RESTAURANT_NAME_FIELD, name);
            binding.fabDetailsChoice.setImageResource(R.drawable.ic_check_ok);
            isRestaurantCheck = true;
        }
    }

    private void getRestaurantDetails(String placeId) {
        restaurantViewModel.getDetailsRestaurant(placeId, BuildConfig.ApiKey).observe(getViewLifecycleOwner(), detailsResult -> {
            String imageUrl;

            binding.tvDetailsNameRestaurant.setText(detailsResult.getName());
            binding.tvDetailsInformation.setText(detailsResult.getAddress());
            binding.ratingDetails.setRating((detailsResult.getRating() * 3) / 5);
            if (detailsResult.getDetailsPhotos() != null) {
                imageUrl = getPhoto(detailsResult.getDetailsPhotos().get(0).getPhotoReference(),
                        detailsResult.getDetailsPhotos().get(0).getWidth(), BuildConfig.ApiKey);
                Picasso.get().load(imageUrl).fit().centerCrop().into(binding.ivDetailsPicture);
            } else {
                binding.ivDetailsPicture.setImageResource(R.drawable.ic_broken_image);
            }

            binding.btnDetailsCall.setOnClickListener(view -> {
                if (detailsResult.getFormattedPhoneNumber() != null) {
                    Intent callRestaurant = new Intent(Intent.ACTION_DIAL);
                    callRestaurant.setData(Uri.parse("tel:" + detailsResult.getFormattedPhoneNumber()));
                    startActivity(callRestaurant);
                } else {
                    Toast.makeText(requireContext(), R.string.alert_details_no_phone_number, Toast.LENGTH_SHORT).show();
                }
            });

            binding.btnDetailsLike.setOnClickListener(view -> {
                if (isFavoriteCheck) {
                    workmatesCollectionRef.document(Objects.requireNonNull(mAuth.getUid())).update(FAVORITE_FIELD, FieldValue.arrayRemove(placeId));
                    binding.btnDetailsLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_red, 0, 0);
                    isFavoriteCheck = false;
                } else {
                    workmatesCollectionRef.document(Objects.requireNonNull(mAuth.getUid())).update(FAVORITE_FIELD, FieldValue.arrayUnion(placeId));
                    binding.btnDetailsLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_yellow, 0, 0);
                    isFavoriteCheck = true;
                }
            });

            binding.btnDetailsWebsite.setOnClickListener(view -> {
                if (detailsResult.getWebsite() != null) {
                    Intent linkRestaurant = new Intent(Intent.ACTION_VIEW);
                    linkRestaurant.setData(Uri.parse(detailsResult.getWebsite()));
                    startActivity(linkRestaurant);
                } else {
                    Toast.makeText(requireContext(), R.string.alert_details_no_website, Toast.LENGTH_SHORT).show();
                }
            });

            binding.fabDetailsChoice.setOnClickListener(view -> {
                if (fragmentChoice == 1) {
                    saveRestaurantForLunch(this.placeId, detailsResult.getName());
                    saveData(this.placeId);
                    alarmNotification(detailsResult.getName(), detailsResult.getAddress(), workmatesToday);
                } else {
                    saveRestaurantForLunch(restaurantResult.getPlaceId(), restaurantResult.getName());
                    saveData(restaurantResult.getPlaceId());
                    alarmNotification(restaurantResult.getName(), restaurantResult.getAddress(), workmatesToday);
                }
            });
        });
    }

    public void saveData(String placeId) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PLACE_ID_SHARED_PREFS, placeId);
        editor.apply();
    }

    public String getPhoto(String photoReference, int maxWidth, String key) {
        StringBuilder url = new StringBuilder();
        url.append("https://maps.googleapis.com/maps/api/place/photo?");
        url.append("maxwidth=");
        url.append(maxWidth);
        url.append("&photoreference=");
        url.append(photoReference);
        url.append("&key=");
        url.append(key);

        return url.toString();
    }

    private void setupRecyclerView(String placeId) {
        Query query = workmatesCollectionRef.whereEqualTo(CHOSEN_RESTAURANT_ID_FIELD, placeId);
        FirestoreRecyclerOptions<Workmate> options = new FirestoreRecyclerOptions.Builder<Workmate>().setQuery(query, Workmate.class).build();

        mAdapter = new WorkmateAdapter(options, requireContext());
        binding.rvDetailsWorkmates.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDetailsWorkmates.setAdapter(mAdapter);
    }

    private void retrieveWorkmatesForLunch(String placeId) {
        workmatesCollectionRef.whereEqualTo(CHOSEN_RESTAURANT_ID_FIELD, placeId).addSnapshotListener((queryDocumentSnapshots, error) -> {
            Timber.d("retrieveWorkmatesForLunch");

            if (error != null) {
                return;
            }
            workmatesToday = "";
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                Workmate workmate = queryDocumentSnapshot.toObject(Workmate.class);
                if (!workmate.getName().equals(currentWorkmate.getName())) {
                    workmatesToday += workmate.getName() + ", ";
                }
            }if (workmatesToday.length() > 2){
                workmatesToday = workmatesToday.substring(0, workmatesToday.length() - 2);
            }
        });
    }

    private void setupDocumentSnapshot() {
        workmateListener =
                workmatesCollectionRef.document(Objects.requireNonNull(mAuth.getUid())).addSnapshotListener((documentSnapshot, error) -> {
                    Timber.d("setupDocumentSnapshot");
                    if (error != null) {
                        Timber.d(error.toString());
                        return;
                    }
                    assert documentSnapshot != null;
                    if (documentSnapshot.exists()) {
                        currentWorkmate = documentSnapshot.toObject(Workmate.class);

                        switch (fragmentChoice) {
                            case 1:
                                restaurantIsForLunch(placeId);
                                restaurantIsFavorite(placeId);
                                retrieveWorkmatesForLunch(placeId);
                                break;
                            case 2:
                                restaurantIsForLunch(restaurantResult.getPlaceId());
                                restaurantIsFavorite(restaurantResult.getPlaceId());
                                retrieveWorkmatesForLunch(restaurantResult.getPlaceId());
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    // Check if restaurant is for lunch
    private void restaurantIsForLunch(String placeId) {
        Timber.d("restaurantIsForLunch");
        if (currentWorkmate.getChosenRestaurantId().equals(placeId)) {
            binding.fabDetailsChoice.setImageResource(R.drawable.ic_check_ok);
            isRestaurantCheck = true;
        }
    }

    // Check if restaurant is favorite
    private void restaurantIsFavorite(String placeId) {
        if (currentWorkmate.getFavorite() != null) {
            for (int i = 0; i < currentWorkmate.getFavorite().size(); i++) {
                if (currentWorkmate.getFavorite().get(i).equals(placeId)) {
                    binding.btnDetailsLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_yellow, 0, 0);
                    isFavoriteCheck = true;
                }
            }
        }
    }

    // Setup back button
    private void setupBackButton() {
        binding.ivBackBtn.setOnClickListener(view -> requireActivity().onBackPressed());
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("onStart");
        mAdapter.startListening();
        setupDocumentSnapshot();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
        workmateListener.remove();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}