package com.example.mail_app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mail_app.LocalDatabase;
import com.example.mail_app.MyApp;
import com.example.mail_app.app.api.LabelAPI;
import com.example.mail_app.data.dao.LabelDao;
import com.example.mail_app.data.entity.Label;

import java.util.LinkedList;
import java.util.List;

public class LabelRepository {
    private final LabelDao labelDao;
    private final LabelListData labelListData;
    private final LabelAPI api;

    public LabelRepository() {
        LocalDatabase db = MyApp.getInstance().getDatabase();
        this.labelDao = db.labelDao();
        this.labelListData = new LabelListData();
        this.api = new LabelAPI(labelListData, labelDao);
    }

    class LabelListData extends MutableLiveData<List<Label>> {
        public LabelListData() {
            super();
            setValue(new LinkedList<Label>());
        }

        @Override
        protected void onActive() {
            super.onActive();
            new Thread(() -> labelListData.postValue(labelDao.getAll())).start();
        }
    }

    public LiveData<List<Label>> getAll() { return labelListData; }

    public Label getById(String id) {
        return labelDao.getById(id);
    }

    public void reload() {
        api.get();
    }
}
