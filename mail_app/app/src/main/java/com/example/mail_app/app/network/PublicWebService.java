package com.example.mail_app.app.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.content.Context;

import com.example.mail_app.MyApp;
import com.example.mail_app.R;

public class PublicWebService {
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(MyApp.getInstance().getString(R.string.BaseUrl))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}