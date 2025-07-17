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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mail_app.R;
import com.example.mail_app.ui.search.SearchActivity;
import com.example.mail_app.ui.user.ProfileDialogFragment;
import com.example.mail_app.ui.view.UserAvatarView;
import com.example.mail_app.utils.AppConstants;
import com.example.mail_app.viewmodel.LoggedInUserViewModel;
import com.example.mail_app.viewmodel.MailViewModel;

import java.util.ArrayList;

/**
 * Activity that displays the main mail page, including:
 * - A search bar that opens SearchActivity.
 * - A profile image button that opens the ProfileDialogFragment.
 * - A menu icon that opens the sidebar drawer.
 *
 * It observes user and mail data, handles category and label selection,
 * and manages sidebar width dynamically.
 */
public class MailPageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private EditText searchInput;
    private ImageView menuIcon;
    private UserAvatarView avatarButton;
    private LoggedInUserViewModel userViewModel;
    private MailViewModel mailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_page);

        // Find DrawerLayout (renamed ID)
        drawerLayout = findViewById(R.id.app_drawer_layout);

        // Dynamically set sidebar width to ¾ of screen width
        FragmentContainerView sidebarFragment = findViewById(R.id.sidebar_fragment);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int targetWidth = (int) (screenWidth * AppConstants.SIDEBAR_WIDTH_RATIO);// ¾ of screen
        ViewGroup.LayoutParams params = sidebarFragment.getLayoutParams();
        params.width = targetWidth;
        sidebarFragment.setLayoutParams(params);

        // Find menu icon and set click listener to open drawer
        menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Find search input
        searchInput = findViewById(R.id.search_input);
        searchInput.setOnClickListener(v ->  startActivity(new Intent(this, SearchActivity.class)));

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
            dialog.show(getSupportFragmentManager(), AppConstants.TAG_PROFILE_DIALOG);
        });

        // Handle back button to close drawer first
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

        mailViewModel = new ViewModelProvider(this).get(MailViewModel.class);
        mailViewModel.loadInitialMails();

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
