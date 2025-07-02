package com.example.mail_app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.MailWithRecipientsAndLabels;
import com.example.mail_app.data.model.MailboxType;
import com.example.mail_app.repository.MailRepository;
import com.example.mail_app.data.dto.MailFromServer;

import java.util.List;

import retrofit2.Callback;

public class MailViewModel extends AndroidViewModel {

    private final MailRepository repository;
    private final LiveData<List<MailWithRecipientsAndLabels>> mails;

    public MailViewModel(@NonNull Application application) {
        super(application);
        repository = new MailRepository(application);
        mails = repository.getAll();
    }

    public LiveData<List<MailWithRecipientsAndLabels>> getMails() {
        return mails;
    }

    public void createMail(Mail mail, List<String> to, boolean isDraft) {
        repository.createMail(mail, to, isDraft);
    }

    public void deleteMailById(String mailId) {
        repository.deleteMailById(mailId);
    }

    public void getMailById(String mailId, Callback<MailFromServer> callback) {
        repository.getMailById(mailId, callback);
    }

    public void reloadMails(MailboxType type) {
        repository.reload(type);
    }

    public void search(String query) {
        repository.search(query);
    }

    public void loadByLabel(String labelId) {
        repository.loadByLabel(labelId);
    }

    public void sendDraft(Mail mail, List<String> to) {
        repository.sendDraft(mail, to);
    }

    public void updateDraft(Mail mail, List<String> to) {
        repository.updateDraft(mail, to);
    }

    public void toggleStar(String mailId) {
        repository.toggleStar(mailId);
    }

    public void setSpam(String mailId, boolean isSpam) {
        repository.setSpam(mailId, isSpam);
    }

    public void addLabelToMail(String mailId, String labelId) {
        repository.addLabelToMail(mailId, labelId);
    }

    public void removeLabelFromMail(String mailId, String labelId) {
        repository.removeLabelFromMail(mailId, labelId);
    }
}