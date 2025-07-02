package com.example.mail_app.data.remote;

import com.example.mail_app.data.dto.MailFromServer;
import com.example.mail_app.data.dto.MailListResponse;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit web service interface for accessing and modifying mail-related data.
 */
public interface MailWebService {

    /** Creates a new mail. */
    @POST("mails")
    Call<Void> createMail(@Body Map<String, Object> body);

    /** Sends a draft mail. */
    @PATCH("mails/{id}/send")
    Call<Void> sendDraft(@Path("id") String mailId, @Body Map<String, Object> body);

    /** Updates an existing mail (e.g., draft). */
    @PATCH("mails/{id}")
    Call<Void> updateMail(@Path("id") String mailId, @Body Map<String, Object> body);

    /** Deletes a mail by its ID. */
    @DELETE("mails/{id}")
    Call<Void> deleteMail(@Path("id") String mailId);

    /** Toggles the star status of a mail. */
    @PATCH("mails/{id}/star")
    Call<Void> toggleStar(@Path("id") String mailId);

    /** Toggles the spam status of a mail. */
    @PATCH("mails/{id}/spam")
    Call<Void> setSpam(@Path("id") String mailId, @Body Map<String, Boolean> body);

    /** Adds a label to a mail. */
    @POST("mails/{id}/labels")
    Call<Void> addLabelToMail(@Path("id") String mailId, @Body Map<String, String> body);

    /** Removes a label from a mail. */
    @DELETE("mails/{id}/labels")
    Call<Void> removeLabelFromMail(@Path("id") String mailId, @Body Map<String, String> body);

    /** Retrieves a mail by its ID. */
    @GET("mails/{id}")
    Call<MailFromServer> getMailById(@Path("id") String mailId);

    /** Retrieves all mails. */
    @GET("mails/allmails")
    Call<MailListResponse> getAllMails(@Query("limit") int limit,
                                           @Query("offset") int offset);

    /** Retrieves inbox mails. */
    @GET("mails/inbox")
    Call<MailListResponse> getInboxMails(@Query("limit") int limit,
                                             @Query("offset") int offset);

    /** Retrieves sent mails. */
    @GET("mails/sent")
    Call<MailListResponse> getSentMails(@Query("limit") int limit,
                                            @Query("offset") int offset);

    /** Retrieves draft mails. */
    @GET("mails/drafts")
    Call<MailListResponse> getDraftMails(@Query("limit") int limit,
                                             @Query("offset") int offset);

    /** Retrieves mails marked as spam. */
    @GET("mails/spam")
    Call<MailListResponse> getSpamMails(@Query("limit") int limit,
                                            @Query("offset") int offset);

    /** Retrieves starred mails. */
    @GET("mails/starred")
    Call<MailListResponse> getStarredMails(@Query("limit") int limit,
                                               @Query("offset") int offset);

    /** Retrieves mails associated with a specific label. */
    @GET("mails/labels-{labelId}")
    Call<MailListResponse> getMailsByLabel(@Path("labelId") String labelId,
                                               @Query("limit") int limit,
                                               @Query("offset") int offset);

    /** Searches for mails by a given query string. */
    @GET("mails/search-{query}")
    Call<MailListResponse> searchMails(@Path("query") String query,
                                       @Query("limit") int limit,
                                       @Query("offset") int offset);
}
