package com.example.mail_app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mail_app.LocalDatabase;
import com.example.mail_app.MyApp;
import com.example.mail_app.app.api.MailAPI;
import com.example.mail_app.data.dao.MailDao;
import com.example.mail_app.data.dao.PublicUserDao;
import com.example.mail_app.data.entity.FullMail;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Repository for managing mail operations via MailAPI and local Room database.
 * Provides LiveData of mails and wraps all mail-related actions.
 */
public class MailRepository {
    private final MailDao dao;
    private final PublicUserDao publicUserDao;
    private final MailListData mailListData;
    private final MailAPI api;

    /**
     * Custom LiveData wrapper for observing and auto-refreshing mail list.
     */
    class MailListData extends MutableLiveData<List<FullMail>> {
        public MailListData() {
            super();
            setValue(new LinkedList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();
//            new Thread(() -> postValue(dao.getAllMails())).start();
            new Thread(() -> postValue(dao.getInboxMails())).start();
        }
    }

    /**
     * Initializes the mail and user DAOs, LiveData wrapper, and MailAPI handler.
     */
    public MailRepository() {
        LocalDatabase db = MyApp.getInstance().getDatabase();
        this.dao = db.mailDao();
        this.publicUserDao = db.publicUserDao();
        this.mailListData = new MailListData();
        this.api = new MailAPI(mailListData, dao, publicUserDao);
    }

    /**
     * Returns observable LiveData of the current list of mails.
     */
    public LiveData<List<FullMail>> getLiveData() {
        return mailListData;
    }

    /** Loads initial batch of mails from the server (first 100), resets Room. */
    public void loadInitialMails() {
        api.loadInitialMails();
    }

    /** Loads inbox mails (from Room first, then server). */
    public void loadInboxMails() {
        api.loadInboxMailsFromRoomThenServer();
    }

    /** Loads all mails (from Room first, then server). */
    public void loadAllMails() {
        api.loadAllMailsFromRoomThenServer();
    }

    /** Loads sent mails (from Room first, then server). */
    public void loadSentMails() {
        api.loadSentMailsFromRoomThenServer();
    }

    /** Loads draft mails (from Room first, then server). */
    public void loadDraftMails() {
        api.loadDraftMailsFromRoomThenServer();
    }

    /** Loads spam mails (from Room first, then server). */
    public void loadSpamMails() {
        api.loadSpamMailsFromRoomThenServer();
    }

    /** Loads starred mails (from Room first, then server). */
    public void loadStarredMails() {
        api.loadStarredMailsFromRoomThenServer();
    }

    /** Loads mails with a specific label (from Room then updates from server). */
    public void loadMailsByLabel(String labelId, int limit, int offset) {
        api.loadMailsByLabelWithoutSaving(labelId, limit, offset);
    }

    /** Searches mails by query (Room first, then server). */
    public void searchMails(String query, int limit, int offset) {
        api.searchMailsWithoutSaving(query, limit, offset);
    }

    /** Loads a specific mail by ID from Room first, then server. */
    public void loadMailById(String mailId) {
        api.loadMailById(mailId);
    }

    /** Sends a draft mail (PATCH + fetch updated mail). */
    public void sendDraft(String mailId, Map<String, Object> body, Consumer<String> onError) {
        api.sendDraft(mailId, body, onError);
    }

    /** Creates a new mail. */
    public void createMail(Map<String, Object> body, Consumer<String> onError) {
        api.createMail(body, onError);
    }

    /** Updates a specific mail by ID. */
    public void updateMail(String mailId, Map<String, Object> body, Consumer<String> onError) {
        api.updateMail(mailId, body, onError);
    }

    /** Deletes a specific mail by ID. */
    public void deleteMail(String mailId, Consumer<String> onError) {
        api.deleteMail(mailId, onError);
    }

    public void refreshSingleMail(String mailId) {
        api.refreshSingleMail(mailId);
    }

    /** Toggles the star status of a mail. */
    public void toggleStar(String mailId, Consumer<String> onError) {
        api.toggleStar(mailId, onError);
    }

    /** Marks a mail as spam or not. */
    public void setSpam(String mailId, Map<String, Boolean> body, Runnable onSuccess,
                        Consumer<String> onError) {
        api.setSpam(mailId, body, onSuccess, onError);
    }

    /** Adds a label to a mail. */
    public void addLabelToMail(String mailId, Map<String, String> body, Runnable onSuccess,
                               Consumer<String> onError) {
        api.addLabelToMail(mailId, body, onSuccess, onError);
    }

    /** Removes a label from a mail. */
    public void removeLabelFromMail(String mailId, Map<String, String> body, Runnable onSuccess,
                                    Consumer<String> onError) {
        api.removeLabelFromMail(mailId, body, onSuccess, onError);
    }

    /** Scroll-loads inbox mails from server (no Room update). */
    public void scrollLoadInboxMails(int offset, int limit) {
        api.loadInboxMails(offset, limit);
    }

    /** Scroll-loads sent mails from server (no Room update). */
    public void scrollLoadSentMails(int offset, int limit) {
        api.loadSentMails(offset, limit);
    }

    /** Scroll-loads draft mails from server (no Room update). */
    public void scrollLoadDraftMails(int offset, int limit) {
        api.loadDraftMails(offset, limit);
    }

    /** Scroll-loads spam mails from server (no Room update). */
    public void scrollLoadSpamMails(int offset, int limit) {
        api.loadSpamMails(offset, limit);
    }

    /** Scroll-loads starred mails from server (no Room update). */
    public void scrollLoadStarredMails(int offset, int limit) {
        api.loadStarredMails(offset, limit);
    }

    /** Scroll-loads all mails from server (no Room update). */
    public void scrollLoadAllMails(int offset, int limit) {
        api.loadAllMails(offset, limit);
    }

    /** Scroll-loads mails by label from server (no Room update). */
    public void scrollLoadMailsByLabel(String labelId, int offset, int limit) {
        api.loadMailsByLabel(labelId, offset, limit);
    }

    /** Scroll-loads search result mails from server (no Room update). */
    public void scrollSearchMails(String query, int offset, int limit) {
        api.searchMails(query, offset, limit);
    }

    public LiveData<FullMail> getLiveMailById(String mailId) {
        return  api.getLiveMailById(mailId);
    }
}
