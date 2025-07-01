package com.example.mail_app.repository;

import android.content.Context;
import com.example.mail_app.app.auth.AuthWebService;
import com.example.mail_app.app.auth.PublicWebService;
import com.example.mail_app.app.service.UserAPI;
import retrofit2.Retrofit;

public class UserRepository {
    private final UserAPI publicUserAPI;
    private final UserAPI authUserAPI;

    public UserRepository(Context context) {
        Retrofit publicClient = PublicWebService.getClient(context);
        Retrofit authClient = AuthWebService.getClient(context);
        publicUserAPI = publicClient.create(UserAPI.class);
        authUserAPI = authClient.create(UserAPI.class);
    }

    public UserAPI getPublicUserAPI() {
        return publicUserAPI;
    }

    public UserAPI getAuthUserAPI() {
        return authUserAPI;
    }
}
