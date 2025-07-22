package com.example.mail_app.ui.mail;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import com.example.mail_app.R;
import com.example.mail_app.ui.search.SearchActivity;
import com.example.mail_app.ui.user.ProfileDialogFragment;
import com.example.mail_app.ui.view.UserAvatarView;
import com.example.mail_app.utils.AppConstants;
import com.example.mail_app.viewmodel.LoggedInUserViewModel;
import com.example.mail_app.viewmodel.MailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * Activity that displays the main Gmail-style mail page.
 *
 * Includes:
 * - A top bar with search, menu icon, and profile button.
 * - A sidebar drawer with categories and labels.
 * - A floating compose button.
 * - Dynamic loading of MailListFragment depending on selected category/label.
 */
public class MailPageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private EditText searchInput;
    private ImageView menuIcon;
    private UserAvatarView avatarButton;
    private LoggedInUserViewModel userViewModel;
    private MailViewModel mailViewModel;
    private FloatingActionButton composeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_page);

        // Find DrawerLayout from XML
        drawerLayout = findViewById(R.id.app_drawer_layout);

        // Dynamically set sidebar width to ¾ of screen width
        FragmentContainerView sidebarFragment = findViewById(R.id.sidebar_fragment);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int targetWidth = (int) (screenWidth * AppConstants.SIDEBAR_WIDTH_RATIO);
        ViewGroup.LayoutParams params = sidebarFragment.getLayoutParams();
        params.width = targetWidth;
        sidebarFragment.setLayoutParams(params);

        // Set up search input to launch SearchActivity
        searchInput = findViewById(R.id.search_input);
        searchInput.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));

        // Set up menu icon to open drawer
        menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Set up compose mail FAB
        composeButton = findViewById(R.id.composeButton);
        composeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivity(intent);
        });

        // Setup profile avatar and click listener
        avatarButton = findViewById(R.id.avatar_button);

        // Observe logged-in user and load profile image
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

        // Open profile dialog on avatar click
        avatarButton.setOnClickListener(v -> {
            ProfileDialogFragment dialog = new ProfileDialogFragment();
            dialog.show(getSupportFragmentManager(), AppConstants.TAG_PROFILE_DIALOG);
        });

        // Intercept back button: close drawer before finishing activity
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        // Initialize mail ViewModel and load mails
        mailViewModel = new ViewModelProvider(this).get(MailViewModel.class);
        mailViewModel.loadInitialMails();

        // Display MailListFragment (initially with inbox category)
        MailListFragment fragment = MailListFragment.newInstance(getString(R.string.sidebar_inbox));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
    }

    /**
     * Loads mails for the given category (Inbox, Starred, Sent, etc.).
     *
     * @param title The category title.
     */
    public void loadCategoryMails(String title) {
        mailViewModel.setCategory(title);
    }

    /**
     * Loads mails associated with the given label ID.
     *
     * @param labelId The unique ID of the label.
     */
    public void loadLabelMails(String labelId) {
        mailViewModel.setLabel(labelId);
    }
}
