package com.example.mail_app.ui.mail;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mail_app.R;
import com.example.mail_app.ui.search.SearchActivity;

/**
 * Activity that displays the main mail page, including a search bar.
 * When the user clicks on the search bar, it opens the SearchActivity.
 */
public class MailPageActivity extends AppCompatActivity {

    private EditText searchInput;

    /**
     * Called when the activity is starting.
     * Sets up the layout and configures the search bar click behavior.
     *
     * @param savedInstanceState If the activity is being re-initialized
     *     after previously being shut down, this Bundle contains the most
     *     recent data. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_page);

        // Find the search input
        searchInput = findViewById(R.id.search_input);

        // Set click listener to open SearchActivity
        searchInput.setOnClickListener(v -> {
            Intent intent = new Intent(MailPageActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }
}
