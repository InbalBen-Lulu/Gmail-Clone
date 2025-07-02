package com.example.mail_app.app.api;
import com.example.mail_app.MyApp;
import com.example.mail_app.app.network.AuthWebService;
import com.example.mail_app.app.network.PublicWebService;
import com.example.mail_app.auth.AuthManager;
import com.example.mail_app.data.dao.LoggedInUserDao;
import com.example.mail_app.data.dto.LoginRequest;
import com.example.mail_app.data.dto.LoginResponse;
import com.example.mail_app.data.dto.RegisterRequest;
import com.example.mail_app.data.entity.LoggedInUser;
import com.example.mail_app.data.remote.LoggedInUserWebService;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Handles authentication, registration, and profile image operations for the currently logged-in user.
 * Interacts with both the remote server and Room database.
 */
public class LoggedInUserAPI {
    private final LoggedInUserDao dao;
    private final LoggedInUserWebService api;

    /**
     * Initializes the DAO and Retrofit API interface with token.
     */
    public LoggedInUserAPI(LoggedInUserDao dao) {
        this.dao = dao;

        String token = AuthManager.getToken(MyApp.getInstance());
        Retrofit retrofit = AuthWebService.getInstance(token);
        api = retrofit.create(LoggedInUserWebService.class);
    }

    /**
     * Registers a new user using the public endpoint (no token required).
     */
    public void registerUser(RegisterRequest request, Callback<Void> callback) {
        Retrofit noAuthRetrofit = AuthWebService.getInstance(null);
        LoggedInUserWebService noAuthApi = noAuthRetrofit.create(LoggedInUserWebService.class);
        noAuthApi.registerUser(request).enqueue(callback);
    }

    /**
     * Retrieves the currently logged-in user from the server and stores it in Room.
     */
    public void get() {
        String userId = AuthManager.getUserId(MyApp.getInstance());
        Call<LoggedInUser> call = api.getMyUser(userId);
        call.enqueue(new Callback<LoggedInUser>() {
            @Override
            public void onResponse(Call<LoggedInUser> call, Response<LoggedInUser> response) {
                LoggedInUser user = response.body();
                if (user != null) {
                    new Thread(() -> {
                        dao.clear();
                        dao.insert(user);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<LoggedInUser> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Uploads a profile image for the logged-in user.
     * On success, triggers a user re-fetch.
     */
    public void uploadImage(File imageFile) {
        String userId = AuthManager.getUserId(MyApp.getInstance());

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

        api.uploadProfileImage(userId, imagePart).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("[Upload Image] Success");
                    get();
                } else {
                    System.out.println("[Upload Image] Failed with code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("[Upload Image] Failed");
                t.printStackTrace();
            }
        });
    }

    /**
     * Deletes the profile image of the logged-in user and refreshes the user object.
     */
    public void deleteImage() {
        String userId = AuthManager.getUserId(MyApp.getInstance());

        api.deleteProfileImage(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("[Delete Image] Success");
                    get();
                } else {
                    System.out.println("[Delete Image] Failed with code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("[Delete Image] Failed");
                t.printStackTrace();
            }
        });
    }

    /**
     * Logs in the user, saves the token and user ID, then fetches the user profile.
     */
    public void login(String userId, String password, Callback<LoginResponse> callback) {
        Retrofit noAuthRetrofit = PublicWebService.getInstance();
        LoggedInUserWebService noAuthApi = noAuthRetrofit.create(LoggedInUserWebService.class);
        LoginRequest request = new LoginRequest(userId, password);

        noAuthApi.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    AuthManager.saveToken(MyApp.getInstance(), loginResponse.getToken());
                    AuthManager.saveUserId(MyApp.getInstance(), loginResponse.getUser().getUserId());
                    get();
                }
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    /**
     * Logs out the user by calling the API and clearing local user data.
     */
    public void logout(Callback<Void> callback) {
        api.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                AuthManager.clearAll(MyApp.getInstance());
                new Thread(() -> dao.clear()).start();
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
}
