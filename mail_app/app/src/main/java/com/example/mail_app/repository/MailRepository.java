package com.example.mail_app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mail_app.LocalDatabase;
import com.example.mail_app.MyApp;
import com.example.mail_app.app.api.MailAPI;
import com.example.mail_app.data.dao.MailDao;
import com.example.mail_app.data.dao.PublicUserDao;
import com.example.mail_app.data.entity.FullMail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MailRepository {
    private final MailDao dao;
    private final PublicUserDao publicUserDao;
    private final MailListData mailListData;
    private final MailAPI api;

    public MailRepository() {
        LocalDatabase db = MyApp.getInstance().getDatabase();
        this.dao = db.mailDao();
        this.publicUserDao = db.publicUserDao();
        this.mailListData = new MailListData();
        this.api = new MailAPI(mailListData, dao, publicUserDao);
    }

    // --- חשיפה החוצה ---

    public LiveData<List<FullMail>> getLiveData() {
        return mailListData;
    }

    // --- API פעולות ---

    public void loadInitialMails() {
        api.loadInitialMails();
    }

    public void loadInboxMails(int offset, int limit) {
        api.loadInboxMails(offset, limit);
    }

    public void loadSentMails(int offset, int limit) {
        api.loadSentMails(offset, limit);
    }

    public void loadDraftMails(int offset, int limit) {
        api.loadDraftMails(offset, limit);
    }

    public void loadSpamMails(int offset, int limit) {
        api.loadSpamMails(offset, limit);
    }

    public void loadStarredMails(int offset, int limit) {
        api.loadStarredMails(offset, limit);
    }

    public void loadAllMails(int offset, int limit) {
        api.loadAllMails(offset, limit);
    }

    public void loadMailsByLabel(String labelId, int offset, int limit) {
        api.loadMailsByLabel(labelId, offset, limit);
    }

    public void searchMails(String query, int offset, int limit) {
        api.searchMails(query, offset, limit);
    }

    public void createMail(Map<String, Object> body) {
        api.createMail(body);
    }

    public void sendDraft(String mailId, Map<String, Object> body) {
        api.sendDraft(mailId, body);
    }

    public void updateMail(String mailId, Map<String, Object> body) {
        api.updateMail(mailId, body);
    }

    public void deleteMail(String mailId) {
        api.deleteMail(mailId);
    }

    public void toggleStar(String mailId) {
        api.toggleStar(mailId);
    }

    public void setSpam(String mailId, Map<String, Boolean> body) {
        api.setSpam(mailId, body);
    }

    public void addLabelToMail(String mailId, Map<String, String> body) {
        api.addLabelToMail(mailId, body);
    }

    public void removeLabelFromMail(String mailId, Map<String, String> body) {
        api.removeLabelFromMail(mailId, body);
    }

    public void getMailById(String mailId) {
        api.getMailById(mailId);
    }

    // --- מחלקה פנימית לניהול LiveData ---

    public class MailListData extends MutableLiveData<List<FullMail>> {

        public MailListData() {
            super();
            setValue(new ArrayList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();
            new Thread(() -> {
                List<FullMail> mails = MyApp.getInstance().getDatabase().mailDao().getAllMails();
                postValue(mails);
            }).start();
        }
    }
}
