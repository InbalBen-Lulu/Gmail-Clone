package com.example.mail_app.app.network;

import com.example.mail_app.MyApp;
import com.example.mail_app.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * AuthWebService is responsible for creating Retrofit instances
 * that include an Authorization header (Bearer token).
 * Used for all authenticated requests to the backend server.
 */
public class AuthWebService {

    /**
     * Creates a Retrofit instance with an Authorization header.
     *
     * @param token JWT or access token to attach as "Bearer" in the header.
     *              If token is null or empty, the header will still be included with "Bearer null".
     * @return A configured Retrofit instance for authenticated API calls.
     */
    public static Retrofit getInstance(String token) {
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
