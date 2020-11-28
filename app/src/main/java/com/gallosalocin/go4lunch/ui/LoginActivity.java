package com.gallosalocin.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.gallosalocin.go4lunch.R;
import com.gallosalocin.go4lunch.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private static final int RC_SIGN_IN = 555;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        iconAnimation();
    }

    protected void showSignInOptions() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .setLogo(R.drawable.ic_logo_login)
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Timber.d("onActivityResult: %s, %s", user.getDisplayName(), user.getEmail());
                if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                    Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Welcome back " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
            } else {
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) {
                    Timber.d("onActivityResult: The user has cancelled the sign in request");
                    this.finishAffinity();
                } else {
                    Timber.d(response.getError(), "MainActivity - onActivityResult: Error");
                }
            }
        }
    }

    // Setup Logo Animation
    private void iconAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -1500);
        animation.setDuration(1500);
        animation.setFillAfter(false);
        animation.setAnimationListener(new MyAnimationListener());
        binding.clLogo.setAnimation(animation);
    }

    private class MyAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            binding.clLogo.clearAnimation();
            binding.clLogo.setVisibility(View.INVISIBLE);
            showSignInOptions();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}