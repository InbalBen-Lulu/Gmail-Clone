package com.example.mail_app;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.viewmodel.LabelViewModel;
import com.example.mail_app.viewmodel.LoggedInUserViewModel;
import com.example.mail_app.viewmodel.MailViewModel;

public class DemoActivity extends AppCompatActivity {
    private static final String TAG = "DemoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🧠 יצירת ViewModel-ים
        LoggedInUserViewModel userViewModel = new ViewModelProvider(this).get(LoggedInUserViewModel.class);
        LabelViewModel labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
        MailViewModel mailViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MailViewModel.class);

        // ✅ תצפית על היוזר
        userViewModel.getUser().observe(this, user -> {
            if (user != null) {
                Log.d(TAG, "👤 Logged-in user: " + user.getName());
            } else {
                Log.d(TAG, "👤 No user logged in.");
            }
        });

        // ✅ תצפית על תוויות
        labelViewModel.getLabels().observe(this, labels -> {
            Log.d(TAG, "🏷️ Labels:");
            for (Label label : labels) {
                Log.d(TAG, "- " + label.getName() + " (color: " + label.getColor() + ")");
            }
        });

        // ✅ תצפית על מיילים (Inbox)
        mailViewModel.getMails().observe(this, mails -> {
            Log.d(TAG, "📥 Inbox Mails:");
            for (FullMail fullMail : mails) {
                if (fullMail.getMail() != null) {
                    Log.d(TAG, "✉️ Subject: " + fullMail.getMail().getSubject());
                    if (fullMail.getFromUser() != null) {
                        Log.d(TAG, "   From: " + fullMail.getFromUser().getName());
                    }
                }
            }
        });

        // 🚀 נטען את המיילים הראשוניים (Inbox למשל)
        mailViewModel.loadInboxMails(0, 50);
    }
}
