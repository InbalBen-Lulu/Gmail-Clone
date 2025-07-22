package com.example.mail_app.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.mail_app.R;
import com.example.mail_app.ui.mail.MailListFragment;
import com.example.mail_app.utils.AppConstants;
import com.example.mail_app.viewmodel.MailViewModel;

/**
 * Activity for performing full-text search on mails.
 * - Updates results in real-time as the user types.
 * - Shows up to 50 matching mails sorted by most recent.
 * - Replaces the fragment view with the search result list.
 */
public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private ImageButton clearButton;
    private MailViewModel viewModel;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private String lastQuery = "";

    /**
     * Initializes the activity, sets up listeners, and loads the initial empty fragment.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.search_input);
        clearButton = findViewById(R.id.clear_button);
        ImageButton backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(MailViewModel.class);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            /**
             * Called when the search text changes.
             * - Runs a delayed search after debounce time.
             * - Updates the mail list with 50 results.
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastQuery = s.toString().trim();
                handler.removeCallbacks(searchRunnable);

                if (lastQuery.isEmpty()) {
                    clearButton.setVisibility(View.GONE);
                    loadEmptyFragment();
                } else {
                    clearButton.setVisibility(View.VISIBLE);

                    searchRunnable = () -> {
                        viewModel.setCategory("Search");
                        viewModel.searchMails(lastQuery, 50, 0);
                        loadMailListFragment();
                    };
                    handler.postDelayed(searchRunnable, AppConstants.SEARCH_DEBOUNCE_DELAY_MS);
                }
            }
        });

        clearButton.setOnClickListener(v -> searchInput.setText(""));

        // Load empty state initially
        loadEmptyFragment();
    }

    /**
     * Loads the MailListFragment to show search results.
     */
    private void loadMailListFragment() {
        Fragment fragment = MailListFragment.newInstance("Search");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.search_fragment_container, fragment);
        transaction.commit();
    }

    /**
     * Loads a blank fragment (used when search input is empty).
     */
    private void loadEmptyFragment() {
        Fragment emptyFragment = new Fragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.search_fragment_container, emptyFragment);
        transaction.commit();
    }
}
