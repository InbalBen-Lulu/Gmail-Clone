package com.example.mail_app.app.api;

import androidx.lifecycle.MutableLiveData;

import com.example.mail_app.LocalDatabase;
import com.example.mail_app.MyApp;
import com.example.mail_app.app.network.AuthWebService;
import com.example.mail_app.app.network.PublicWebService;
import com.example.mail_app.auth.AuthManager;
import com.example.mail_app.data.dao.LoggedInUserDao;
import com.example.mail_app.data.dto.ImageUploadRequest;
import com.example.mail_app.data.dto.LoginRequest;
import com.example.mail_app.data.dto.LoginResponse;
import com.example.mail_app.data.dto.RegisterRequest;
import com.example.mail_app.data.entity.LoggedInUser;
import com.example.mail_app.data.entity.PublicUser;
import com.example.mail_app.data.remote.LoggedInUserWebService;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * API class responsible for managing all operations related to the logged-in user.
 * Handles registration, login/logout, user retrieval, and profile image updates.
 */
public class LoggedInUserAPI {
    private final LoggedInUserDao dao;
    private LoggedInUserWebService api;
    private MutableLiveData<LoggedInUser> userLiveData;

    public LoggedInUserAPI(LoggedInUserDao dao, MutableLiveData<LoggedInUser> userLiveData) {
        this.dao = dao;
        this.userLiveData = userLiveData;

        String token = AuthManager.getToken(MyApp.getInstance());
        Retrofit retrofit = (token == null || token.isEmpty())
                ? PublicWebService.getInstance()
                : AuthWebService.getInstance(token);
        api = retrofit.create(LoggedInUserWebService.class);
    }

    /**
     * Registers a new user and logs them in upon success.
     */
    public void registerUser(RegisterRequest request, Callback<LoginResponse> callback) {
        api.registerUser(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    login(request.getUserId(), request.getPassword(), callback);
                } else {
                    callback.onResponse(null, Response.error(response.code(), response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }

    /**
     * Fetches the full profile of the currently logged-in user from the server.
     * Saves the user to the local database and updates LiveData.
     */
    public void get(Callback<LoggedInUser> callback) {
        String userId = AuthManager.getUserId(MyApp.getInstance());
        api.getMyUser(userId).enqueue(new Callback<LoggedInUser>() {
            @Override
            public void onResponse(Call<LoggedInUser> call, Response<LoggedInUser> response) {
                LoggedInUser user = response.body();
                if (user != null) {
                    new Thread(() -> {
                        dao.clear();
                        dao.insert(user);
                        userLiveData.postValue(user);
                    }).start();
                }
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<LoggedInUser> call, Throwable t) {
                t.printStackTrace();
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    /**
     * Retrieves public user info (e.g. sender info) without requiring authentication.
     */
    public void getPublicUserInfo(String userId, Callback<PublicUser> callback) {
        Call<PublicUser> call = api.getPublicUserInfo(userId);
        call.enqueue(new Callback<PublicUser>() {
            @Override
            public void onResponse(Call<PublicUser> call, Response<PublicUser> response) {
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<PublicUser> call, Throwable t) {
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    /**
     * Uploads a profile image to the server and refreshes local user data.
     */
    public void uploadImage(String base64, Callback<Void> callback) {
        String userId = AuthManager.getUserId(MyApp.getInstance());
        ImageUploadRequest request = new ImageUploadRequest(base64);

        api.uploadProfileImage(userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("[Upload Image] Success");
                    get(new Callback<LoggedInUser>() {
                        @Override
                        public void onResponse(Call<LoggedInUser> call2, Response<LoggedInUser> response2) {
                            callback.onResponse(call, response);
                        }

                        @Override
                        public void onFailure(Call<LoggedInUser> call2, Throwable t2) {
                            callback.onFailure(call, t2);
                        }
                    });
                } else {
                    if (callback != null) {
                        callback.onResponse(call, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("[Upload Image] Failed");
                t.printStackTrace();
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    /**
     * Deletes the user's profile image and refreshes local user data.
     */
    public void deleteImage(Callback<Void> callback) {
        String userId = AuthManager.getUserId(MyApp.getInstance());

        api.deleteProfileImage(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("[Delete Image] Success");
                    get(new Callback<LoggedInUser>() {
                        @Override
                        public void onResponse(Call<LoggedInUser> call2, Response<LoggedInUser> response2) {
                            callback.onResponse(call, response);
                        }

                        @Override
                        public void onFailure(Call<LoggedInUser> call2, Throwable t2) {
                            callback.onFailure(call, t2);
                        }
                    });
                } else {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("[Delete Image] Failed");
                t.printStackTrace();
                callback.onFailure(call, t);
            }
        });
    }

    /**
     * Logs in a user and stores the token and userId on success.
     */
    public void login(String userId, String password, Callback<LoginResponse> callback) {
        LoginRequest request = new LoginRequest(userId, password);

        api.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    AuthManager.saveToken(MyApp.getInstance(), loginResponse.getToken());
                    AuthManager.saveUserId(MyApp.getInstance(), loginResponse.getUser().getUserId());

                    Retrofit retrofit = AuthWebService.getInstance(loginResponse.getToken());
                    api = retrofit.create(LoggedInUserWebService.class);

                    get(new Callback<LoggedInUser>() {
                        @Override
                        public void onResponse(Call<LoggedInUser> call2, Response<LoggedInUser> response2) {
                            callback.onResponse(call, response);
                        }

                        @Override
                        public void onFailure(Call<LoggedInUser> call2, Throwable t2) {
                            callback.onFailure(call, t2);
                        }
                    });
                } else {
                    String errorMsg = "Login failed.";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            JSONObject json = new JSONObject(errorJson);
                            if (json.has("error")) {
                                errorMsg = json.getString("error");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    callback.onFailure(call, new Throwable(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    /**
     * Logs out the current user and clears local data.
     */
    public void logout(Callback<Void> callback) {
        api.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                AuthManager.clearAll(MyApp.getInstance());
                new Thread(() -> {
                    LocalDatabase db = MyApp.getInstance().getDatabase();
                    db.userDao().clear();
                    db.mailDao().clearAllMails();
                    db.publicUserDao().clearAllUsers();
                    db.labelDao().clear();
                }).start();
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
}
