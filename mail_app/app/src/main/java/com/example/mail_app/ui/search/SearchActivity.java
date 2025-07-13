package com.example.mail_app.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.viewmodel.MailViewModel;
import java.util.List;

/**
 * Activity for searching mails and displaying quick search results.
 * Shows results dynamically as the user types in the search bar.
 */
public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private TextView quickResultsTitle, recentSearchText, debugMailsText;
    private View recentSearchRow;
    private ImageButton clearButton;
    private MailViewModel viewModel;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize UI components
        searchInput = findViewById(R.id.search_input);
        quickResultsTitle = findViewById(R.id.quick_results_label);
        recentSearchRow = findViewById(R.id.recent_search_row);
        recentSearchText = findViewById(R.id.recent_search_text);
        debugMailsText = findViewById(R.id.debug_mails_text);
        clearButton = findViewById(R.id.clear_button);
        ImageButton backButton = findViewById(R.id.back_button);

        // Back button to exit search screen
        backButton.setOnClickListener(v -> finish());

        // Hide all UI elements initially
        quickResultsTitle.setVisibility(View.GONE);
        recentSearchRow.setVisibility(View.GONE);
        clearButton.setVisibility(View.GONE);
        debugMailsText.setVisibility(View.GONE);

        // Get ViewModel instance
        viewModel = new ViewModelProvider(this).get(MailViewModel.class);

        // Observe mail search results
        viewModel.getMails().observe(this, this::handleSearchResults);

        // Listen to text changes in search input
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                // Cancel any previous search task
                handler.removeCallbacks(searchRunnable);

                if (query.isEmpty()) {
                    // Hide UI when search is empty
                    clearButton.setVisibility(View.GONE);
                    quickResultsTitle.setVisibility(View.GONE);
                    recentSearchRow.setVisibility(View.GONE);
                    debugMailsText.setVisibility(View.GONE);
                } else {
                    // Show clear button and recent search row
                    clearButton.setVisibility(View.VISIBLE);
                    recentSearchRow.setVisibility(View.VISIBLE);
                    recentSearchText.setText(query);

                    // Run search after 300ms (debounce)
                    searchRunnable = () -> viewModel.searchMails(query, 5, 0);
                    handler.postDelayed(searchRunnable, 300);
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        // Clear search input when clear button is clicked
        clearButton.setOnClickListener(v -> searchInput.setText(""));
    }

    /**
     * Handles search result updates and updates the UI.
     *
     * @param mails List of FullMail objects returned from search.
     */
    private void handleSearchResults(List<FullMail> mails) {
        String query = searchInput.getText().toString().trim();

        if (query.isEmpty()) {
            quickResultsTitle.setVisibility(View.GONE);
            debugMailsText.setVisibility(View.GONE);
            return;
        }

        if (mails != null && !mails.isEmpty()) {
            quickResultsTitle.setVisibility(View.VISIBLE);
            debugMailsText.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();
            int maxResultsToShow = Math.min(mails.size(), 10);
            for (int i = 0; i < maxResultsToShow; i++) {
                FullMail mail = mails.get(i);
                String subject = mail.getMail().getSubject() != null ? mail.getMail().getSubject() : getString(R.string.no_subject);
                String from = mail.getFromUser() != null ? mail.getFromUser().getName() : getString(R.string.unknown_sender);
                builder.append("• ").append(subject).append(" — ").append(from).append("\n");
            }
            debugMailsText.setText(builder.toString());

        } else {
            quickResultsTitle.setVisibility(View.GONE);
            debugMailsText.setVisibility(View.VISIBLE);
            debugMailsText.setText(getString(R.string.no_results_found));
        }
    }
}
