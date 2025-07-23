package com.example.mail_app.app.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// Main API class responsible for mail operations between server (via Retrofit) and local Room database
public class MailAPI {
    // DAOs for accessing Room tables
    private final MailDao mailDao;
    private final PublicUserDao publicUserDao;

    // LiveData to expose current mail list to the UI
    private final MutableLiveData<List<FullMail>> mailListData;

    // Retrofit interface to perform HTTP calls
    private final MailWebService api;

    private static final int INITIAL_MAIL_LIMIT = 100; // for initial sync
    private static final int CATEGORY_MAIL_LIMIT = 20; // for category-specific pagination

    // Constructor sets up DAOs and Retrofit instance with auth token
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

                    }).start();
                } else {
                    postToMain(() -> mailListData.setValue(Collections.emptyList()));
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

        // Fetch and post local results immediately
        new Thread(() -> {
            List<FullMail> local = roomFetcher.get();
            postToMain(() -> mailListData.setValue(local));
        }).start();

        // Then try to fetch fresh data from API and overwrite Room + UI
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
                } else {
                    Log.w("MailAPI", logTag + " | empty or failed response");
                    // Falls back to local results on failure
                    new Thread(() -> {
                        List<FullMail> fallback = roomFetcher.get();
                        postToMain(() -> mailListData.setValue(fallback));
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<MailListResponse> call, Throwable t) {
                Log.e("MailAPI", logTag + " failed: " + t.getMessage());
                // Falls back to local results on failure
                new Thread(() -> {
                    List<FullMail> fallback = roomFetcher.get();
                    postToMain(() -> mailListData.setValue(fallback));
                }).start();
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
                } else {
                    new Thread(() -> {
                        List<FullMail> fallback = mailDao.getMailsByLabel(labelId);
                        postToMain(() -> mailListData.setValue(fallback));
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<MailListResponse> call, Throwable t) {
                Log.e("MailAPI", "loadMailsByLabelWithoutSaving failed: " + t.getMessage());
                new Thread(() -> {
                    List<FullMail> fallback = mailDao.getMailsByLabel(labelId);
                    postToMain(() -> mailListData.setValue(fallback));
                }).start();
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
                } else {
                    new Thread(() -> {
                        List<FullMail> fallback = mailDao.searchMails(query);
                        postToMain(() -> mailListData.setValue(fallback));
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<MailListResponse> call, Throwable t) {
                Log.e("MailAPI", "searchMailsWithoutSaving failed: " + t.getMessage());
                new Thread(() -> {
                    List<FullMail> fallback = mailDao.searchMails(query);
                    postToMain(() -> mailListData.setValue(fallback));
                }).start();
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
        String mailId = fullMail.getMail().getId();

        mailDao.insertMail(fullMail.getMail());

        if (!fullMail.getMail().isDraft()) {
            mailDao.deleteRecipientsByMailId(mailId);
            mailDao.insertRecipients(fullMail.getRecipientRefs());
        }

        mailDao.deleteLabelsByMailId(mailId);
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
        api.getInboxMails(limit, offset).enqueue(loadMailListCallback(() -> mailDao.getInboxMails()));
    }

    // Loads sent mails from the server with offset and limit
    public void loadSentMails(int offset, int limit) {
        api.getSentMails(limit, offset).enqueue(loadMailListCallback(() -> mailDao.getSentMails()));
    }

    // Loads draft mails from the server with offset and limit
    public void loadDraftMails(int offset, int limit) {
        api.getDraftMails(limit, offset).enqueue(loadMailListCallback(() -> mailDao.getDraftMails()));
    }

    // Loads spam mails from the server with offset and limit
    public void loadSpamMails(int offset, int limit) {
        api.getSpamMails(limit, offset).enqueue(loadMailListCallback(() -> mailDao.getSpamMails()));
    }

    // Loads starred mails from the server with offset and limit
    public void loadStarredMails(int offset, int limit) {
        api.getStarredMails(limit, offset).enqueue(loadMailListCallback(() -> mailDao.getStarredMails()));
    }

    // Loads all mails from the server with offset and limit
    public void loadAllMails(int offset, int limit) {
        api.getAllMails(limit, offset).enqueue(loadMailListCallback(() -> mailDao.getAllMails()));
    }

    // Loads mails by label from the server with offset and limit
    public void loadMailsByLabel(String labelId, int offset, int limit) {
        api.getMailsByLabel(labelId, limit, offset).enqueue(loadMailListCallback(() -> mailDao.getMailsByLabel(labelId)));
    }

    // Searches mails from the server with offset and limit
    public void searchMails(String query, int offset, int limit) {
        api.searchMails(query, limit, offset).enqueue(loadMailListCallback(() -> mailDao.searchMails(query)));
    }

    // Returns a LiveData object for observing a specific mail by ID
    public LiveData<FullMail> getLiveMailById(String mailId) {
        return mailDao.getLiveMailById(mailId);
    }

    // Reusable callback for list-based responses (updates Room + LiveData)
    private Callback<MailListResponse> loadMailListCallback(Supplier<List<FullMail>> roomFetcher) {
        return new Callback<MailListResponse>() {
            @Override
            public void onResponse(Call<MailListResponse> call, Response<MailListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MailFromServer> mails = response.body().getMails();
                    new Thread(() -> {
                        saveMailsFromResponse(mails);
                        List<FullMail> updatedMails = roomFetcher.get();
                        postToMain(() -> mailListData.setValue(updatedMails));
                    }).start();
                } else {
                    new Thread(() -> {
                        List<FullMail> fallback = roomFetcher.get();
                        postToMain(() -> mailListData.setValue(fallback));
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<MailListResponse> call, Throwable t) {
                Log.e("MailAPI", "loadMailListCallback failed: " + t.getMessage());
                new Thread(() -> {
                    List<FullMail> fallback = roomFetcher.get();
                    postToMain(() -> mailListData.setValue(fallback));
                }).start();
            }
        };
    }


    // Sends a request to create a new mail
    public void createMail(Map<String, Object> body, Consumer<String> onError) {
        api.createMail(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseStr = response.body().string();

                        JSONObject json = new JSONObject(responseStr);
                        if (json.has("id")) {
                            String mailId = json.getString("id");
                            fetchAndSaveMailById(mailId);
                            postToMain(() -> onError.accept(null));
                        } else {
                            postToMain(() -> onError.accept("Mail created, but missing ID"));
                        }
                    } catch (Exception e) {
                        postToMain(() -> onError.accept("Failed to parse server response"));
                    }

                } else {
                    try {
                        String errorStr = response.errorBody() != null ? response.errorBody().string() : "";
                        if (!errorStr.isEmpty()) {
                            JSONObject json = new JSONObject(errorStr);
                            if (json.has("error")) {
                                String errorMsg = json.getString("error");
                                postToMain(() -> onError.accept(errorMsg));
                                return;
                            }
                        }

                        postToMain(() -> onError.accept("Failed to create mail – unknown error"));

                    } catch (Exception e) {
                        postToMain(() -> onError.accept("Failed to create mail – error parsing response"));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("MailAPI", "createMail failure: " + t.getMessage());
                postToMain(() -> onError.accept("Failed to create mail – check your internet connection."));
            }
        });
    }

    // Sends a request to send a saved draft mail
    public void sendDraft(String mailId, Map<String, Object> body, Consumer<String> onError) {
        api.sendDraft(mailId, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    fetchAndSaveMailById(mailId);
                    postToMain(() -> onError.accept(null));
                } else {
                    postToMain(() -> onError.accept(extractErrorMessage(response, "Failed to send draft")));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                postToMain(() -> onError.accept("Failed to send draft – check your internet connection."));
            }
        });
    }

    // Sends a request to update an existing draft
    public void updateMail(String mailId, Map<String, Object> body, Consumer<String> onError) {
        api.updateMail(mailId, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    fetchAndSaveMailById(mailId);
                    postToMain(() -> onError.accept(null));
                } else {
                    postToMain(() -> onError.accept(extractErrorMessage(response, "Failed to update draft")));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                postToMain(() -> onError.accept("Failed to update draft – check your internet connection."));
            }
        });
    }



    // Sends a request to delete a mail and removes it from Room and LiveData
    public void deleteMail(String mailId, Consumer<String> onError) {
        api.deleteMail(mailId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    postToMain(() -> onError.accept("Server failed to delete mail"));
                    return;
                }

                new Thread(() -> {
                    mailDao.deleteMailById(mailId);
                    mailDao.deleteRecipientsByMailId(mailId);
                    mailDao.deleteLabelsByMailId(mailId);

                    List<FullMail> current = mailListData.getValue();
                    if (current == null) current = new ArrayList<>();
                    List<FullMail> updated = new ArrayList<>(current);
                    updated.removeIf(m -> m.getMail().getId().equals(mailId));

                    postToMain(() -> {
                        mailListData.setValue(updated);
                        onError.accept(null);
                    });
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "deleteMail failed: " + t.getMessage());
                postToMain(() -> onError.accept("Failed to delete mail – check your internet connection."));
            }
        });
    }

    // Refreshes a single mail inside the current LiveData list
    public void refreshSingleMail(String mailId) {
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
    public void toggleStar(String mailId, Consumer<String> onError) {
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
                postToMain(() -> onError.accept("Failed to toggle star – check your internet connection."));
            }
        });
    }

    // Toggles the spam status of a mail
    public void setSpam(String mailId, Map<String, Boolean> body, Runnable onSuccess, Consumer<String> onError) {
        api.setSpam(mailId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        mailDao.setSpam(mailId);
                        fetchAndSaveMailById(mailId);
                        postToMain(onSuccess);
                    }).start();
                } else {
                    Log.e("MailAPI", "setSpam failed: " + response.code());
                    postToMain(() -> onError.accept("Failed to mark as spam – check your internet connection."));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "setSpam failed", t);
                postToMain(() -> onError.accept("Failed to mark as spam – check your internet connection."));
            }
        });
    }


    // Adds a label to a mail
    public void addLabelToMail(String mailId, Map<String, String> body, Runnable onSuccess, Consumer<String> onError) {
        api.addLabelToMail(mailId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && body.containsKey("labelId")) {
                    String labelId = body.get("labelId");
                    new Thread(() -> {
                        mailDao.insertLabelToMail(new MailLabelCrossRef(mailId, labelId));
                        fetchAndSaveMailById(mailId);
                        postToMain(onSuccess);
                    }).start();
                } else {
                    postToMain(() -> onError.accept("Failed to add label – check your internet connection."));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "addLabelToMail failed: " + t.getMessage());
                postToMain(() -> onError.accept("Failed to add label – check your internet connection."));
            }
        });
    }


    // Removes a label from a mail
    public void removeLabelFromMail(String mailId, Map<String, String> body, Runnable onSuccess, Consumer<String> onError) {
        api.removeLabelFromMail(mailId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && body.containsKey("labelId")) {
                    String labelId = body.get("labelId");
                    new Thread(() -> {
                        mailDao.removeLabelFromMail(mailId, labelId);
                        fetchAndSaveMailById(mailId);
                        postToMain(onSuccess);
                    }).start();
                } else {
                    postToMain(() -> onError.accept("Failed to remove label – check your internet connection."));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "removeLabelFromMail failed: " + t.getMessage());
                postToMain(() -> onError.accept("Failed to remove label – check your internet connection."));
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

    private String extractErrorMessage(Response<?> response, String fallbackMessage) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();

                if (!errorJson.isEmpty()) {
                    JSONObject json = new JSONObject(errorJson);
                    if (json.has("error")) {
                        return json.getString("error");
                    }
                }
            }
        } catch (Exception e) {
            Log.e("MailAPI", "Error parsing error body: " + e.getMessage());
        }
        return fallbackMessage;
    }

}
