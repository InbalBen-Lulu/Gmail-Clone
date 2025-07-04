package com.example.mail_app.ui.mail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.ui.view.UserAvatarView;
import com.example.mail_app.viewmodel.MailViewModel;

public class MailDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MAIL_ID = "mail_id";

    private TextView subjectText, fromNameText, fromEmailText, dateText, bodyText;
    private UserAvatarView fromAvatar;

    private final MailViewModel viewModel = new MailViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_details);

        initViews();

        String mailId = getIntent().getStringExtra(EXTRA_MAIL_ID);
        if (mailId == null) {
            Toast.makeText(this, "Mail not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel.getMails().observe(this, mails -> {
            for (FullMail mail : mails) {
                if (mail.getMail().getId().equals(mailId)) {
                    bindData(mail);
                    break;
                }
            }
        });

        viewModel.loadMailById(mailId);
    }

    private void initViews() {
        subjectText = findViewById(R.id.subjectText);
        fromNameText = findViewById(R.id.fromNameText);
        fromEmailText = findViewById(R.id.fromEmailText);
        dateText = findViewById(R.id.dateText);
        bodyText = findViewById(R.id.bodyText);
        fromAvatar = findViewById(R.id.fromAvatar);
    }

    private void bindData(FullMail mail) {
        subjectText.setText(mail.getMail().getSubject());
        fromNameText.setText(mail.getFromUser().getName());
        fromEmailText.setText("<" + mail.getFromUser().getUserId() + ">");
        dateText.setText(mail.getMail().getSentAt() != null ? mail.getMail().getSentAt().toString() : "");
        bodyText.setText(mail.getMail().getBody());

        String uri = mail.getFromUser().getProfileImage();
        if (uri != null && !uri.isEmpty()) {
            fromAvatar.setImageUri(Uri.parse(uri));
        } else {
            fromAvatar.setImageRes(R.drawable.default_avatar);
        }
    }

    public static void open(Context context, String mailId) {
        Intent intent = new Intent(context, MailDetailsActivity.class);
        intent.putExtra(EXTRA_MAIL_ID, mailId);
        context.startActivity(intent);
    }
}
