package com.example.mail_app.data.remote;

import com.example.mail_app.data.entity.Label;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LabelWebService {

    @GET("labels")
    Call<List<Label>> getLabels();

    @GET("labels/{id}")
    Call<Label> getLabelById(@Path("id") String id);

}
