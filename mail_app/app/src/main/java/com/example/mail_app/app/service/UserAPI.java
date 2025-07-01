package com.example.mail_app.app.service;

import com.example.mail_app.data.model.PublicUser;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserAPI {
    @GET("api/users/{userId}/public")
    Call<PublicUser> getPublicUser(@Path("userId") String userId);

    @POST("api/users")
    Call<Void> createUser(@Body PublicUser newUser);

    @POST("api/users/{userId}/profile-image")
    Call<Void> uploadProfileImage(@Path("userId") String userId, @Body String base64Image);

    @DELETE("api/users/{userId}/profile-image")
    Call<Void> removeProfileImage(@Path("userId") String userId);
}
