package com.example.mail_app.data.remote;

import com.example.mail_app.data.dto.LoginRequest;
import com.example.mail_app.data.dto.LoginResponse;
import com.example.mail_app.data.dto.RegisterRequest;
import com.example.mail_app.data.entity.LoggedInUser;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Retrofit web service interface for user authentication and profile management.
 */
public interface LoggedInUserWebService {

    /** Sends a registration request to create a new user. */
    @POST("users")
    Call<Void> registerUser(@Body RegisterRequest request);

    /** Retrieves the logged-in user's data by user ID. */
    @GET("users/{id}")
    Call<LoggedInUser> getMyUser(@Path("id") String userId);

    /** Uploads a profile image for the user. */
    @POST("users/{id}/profile-image")
    @Multipart
    Call<Void> uploadProfileImage(@Path("id") String userId,
                                  @Part MultipartBody.Part image);

    /** Deletes the user's profile image. */
    @DELETE("users/{id}/profile-image")
    Call<Void> deleteProfileImage(@Path("id") String userId);

    /** Sends a login request with credentials. */
    @POST("token/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    /** Logs out the currently logged-in user. */
    @POST("token/logout")
    Call<Void> logout();
}
