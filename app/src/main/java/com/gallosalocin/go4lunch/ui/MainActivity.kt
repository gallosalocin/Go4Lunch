package com.gallosalocin.go4lunch.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.gallosalocin.go4lunch.R
import com.gallosalocin.go4lunch.databinding.ActivityMainBinding
import com.gallosalocin.go4lunch.models.Workmate
import com.gallosalocin.go4lunch.util.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mAuth = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val workmatesCollectionRef = db.collection(Constants.WORKMATES_COLLECTION)
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate: called")
        loadSettings()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val view: View = binding.root

        setContentView(view)
        checkUserLogged()
        setupNavigationComponent()
        setupViewVisibility()
    }

    // Setup Navigation Component
    private fun setupNavigationComponent() {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?)!!
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.bottom_nav_mapview, R.id.bottom_nav_listview,
                R.id.bottom_nav_workmates).setOpenableLayout(binding.drawerLayout).build()
        setSupportActionBar(binding.toolbar)
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.nvMainDrawer, navController)
        NavigationUI.setupWithNavController(binding.mainBottomNav, navController)
        binding.mainBottomNav.setOnNavigationItemReselectedListener { }
    }

    // Display User Info Drawer
    private fun setupUserInfoDrawer() {
        Timber.d("setupUserInfoDrawer")
        val mUserName = binding.nvMainDrawer.getHeaderView(0).findViewById<TextView>(R.id.tv_drawer_name)
        val mMail = binding.nvMainDrawer.getHeaderView(0).findViewById<TextView>(R.id.tv_drawer_mail)
        val mUserImage: CircleImageView = binding.nvMainDrawer.getHeaderView(0).findViewById(R.id.iv_drawer_user_image)
        mUserName.text = mAuth!!.displayName
        mMail.text = mAuth.email
        Picasso.get().load(mAuth.photoUrl).into(mUserImage)
    }

    // Setup View Visibility
    private fun setupViewVisibility() {
        navController.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
            when (destination.id) {
                R.id.bottom_nav_mapview, R.id.bottom_nav_workmates, R.id.bottom_nav_listview -> {
                    binding.mainBottomNav.visibility = View.VISIBLE
                    binding.toolbar.visibility = View.VISIBLE
                    binding.toolbar.setBackgroundResource(R.color.colorPrimary)
                    invalidateOptionsMenu()
                }
                R.id.lunchFragment, R.id.settingsFragment -> {
                    binding.mainBottomNav.visibility = View.GONE
                    invalidateOptionsMenu()
                }
                R.id.detailsFragment -> {
                    binding.mainBottomNav.visibility = View.GONE
                    binding.toolbar.visibility = View.GONE
                    invalidateOptionsMenu()
                }
                else -> {
                }
            }
        }
    }

    // Check If User Is Logged
    private fun checkUserLogged() {
        if (mAuth != null) {
            setupUserInfoDrawer()
            checkUserExistFirestore()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Check if Workmate exist on Firestore
    private fun checkUserExistFirestore() {
        workmatesCollectionRef.document(mAuth!!.uid).get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            Timber.d("checkUserExistFirestore")
            if (task.isSuccessful) {
                val document = task.result!!
                if (document.exists()) {
                    Timber.d("User already created to firestore")
                } else {
                    saveUserFirestore()
                }
            } else {
                Timber.d(task.exception, "checkUserExistFirestore")
            }
        }
    }

    // Save User to Firestore
    private fun saveUserFirestore() {
        if (mAuth != null) {
            Timber.d("saveUserFirestore")
            val name = mAuth.displayName
            val photoProfile = Objects.requireNonNull(mAuth.photoUrl).toString()
            val chosenRestaurantId = ""
            val chosenRestaurantName = ""
            val like: List<String> = ArrayList()
            val workmate = Workmate(name, photoProfile, chosenRestaurantId, chosenRestaurantName, like)
            workmatesCollectionRef.document(mAuth.uid).set(workmate)
                    .addOnSuccessListener { Timber.d("User saved") }
                    .addOnFailureListener { e: Exception ->
                        Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                        Timber.d(e.toString())
                    }
        }
    }

    // Change App Language
    private fun loadSettings() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val language = sharedPreferences.getString("key_pref_language", "fr")
        if (language != null) {
            setAppLocale(language)
        }
    }

    private fun setAppLocale(locale: String) {
        val resources = resources
        val dm = resources.displayMetrics
        val config = resources.configuration
        config.setLocale(Locale(locale))
        resources.updateConfiguration(config, dm)
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume: called")
        setupViewVisibility()
    }
}