package com.example.mail_app.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.repository.MailRepository;
import com.example.mail_app.utils.AppConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * ViewModel for managing mail operations with support for category/label state and pagination.
 */
public class MailViewModel extends ViewModel {

    // Repository handling data operations (Room + Retrofit)
    private final MailRepository repository;

    // LiveData to observe mail list updates
    private final LiveData<List<FullMail>> mails;

    // Current UI state
    private String currentCategoryTitle = null;
    private String currentLabelId = null;
    private boolean isLabelMode = false;
    private int currentOffset = 0;
    private String lastQuery = "";


    public MailViewModel() {
        repository = new MailRepository();
        mails = repository.getLiveData();
    }

    // Returns LiveData list of current mails
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
     * Updates state to a specific category (Inbox, Sent, etc.) and loads data accordingly.
     */
    public void setCategory(String title) {
        currentCategoryTitle = title;
        currentLabelId = null;
        isLabelMode = false;
        currentOffset = 0;

        Log.d("MailViewModel", "setCategory called with title: " + title);

        switch (title) {
            case "Inbox":
                loadInboxMails();
                break;
            case "Starred":
                loadStarredMails();
                break;
            case "Sent":
                loadSentMails();
                break;
            case "Drafts":
                loadDraftMails();
                break;
            case "All Mail":
                loadAllMails();
                break;
            case "Spam":
                loadSpamMails();
                break;
            case "Search":
                // don't auto-load; rely on searchMails() calls
                break;
            default:
                Log.w("MailViewModel", "Unknown category title: " + title);
                break;
        }
    }

    /**
     * Sets label mode and loads mails by label ID.
     */
    public void setLabel(String labelId) {
        currentLabelId = labelId;
        currentCategoryTitle = null;
        isLabelMode = true;
        currentOffset = 0;

        loadMailsByLabel(labelId, AppConstants.DEFAULT_PAGE_SIZE, currentOffset);
    }

    /**
     * Reloads the current selected category or label from scratch (used after updates).
     */
    public void reloadCurrentCategory() {
        currentOffset = 0;
        if (isLabelMode && currentLabelId != null) {
            loadMailsByLabel(currentLabelId, AppConstants.DEFAULT_PAGE_SIZE, currentOffset);
        } else if (!isLabelMode && currentCategoryTitle != null) {
            setCategory(currentCategoryTitle);  // reload from start
        }
    }

    /**
     * Loads next page of mails for infinite scroll, depending on current state.
     */
    public void loadMoreMails() {
        currentOffset += AppConstants.DEFAULT_PAGE_SIZE;

        if (isLabelMode && currentLabelId != null) {
            scrollLoadMailsByLabel(currentLabelId, currentOffset, AppConstants.DEFAULT_PAGE_SIZE);
        } else if (!isLabelMode && currentCategoryTitle != null) {
            switch (currentCategoryTitle) {
                case "Inbox":
                    scrollLoadInboxMails(currentOffset, AppConstants.DEFAULT_PAGE_SIZE);
                    break;
                case "Starred":
                    scrollLoadStarredMails(currentOffset, AppConstants.DEFAULT_PAGE_SIZE);
                    break;
                case "Sent":
                    scrollLoadSentMails(currentOffset, AppConstants.DEFAULT_PAGE_SIZE);
                    break;
                case "Drafts":
                    scrollLoadDraftMails(currentOffset, AppConstants.DEFAULT_PAGE_SIZE);
                    break;
                case "All Mail":
                    scrollLoadAllMails(currentOffset, AppConstants.DEFAULT_PAGE_SIZE);
                    break;
                case "Spam":
                    scrollLoadSpamMails(currentOffset, AppConstants.DEFAULT_PAGE_SIZE);
                    break;
                case "Search":
                    scrollSearchMails(lastQuery, currentOffset, AppConstants.DEFAULT_SEARCH_SIZE);
                    break;
            }
        }
    }

    // --- Mail actions ---

    /**
     * Toggles star status of a mail.
     */
    public void toggleStar(String mailId, Consumer<String> onError) {
        repository.toggleStar(mailId, onError);
    }

    /**
     * Deletes a mail by ID.
     */
    public void deleteMail(String mailId, Consumer<String> onError) {
        repository.deleteMail(mailId, onError);
    }

    /**
     * Fetches updated data for a single mail from the local DB.
     */
    public void refreshSingleMail(String mailId) {
        repository.refreshSingleMail(mailId);
    }

    /**
     * Sends a draft mail using the given fields.
     */
    public void sendDraft(String mailId, String toRaw, String subject, String body, Consumer<String> onError) {
        List<String> recipients = Arrays.stream(toRaw.split("[,\\s]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        Map<String, Object> request = new HashMap<>();
        request.put("to", recipients);
        request.put("subject", subject);
        request.put("body", body);

        repository.sendDraft(mailId, request, onError);
    }

    /**
     * Creates a new mail or draft depending on isDraft flag.
     */
    public void createMail(String toRaw, String subject, String body, boolean isDraft, Consumer<String> onError) {
        List<String> recipients = Arrays.stream(toRaw.split("[,\\s]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        Map<String, Object> mailData = new HashMap<>();
        mailData.put("to", recipients);
        mailData.put("subject", subject);
        mailData.put("body", body);
        mailData.put("id", UUID.randomUUID().toString());
        mailData.put("isDraft", isDraft);

        repository.createMail(mailData, onError);
    }

    /**
     * Updates an existing draft with new content.
     */
    public void updateMail(String mailId, String toRaw, String subject, String body, Consumer<String> onError) {
        List<String> recipients = Arrays.stream(toRaw.split("[,\\s]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        Map<String, Object> updates = new HashMap<>();
        updates.put("to", recipients);
        updates.put("subject", subject);
        updates.put("body", body);

        repository.updateMail(mailId, updates, onError);
    }

    /**
     * Adds a label to a mail.
     */
    public void addLabelToMail(String mailId, String labelId, Runnable onSuccess, Consumer<String> onError) {
        Map<String, String> body = new HashMap<>();
        body.put("labelId", labelId);
        repository.addLabelToMail(mailId, body, onSuccess, onError);
    }

    /**
     * Removes a label from a mail.
     */
    public void removeLabelFromMail(String mailId, String labelId, Runnable onSuccess, Consumer<String> onError) {
        Map<String, String> body = new HashMap<>();
        body.put("labelId", labelId);
        repository.removeLabelFromMail(mailId, body, onSuccess, onError);
    }

    /**
     * Marks or unmarks a mail as spam.
     */
    public void setSpam(String mailId, boolean isSpam, Runnable onSuccess, Consumer<String> onError) {
        Map<String, Boolean> body = new HashMap<>();
        body.put("isSpam", isSpam);
        repository.setSpam(mailId, body, onSuccess, onError);
    }

    // --- Loaders for full refresh (reset offset)

    public void loadInboxMails() {
        repository.loadInboxMails();
    }

    public void loadAllMails() {
        repository.loadAllMails();
    }

    public void loadSentMails() {
        repository.loadSentMails();
    }

    public void loadDraftMails() {
        repository.loadDraftMails();
    }

    public void loadSpamMails() {
        repository.loadSpamMails();
    }

    public void loadStarredMails() {
        repository.loadStarredMails();
    }

    public void loadMailsByLabel(String labelId, int limit, int offset) {
        repository.loadMailsByLabel(labelId, limit, offset);
    }

    // --- Loaders for pagination (scroll)

    public void scrollLoadInboxMails(int offset, int limit) {
        repository.scrollLoadInboxMails(offset, limit);
    }

    public void scrollLoadSentMails(int offset, int limit) {
        repository.scrollLoadSentMails(offset, limit);
    }

    public void scrollLoadDraftMails(int offset, int limit) {
        repository.scrollLoadDraftMails(offset, limit);
    }

    public void scrollLoadSpamMails(int offset, int limit) {
        repository.scrollLoadSpamMails(offset, limit);
    }

    public void scrollLoadStarredMails(int offset, int limit) {
        repository.scrollLoadStarredMails(offset, limit);
    }

    public void scrollLoadAllMails(int offset, int limit) {
        repository.scrollLoadAllMails(offset, limit);
    }

    public void scrollLoadMailsByLabel(String labelId, int offset, int limit) {
        repository.scrollLoadMailsByLabel(labelId, offset, limit);
    }

    /**
     * Starts a search by query (first page).
     */
    public void searchMails(String query, int limit, int offset) {
        lastQuery = query;
        repository.searchMails(query, limit, offset);
    }

    /**
     * Loads next search page based on offset.
     */
    public void scrollSearchMails(String query, int offset, int limit) {
        repository.scrollSearchMails(query, offset, limit);
    }

    /**
     * Gets observable LiveData for a specific mail by ID.
     */
    public LiveData<FullMail> getLiveMailById(String mailId) {
        return repository.getLiveMailById(mailId);
    }
}
