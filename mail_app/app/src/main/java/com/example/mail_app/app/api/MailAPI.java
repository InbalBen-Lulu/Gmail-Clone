package com.example.mail_app.app.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.example.mail_app.MyApp;
import com.example.mail_app.app.network.AuthWebService;
import com.example.mail_app.auth.AuthManager;
import com.example.mail_app.data.dao.MailDao;
import com.example.mail_app.data.dao.PublicUserDao;
import com.example.mail_app.data.dto.MailFromServer;
import com.example.mail_app.data.dto.MailListResponse;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.data.entity.MailLabelCrossRef;
import com.example.mail_app.data.entity.PublicUser;
import com.example.mail_app.data.remote.MailWebService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// API class for managing mail data between server (Retrofit) and local database (Room)
public class MailAPI {
    private final MailDao mailDao; // DAO for accessing mail table in Room
    private final PublicUserDao publicUserDao; // DAO for accessing public users (senders)
    private final MutableLiveData<List<FullMail>> mailListData; // LiveData to notify UI
    private final MailWebService api; // Retrofit API interface to the backend server

    private static final int INITIAL_MAIL_LIMIT = 100; // Number of mails to load initially
    private static final int CATEGORY_MAIL_LIMIT = 20; // Default page size for category queries

    // Constructor – sets up DAOs and Retrofit with auth token
    public MailAPI(MutableLiveData<List<FullMail>> mailListData, MailDao mailDao, PublicUserDao publicUserDao) {
        this.mailDao = mailDao;
        this.publicUserDao = publicUserDao;
        this.mailListData = mailListData;

        String token = AuthManager.getToken(MyApp.getInstance());
        Retrofit retrofit = AuthWebService.getInstance(token);
        this.api = retrofit.create(MailWebService.class);
    }

    // Utility to run code on the UI thread
    private void postToMain(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }

    // Loads the initial mails from the server and replaces Room database
    public void loadInitialMails() {
        api.getAllMails(INITIAL_MAIL_LIMIT, 0).enqueue(new Callback<MailListResponse>() {
            @Override
            public void onResponse(Call<MailListResponse> call, Response<MailListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        List<MailFromServer> serverMails = response.body().getMails();
                        List<FullMail> mails = new ArrayList<>();
                        for (MailFromServer mail : serverMails) {
                            mails.add(mail.toFullMail());
                        }

                        // Clear local database
                        mailDao.clearAllMails();
                        publicUserDao.clearAllUsers();

                        // Save users and mails
                        publicUserDao.insertAll(extractPublicUsers(mails));
                        for (FullMail mail : mails) {
                            mailDao.insertMail(mail.getMail());
                            mailDao.insertRecipients(mail.getRecipientRefs());
                            for (MailLabelCrossRef ref : mail.getLabelRefs()) {
                                mailDao.insertLabelToMail(ref);
                            }
                        }

                        postToMain(() -> mailListData.setValue(mails));
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<MailListResponse> call, Throwable t) {
                Log.e("MailAPI", "loadInitialMails failed: " + t.getMessage());
            }
        });
    }

    // Loads mails from Room first, then refreshes from the server and updates LiveData
    private void loadFromRoomThenServer(
            Supplier<List<FullMail>> roomFetcher,
            Call<MailListResponse> apiCall,
            String logTag) {

        // Show local mails immediately
        new Thread(() -> {
            List<FullMail> local = roomFetcher.get();
            postToMain(() -> mailListData.setValue(local));
        }).start();

        // Then request updated mails from the server
        apiCall.enqueue(new Callback<MailListResponse>() {
            @Override
            public void onResponse(Call<MailListResponse> call, Response<MailListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        List<MailFromServer> serverMails = response.body().getMails();
                        List<FullMail> mails = new ArrayList<>();
                        for (MailFromServer mail : serverMails) {
                            mails.add(mail.toFullMail());
                        }

                        // Update local database
                        for (FullMail mail : mails) {
                            mailDao.insertMail(mail.getMail());
                            mailDao.insertRecipients(mail.getRecipientRefs());
                            for (MailLabelCrossRef ref : mail.getLabelRefs()) {
                                mailDao.insertLabelToMail(ref);
                            }
                            publicUserDao.insert(mail.getFromUser());
                        }

                        postToMain(() -> mailListData.setValue(mails));
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<MailListResponse> call, Throwable t) {
                Log.e("MailAPI", logTag + " failed: " + t.getMessage());
            }
        });
    }

    // Loads inbox mails from Room then from server
    public void loadInboxMailsFromRoomThenServer() {
        loadFromRoomThenServer(
                () -> mailDao.getInboxMails(),
                api.getInboxMails(CATEGORY_MAIL_LIMIT, 0),
                "Inbox"
        );
    }

    // Loads all mails from Room then from server
    public void loadAllMailsFromRoomThenServer() {
        loadFromRoomThenServer(
                () -> mailDao.getAllMails(),
                api.getAllMails(CATEGORY_MAIL_LIMIT, 0),
                "AllMails"
        );
    }

    // Loads sent mails from Room then from server
    public void loadSentMailsFromRoomThenServer() {
        loadFromRoomThenServer(
                () -> mailDao.getSentMails(),
                api.getSentMails(CATEGORY_MAIL_LIMIT, 0),
                "Sent"
        );
    }

    // Loads draft mails from Room then from server
    public void loadDraftMailsFromRoomThenServer() {
        loadFromRoomThenServer(
                () -> mailDao.getDraftMails(),
                api.getDraftMails(CATEGORY_MAIL_LIMIT, 0),
                "Drafts"
        );
    }

    // Loads spam mails from Room then from server
    public void loadSpamMailsFromRoomThenServer() {
        loadFromRoomThenServer(
                () -> mailDao.getSpamMails(),
                api.getSpamMails(CATEGORY_MAIL_LIMIT, 0),
                "Spam"
        );
    }

    // Loads starred mails from Room then from server
    public void loadStarredMailsFromRoomThenServer() {
        loadFromRoomThenServer(
                () -> mailDao.getStarredMails(),
                api.getStarredMails(CATEGORY_MAIL_LIMIT, 0),
                "Starred"
        );
    }

    // Loads mails by label ID from Room first, then fetches from server without saving permanently
    public void loadMailsByLabelWithoutSaving(String labelId, int limit, int offset) {
        new Thread(() -> {
            List<FullMail> localResults = mailDao.getMailsByLabel(labelId);
            postToMain(() -> mailListData.setValue(localResults));
        }).start();

        api.getMailsByLabel(labelId, limit, offset).enqueue(new Callback<MailListResponse>() {
            @Override
            public void onResponse(Call<MailListResponse> call, Response<MailListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MailFromServer> mails = response.body().getMails();
                    new Thread(() -> {
                        saveMailsFromResponse(mails);
                        List<FullMail> updatedResults = mailDao.getMailsByLabel(labelId);
                        postToMain(() -> mailListData.setValue(updatedResults));
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<MailListResponse> call, Throwable t) {
                Log.e("MailAPI", "loadMailsByLabelWithoutSaving failed: " + t.getMessage());
            }
        });
    }

    // Searches mails in Room first, then fetches from server without saving permanently
    public void searchMailsWithoutSaving(String query, int limit, int offset) {
        new Thread(() -> {
            List<FullMail> localResults = mailDao.searchMails(query);
            postToMain(() -> mailListData.setValue(localResults));
        }).start();

        api.searchMails(query, limit, offset).enqueue(new Callback<MailListResponse>() {
            @Override
            public void onResponse(Call<MailListResponse> call, Response<MailListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MailFromServer> mails = response.body().getMails();
                    new Thread(() -> {
                        saveMailsFromResponse(mails);
                        List<FullMail> updatedResults = mailDao.searchMails(query);
                        postToMain(() -> mailListData.setValue(updatedResults));
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<MailListResponse> call, Throwable t) {
                Log.e("MailAPI", "searchMailsWithoutSaving failed: " + t.getMessage());
            }
        });
    }

    // Extracts a unique list of senders from the given mails
    private List<PublicUser> extractPublicUsers(List<FullMail> mails) {
        Set<PublicUser> users = new HashSet<>();
        for (FullMail mail : mails) {
            users.add(mail.getFromUser());
        }
        return new ArrayList<>(users);
    }

    // Saves a single mail (converted from server format) to Room
    private void saveMailFromServer(MailFromServer mail) {
        FullMail fullMail = mail.toFullMail();

        mailDao.insertMail(fullMail.getMail());

        if (!fullMail.getMail().isDraft()) {
            mailDao.insertRecipients(fullMail.getRecipientRefs());
        }

        for (MailLabelCrossRef ref : fullMail.getLabelRefs()) {
            mailDao.insertLabelToMail(ref);
        }

        publicUserDao.insert(fullMail.getFromUser());
    }

    // Saves a list of mails (converted from server format) to Room
    private void saveMailsFromResponse(List<MailFromServer> mails) {
        for (MailFromServer mail : mails) {
            saveMailFromServer(mail);
        }
    }

    // Loads inbox mails from the server with offset and limit
    public void loadInboxMails(int offset, int limit) {
        api.getInboxMails(limit, offset).enqueue(loadMailListCallback());
    }

    // Loads sent mails from the server with offset and limit
    public void loadSentMails(int offset, int limit) {
        api.getSentMails(limit, offset).enqueue(loadMailListCallback());
    }

    // Loads draft mails from the server with offset and limit
    public void loadDraftMails(int offset, int limit) {
        api.getDraftMails(limit, offset).enqueue(loadMailListCallback());
    }

    // Loads spam mails from the server with offset and limit
    public void loadSpamMails(int offset, int limit) {
        api.getSpamMails(limit, offset).enqueue(loadMailListCallback());
    }

    // Loads starred mails from the server with offset and limit
    public void loadStarredMails(int offset, int limit) {
        api.getStarredMails(limit, offset).enqueue(loadMailListCallback());
    }

    // Loads all mails from the server with offset and limit
    public void loadAllMails(int offset, int limit) {
        api.getAllMails(limit, offset).enqueue(loadMailListCallback());
    }

    // Loads mails by label from the server with offset and limit
    public void loadMailsByLabel(String labelId, int offset, int limit) {
        api.getMailsByLabel(labelId, limit, offset).enqueue(loadMailListCallback());
    }

    // Searches mails from the server with offset and limit
    public void searchMails(String query, int offset, int limit) {
        api.searchMails(query, limit, offset).enqueue(loadMailListCallback());
    }

    // General callback to handle mail list responses and save to Room
    private Callback<MailListResponse> loadMailListCallback() {
        return new Callback<MailListResponse>() {
            @Override
            public void onResponse(Call<MailListResponse> call, Response<MailListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MailFromServer> mails = response.body().getMails();
                    new Thread(() -> {
                        saveMailsFromResponse(mails);
                        List<FullMail> updatedMails = mailDao.getAllMails(); // can be changed for category
                        postToMain(() -> mailListData.setValue(updatedMails));
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<MailListResponse> call, Throwable t) {
                Log.e("MailAPI", "load mails failed: " + t.getMessage());
            }
        };
    }

    // Sends a request to create a new mail
    public void createMail(Map<String, Object> body) {
        api.createMail(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && body.containsKey("id")) {
                    String mailId = (String) body.get("id");
                    fetchAndSaveMailById(mailId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "createMail failed: " + t.getMessage());
            }
        });
    }

    // Sends a request to send a saved draft mail
    public void sendDraft(String mailId, Map<String, Object> body) {
        api.sendDraft(mailId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchAndSaveMailById(mailId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "sendDraft failed: " + t.getMessage());
            }
        });
    }

    // Sends a request to update an existing draft
    public void updateMail(String mailId, Map<String, Object> body) {
        api.updateMail(mailId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchAndSaveMailById(mailId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "updateMail failed: " + t.getMessage());
            }
        });
    }

    // Sends a request to delete a mail and removes it from Room and LiveData
    public void deleteMail(String mailId) {
        api.deleteMail(mailId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    mailDao.deleteMailById(mailId);
                    mailDao.deleteRecipientsByMailId(mailId);
                    mailDao.deleteLabelsByMailId(mailId);

                    List<FullMail> current = mailListData.getValue();
                    if (current == null) return;
                    List<FullMail> updated = new ArrayList<>(current);
                    updated.removeIf(m -> m.getMail().getId().equals(mailId));
                    postToMain(() -> mailListData.setValue(updated));
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "deleteMail failed: " + t.getMessage());
            }
        });
    }

    // Refreshes a single mail inside the current LiveData list
    private void refreshSingleMail(String mailId) {
        new Thread(() -> {
            FullMail updated = mailDao.getMailById(mailId);
            if (updated == null) return;

            List<FullMail> currentList = mailListData.getValue();
            if (currentList == null) return;

            List<FullMail> updatedList = new ArrayList<>(currentList);
            for (int i = 0; i < updatedList.size(); i++) {
                if (updatedList.get(i).getMail().getId().equals(mailId)) {
                    updatedList.set(i, updated);
                    break;
                }
            }

            postToMain(() -> mailListData.setValue(updatedList));
        }).start();
    }

    // Toggles the star status of a mail
    public void toggleStar(String mailId) {
        api.toggleStar(mailId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    mailDao.toggleStar(mailId);
                    refreshSingleMail(mailId);
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "toggleStar failed: " + t.getMessage());
            }
        });
    }

    // Toggles the spam status of a mail
    public void setSpam(String mailId, Map<String, Boolean> body) {
        api.setSpam(mailId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    mailDao.setSpam(mailId);
                    refreshSingleMail(mailId);
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "setSpam failed: " + t.getMessage());
            }
        });
    }

    // Adds a label to a mail
    public void addLabelToMail(String mailId, Map<String, String> body) {
        api.addLabelToMail(mailId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (body.containsKey("id")) {
                    String labelId = body.get("id");
                    new Thread(() -> {
                        mailDao.insertLabelToMail(new MailLabelCrossRef(mailId, labelId));
                        refreshSingleMail(mailId);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "addLabelToMail failed: " + t.getMessage());
            }
        });
    }

    // Removes a label from a mail
    public void removeLabelFromMail(String mailId, Map<String, String> body) {
        api.removeLabelFromMail(mailId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (body.containsKey("id")) {
                    String labelId = body.get("id");
                    new Thread(() -> {
                        mailDao.removeLabelFromMail(mailId, labelId);
                        refreshSingleMail(mailId);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "removeLabelFromMail failed: " + t.getMessage());
            }
        });
    }

    // Fetches a mail by ID from the server and saves it to Room
    public void fetchAndSaveMailById(String mailId) {
        api.getMailById(mailId).enqueue(new Callback<MailFromServer>() {
            @Override
            public void onResponse(Call<MailFromServer> call, Response<MailFromServer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        saveMailFromServer(response.body());
                        refreshSingleMail(mailId);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<MailFromServer> call, Throwable t) {
                Log.e("MailAPI", "getMailById failed: " + t.getMessage());
            }
        });
    }

    // Loads mail by ID: shows from Room first, then fetches from server and marks as read
    public void loadMailById(String mailId) {
        new Thread(() -> {
            FullMail localMail = mailDao.getMailById(mailId);
            if (localMail != null) {
                mailDao.markAsRead(mailId);
                postToMain(() -> mailListData.setValue(Collections.singletonList(localMail)));
            }
        }).start();

        api.getMailById(mailId).enqueue(new Callback<MailFromServer>() {
            @Override
            public void onResponse(Call<MailFromServer> call, Response<MailFromServer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        saveMailFromServer(response.body());
                        refreshSingleMail(mailId);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<MailFromServer> call, Throwable t) {
                Log.e("MailAPI", "loadMailById failed: " + t.getMessage());
            }
        });
    }
}
