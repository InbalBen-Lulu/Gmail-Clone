package com.example.mail_app.app.network;

import com.example.mail_app.MyApp;
import com.example.mail_app.R;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * PublicWebService is responsible for creating a singleton Retrofit instance
 * for making unauthenticated (public) requests, such as login and register.
 */
public class PublicWebService {
    private static Retrofit retrofit;

    /**
     * Returns a singleton Retrofit instance without an Authorization header.
     *
     * @return A Retrofit instance for public API endpoints.
     */
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
