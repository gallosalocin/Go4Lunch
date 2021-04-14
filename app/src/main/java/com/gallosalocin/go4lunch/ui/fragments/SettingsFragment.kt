package com.gallosalocin.go4lunch.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.gallosalocin.go4lunch.R
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
        loadSettings()
    }

    private fun loadSettings() {
        val language = sharedPreferences.getString("key_pref_language", "")
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

    override fun onPause() {
        loadSettings()
        super.onPause()
    }
}