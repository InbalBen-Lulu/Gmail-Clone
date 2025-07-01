package com.example.mail_app.data.remote;

import com.example.mail_app.data.entity.Label;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface LabelWebService {

    @GET("labels")
    Call<List<Label>> getLabels();

    @POST("labels")
    Call<Void> createLabel(@Body Label label);

    @GET("labels/{id}")
    Call<Label> getLabelById(@Path("id") String id);

    @PATCH("labels/{id}")
    Call<Void> renameLabel(@Path("id") String id, @Body Map<String, String> body); // { name }

    @DELETE("labels/{id}")
    Call<Void> deleteLabel(@Path("id") String id);

    @PATCH("labels/{id}/color")
    Call<Void> setLabelColor(@Path("id") String id, @Body Map<String, String> body); // { color }

    @DELETE("labels/{id}/color")
    Call<Void> resetLabelColor(@Path("id") String id);
}
