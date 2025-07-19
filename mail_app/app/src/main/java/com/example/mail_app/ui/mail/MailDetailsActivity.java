//package com.example.mail_app.ui.mail;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.content.ContextCompat;
//
//import com.example.mail_app.R;
//import com.example.mail_app.data.entity.FullMail;
//import com.example.mail_app.ui.mail.component.LabelChip;
//import com.example.mail_app.ui.mail.component.MailMenu;
//import com.example.mail_app.ui.view.UserAvatarView;
//import com.example.mail_app.viewmodel.MailViewModel;
//
//public class MailDetailsActivity extends AppCompatActivity {
//
//    public static final String EXTRA_MAIL_ID = "mail_id";
//
//    private TextView subjectText, fromNameText, fromEmailText, dateText, bodyText;
//    private UserAvatarView fromAvatar;
//    private Toolbar toolbar;
//    private ImageView mailStar;
//
//    private final MailViewModel viewModel = new MailViewModel();
//    private FullMail currentMail;  // נשמר עבור התפריט
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mail_details);
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle(null);  // מסיר את הטקסט "MailMe"
//
//        initViews();
//
//        String mailId = getIntent().getStringExtra(EXTRA_MAIL_ID);
//        if (mailId == null) {
//            Toast.makeText(this, "Mail not found", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        viewModel.getLiveMailById(mailId).observe(this, mail -> {
//            if (mail != null) {
//                currentMail = mail;
//                bindData(mail); // מציג את הדאטה
//            }
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        if (currentMail != null) {
//            MailMenu.setupMenu(this, menu, currentMail);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish(); // חוזר אחורה
//            return true;
//        }
//        return MailMenu.handleMenuItemClick(
//                this, item, currentMail, viewModel,
//                () -> {
//                    if (item.getItemId() == R.id.action_delete) {
//                        finish();
//                    }
//                },
//                updated -> {
//                    currentMail = updated;
//                    bindData(updated);
//                }
//        );
//
//    }
//
//    private void initViews() {
//        subjectText = findViewById(R.id.subjectText);
//        fromNameText = findViewById(R.id.fromNameText);
//        fromEmailText = findViewById(R.id.fromEmailText);
//        dateText = findViewById(R.id.dateText);
//        bodyText = findViewById(R.id.bodyText);
//        fromAvatar = findViewById(R.id.fromAvatar);
//        mailStar = findViewById(R.id.mailStar);
//    }
//
//    private void bindData(FullMail mail) {
//        subjectText.setText(mail.getMail().getSubject());
//        fromNameText.setText(mail.getFromUser().getName());
//        fromEmailText.setText("To: " + String.join(", ", mail.getToUserIds()));
//        dateText.setText(mail.getMail().getSentAt() != null ? mail.getMail().getSentAt().toString() : "");
//        bodyText.setText(mail.getMail().getBody());
//
//        String uri = mail.getFromUser().getProfileImage();
//        if (uri != null && !uri.isEmpty()) {
//            fromAvatar.setImageUrl(uri);
//        } else {
//            fromAvatar.setImageRes(R.drawable.default_avatar);
//        }
//
//        // כוכב
//        if (mail.getMail().isSpam()) {
//            mailStar.setVisibility(View.GONE);
//        } else {
//            mailStar.setVisibility(View.VISIBLE);
//            updateStarIcon(mail.getMail().isStar());
//
//            mailStar.setOnClickListener(v -> {
//                viewModel.toggleStar(mail.getMail().getId()); // ← שולח בקשת שינוי
//                mail.getMail().setStar(!mail.getMail().isStar()); // ← עדכון מקומי ל־UI
//                updateStarIcon(mail.getMail().isStar());
//            });
//        }
//
//        // תיוגים
//        LabelChip.displayLabelChips(this, findViewById(R.id.labelContainer), mail.getLabels());
//    }
//
//    private void updateStarIcon(boolean isStarred) {
//        mailStar.setImageResource(isStarred ? R.drawable.baseline_star : R.drawable.outline_star);
//        int color = ContextCompat.getColor(this, isStarred ? R.color.star_yellow : R.color.gray);
//        mailStar.setColorFilter(color);
//    }
//
//    public static void open(Context context, String mailId) {
//        Intent intent = new Intent(context, MailDetailsActivity.class);
//        intent.putExtra(EXTRA_MAIL_ID, mailId);
//        context.startActivity(intent);
//    }
//}

package com.example.mail_app.ui.mail;

import android.app.Activity;
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
import com.example.mail_app.utils.UiUtils;
import com.example.mail_app.viewmodel.MailViewModel;

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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);  // להסרת "MailMe"

        initViews();

        String mailId = getIntent().getStringExtra(EXTRA_MAIL_ID);
        if (mailId == null) {
            Toast.makeText(this, "Mail not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel.getLiveMailById(mailId).observe(this, mail -> {
            if (mail != null) {
                currentMail = mail;
                bindData(mail);
                invalidateOptionsMenu(); // מרענן את התפריט אם טרם הוצג
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentMail != null) {
            MailMenu.setupMenu(this, menu, currentMail);
            return true;
        }
        return false;
    }

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

    private void initViews() {
        subjectText = findViewById(R.id.subjectText);
        fromNameText = findViewById(R.id.fromNameText);
        fromEmailText = findViewById(R.id.fromEmailText);
        dateText = findViewById(R.id.dateText);
        bodyText = findViewById(R.id.bodyText);
        fromAvatar = findViewById(R.id.fromAvatar);
        mailStar = findViewById(R.id.mailStar);
    }

    private void bindData(FullMail mail) {
        subjectText.setText(mail.getMail().getSubject());
        fromNameText.setText(mail.getFromUser().getName());
        fromEmailText.setText("To: " + String.join(", ", mail.getToUserIds()));
        dateText.setText(mail.getMail().getSentAt() != null ? mail.getMail().getSentAt().toString() : "");
        bodyText.setText(mail.getMail().getBody());

        String uri = mail.getFromUser().getProfileImage();
        if (uri != null && !uri.isEmpty()) {
            fromAvatar.setImageUrl(uri);
        } else {
            fromAvatar.setImageRes(R.drawable.default_avatar);
        }

        if (mail.getMail().isSpam()) {
            mailStar.setVisibility(View.GONE);
        } else {
            mailStar.setVisibility(View.VISIBLE);
            updateStarIcon(mail.getMail().isStar());

            mailStar.setOnClickListener(v -> {
                viewModel.toggleStar(mail.getMail().getId(), msg -> {
                    UiUtils.showMessage(MailDetailsActivity.this, msg);
                });
                updateStarIcon(!mail.getMail().isStar()); // שינוי מיידי ב־UI
            });
        }

        LabelChip.displayLabelChips(this, findViewById(R.id.labelContainer), mail.getLabels());
    }

    private void updateStarIcon(boolean isStarred) {
        mailStar.setImageResource(isStarred ? R.drawable.baseline_star : R.drawable.outline_star);
        int color = ContextCompat.getColor(this, isStarred ? R.color.star_yellow : R.color.gray);
        mailStar.setColorFilter(color);
    }

    public static void open(Context context, String mailId) {
        Intent intent = new Intent(context, MailDetailsActivity.class);
        intent.putExtra(EXTRA_MAIL_ID, mailId);
        context.startActivity(intent);
    }
}
