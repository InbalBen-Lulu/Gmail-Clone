package com.example.mail_app.ui.mail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.mail_app.R;
import com.example.mail_app.ui.search.SearchActivity;
import com.example.mail_app.ui.user.ProfileDialogFragment;
import com.example.mail_app.ui.view.UserAvatarView;
import com.example.mail_app.viewmodel.LoggedInUserViewModel;

/**
 * Activity that displays the main mail page, including:
 * - A search bar that opens SearchActivity.
 * - A profile image button that opens the ProfileDialogFragment.
 */
public class MailPageActivity extends AppCompatActivity {

    private EditText searchInput;
    private UserAvatarView avatarButton;
    private LoggedInUserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_page);

        // Find search input
        searchInput = findViewById(R.id.search_input);

        // Set click listener to open SearchActivity
        searchInput.setOnClickListener(v -> {
            Intent intent = new Intent(MailPageActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // Find avatar button
        avatarButton = findViewById(R.id.avatar_button);

        // Set up ViewModel to observe user data
        userViewModel = new ViewModelProvider(this).get(LoggedInUserViewModel.class);
        userViewModel.getUser().observe(this, user -> {
            if (user == null) return;
            String imageUrl = user.getProfileImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                avatarButton.setImageUrl(imageUrl);
            } else {
                avatarButton.setImageRes(R.drawable.default_avatar);
            }
        });

        // Set click listener to open ProfileDialogFragment
        avatarButton.setOnClickListener(v -> {
            ProfileDialogFragment dialog = new ProfileDialogFragment();
            dialog.show(getSupportFragmentManager(), "ProfileDialog");
        });
    }
}
