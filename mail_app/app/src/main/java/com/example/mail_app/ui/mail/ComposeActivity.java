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

public class ComposeActivity extends AppCompatActivity {

    private EditText toInput, subjectInput, bodyInput;
    private ImageView sendButton, backButton;
    private MailViewModel viewModel;

    public static final String EXTRA_MAIL_ID = "mailId";

    private String mailId = null; // אם לא null → מצב עריכת טיוטה קיימת

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        toInput = findViewById(R.id.to_input);
        subjectInput = findViewById(R.id.subject_input);
        bodyInput = findViewById(R.id.body_input);
        sendButton = findViewById(R.id.send_button);
        backButton = findViewById(R.id.back_button);

        viewModel = new ViewModelProvider(this).get(MailViewModel.class);

        // קבלה מה־Intent (אם קיימת טיוטה לעריכה)
        mailId = getIntent().getStringExtra(EXTRA_MAIL_ID);
        if (mailId != null) {
            viewModel.getLiveMailById(mailId).observe(this, mail -> {
                if (mail != null) {
                    fillDraftFields(mail);
                }
            });
        }

        sendButton.setOnClickListener(v -> handleSend());
        backButton.setOnClickListener(v -> handleBack());
    }

    private void fillDraftFields(FullMail draft) {
        Mail mail = draft.getMail();

        List<String> toList = draft.getRecipientRefs().stream()
                .map(MailRecipientCrossRef::getUserId)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (!toList.isEmpty()) {
            String toDisplay = String.join(", ", toList);
            toInput.setText(toDisplay);
        }



        String subject = mail.getSubject();
        if (subject != null && !subject.isBlank()) {
            subjectInput.setText(subject);
        }

        String body = mail.getBody();
        if (body != null && !body.isBlank()) {
            bodyInput.setText(body);
        }
    }

    private void handleSend() {
        String to = toInput.getText().toString().trim();
        String subject = subjectInput.getText().toString().trim();
        String body = bodyInput.getText().toString().trim();

        if (to.isEmpty()) {
            UiUtils.showMessage(this, getString(R.string.compose_error_no_recipient));
            return;
        }

        Consumer<String> callback = msg -> {
            if (msg == null || msg.isEmpty()) {
                finish(); // הצלחה
            } else {
                UiUtils.showMessage(this, msg); // שגיאה – לא יוצאים מהמסך
            }
        };

        if (mailId != null) {
            viewModel.sendDraft(mailId, to, subject, body, callback);
        } else {
            viewModel.createMail(to, subject, body, false, callback);
        }
    }

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
                finish(); // אין מה לשמור או למחוק
            }
            return;
        }

        if (mailId != null) {
            viewModel.updateMail(mailId, to, subject, body, msg -> {
                UiUtils.showMessage(this, msg, this::finish); // רק אחרי שה־Snackbar נעלם
            });
        } else {
            viewModel.createMail(to, subject, body, true, msg -> {
                UiUtils.showMessage(this, msg, this::finish); // אותו דבר כאן
            });
        }
    }
}
