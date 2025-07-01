package com.example.mail_app.app.service;

import androidx.lifecycle.MutableLiveData;

import com.example.mail_app.MyApp;
import com.example.mail_app.R;
import com.example.mail_app.data.dao.LabelDao;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.data.remote.LabelWebService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class LabelAPI {
    private final MutableLiveData<List<Label>> labelListData;
    private final LabelDao dao;
    private final LabelWebService api;

    public LabelAPI(MutableLiveData<List<Label>> labelListData, LabelDao dao) {
        this.labelListData = labelListData;
        this.dao = dao;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyApp.getInstance().getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(LabelWebService.class);
    }

    public void get() {
        api.getLabels().enqueue(new Callback<List<Label>>() {
            @Override
            public void onResponse(Call<List<Label>> call, Response<List<Label>> response) {
                if (response.body() == null) return;
                new Thread(() -> {
                    dao.clear();
                    dao.insertAll(response.body());
                    labelListData.postValue(dao.getAll());
                }).start();
            }
            @Override public void onFailure(Call<List<Label>> call, Throwable t) {}
        });
    }

    public void create(Label label) {
        api.createLabel(label).enqueue(emptyCallback());
    }

    public void delete(String labelId) {
        api.deleteLabel(labelId).enqueue(emptyCallback());
    }

    public void rename(String labelId, String newName) {
        Map<String, String> body = new HashMap<>();
        body.put("name", newName);
        api.renameLabel(labelId, body).enqueue(emptyCallback());
    }

    public void setColor(String labelId, String color) {
        Map<String, String> body = new HashMap<>();
        body.put("color", color);
        api.setLabelColor(labelId, body).enqueue(emptyCallback());
    }

    public void resetColor(String labelId) {
        api.resetLabelColor(labelId).enqueue(emptyCallback());
    }

    private Callback<Void> emptyCallback() {
        return new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        };
    }
}
