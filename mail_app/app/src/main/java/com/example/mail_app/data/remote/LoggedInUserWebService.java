package com.example.mail_app.data.remote;

import com.example.mail_app.data.dto.ImageUploadRequest;
import com.example.mail_app.data.dto.LoginRequest;
import com.example.mail_app.data.dto.LoginResponse;
import com.example.mail_app.data.dto.RegisterRequest;
import com.example.mail_app.data.entity.LoggedInUser;
import com.example.mail_app.data.entity.PublicUser;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Retrofit web service interface for user authentication and profile management.
 */
public interface LoggedInUserWebService {

    /** Sends a registration request to create a new user. */
    @POST("users")
    Call<Void> registerUser(@Body RegisterRequest request);

    /** Retrieves public info for the specified user. */
    @GET("users/{id}/public")
    Call<PublicUser> getPublicUserInfo(@Path("id") String userId);

    /** Retrieves the logged-in user's data by user ID. */
    @GET("users/{id}")
    Call<LoggedInUser> getMyUser(@Path("id") String userId);

    /** Uploads a profile image for the user. */
    @POST("users/{id}/profile-image")
    Call<Void> uploadProfileImage(@Path("id") String userId, @Body ImageUploadRequest request);

    /** Deletes the user's profile image. */
    @DELETE("users/{id}/profile-image")
    Call<Void> deleteProfileImage(@Path("id") String userId);

    /** Sends a login request with credentials. */
    @POST("tokens/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    /** Logs out the currently logged-in user. */
    @POST("tokens/logout")
    Call<Void> logout();
}