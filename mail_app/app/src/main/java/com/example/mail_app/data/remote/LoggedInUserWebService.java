package com.example.mail_app.data.remote;

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

public interface LoggedInUserWebService {

    @POST("users")
    Call<Void> registerUser(@Body RegisterRequest request);

    @GET("users/{id}")
    Call<LoggedInUser> getMyUser(@Path("id") String userId);

    @POST("users/{id}/profile-image")
    @Multipart
    Call<Void> uploadProfileImage(@Path("id") String userId,
                                  @Part MultipartBody.Part image);

    @DELETE("users/{id}/profile-image")
    Call<Void> deleteProfileImage(@Path("id") String userId);
}
