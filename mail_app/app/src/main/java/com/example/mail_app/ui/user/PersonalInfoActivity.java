package com.example.mail_app.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mail_app.R;
import com.example.mail_app.ui.view.UserAvatarView;
import com.example.mail_app.utils.EmailUtils;
import com.example.mail_app.viewmodel.LoggedInUserViewModel;

/**
 * Activity that displays the personal information of the currently logged-in user.
 * This includes name, birthday, gender, email, and profile image.
 * Tapping the profile image navigates to the profile image editor.
 */
public class PersonalInfoActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 101;

    private UserAvatarView profileImageView;
    private LoggedInUserViewModel userViewModel;

    private TextView nameTextView;
    private TextView birthdayTextView;
    private TextView genderTextView;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        // Views
        profileImageView = findViewById(R.id.profile_image);
        nameTextView = findViewById(R.id.text_name);
        birthdayTextView = findViewById(R.id.text_birthday);
        genderTextView = findViewById(R.id.text_gender);
        emailTextView = findViewById(R.id.text_email);

        // ViewModel
        userViewModel = new ViewModelProvider(this).get(LoggedInUserViewModel.class);

        // Observe user data
        userViewModel.getUser().observe(this, user -> {
            Log.d("PersonalInfo", "observed user = " + user);
            if (user != null) {
                nameTextView.setText(user.getName());
                birthdayTextView.setText(user.getBirthDate().toString());
                genderTextView.setText(user.getGender());

                String email = EmailUtils.buildEmail(this, user.getUserId());
                emailTextView.setText(email);

                if (user.getProfileImage() != null) {
                    profileImageView.setImageUri(Uri.parse(user.getProfileImage()));
                }
            }
        });

        // Clicking the image opens the profile picture activity
        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfilePictureActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            profileImageView.setImageUri(imageUri);
        }
    }
}
