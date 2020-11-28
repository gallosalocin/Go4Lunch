package com.gallosalocin.go4lunch.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.gallosalocin.go4lunch.databinding.FragmentLogoutBinding;
import com.gallosalocin.go4lunch.ui.MainActivity;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class LogoutFragment extends Fragment {

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        com.gallosalocin.go4lunch.databinding.FragmentLogoutBinding binding = FragmentLogoutBinding.inflate(inflater, container, false);

        AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finishAffinity();
            } else {
                Timber.e(task.getException(), "LogoutFragment - onComplete: Error");
            }
        });
        return binding.getRoot();
    }
}