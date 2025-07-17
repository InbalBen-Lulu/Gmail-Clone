package com.example.mail_app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mail_app.R;
import com.example.mail_app.data.dto.LoginResponse;
import com.example.mail_app.data.entity.PublicUser;
import com.example.mail_app.ui.mail.MailPageActivity;
import com.example.mail_app.utils.AppConstants;
import com.example.mail_app.viewmodel.LoggedInUserViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout emailStep, passwordStep;
    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText emailInput, passwordInput;
    private TextView emailDisplay;
    private Button nextButton;
    private CheckBox showPasswordCheckBox;
    private LoggedInUserViewModel userViewModel;
    private Button createAccountButton;
    private int step = 1;
    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getIntent().getBooleanExtra(AppConstants.EXTRA_SHOW_SIGN_OUT_MESSAGE, false)) {
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.signed_out), Snackbar.LENGTH_LONG).show();
        }

        userViewModel = new LoggedInUserViewModel();

        initViews();
        setupListeners();
    }

    /**
     * Initializes all view components.
     */
    private void initViews() {
        emailStep = findViewById(R.id.emailStep);
        passwordStep = findViewById(R.id.passwordStep);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        emailDisplay = findViewById(R.id.emailDisplay);
        nextButton = findViewById(R.id.nextButton);
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        createAccountButton = findViewById(R.id.createAccountButton);
    }

    /**
     * Sets up listeners for buttons and checkboxes.
     */
    private void setupListeners() {
        createAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        nextButton.setOnClickListener(v -> {
            if (step == 1) {
                handleEmailSubmit();
            } else {
                handlePasswordSubmit();
            }
        });

        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            togglePasswordVisibility(isChecked);
        });
    }

    /**
     * Shows or hides password input based on checkbox state.
     */
    private void togglePasswordVisibility(boolean visible) {
        int inputType = visible
                ? android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                : android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

        passwordInput.setInputType(inputType);
        passwordInput.setSelection(passwordInput.getText().length());
    }

    /**
     * Handles the logic when user submits email.
     */
    private void handleEmailSubmit() {
        if (!validateEmail()) return;

        userViewModel.getPublicUserInfo(email, new Callback<PublicUser>() {
            @Override
            public void onResponse(Call<PublicUser> call, Response<PublicUser> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showEmailError(getString(R.string.user_not_found));
                    return;
                }

                goToPasswordStep();
                emailDisplay.setText(getDisplayEmail(email));
            }

            @Override
            public void onFailure(Call<PublicUser> call, Throwable t) {
                t.printStackTrace();
                showEmailError("Connection error");
            }
        });
    }

    /**
     * Handles the logic when user submits password.
     */
    private void handlePasswordSubmit() {
        if (!validatePassword()) return;

        userViewModel.login(email, passwordInput.getText().toString().trim(), new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MailPageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        showPasswordError("Incorrect password. Try again.");
                    }
                });
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                runOnUiThread(() ->
                        showPasswordError(t.getMessage() != null ? t.getMessage() : "Login failed."));
            }
        });
    }

    /**
     * Validates the email field.
     */
    private boolean validateEmail() {
        email = emailInput.getText().toString().trim();
        emailLayout.setError(null);

        if (TextUtils.isEmpty(email)) {
            showEmailError("Email is required");
            return false;
        }

        return true;
    }

    /**
     * Validates the password field.
     */
    private boolean validatePassword() {
        passwordLayout.setError(null);
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            showPasswordError("Password is required");
            return false;
        }

        return true;
    }

    /**
     * Combines local part and domain for display.
     */
    private String getDisplayEmail(String input) {
        String domain = getString(R.string.email_domain);
        return input.endsWith(domain) ? input : input + domain;
    }

    /**
     * Updates UI to show the password step.
     */
    private void goToPasswordStep() {
        step = 2;
        emailStep.setVisibility(View.GONE);
        passwordStep.setVisibility(View.VISIBLE);
    }

    /**
     * Displays error message in email field.
     */
    private void showEmailError(String message) {
        emailLayout.setError(message);
    }

    /**
     * Displays error message in password field.
     */
    private void showPasswordError(String message) {
        passwordLayout.setError(message);
    }
}
