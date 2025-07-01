package com.example.mail_app.repository;

import android.content.Context;
import com.example.mail_app.app.auth.AuthWebService;
import com.example.mail_app.app.service.LabelAPI;
import retrofit2.Retrofit;

public class LabelRepository {
    private final LabelAPI labelAPI;

    public LabelRepository(Context context) {
        Retrofit retrofit = AuthWebService.getClient(context);
        labelAPI = retrofit.create(LabelAPI.class);
    }

    public LabelAPI getApi() {
        return labelAPI;
    }
}
