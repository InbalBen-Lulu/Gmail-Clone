package com.example.mail_app.ui.auth;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mail_app.R;
import com.example.mail_app.utils.DateUtils;
import com.example.mail_app.viewmodel.LoggedInUserViewModel;
import com.example.mail_app.data.dto.LoginResponse;
import com.example.mail_app.data.entity.PublicUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private LinearLayout step1, step2, step3, step4;
    private TextInputEditText firstNameInput, lastNameInput, dayInput, yearInput, usernameInput, passwordInput;
    private AutoCompleteTextView monthSpinner, genderSpinner;
    private TextInputLayout firstNameLayout, lastNameLayout, dayLayout, yearLayout, monthLayout, genderLayout, usernameLayout, passwordLayout;
    private TextView subtitle;
    private CheckBox showPasswordCheckBox;
    private Button nextButton;

    private int currentStep = 1;
    private LoggedInUserViewModel viewModel;
    private LoggedInUserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize view models
        viewModel = new LoggedInUserViewModel();
        userViewModel = new LoggedInUserViewModel();

        // Initialize view references (text inputs, layouts, etc.)
        // Step layouts
        subtitle = findViewById(R.id.subtitle);
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);
        step4 = findViewById(R.id.step4);

        // Input fields
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        dayInput = findViewById(R.id.dayInput);
        yearInput = findViewById(R.id.yearInput);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);

        // Dropdowns for month and gender
        monthSpinner = findViewById(R.id.monthSpinner);
        genderSpinner = findViewById(R.id.genderSpinner);

        // Input layouts for validation error display
        firstNameLayout = findViewById(R.id.firstNameLayout);
        lastNameLayout = findViewById(R.id.lastNameLayout);
        dayLayout = findViewById(R.id.dayLayout);
        yearLayout = findViewById(R.id.yearLayout);
        monthLayout = findViewById(R.id.monthLayout);
        genderLayout = findViewById(R.id.genderLayout);
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        // Password visibility checkbox and the action button
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        nextButton = findViewById(R.id.nextButton);

        // Set up month and gender dropdown adapters
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.months_array));
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gender_array));
        genderSpinner.setAdapter(genderAdapter);

        // Disable manual input for dropdowns
        monthSpinner.setKeyListener(null);
        monthSpinner.setOnClickListener(v -> monthSpinner.showDropDown());
        monthSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) monthSpinner.showDropDown();
        });

        // Auto-show dropdown when clicked or focused
        genderSpinner.setKeyListener(null);
        genderSpinner.setOnClickListener(v -> genderSpinner.showDropDown());
        genderSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) genderSpinner.showDropDown();
        });

        // Toggle password visibility
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            passwordInput.setTransformationMethod(isChecked ? null : new PasswordTransformationMethod());
            passwordInput.setSelection(passwordInput.getText().length());
        });

        // Handle "Next" or "Register" button click
        nextButton.setOnClickListener(v -> handleNextStep());
    }

    private void handleNextStep() {
        clearErrors();

        switch (currentStep) {
            case 1:
                // Validate name inputs
                boolean step1Valid = true;

                if (firstNameInput.getText().toString().trim().isEmpty()) {
                    firstNameLayout.setError(getString(R.string.error_required));
                    step1Valid = false;
                }

                if (lastNameInput.getText().toString().trim().isEmpty()) {
                    lastNameLayout.setError(getString(R.string.error_required));
                    step1Valid = false;
                }

                if (!step1Valid) return;

                step1.setVisibility(View.GONE);
                step2.setVisibility(View.VISIBLE);
                subtitle.setText(getString(R.string.step2_subtitle));
                currentStep++;
                break;

            case 2:
                // Validate birthday and gender
                boolean step2Valid = true;

                String dayStr = dayInput.getText().toString().trim();
                String yearStr = yearInput.getText().toString().trim();
                String monthStr = monthSpinner.getText().toString().trim();
                String genderStr = genderSpinner.getText().toString().trim();

                boolean isDayMissing = dayStr.isEmpty();
                boolean isMonthMissing = monthStr.isEmpty();
                boolean isYearMissing = yearStr.isEmpty();

                int day = isDayMissing ? -1 : Integer.parseInt(dayStr);
                int year = isYearMissing ? -1 : Integer.parseInt(yearStr);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);

                boolean isDayValid = day >= 1 && day <= 31;
                boolean isYearValid = year >= 1900 && year <= currentYear;

                boolean isDateValid = isDayValid && isYearValid &&
                        DateUtils.isValidDate(this, day, monthStr, year);

                if (isDayMissing || isMonthMissing || isYearMissing) {
                    dayLayout.setError(" ");
                    monthLayout.setError(" ");
                    yearLayout.setError(" ");
                    Toast.makeText(this, getString(R.string.error_incomplete_birthday), Toast.LENGTH_SHORT).show();
                    step2Valid = false;
                } else if (!isDateValid) {
                    dayLayout.setError(" ");
                    monthLayout.setError(" ");
                    yearLayout.setError(" ");
                    Toast.makeText(this, getString(R.string.error_invalid_date), Toast.LENGTH_SHORT).show();
                    step2Valid = false;
                }

                if (genderStr.isEmpty()) {
                    genderLayout.setError(getString(R.string.error_select_gender));
                    step2Valid = false;
                }

                if (!step2Valid) return;

                step2.setVisibility(View.GONE);
                step3.setVisibility(View.VISIBLE);
                subtitle.setText(getString(R.string.step3_subtitle));
                currentStep++;

                break;

            case 3:
                // Validate username (email) and check uniqueness via API
                String email = usernameInput.getText().toString().trim();
                if (email.isEmpty()) {
                    usernameLayout.setError(getString(R.string.error_required));
                    return;
                }

                userViewModel.getPublicUserInfo(email, new Callback<PublicUser>() {
                    @Override
                    public void onResponse(Call<PublicUser> call, Response<PublicUser> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            usernameLayout.setError(getString(R.string.error_username_taken));
                        } else {
                            step3.setVisibility(View.GONE);
                            step4.setVisibility(View.VISIBLE);
                            subtitle.setText(getString(R.string.step4_subtitle));
                            nextButton.setText(getString(R.string.register_button_text));
                            currentStep++;
                        }
                    }

                    @Override
                    public void onFailure(Call<PublicUser> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.generic_error, t.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case 4:
                // Validate password and collect all user input for registration
                String password = passwordInput.getText().toString().trim();
                if (password.length() < 8) {
                    passwordLayout.setError(getString(R.string.error_password_short));
                    return;
                }

                // Collect data
                String fullName = firstNameInput.getText().toString().trim() + " " + lastNameInput.getText().toString().trim();
                String gender = genderSpinner.getText().toString().trim();
                int selectedDay = Integer.parseInt(dayInput.getText().toString());
                int selectedYear = Integer.parseInt(yearInput.getText().toString());
                String selectedMonth = monthSpinner.getText().toString().trim();

                String[] monthsArray = getResources().getStringArray(R.array.months_array);
                int month = Arrays.asList(monthsArray).indexOf(selectedMonth);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, selectedYear);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                viewModel.registerUser(
                        usernameInput.getText().toString().trim(),
                        password,
                        fullName,
                        gender,
                        calendar.getTime(),
                        new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.registration_failed), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResponse> call, Throwable t) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.generic_error, t.getMessage()), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                break;
        }
    }

    private void clearErrors() {
        // Clear all input layout error messages
        firstNameLayout.setError(null);
        lastNameLayout.setError(null);
        dayLayout.setError(null);
        yearLayout.setError(null);
        monthLayout.setError(null);
        genderLayout.setError(null);
        usernameLayout.setError(null);
        passwordLayout.setError(null);
    }
}
