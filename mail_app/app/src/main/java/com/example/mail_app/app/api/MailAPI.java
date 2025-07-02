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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * MailAPI handles all operations related to mail synchronization between the
 * local Room database and the remote mail server using Retrofit.
 */
public class MailAPI {
    private final MailDao mailDao;
    private final PublicUserDao publicUserDao;
    private final MutableLiveData<List<FullMail>> mailListData;
    private final MailWebService api;

    /**
     * Constructor initializes DAOs, LiveData, and Retrofit API instance.
     */
    public MailAPI(MutableLiveData<List<FullMail>> mailListData, MailDao mailDao, PublicUserDao publicUserDao) {
        this.mailDao = mailDao;
        this.publicUserDao = publicUserDao;
        this.mailListData = mailListData;

        String token = AuthManager.getToken(MyApp.getInstance());
        Retrofit retrofit = AuthWebService.getInstance(token);
        this.api = retrofit.create(MailWebService.class);
    }

    /**
     * Posts a Runnable on the main thread (used to update LiveData).
     */
    private void postToMain(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }

    /**
     * Loads the initial 100 mails from the server.
     * This method should be called once when the app starts (not during infinite scroll).
     * Clears existing mails and users from Room, and saves the newly fetched ones.
     */
    public void loadInitialMails() {
        api.getAllMails(100, 0).enqueue(new Callback<MailListResponse>() {
            @Override
            public void onResponse(Call<MailListResponse> call, Response<MailListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        List<MailFromServer> serverMails = response.body().getMails();
                        List<FullMail> mails = new ArrayList<>();
                        for (MailFromServer serverMail : serverMails) {
                            mails.add(serverMail.toFullMail());
                        }

                        mailDao.clearAllMails();
                        publicUserDao.clearAllUesrs();
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

    /**
     * Extracts all sender users from a list of FullMail objects to sync PublicUser table.
     */
    private List<PublicUser> extractPublicUsers(List<FullMail> mails) {
        Set<PublicUser> users = new HashSet<>();
        for (FullMail mail : mails) {
            users.add(mail.getFromUser());
        }
        return new ArrayList<>(users);
    }

    /**
     * Saves a single MailFromServer object into the Room database.
     */
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

    /**
     * Saves a list of MailFromServer objects into Room.
     */
    private void saveMailsFromResponse(List<MailFromServer> mails) {
        for (MailFromServer mail : mails) {
            saveMailFromServer(mail);
        }
    }

    // ---------------- Mail Loading by Type ---------------- //

    public void loadInboxMails(int offset, int limit) {
        api.getInboxMails(limit, offset).enqueue(loadMailListCallback());
    }

    public void loadSentMails(int offset, int limit) {
        api.getSentMails(limit, offset).enqueue(loadMailListCallback());
    }

    public void loadDraftMails(int offset, int limit) {
        api.getDraftMails(limit, offset).enqueue(loadMailListCallback());
    }

    public void loadSpamMails(int offset, int limit) {
        api.getSpamMails(limit, offset).enqueue(loadMailListCallback());
    }

    public void loadStarredMails(int offset, int limit) {
        api.getStarredMails(limit, offset).enqueue(loadMailListCallback());
    }

    public void loadAllMails(int offset, int limit) {
        api.getAllMails(limit, offset).enqueue(loadMailListCallback());
    }

    public void loadMailsByLabel(String labelId, int offset, int limit) {
        api.getMailsByLabel(labelId, limit, offset).enqueue(loadMailListCallback());
    }

    public void searchMails(String query, int offset, int limit) {
        api.searchMails(query, limit, offset).enqueue(loadMailListCallback());
    }

    /**
     * Shared callback for all mail-loading methods.
     * Saves fetched mails into Room.
     */
    private Callback<MailListResponse> loadMailListCallback() {
        return new Callback<MailListResponse>() {
            @Override
            public void onResponse(Call<MailListResponse> call, Response<MailListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MailFromServer> mails = response.body().getMails();
                    new Thread(() -> saveMailsFromResponse(mails)).start();
                }
            }

            @Override
            public void onFailure(Call<MailListResponse> call, Throwable t) {
                Log.e("MailAPI", "load mails failed: " + t.getMessage());
            }
        };
    }

    // ---------------- Mail Creation / Update / Delete ---------------- //

    public void createMail(Map<String, Object> body) {
        api.createMail(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && body.containsKey("id")) {
                    String mailId = (String) body.get("id");
                    getMailById(mailId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "createMail failed: " + t.getMessage());
            }
        });
    }

    public void sendDraft(String mailId, Map<String, Object> body) {
        api.sendDraft(mailId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    getMailById(mailId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "sendDraft failed: " + t.getMessage());
            }
        });
    }

    public void updateMail(String mailId, Map<String, Object> body) {
        api.updateMail(mailId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    getMailById(mailId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "updateMail failed: " + t.getMessage());
            }
        });
    }

    public void deleteMail(String mailId) {
        api.deleteMail(mailId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> mailDao.deleteMailById(mailId)).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "deleteMail failed: " + t.getMessage());
            }
        });
    }

    // ---------------- Mail Actions: Star / Spam / Label ---------------- //

    public void toggleStar(String mailId) {
        api.toggleStar(mailId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> mailDao.toggleStar(mailId)).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "toggleStar failed: " + t.getMessage());
            }
        });
    }

    public void setSpam(String mailId, Map<String, Boolean> body) {
        api.setSpam(mailId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> mailDao.setSpam(mailId)).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "setSpam failed: " + t.getMessage());
            }
        });
    }

    public void addLabelToMail(String mailId, Map<String, String> body) {
        api.addLabelToMail(mailId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (body.containsKey("id")) {
                    String labelId = body.get("id");
                    new Thread(() -> mailDao.insertLabelToMail(new MailLabelCrossRef(mailId, labelId))).start();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "addLabelToMail failed: " + t.getMessage());
            }
        });
    }

    public void removeLabelFromMail(String mailId, Map<String, String> body) {
        api.removeLabelFromMail(mailId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (body.containsKey("id")) {
                    String labelId = body.get("id");
                    new Thread(() -> mailDao.removeLabelFromMail(mailId, labelId)).start();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", "removeLabelFromMail failed: " + t.getMessage());
            }
        });
    }

    /**
     * Retrieves a specific mail by ID from the server and stores it in Room.
     */
    public void getMailById(String mailId) {
        api.getMailById(mailId).enqueue(new Callback<MailFromServer>() {
            @Override
            public void onResponse(Call<MailFromServer> call, Response<MailFromServer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> saveMailFromServer(response.body())).start();
                }
            }

            @Override
            public void onFailure(Call<MailFromServer> call, Throwable t) {
                Log.e("MailAPI", "getMailById failed: " + t.getMessage());
            }
        });
    }

    /**
     * Utility method that returns a simple logging-only callback.
     */
    private Callback<Void> logOnlyCallback(String tag) {
        return new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("MailAPI", tag + " success");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MailAPI", tag + " failed: " + t.getMessage());
            }
        };
    }
}
