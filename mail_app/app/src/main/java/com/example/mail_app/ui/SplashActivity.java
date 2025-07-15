package com.example.mail_app.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mail_app.ui.auth.LoginActivity;
import com.example.mail_app.ui.mail.MailPageActivity;
import com.example.mail_app.viewmodel.LoggedInUserViewModel;

/**
 * SplashActivity is the launcher activity.
 * It checks asynchronously if a user is logged in and navigates accordingly.
 */
public class SplashActivity extends AppCompatActivity {

    private LoggedInUserViewModel userViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(LoggedInUserViewModel.class);

        userViewModel.getUser().observe(this, user -> {
            if (user != null) {
                // User exists → go to MailPageActivity
                startActivity(new Intent(SplashActivity.this, MailPageActivity.class));
            } else {
                // No user → go to LoginActivity
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();  // close SplashActivity to avoid going back here
        });
    }
}
