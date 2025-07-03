package com.example.mail_app.data.remote;

import com.example.mail_app.data.entity.Label;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit web service interface for accessing label-related API endpoints.
 */
public interface LabelWebService {

    /** Retrieves all labels. */
    @GET("labels")
    Call<List<Label>> getLabels();

    /** Retrieves a single label by its ID. */
    @GET("labels/{id}")
    Call<Label> getLabelById(@Path("id") String id);

}
