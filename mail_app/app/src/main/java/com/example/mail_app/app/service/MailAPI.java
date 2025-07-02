package com.example.mail_app.app.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.mail_app.MyApp;
import com.example.mail_app.app.network.AuthWebService;
import com.example.mail_app.auth.AuthManager;
import com.example.mail_app.data.dto.MailFromServer;
import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.MailWithRecipientsAndLabels;
import com.example.mail_app.data.remote.MailWebService;
import com.example.mail_app.repository.MailRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MailAPI {

    private final MutableLiveData<List<MailWithRecipientsAndLabels>> mailListData;
    private final MailRepository repository;
    private final MailWebService api;

    public MailAPI(MutableLiveData<List<MailWithRecipientsAndLabels>> mailListData, MailRepository repository) {
        this.mailListData = mailListData;
        this.repository = repository;

        String token = AuthManager.getToken(MyApp.getInstance());
        Retrofit retrofit = AuthWebService.getInstance(token);
        api = retrofit.create(MailWebService.class);
    }

    // ===================== Load Methods =====================
    public void loadInboxMails() {
        loadFromCall(api.getInboxMails());
    }

    public void loadSentMails() {
        loadFromCall(api.getSentMails());
    }

    public void loadDraftMails() {
        loadFromCall(api.getDraftMails());
    }

    public void loadSpamMails() {
        loadFromCall(api.getSpamMails());
    }

    public void loadStarredMails() {
        loadFromCall(api.getStarredMails());
    }

    public void loadAllMails() {
        loadFromCall(api.getAllMails());
    }

    public void loadByLabel(String labelId) {
        loadFromCall(api.getMailsByLabel(labelId));
    }

    public void searchMails(String query) {
        loadFromCall(api.searchMails(query));
    }

    private void loadFromCall(Call<List<MailFromServer>> call) {
        call.enqueue(new Callback<List<MailFromServer>>() {
            @Override
            public void onResponse(Call<List<MailFromServer>> call, Response<List<MailFromServer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    repository.saveMany(response.body());
                } else {
                    handleError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<List<MailFromServer>> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
                Log.e("MailAPI", "Network error", t);
            }
        });
    }

    // ===================== CRUD Methods =====================
    public void createMail(Mail mail, List<String> to, boolean isDraft) {
        Map<String, Object> body = new HashMap<>();
        body.put("to", to);
        body.put("subject", mail.getSubject());
        body.put("body", mail.getBody());
        body.put("isDraft", isDraft);

        api.createMail(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (isDraft) loadDraftMails();
                    else loadSentMails();
                } else {
                    handleError(response.code(), "Create mail failed");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast("Failed to create mail");
                Log.e("MailAPI", "createMail error", t);
            }
        });
    }

    public void updateDraft(String mailId, Mail mail, List<String> to) {
        Map<String, Object> body = new HashMap<>();
        body.put("to", to);
        body.put("subject", mail.getSubject());
        body.put("body", mail.getBody());

        api.updateMail(mailId, body).enqueue(emptyCallback(() -> loadDraftMails()));
    }

    public void sendDraft(String mailId, Mail mail, List<String> to) {
        Map<String, Object> body = new HashMap<>();
        body.put("to", to);
        body.put("subject", mail.getSubject());
        body.put("body", mail.getBody());

        api.sendDraft(mailId, body).enqueue(emptyCallback(() -> {
            loadSentMails();
            loadDraftMails();
        }));
    }

    public void deleteMail(String mailId) {
        api.deleteMail(mailId).enqueue(emptyCallback(this::loadAllMails));
    }

    // ===================== Flags =====================
    public void toggleStar(String mailId) {
        api.toggleStar(mailId).enqueue(emptyCallback(this::loadStarredMails));
    }

    public void setSpam(String mailId, boolean isSpam) {
        Map<String, Boolean> body = new HashMap<>();
        body.put("isSpam", isSpam);
        api.setSpam(mailId, body).enqueue(emptyCallback(() -> {
            loadSpamMails();
            loadInboxMails();
        }));
    }

    // ===================== Labels =====================
    public void addLabelToMail(String mailId, String labelId) {
        Map<String, String> body = new HashMap<>();
        body.put("labelId", labelId);
        api.addLabelToMail(mailId, body).enqueue(emptyCallback(() -> loadByLabel(labelId)));
    }

    public void removeLabelFromMail(String mailId, String labelId) {
        Map<String, String> body = new HashMap<>();
        body.put("labelId", labelId);
        api.removeLabelFromMail(mailId, body).enqueue(emptyCallback(() -> loadByLabel(labelId)));
    }

    // ===================== Helpers =====================
    private Callback<Void> emptyCallback(Runnable onSuccess) {
        return new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && onSuccess != null) {
                    onSuccess.run();
                } else {
                    handleError(response.code(), "Action failed");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
            }
        };
    }

    private void handleError(int code, String message) {
        Log.e("MailAPI", message + " [" + code + "]");
        showToast("Error " + code + ": " + message);
    }

    private void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(MyApp.getInstance(), message, Toast.LENGTH_SHORT).show()
        );
    }
}
