package com.example.mail_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.repository.MailRepository;
import java.util.List;
import java.util.Map;

/**
 * ViewModel for managing mail-related operations and exposing mail data to the UI.
 */
public class MailViewModel extends ViewModel {

    private final MailRepository repository;
    private final LiveData<List<FullMail>> mails;

    /**
     * Initializes the mail repository and LiveData for mails.
     */
    public MailViewModel() {
        repository = new MailRepository();
        mails = repository.getLiveData();
    }

    /**
     * Returns the LiveData containing the list of mails.
     */
    public LiveData<List<FullMail>> getMails() {
        return mails;
    }

    /**
     * Loads initial mails from server and updates local storage.
     */
    public void loadInitialMails() {
        repository.loadInitialMails();
    }

    /**
     * Loads inbox mails (received and not spam).
     */
    public void loadInboxMails() {
        repository.loadInboxMails();
    }

    /**
     * Loads all non-spam mails.
     */
    public void loadAllMails() {
        repository.loadAllMails();
    }

    /**
     * Loads mails sent by the user.
     */
    public void loadSentMails() {
        repository.loadSentMails();
    }

    /**
     * Loads draft mails.
     */
    public void loadDraftMails() {
        repository.loadDraftMails();
    }

    /**
     * Loads spam mails.
     */
    public void loadSpamMails() {
        repository.loadSpamMails();
    }

    /**
     * Loads starred mails.
     */
    public void loadStarredMails() {
        repository.loadStarredMails();
    }

    /**
     * Loads mails by specific label.
     */
    public void loadMailsByLabel(String labelId, int limit, int offset) {
        repository.loadMailsByLabel(labelId, limit, offset);
    }

    /**
     * Searches mails by query string.
     */
    public void searchMails(String query, int limit, int offset) {
        repository.searchMails(query, limit, offset);
    }

    /**
     * Loads a single mail by its ID.
     */
    public void loadMailById(String mailId) {
        repository.loadMailById(mailId);
    }

    /**
     * Sends a draft mail.
     */
    public void sendDraft(String mailId, Map<String, Object> body) {
        repository.sendDraft(mailId, body);
    }

    /**
     * Creates a new mail.
     */
    public void createMail(Map<String, Object> body) {
        repository.createMail(body);
    }

    /**
     * Updates an existing draft mail.
     */
    public void updateMail(String mailId, Map<String, Object> body) {
        repository.updateMail(mailId, body);
    }

    /**
     * Deletes a mail by its ID.
     */
    public void deleteMail(String mailId) {
        repository.deleteMail(mailId);
    }

    /**
     * Toggles the star status of a mail.
     */
    public void toggleStar(String mailId) {
        repository.toggleStar(mailId);
    }

    /**
     * Toggles the spam status of a mail.
     */
    public void setSpam(String mailId, Map<String, Boolean> body) {
        repository.setSpam(mailId, body);
    }

    /**
     * Adds a label to a specific mail.
     */
    public void addLabelToMail(String mailId, Map<String, String> body) {
        repository.addLabelToMail(mailId, body);
    }

    /**
     * Removes a label from a specific mail.
     */
    public void removeLabelFromMail(String mailId, Map<String, String> body) {
        repository.removeLabelFromMail(mailId, body);
    }

    /**
     * Loads more inbox mails for infinite scroll.
     */
    public void scrollLoadInboxMails(int offset, int limit) {
        repository.scrollLoadInboxMails(offset, limit);
    }

    /**
     * Loads more sent mails for infinite scroll.
     */
    public void scrollLoadSentMails(int offset, int limit) {
        repository.scrollLoadSentMails(offset, limit);
    }

    /**
     * Loads more draft mails for infinite scroll.
     */
    public void scrollLoadDraftMails(int offset, int limit) {
        repository.scrollLoadDraftMails(offset, limit);
    }

    /**
     * Loads more spam mails for infinite scroll.
     */
    public void scrollLoadSpamMails(int offset, int limit) {
        repository.scrollLoadSpamMails(offset, limit);
    }

    /**
     * Loads more starred mails for infinite scroll.
     */
    public void scrollLoadStarredMails(int offset, int limit) {
        repository.scrollLoadStarredMails(offset, limit);
    }

    /**
     * Loads more mails for "All Mails" view (infinite scroll).
     */
    public void scrollLoadAllMails(int offset, int limit) {
        repository.scrollLoadAllMails(offset, limit);
    }

    /**
     * Loads more mails by label for infinite scroll.
     */
    public void scrollLoadMailsByLabel(String labelId, int offset, int limit) {
        repository.scrollLoadMailsByLabel(labelId, offset, limit);
    }

    /**
     * Loads more search results for infinite scroll.
     */
    public void scrollSearchMails(String query, int offset, int limit) {
        repository.scrollSearchMails(query, offset, limit);
    }
}
