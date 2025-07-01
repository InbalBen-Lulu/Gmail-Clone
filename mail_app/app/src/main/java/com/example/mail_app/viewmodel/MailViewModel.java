package com.example.mail_app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.MailWithRecipientsAndLabels;
import com.example.mail_app.repository.MailRepository;

import java.util.List;

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

    public void addMail(Mail mail) {
        repository.add(mail);
    }

    public void deleteMail(Mail mail) {
        repository.delete(mail);
    }

    public void reloadMails(String path) {

    }

    public void sendDraft(String mailId, Mail mail, List<String> to) {
        repository.sendDraft(mailId, mail, to);
    }

    public void updateDraft(String mailId, Mail mail, List<String> to) {
        repository.updateDraft(mailId, mail, to);
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

    public void deleteMailById(String mailId) {
        repository.deleteMailById(mailId);
    }
}