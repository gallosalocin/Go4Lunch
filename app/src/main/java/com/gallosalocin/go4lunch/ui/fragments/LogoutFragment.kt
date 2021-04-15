package com.gallosalocin.go4lunch.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.gallosalocin.go4lunch.R
import com.gallosalocin.go4lunch.databinding.FragmentLogoutBinding
import com.gallosalocin.go4lunch.ui.MainActivity
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LogoutFragment : Fragment(R.layout.fragment_logout) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentLogoutBinding.inflate(inflater, container, false)
        AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener { task: Task<Void?> ->
            if (task.isSuccessful) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finishAffinity()
            } else {
                Timber.e(task.exception, "LogoutFragment - onComplete: Error")
            }
        }
        return binding.root
    }
}