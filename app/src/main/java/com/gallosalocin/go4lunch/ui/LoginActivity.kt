package com.gallosalocin.go4lunch.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.FacebookBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.IdpResponse
import com.gallosalocin.go4lunch.R
import com.gallosalocin.go4lunch.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

open class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)
        iconAnimation()
    }

    protected fun showSignInOptions() {
        val providers = listOf(
                FacebookBuilder().build(),
                GoogleBuilder().build())
        val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .setLogo(R.drawable.ic_logo_login)
                .setIsSmartLockEnabled(false)
                .build()
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                Timber.d("onActivityResult: %s, %s", user.displayName, user.email)
                if (user.metadata.creationTimestamp == user.metadata.lastSignInTimestamp) {
                    Toast.makeText(this, "Welcome " + user.displayName, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Welcome back " + user.displayName, Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val response = IdpResponse.fromResultIntent(data)
                if (response == null) {
                    Timber.d("onActivityResult: The user has cancelled the sign in request")
                    finishAffinity()
                } else {
                    Timber.d(response.error, "MainActivity - onActivityResult: Error")
                }
            }
        }
    }

    // Setup Logo Animation
    private fun iconAnimation() {
        val animation = TranslateAnimation(0F, 0F, 0F, -1500F)
        animation.duration = 1500
        animation.fillAfter = false
        animation.setAnimationListener(MyAnimationListener())
        binding!!.clLogo.animation = animation
    }

    private inner class MyAnimationListener : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {
            binding!!.clLogo.clearAnimation()
            binding!!.clLogo.visibility = View.INVISIBLE
            showSignInOptions()
        }

        override fun onAnimationRepeat(animation: Animation) {}
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    companion object {
        private const val RC_SIGN_IN = 555
    }
}