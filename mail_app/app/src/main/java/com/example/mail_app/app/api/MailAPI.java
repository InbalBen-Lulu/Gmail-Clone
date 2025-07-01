package com.example.mail_app.app.api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.mail_app.MyApp;
import com.example.mail_app.app.network.AuthWebService;
import com.example.mail_app.auth.AuthManager;
import com.example.mail_app.data.dto.MailFromServer;
import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.MailWithRecipientsAndLabels;
import com.example.mail_app.data.model.MailboxType;
import com.example.mail_app.data.remote.MailWebService;
import com.example.mail_app.repository.MailRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.*;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class MailAPI {
    private final MutableLiveData<List<MailWithRecipientsAndLabels>> mailListData;
    private final MailRepository repository;
    private final MailWebService api;

    public MailAPI(MutableLiveData<List<MailWithRecipientsAndLabels>> mailListData,
                   MailRepository repository) {
        this.mailListData = mailListData;
        this.repository = repository;

        String token = AuthManager.getToken(MyApp.getInstance());

        Retrofit retrofit = AuthWebService.getInstance(token);
        api = retrofit.create(MailWebService.class);
    }

    private void loadFromCall(Call<List<MailFromServer>> call) {
        call.enqueue(new Callback<List<MailFromServer>>() {

            @Override
            public void onResponse(Call<List<MailFromServer>> call, Response<List<MailFromServer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    repository.saveMany(response.body());
                } else {
                    int code = response.code();
                    String errorMessage = "Unknown error";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorMessage = "Failed to read error body";
                    }

                    Log.e("MailAPI", "Response failed [" + code + "]: " + errorMessage);
                    showToast("Error " + code + ": " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<MailFromServer>> call, Throwable t) {
                Log.e("MailAPI", "Network error", t);
                showToast(t.getMessage());
            }

            private void showToast(String message) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() ->
                        Toast.makeText(MyApp.getInstance(), message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    public void loadByType(MailboxType type) {
        loadFromCall(api.getMailsByType(type.getPath()));
    }

    public void search(String query) {
        loadFromCall(api.searchMails(query));
    }

    public void getByLabel(String labelId) {
        loadFromCall(api.getMailsByLabel(labelId));
    }


    public void sendDraft(String mailId, Mail mail, List<String> to) {
        Map<String, Object> body = new HashMap<>();
        body.put("to", to);
        body.put("subject", mail.getSubject());
        body.put("body", mail.getBody());
        api.sendDraft(mailId, body).enqueue(emptyCallback());
    }

    public void updateDraft(String mailId, Mail mail, List<String> to) {
        Map<String, Object> body = new HashMap<>();
        body.put("to", to);
        body.put("subject", mail.getSubject());
        body.put("body", mail.getBody());
        api.updateMail(mailId, body).enqueue(emptyCallback());
    }

    public void toggleStar(String mailId) {
        api.toggleStar(mailId).enqueue(emptyCallback());
    }

    public void setSpam(String mailId, boolean isSpam) {
        Map<String, Boolean> body = new HashMap<>();
        body.put("isSpam", isSpam);
        api.setSpam(mailId, body).enqueue(emptyCallback());
    }

    public void deleteMail(String mailId) {
        api.deleteMail(mailId).enqueue(emptyCallback());
    }

    public void addLabelToMail(String mailId, String labelId) {
        Map<String, String> body = new HashMap<>();
        body.put("labelId", labelId);
        api.addLabelToMail(mailId, body).enqueue(emptyCallback());
    }

    public void removeLabelFromMail(String mailId, String labelId) {
        Map<String, String> body = new HashMap<>();
        body.put("labelId", labelId);
        api.removeLabelFromMail(mailId, body).enqueue(emptyCallback());
    }

    private Callback<Void> emptyCallback() {
        return new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override public void onFailure(Call<Void> call, Throwable t) {}
  };
}
}