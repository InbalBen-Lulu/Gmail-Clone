package com.example.mail_app.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.mail_app.R;
import com.example.mail_app.ui.view.UserAvatarView;
import com.example.mail_app.utils.AppConstants;
import com.example.mail_app.utils.EmailUtils;
import com.example.mail_app.viewmodel.LoggedInUserViewModel;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Activity that displays the personal information of the currently logged-in user.
 * This includes name, birthday, gender, email, and profile image.
 * Tapping the profile image navigates to the profile image editor.
 */
public class PersonalInfoActivity extends AppCompatActivity {

    private UserAvatarView profileImageView;
    private LoggedInUserViewModel userViewModel;
    private TextView nameTextView;
    private TextView birthdayTextView;
    private TextView genderTextView;
    private TextView emailTextView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        // Initialize views
        profileImageView = findViewById(R.id.profile_image);
        nameTextView = findViewById(R.id.text_name);
        birthdayTextView = findViewById(R.id.text_birthday);
        genderTextView = findViewById(R.id.text_gender);
        emailTextView = findViewById(R.id.text_email);
        ImageButton btnClose = findViewById(R.id.btn_close);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        swipeRefreshLayout.setOnChildScrollUpCallback((parent, child) -> false);
        btnClose.setOnClickListener(v -> finish());

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(LoggedInUserViewModel.class);

        // Observe and display user data
        userViewModel.getUser().observe(this, user -> {
            swipeRefreshLayout.setRefreshing(false); // Stop spinner when data loads
            if (user != null) {
                nameTextView.setText(user.getName());
                String formattedDate = new SimpleDateFormat(AppConstants.DATE_FORMAT_DISPLAY, Locale.getDefault())
                        .format(user.getBirthDate());
                birthdayTextView.setText(formattedDate);
                genderTextView.setText(user.getGender());

                String email = EmailUtils.buildEmail(this, user.getUserId());
                emailTextView.setText(email);

                if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                    profileImageView.setImageUrl(user.getProfileImage());
                }
            }
        });

        // Clicking the image opens the profile picture activity
        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfilePictureActivity.class);
            startActivity(intent);
        });

        // Swipe to refresh user data
        swipeRefreshLayout.setOnRefreshListener(() -> {
            userViewModel.reload(); // We don't need a callback
        });
    }
}