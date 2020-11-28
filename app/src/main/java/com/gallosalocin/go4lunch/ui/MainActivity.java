package com.gallosalocin.go4lunch.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.gallosalocin.go4lunch.R;
import com.gallosalocin.go4lunch.databinding.ActivityMainBinding;
import com.gallosalocin.go4lunch.models.Workmate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import static com.gallosalocin.go4lunch.util.Constants.WORKMATES_COLLECTION;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference workmatesCollectionRef = db.collection(WORKMATES_COLLECTION);
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate: called");
        loadSettings();
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        checkUserLogged();
        setupNavigationComponent();
        setupViewVisibility();

    }

    // Setup Navigation Component
    private void setupNavigationComponent() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.bottom_nav_mapview, R.id.bottom_nav_listview,
                R.id.bottom_nav_workmates).setOpenableLayout(binding.drawerLayout).build();

        setSupportActionBar(binding.toolbar);
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.nvMainDrawer, navController);
        NavigationUI.setupWithNavController(binding.mainBottomNav, navController);
        binding.mainBottomNav.setOnNavigationItemReselectedListener(item -> { /* No Reload Fragment */ });

    }


    // Display User Info Drawer
    private void setupUserInfoDrawer() {
        Timber.d("setupUserInfoDrawer");
        TextView mUserName = binding.nvMainDrawer.getHeaderView(0).findViewById(R.id.tv_drawer_name);
        TextView mMail = binding.nvMainDrawer.getHeaderView(0).findViewById(R.id.tv_drawer_mail);
        CircleImageView mUserImage = binding.nvMainDrawer.getHeaderView(0).findViewById(R.id.iv_drawer_user_image);

        mUserName.setText(mAuth.getDisplayName());
        mMail.setText(mAuth.getEmail());
        Picasso.get().load(mAuth.getPhotoUrl()).into(mUserImage);
    }

    // Setup View Visibility
    private void setupViewVisibility() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.bottom_nav_mapview:
                case R.id.bottom_nav_workmates:
                case R.id.bottom_nav_listview:
                    binding.mainBottomNav.setVisibility(View.VISIBLE);
                    binding.toolbar.setVisibility(View.VISIBLE);
                    binding.toolbar.setBackgroundResource(R.color.colorPrimary);
                    invalidateOptionsMenu();
                    break;
                case R.id.lunchFragment:
                case R.id.settingsFragment:
                    binding.mainBottomNav.setVisibility(View.GONE);
                    invalidateOptionsMenu();
                    break;
                case R.id.detailsFragment:
                    binding.mainBottomNav.setVisibility(View.GONE);
                    binding.toolbar.setVisibility(View.GONE);
                    invalidateOptionsMenu();
                    break;
                default:
                    break;
            }
        });
    }

    // Check If User Is Logged
    private void checkUserLogged() {
        if (mAuth != null) {
            setupUserInfoDrawer();
            checkUserExistFirestore();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Check if Workmate exist on Firestore
    private void checkUserExistFirestore() {
        workmatesCollectionRef.document(mAuth.getUid()).get().addOnCompleteListener(task -> {
            Timber.d("checkUserExistFirestore");
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    Timber.d("User already created to firestore");
                } else {
                    saveUserFirestore();
                }
            } else {
                Timber.d(task.getException(), "checkUserExistFirestore");
            }
        });
    }

    // Save User to Firestore
    private void saveUserFirestore() {
        if (mAuth != null) {
            Timber.d("saveUserFirestore");

            String name = mAuth.getDisplayName();
            String photoProfile = Objects.requireNonNull(mAuth.getPhotoUrl()).toString();
            String chosenRestaurantId = "";
            String chosenRestaurantName = "";
            List<String> like = new ArrayList<>();

            Workmate workmate = new Workmate(name, photoProfile, chosenRestaurantId, chosenRestaurantName, like);

            workmatesCollectionRef.document(mAuth.getUid()).set(workmate)
                    .addOnSuccessListener(aVoid -> Timber.d("User saved"))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
                        Timber.d(e.toString());
                    });
        }
    }

    // Change App Language
    private void loadSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String language = sharedPreferences.getString("key_pref_language", "fr");
        setAppLocale(language);
    }

    public void setAppLocale(String locale) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(new Locale(locale));
        resources.updateConfiguration(config, dm);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.d("onResume: called");
        setupViewVisibility();
    }
}