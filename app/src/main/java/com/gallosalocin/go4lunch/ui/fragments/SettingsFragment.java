package com.gallosalocin.go4lunch.ui.fragments;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.gallosalocin.go4lunch.R;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext().getApplicationContext());

        loadSettings();

    }

    private void loadSettings() {
        String language = sharedPreferences.getString("key_pref_language", "");
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
    public void onPause() {
        loadSettings();
        super.onPause();
    }
}
