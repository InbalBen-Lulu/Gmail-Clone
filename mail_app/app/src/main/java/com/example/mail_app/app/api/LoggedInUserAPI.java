package com.example.mail_app.app.api;

import com.example.mail_app.MyApp;
import com.example.mail_app.app.network.AuthWebService;
import com.example.mail_app.auth.AuthManager;
import com.example.mail_app.data.dao.LoggedInUserDao;
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

public class LoggedInUserAPI {
    private final LoggedInUserDao dao;
    private final LoggedInUserWebService api;
    private final OnUserFetched callback;

    public interface OnUserFetched {
        void onFetched(LoggedInUser user);
    }

    public LoggedInUserAPI(LoggedInUserDao dao, OnUserFetched callback) {
        this.dao = dao;
        this.callback = callback;

        String token = AuthManager.getToken(MyApp.getInstance());
        Retrofit retrofit = AuthWebService.getInstance(token);
        api = retrofit.create(LoggedInUserWebService.class);
    }

    public void registerUser(RegisterRequest request, Callback<Void> callback) {
        Retrofit noAuthRetrofit = AuthWebService.getInstance(null); // no token for registration
        LoggedInUserWebService noAuthApi = noAuthRetrofit.create(LoggedInUserWebService.class);
        noAuthApi.registerUser(request).enqueue(callback);
    }

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
                        callback.onFetched(user);
                    }).start();
                }
            }

            @Override public void onFailure(Call<LoggedInUser> call, Throwable t) {}
        });
    }

    public void uploadImage(File imageFile) {
        String userId = AuthManager.getUserId(MyApp.getInstance());

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

        api.uploadProfileImage(userId, imagePart).enqueue(emptyCallback());
    }

    public void deleteImage() {
        String userId = AuthManager.getUserId(MyApp.getInstance());
        api.deleteProfileImage(userId).enqueue(emptyCallback());
    }

    private Callback<Void> emptyCallback() {
        return new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        };
    }
}
