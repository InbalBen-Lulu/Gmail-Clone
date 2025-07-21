package com.example.mail_app.ui.mail;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.MailRecipientCrossRef;
import com.example.mail_app.utils.UiUtils;
import com.example.mail_app.viewmodel.MailViewModel;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Activity for composing a new mail or editing an existing draft.
 * Handles UI input, send/back button logic, and integrates with MailViewModel.
 */
public class ComposeActivity extends AppCompatActivity {

    private EditText toInput, subjectInput, bodyInput;
    private ImageView sendButton, backButton;
    private MailViewModel viewModel;

    public static final String EXTRA_MAIL_ID = "mailId";

    // If not null → we're editing an existing draft
    private String mailId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // Initialize input fields and buttons
        toInput = findViewById(R.id.to_input);
        subjectInput = findViewById(R.id.subject_input);
        bodyInput = findViewById(R.id.body_input);
        sendButton = findViewById(R.id.send_button);
        backButton = findViewById(R.id.back_button);

        viewModel = new ViewModelProvider(this).get(MailViewModel.class);

        // Check if we're editing an existing draft
        mailId = getIntent().getStringExtra(EXTRA_MAIL_ID);
        if (mailId != null) {
            viewModel.getLiveMailById(mailId).observe(this, mail -> {
                if (mail != null) {
                    fillDraftFields(mail);
                }
            });
        }

        // Handle clicks on send/back buttons
        sendButton.setOnClickListener(v -> handleSend());
        backButton.setOnClickListener(v -> handleBack());
    }

    /**
     * Fills input fields with content from a draft mail.
     */
    private void fillDraftFields(FullMail draft) {
        Mail mail = draft.getMail();

        // Build recipient string from cross-ref list
        List<String> toList = draft.getRecipientRefs().stream()
                .map(MailRecipientCrossRef::getUserId)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (!toList.isEmpty()) {
            String toDisplay = String.join(", ", toList);
            toInput.setText(toDisplay);
        }

        // Set subject if not empty
        String subject = mail.getSubject();
        if (subject != null && !subject.isBlank()) {
            subjectInput.setText(subject);
        }

        // Set body if not empty
        String body = mail.getBody();
        if (body != null && !body.isBlank()) {
            bodyInput.setText(body);
        }
    }

    /**
     * Handles the send button logic.
     * If editing a draft → calls sendDraft, otherwise creates a new mail.
     */
    private void handleSend() {
        String to = toInput.getText().toString().trim();
        String subject = subjectInput.getText().toString().trim();
        String body = bodyInput.getText().toString().trim();

        if (to.isEmpty()) {
            UiUtils.showMessage(this, getString(R.string.compose_error_no_recipient));
            return;
        }

        // Common callback for success/error
        Consumer<String> callback = msg -> {
            if (msg == null || msg.isEmpty()) {
                finish(); // Success
            } else {
                UiUtils.showMessage(this, msg); // Show error, stay on screen
            }
        };

        if (mailId != null) {
            viewModel.sendDraft(mailId, to, subject, body, callback);
        } else {
            viewModel.createMail(to, subject, body, false, callback);
        }
    }

    /**
     * Handles the back button logic.
     * If fields are empty → exits (or deletes existing draft).
     * Otherwise → saves as draft (update or create).
     */
    private void handleBack() {
        String to = toInput.getText().toString().trim();
        String subject = subjectInput.getText().toString().trim();
        String body = bodyInput.getText().toString().trim();

        if (to.isEmpty() && subject.isEmpty() && body.isEmpty()) {
            if (mailId != null) {
                viewModel.deleteMail(mailId, msg -> {
                    UiUtils.showMessage(this, msg);
                    finish();
                });
            } else {
                finish(); // Nothing to save or delete
            }
            return;
        }

        if (mailId != null) {
            viewModel.updateMail(mailId, to, subject, body, msg -> {
                UiUtils.showMessage(this, msg, this::finish); // Delay finish until Snackbar closes
            });
        } else {
            viewModel.createMail(to, subject, body, true, msg -> {
                UiUtils.showMessage(this, msg, this::finish);
            });
        }
    }
}
