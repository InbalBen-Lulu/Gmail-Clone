package com.example.mail_app.app.api;

import androidx.lifecycle.MutableLiveData;

import com.example.mail_app.MyApp;
import com.example.mail_app.app.network.AuthWebService;
import com.example.mail_app.auth.AuthManager;
import com.example.mail_app.data.dao.LabelDao;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.data.remote.LabelWebService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LabelAPI {
    private final MutableLiveData<List<Label>> labelListData;
    private final LabelDao dao;
    private final LabelWebService api;

    public LabelAPI(MutableLiveData<List<Label>> labelListData, LabelDao dao) {
        this.labelListData = labelListData;
        this.dao = dao;

        String token = AuthManager.getToken(MyApp.getInstance());
        Retrofit retrofit = AuthWebService.getInstance(token);
        api = retrofit.create(LabelWebService.class);
    }

    public void get() {
        Call<List<Label>> call = api.getLabels();
        call.enqueue(new Callback<List<Label>>() {
            @Override
            public void onResponse(Call<List<Label>> call, Response<List<Label>> response) {
                if (response.body() == null) return;
                new Thread(() -> {
                    dao.clear();
                    dao.insertAll(response.body());
                    labelListData.postValue(dao.getAll());
                }).start();
            }
            @Override public void onFailure(Call<List<Label>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public Label getById(String id) {
        return dao.getById(id);
    }
}
