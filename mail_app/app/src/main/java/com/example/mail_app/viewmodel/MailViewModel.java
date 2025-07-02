package com.example.mail_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.repository.MailRepository;

import java.util.List;
import java.util.Map;

public class MailViewModel extends ViewModel {

    private final MailRepository repository;
    private final LiveData<List<FullMail>> mails;

    public MailViewModel() {
        repository = new MailRepository();
        mails = repository.getLiveData();
    }

    public LiveData<List<FullMail>> getMails() {
        return mails;
    }

    public void loadInitialMails() {
        repository.loadInitialMails();
    }

    public void loadInboxMails(int offset, int limit) {
        repository.loadInboxMails(offset, limit);
    }

    public void loadSentMails(int offset, int limit) {
        repository.loadSentMails(offset, limit);
    }

    public void loadDraftMails(int offset, int limit) {
        repository.loadDraftMails(offset, limit);
    }

    public void loadSpamMails(int offset, int limit) {
        repository.loadSpamMails(offset, limit);
    }

    public void loadStarredMails(int offset, int limit) {
        repository.loadStarredMails(offset, limit);
    }

    public void loadAllMails(int offset, int limit) {
        repository.loadAllMails(offset, limit);
    }

    public void loadMailsByLabel(String labelId, int offset, int limit) {
        repository.loadMailsByLabel(labelId, offset, limit);
    }

    public void searchMails(String query, int offset, int limit) {
        repository.searchMails(query, offset, limit);
    }

    public void deleteMail(String mailId) {
        repository.deleteMail(mailId);
    }

    public void toggleStar(String mailId) {
        repository.toggleStar(mailId);
    }

    public void setSpam(String mailId, Map<String, Boolean> body) {
        repository.setSpam(mailId, body);
    }

    public void addLabelToMail(String mailId, Map<String, String> body) {
        repository.addLabelToMail(mailId, body);
    }

    public void removeLabelFromMail(String mailId, Map<String, String> body) {
        repository.removeLabelFromMail(mailId, body);
    }

    public void getMailById(String mailId) {
        repository.getMailById(mailId);
    }

    public void createMail(Map<String, Object> body) {
        repository.createMail(body);
    }

    public void sendDraft(String mailId, Map<String, Object> body) {
        repository.sendDraft(mailId, body);
    }

    public void updateMail(String mailId, Map<String, Object> body) {
        repository.updateMail(mailId, body);
    }
}
