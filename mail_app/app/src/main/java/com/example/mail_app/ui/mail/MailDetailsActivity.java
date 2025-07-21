package com.example.mail_app.ui.mail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.ui.mail.component.LabelChip;
import com.example.mail_app.ui.mail.component.MailMenu;
import com.example.mail_app.ui.view.UserAvatarView;
import com.example.mail_app.utils.MailUtils;
import com.example.mail_app.utils.UiUtils;
import com.example.mail_app.viewmodel.MailViewModel;

/**
 * Activity for displaying the full details of a selected mail.
 * Includes toolbar actions, sender info, body content, and label chips.
 */
public class MailDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MAIL_ID = "mail_id";

    private TextView subjectText, fromNameText, fromEmailText, dateText, bodyText;
    private UserAvatarView fromAvatar;
    private Toolbar toolbar;
    private ImageView mailStar;

    private MailViewModel viewModel;
    private FullMail currentMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_details);

        viewModel = new ViewModelProvider(this).get(MailViewModel.class);

        // Setup toolbar with back arrow and no title
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);  // Remove default title

        initViews();

        // Get the mail ID from intent extras
        String mailId = getIntent().getStringExtra(EXTRA_MAIL_ID);
        if (mailId == null) {
            UiUtils.showMessage(this, getString(R.string.error_mail_not_found));
            finish();
            return;
        }

        // Observe LiveData for the given mail ID
        viewModel.getLiveMailById(mailId).observe(this, mail -> {
            if (mail != null) {
                currentMail = mail;
                bindData(mail);
                invalidateOptionsMenu(); // Refresh the toolbar menu
            }
        });
    }

    /**
     * Creates the toolbar menu using MailMenu helper.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentMail != null) {
            MailMenu.setupMenu(this, menu, currentMail);
            return true;
        }
        return false;
    }

    /**
     * Handles toolbar item clicks.
     * If delete is clicked, closes the screen.
     * If mail was updated (e.g. star/spam), refreshes view.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return MailMenu.handleMenuItemClick(
                this, item, currentMail, viewModel,
                () -> {
                    if (item.getItemId() == R.id.action_delete) {
                        finish();
                    }
                },
                updated -> {
                    currentMail = updated;
                    bindData(updated);
                }
        );
    }

    /**
     * Binds view elements from XML to class fields.
     */
    private void initViews() {
        subjectText = findViewById(R.id.subjectText);
        fromNameText = findViewById(R.id.fromNameText);
        fromEmailText = findViewById(R.id.fromEmailText);
        dateText = findViewById(R.id.dateText);
        bodyText = findViewById(R.id.bodyText);
        fromAvatar = findViewById(R.id.fromAvatar);
        mailStar = findViewById(R.id.mailStar);
    }

    /**
     * Binds mail data to all views: subject, sender info, body, avatar, star status, and labels.
     */
    private void bindData(FullMail mail) {
        subjectText.setText(mail.getMail().getSubject());
        fromNameText.setText(mail.getFromUser().getName());

        String toLine = getString(R.string.mail_to_prefix, String.join(", ", mail.getToUserIds()));
        fromEmailText.setText(toLine);

        dateText.setText(
                mail.getMail().getSentAt() != null
                        ? MailUtils.formatMailDate(mail.getMail().getSentAt())
                        : ""
        );

        bodyText.setText(mail.getMail().getBody());

        // Load avatar image or fallback
        String uri = mail.getFromUser().getProfileImage();
        if (uri != null && !uri.isEmpty()) {
            fromAvatar.setImageUrl(uri);
        } else {
            fromAvatar.setImageRes(R.drawable.default_avatar);
        }

        // Handle star visibility and toggle
        if (mail.getMail().isSpam()) {
            mailStar.setVisibility(View.GONE);
        } else {
            mailStar.setVisibility(View.VISIBLE);
            updateStarIcon(mail.getMail().isStar());

            mailStar.setOnClickListener(v -> {
                viewModel.toggleStar(mail.getMail().getId(), msg -> {
                    UiUtils.showMessage(MailDetailsActivity.this, msg);
                });
                updateStarIcon(!mail.getMail().isStar()); // Immediate UI feedback
            });
        }

        // Display label chips
        LabelChip.displayLabelChips(this, findViewById(R.id.labelContainer), mail.getLabels(), true);
    }

    /**
     * Updates the star icon and color based on whether the mail is starred.
     */
    private void updateStarIcon(boolean isStarred) {
        mailStar.setImageResource(isStarred ? R.drawable.baseline_star : R.drawable.outline_star);
        int color = ContextCompat.getColor(this, isStarred ? R.color.star_yellow : R.color.gray);
        mailStar.setColorFilter(color);
    }

    /**
     * Static helper to launch this screen with the given mail ID.
     */
    public static void open(Context context, String mailId) {
        Intent intent = new Intent(context, MailDetailsActivity.class);
        intent.putExtra(EXTRA_MAIL_ID, mailId);
        context.startActivity(intent);
    }
}
