package com.example.mail_app.app.service;

import com.example.mail_app.data.entity.Label;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface LabelAPI {
    @GET("api/labels")
    Call<List<Label>> getAllLabels();

    @POST("api/labels")
    Call<Void> createLabel(@Body Label label);

    @GET("api/labels/{labelId}")
    Call<Label> getLabelById(@Path("labelId") String labelId);

    @PATCH("api/labels/{labelId}")
    Call<Void> renameLabel(@Path("labelId") String labelId, @Body Map<String, String> body);

    @PATCH("api/labels/{labelId}/color")
    Call<Void> setLabelColor(@Path("labelId") String labelId, @Body Map<String, String> body);

    @DELETE("api/labels/{labelId}/color")
    Call<Void> resetLabelColor(@Path("labelId") String labelId);

    @DELETE("api/labels/{labelId}")
    Call<Void> deleteLabel(@Path("labelId") String labelId);

    @POST("api/mails/{mailId}/labels")
    Call<Void> addLabelToMail(@Path("mailId") String mailId, @Body Map<String, String> body);

    @HTTP(method = "DELETE", path = "api/mails/{mailId}/labels", hasBody = true)
    Call<Void> removeLabelFromMail(@Path("mailId") String mailId, @Body Map<String, String> body);
}
