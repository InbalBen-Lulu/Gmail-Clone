package com.example.mail_app.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mail_app.MyApp;
import com.example.mail_app.data.dao.LabelDao;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.LocalDatabase;
import com.example.mail_app.app.service.LabelAPI;

import java.util.ArrayList;
import java.util.List;

public class LabelRepository {
    private final LabelDao dao;
    private final MutableLiveData<List<Label>> labelListData;
    private final LabelAPI api;

    public LabelRepository(Context context) {
        LocalDatabase db = MyApp.getInstance().getDatabase();
        this.dao = db.labelDao();
        this.labelListData = new LabelListData();
        this.api = new LabelAPI(labelListData, dao);
    }

    class LabelListData extends MutableLiveData<List<Label>> {
        public LabelListData() {
            setValue(new ArrayList<>());
        }

        @Override
        protected void onActive() {
            new Thread(() -> labelListData.postValue(dao.getAll())).start();
        }
    }

    public LiveData<List<Label>> getAll() { return labelListData; }

    public void add(Label label) {
        new Thread(() -> {
            dao.insert(label);
            reload();
        }).start();
    }

    public void delete(Label label) {
        new Thread(() -> {
            dao.delete(label);
            reload();
        }).start();
    }

    public void rename(String labelId, String newName) {
        api.rename(labelId, newName);
    }

    public void setColor(String labelId, String color) {
        api.setColor(labelId, color);
    }

    public void resetColor(String labelId) {
        api.resetColor(labelId);
    }

    public void reload() {
        api.get();
    }
}
