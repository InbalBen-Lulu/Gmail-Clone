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

/**
 * Handles all API-related operations for label data.
 * Fetches labels from the server and syncs them to local Room database.
 */
public class LabelAPI {
    private final MutableLiveData<List<Label>> labelListData;
    private final LabelDao dao;
    private final LabelWebService api;

    /**
     * Constructor initializing DAO, API, and LiveData.
     */
    public LabelAPI(MutableLiveData<List<Label>> labelListData, LabelDao dao) {
        this.labelListData = labelListData;
        this.dao = dao;

        String token = AuthManager.getToken(MyApp.getInstance());
        Retrofit retrofit = AuthWebService.getInstance(token);
        api = retrofit.create(LabelWebService.class);
    }

    /**
     * Retrieves the list of labels from the server and saves it to Room database.
     * Updates LiveData with the current label list from Room.
     */
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
            @Override
            public void onFailure(Call<List<Label>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Returns a label by its ID from the local database.
     */
    public Label getById(String id) {
        return dao.getById(id);
    }
}
