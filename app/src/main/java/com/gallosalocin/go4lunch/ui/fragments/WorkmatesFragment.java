package com.gallosalocin.go4lunch.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gallosalocin.go4lunch.R;
import com.gallosalocin.go4lunch.adapters.WorkmateAdapter;
import com.gallosalocin.go4lunch.databinding.FragmentWorkmatesBinding;
import com.gallosalocin.go4lunch.models.Workmate;
import com.gallosalocin.go4lunch.viewmodels.RestaurantViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import static com.gallosalocin.go4lunch.util.Constants.CHOSEN_RESTAURANT_ID_FIELD;
import static com.gallosalocin.go4lunch.util.Constants.NAME_FIELD;
import static com.gallosalocin.go4lunch.util.Constants.WORKMATES_COLLECTION;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference workmateCollectionRef = db.collection(WORKMATES_COLLECTION);
    private NavController navController;

    private WorkmateAdapter mAdapter;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);

        setupRecyclerView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
    }

    // Setup RecyclerView
    private void setupRecyclerView() {
        Query query = workmateCollectionRef.orderBy(CHOSEN_RESTAURANT_ID_FIELD, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Workmate> options = new FirestoreRecyclerOptions.Builder<Workmate>().setQuery(query, Workmate.class).build();

        mAdapter = new WorkmateAdapter(options, requireContext());
        binding.rvWorkmates.setLayoutManager(new LinearLayoutManager(requireContext()));

        mAdapter.setOnItemClickListener(documentSnapshot -> {
            String placeId = (String) documentSnapshot.get(CHOSEN_RESTAURANT_ID_FIELD);
            if (placeId.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.toast_workmate_not_decided, documentSnapshot.get(NAME_FIELD)),
                        Toast.LENGTH_SHORT).show();
            } else {
                WorkmatesFragmentDirections.ActionWorkmatesFragmentToDetailsFragment action =
                        WorkmatesFragmentDirections.actionWorkmatesFragmentToDetailsFragment(null, placeId);
                navController.navigate(action);
            }
        });
        binding.rvWorkmates.setAdapter(mAdapter);
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
        menu.findItem(R.id.tb_menu_search_btn).setVisible(false);
        menu.findItem(R.id.tb_menu_sort_btn).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.tb_menu_search_btn) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}