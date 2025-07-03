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

/**
 * Repository class responsible for managing label data.
 * It bridges between the local Room database and the remote API.
 */
public class LabelRepository {
    private final LabelDao labelDao;
    private final LabelListData labelListData;
    private final LabelAPI api;

    /** Initializes the repository with database access and API. */
    public LabelRepository() {
        LocalDatabase db = MyApp.getInstance().getDatabase();
        this.labelDao = db.labelDao();
        this.labelListData = new LabelListData();
        this.api = new LabelAPI(labelListData, labelDao);
    }

    /**
     * LiveData implementation that loads the list of labels from the local database
     * when it becomes active (i.e., observed).
     */
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

    /** Returns LiveData containing all labels from the local database. */
    public LiveData<List<Label>> getAll() {
        return labelListData;
    }

    /** Returns a single label by ID, fetched from the local database. */
    public Label getById(String id) {
        return labelDao.getById(id);
    }

    /** Reloads the label list from the remote server via API. */
    public void reload() {
        api.get();
    }
}
