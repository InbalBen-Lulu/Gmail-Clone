package com.example.mail_app.app.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Context;

import com.example.mail_app.MyApp;
import com.example.mail_app.R;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AuthWebService {
    public static Retrofit getInstance(Context context, String token) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(MyApp.getInstance().getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}