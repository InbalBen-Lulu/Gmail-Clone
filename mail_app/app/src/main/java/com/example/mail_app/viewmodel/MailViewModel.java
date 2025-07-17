package com.example.mail_app.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.repository.MailRepository;
import com.example.mail_app.utils.AppConstants;

import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * ViewModel for managing mail operations with support for category/label state and pagination.
 */
public class MailViewModel extends ViewModel {

    private final MailRepository repository;
    private final LiveData<List<FullMail>> mails;

    private String currentCategoryTitle = null;
    private String currentLabelId = null;
    private boolean isLabelMode = false;
    private int currentOffset = 0;

    public MailViewModel() {
        repository = new MailRepository();
        mails = repository.getLiveData();
    }

    public LiveData<List<FullMail>> getMails() {
        return mails;
    }

    /**
     * Loads initial mails from server and updates local storage.
     */
    public void loadInitialMails() {
        repository.loadInitialMails();
    }

    // State Management
    public void setCategory(String title) {
        currentCategoryTitle = title;
        currentLabelId = null;
        isLabelMode = false;
        currentOffset = 0;

        Log.d("MailViewModel", "setCategory called with title: " + title);

        switch (title) {
            case "Inbox":
                Log.d("MailViewModel", "Loading inbox mails");
                loadInboxMails();
                break;
            case "Starred":
                Log.d("MailViewModel", "Loading starred mails");
                loadStarredMails();
                break;
            case "Sent":
                Log.d("MailViewModel", "Loading sent mails");
                loadSentMails();
                break;
            case "Drafts":
                Log.d("MailViewModel", "Loading draft mails");
                loadDraftMails();
                break;
            case "All Mail":
                Log.d("MailViewModel", "Loading all mails");
                loadAllMails();
                break;
            case "Spam":
                Log.d("MailViewModel", "Loading spam mails");
                loadSpamMails();
                break;
            default:
                Log.w("MailViewModel", "Unknown category title: " + title);
                break;
        }
    }


    public void setLabel(String labelId) {
        currentLabelId = labelId;
        currentCategoryTitle = null;
        isLabelMode = true;
        currentOffset = 0;

        loadMailsByLabel(labelId, AppConstants.DEFAULT_PAGE_SIZE, currentOffset);
    }

    public void reloadCurrentCategory() {
        currentOffset = 0;
        if (isLabelMode && currentLabelId != null) {
            loadMailsByLabel(currentLabelId, AppConstants.DEFAULT_PAGE_SIZE, currentOffset);
        } else if (!isLabelMode && currentCategoryTitle != null) {
            setCategory(currentCategoryTitle);  // reload from start
        }
    }

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
            }
        }
    }

    // Mail operations
    public void toggleStar(String mailId) {
        repository.toggleStar(mailId);
    }

    public void deleteMail(String mailId) {
        repository.deleteMail(mailId);
    }

    public void sendDraft(String mailId, Map<String, Object> body) {
        repository.sendDraft(mailId, body);
    }

    public void createMail(Map<String, Object> body) {
        repository.createMail(body);
    }

    public void updateMail(String mailId, Map<String, Object> body) {
        repository.updateMail(mailId, body);
    }

    public void loadMailById(String mailId) {
        repository.loadMailById(mailId);
    }

    public void addLabelToMail(String mailId, Map<String, String> body) {
        repository.addLabelToMail(mailId, body);
    }

    public void removeLabelFromMail(String mailId, Map<String, String> body) {
        repository.removeLabelFromMail(mailId, body);
    }

    public void setSpam(String mailId, Map<String, Boolean> body) {
        repository.setSpam(mailId, body);
    }

    // --- Basic Loads (reset offset)
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

    // --- Scroll loads (load more)
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

    public void searchMails(String query, int limit, int offset) {
        repository.searchMails(query, limit, offset);
    }

    public void scrollSearchMails(String query, int offset, int limit) {
        repository.scrollSearchMails(query, offset, limit);
    }

    // Getters for current state (useful for Fragment/debug/logging)
    public String getCurrentCategoryTitle() {
        return currentCategoryTitle;
    }

    public String getCurrentLabelId() {
        return currentLabelId;
    }

    public boolean isLabelMode() {
        return isLabelMode;
    }

    public int getCurrentOffset() {
        return currentOffset;
    }


}
