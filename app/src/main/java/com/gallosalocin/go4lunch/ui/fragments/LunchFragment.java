package com.gallosalocin.go4lunch.ui.fragments;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gallosalocin.go4lunch.BuildConfig;
import com.gallosalocin.go4lunch.R;
import com.gallosalocin.go4lunch.adapters.WorkmateAdapter;
import com.gallosalocin.go4lunch.databinding.FragmentLunchBinding;
import com.gallosalocin.go4lunch.models.Workmate;
import com.gallosalocin.go4lunch.viewmodels.RestaurantViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import timber.log.Timber;

import static com.gallosalocin.go4lunch.util.Constants.CHOSEN_RESTAURANT_ID_FIELD;
import static com.gallosalocin.go4lunch.util.Constants.CHOSEN_RESTAURANT_NAME_FIELD;
import static com.gallosalocin.go4lunch.util.Constants.FAVORITE_FIELD;
import static com.gallosalocin.go4lunch.util.Constants.PLACE_ID_SHARED_PREFS;
import static com.gallosalocin.go4lunch.util.Constants.SHARED_PREFS;
import static com.gallosalocin.go4lunch.util.Constants.WORKMATES_COLLECTION;

public class LunchFragment extends Fragment {

    private FragmentLunchBinding binding;

    private ListenerRegistration workmateListener;
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference workmatesCollectionRef = db.collection(WORKMATES_COLLECTION);

    private WorkmateAdapter mAdapter;
    private Workmate currentWorkmate;
    private RestaurantViewModel restaurantViewModel;
    private boolean isRestaurantCheck = false;
    private boolean isFavoriteCheck = false;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLunchBinding.inflate(inflater, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String placeId = sharedPreferences.getString(PLACE_ID_SHARED_PREFS, "");

        setupRecyclerView(placeId);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);

    }

    private void getRestaurantDetails(String placeId) {
        restaurantViewModel.getDetailsRestaurant(placeId, BuildConfig.ApiKey).observe(getViewLifecycleOwner(), detailsResult -> {
            Timber.d("getRestaurantDetails");
            if (isRestaurantCheck) {
                String imageUrl;
                binding.tvMyLunchNameRestaurant.setText(detailsResult.getName());
                binding.tvMyLunchInformation.setText(detailsResult.getAddress());
                binding.ratingMyLunch.setRating((detailsResult.getRating() * 3) / 5);
                if (detailsResult.getDetailsPhotos() != null) {
                    imageUrl = getPhoto(detailsResult.getDetailsPhotos().get(0).getPhotoReference(),
                            detailsResult.getDetailsPhotos().get(0).getWidth(), BuildConfig.ApiKey);
                    Picasso.get().load(imageUrl).fit().centerCrop().into(binding.ivMyLunchPicture);
                } else {
                    binding.ivMyLunchPicture.setImageResource(R.drawable.ic_broken_image);
                }

                binding.btnMyLunchCall.setOnClickListener(view -> {
                    if (detailsResult.getFormattedPhoneNumber() != null) {
                        Intent callRestaurant = new Intent(Intent.ACTION_DIAL);
                        callRestaurant.setData(Uri.parse("tel:" + detailsResult.getFormattedPhoneNumber()));
                        startActivity(callRestaurant);
                    } else {
                        Toast.makeText(requireContext(), R.string.alert_details_no_phone_number, Toast.LENGTH_SHORT).show();
                    }
                });

                binding.btnMyLunchLike.setOnClickListener(view -> {
                    if (isFavoriteCheck) {
                        workmatesCollectionRef.document(Objects.requireNonNull(mAuth.getUid())).update(FAVORITE_FIELD,
                                FieldValue.arrayRemove(placeId));
                        binding.btnMyLunchLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_red, 0, 0);
                        isFavoriteCheck = false;
                    } else {
                        workmatesCollectionRef.document(Objects.requireNonNull(mAuth.getUid())).update(FAVORITE_FIELD,
                                FieldValue.arrayUnion(placeId));
                        binding.btnMyLunchLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_yellow, 0, 0);
                        isFavoriteCheck = true;
                    }
                });

                binding.btnMyLunchWebsite.setOnClickListener(view -> {
                    if (detailsResult.getWebsite() != null) {
                        Intent linkRestaurant = new Intent(Intent.ACTION_VIEW);
                        linkRestaurant.setData(Uri.parse(detailsResult.getWebsite()));
                        startActivity(linkRestaurant);
                    } else {
                        Toast.makeText(requireContext(), R.string.alert_details_no_website, Toast.LENGTH_SHORT).show();
                    }
                });

                binding.fabMyLunchChoice.setOnClickListener(view -> {
                    if (isRestaurantCheck) {
                        workmatesCollectionRef.document(mAuth.getUid()).update(CHOSEN_RESTAURANT_ID_FIELD, "");
                        workmatesCollectionRef.document(mAuth.getUid()).update(CHOSEN_RESTAURANT_NAME_FIELD, "");
                        binding.fabMyLunchChoice.setImageResource(R.drawable.ic_check_empty);
                        isRestaurantCheck = false;
                        viewVisibility();
                    }
                });
            }
        });
    }

    private void setupRecyclerView(String placeID) {
        Timber.d("setupRecyclerView");
        Query query = workmatesCollectionRef.whereEqualTo(CHOSEN_RESTAURANT_ID_FIELD, placeID);
        FirestoreRecyclerOptions<Workmate> options = new FirestoreRecyclerOptions.Builder<Workmate>().setQuery(query, Workmate.class).build();

        binding.rvMyLunch.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdapter = new WorkmateAdapter(options, requireContext());
        binding.rvMyLunch.setAdapter(mAdapter);
    }

    public String getPhoto(String photoReference, int maxWidth, String key) {
        return "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=" + maxWidth +
                "&photoreference=" + photoReference +
                "&key=" + key;
    }

    private void setupDocumentSnapshot() {
        Timber.d("setupDocumentSnapshot");
        workmateListener = workmatesCollectionRef.document(mAuth.getUid()).addSnapshotListener((documentSnapshot, error) -> {
            viewVisibility();

            if (error != null) {
                Timber.d(error.toString());
                return;
            }
            if (documentSnapshot.exists()) {
                currentWorkmate = documentSnapshot.toObject(Workmate.class);
                if (!currentWorkmate.getChosenRestaurantId().isEmpty()) {
                    restaurantIsFavorite(currentWorkmate.getChosenRestaurantId());
                    isRestaurantCheck = true;
                    binding.fabMyLunchChoice.setImageResource(R.drawable.ic_check_ok);
                    getRestaurantDetails(currentWorkmate.getChosenRestaurantId());
                    viewVisibility();
                    Timber.d("setupDocumentSnapshot placeId: %s", currentWorkmate.getChosenRestaurantId());
                }
            }
        });
    }

    // Check if restaurant is favorite
    private void restaurantIsFavorite(String placeId) {
        if (currentWorkmate.getFavorite() != null) {
            for (int i = 0; i < currentWorkmate.getFavorite().size(); i++) {
                if (currentWorkmate.getFavorite().get(i).equals(placeId)) {
                    binding.btnMyLunchLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_yellow, 0, 0);
                    isFavoriteCheck = true;
                }
            }
        }
    }

    private void viewVisibility() {
        if (isRestaurantCheck) {
            binding.llMyLunch.setVisibility(View.INVISIBLE);
            binding.fabMyLunchChoice.setVisibility(View.VISIBLE);
            binding.btnMyLunchCall.setVisibility(View.VISIBLE);
            binding.btnMyLunchLike.setVisibility(View.VISIBLE);
            binding.btnMyLunchWebsite.setVisibility(View.VISIBLE);
            binding.cvMyLunchAddress.setVisibility(View.VISIBLE);
            binding.ivMyLunchPicture.setVisibility(View.VISIBLE);
            binding.ratingMyLunch.setVisibility(View.VISIBLE);
            binding.tvMyLunchInformation.setVisibility(View.VISIBLE);
            binding.tvMyLunchNameRestaurant.setVisibility(View.VISIBLE);
            binding.rvMyLunch.setVisibility(View.VISIBLE);
        } else {
            binding.llMyLunch.setVisibility(View.VISIBLE);
            binding.fabMyLunchChoice.setVisibility(View.INVISIBLE);
            binding.btnMyLunchCall.setVisibility(View.INVISIBLE);
            binding.btnMyLunchLike.setVisibility(View.INVISIBLE);
            binding.btnMyLunchWebsite.setVisibility(View.INVISIBLE);
            binding.cvMyLunchAddress.setVisibility(View.INVISIBLE);
            binding.ivMyLunchPicture.setVisibility(View.INVISIBLE);
            binding.ratingMyLunch.setVisibility(View.INVISIBLE);
            binding.tvMyLunchInformation.setVisibility(View.INVISIBLE);
            binding.tvMyLunchNameRestaurant.setVisibility(View.INVISIBLE);
            binding.rvMyLunch.setVisibility(View.INVISIBLE);
        }
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
        Timber.d("onStop");
        mAdapter.stopListening();
        workmateListener.remove();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}