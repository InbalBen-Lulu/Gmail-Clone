package com.example.mail_app.data.remote;

import com.example.mail_app.data.dto.MailFromServer;
import com.example.mail_app.data.dto.MailListResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MailWebService {

    // Creates a new mail (draft or sent)
    @POST("mails")
    Call<Void> createMail(@Body Map<String, Object> body);

    // Sends an existing draft mail
    @PATCH("mails/{id}/send")
    Call<Void> sendDraft(@Path("id") String mailId, @Body Map<String, Object> body);

    // Updates an existing draft (without sending it)
    @PATCH("mails/{id}")
    Call<Void> updateMail(@Path("id") String mailId, @Body Map<String, Object> body);

    // Deletes a mail by its ID
    @DELETE("mails/{id}")
    Call<Void> deleteMail(@Path("id") String mailId);

    // Toggles the star status of a mail
    @PATCH("mails/{id}/star")
    Call<Void> toggleStar(@Path("id") String mailId);

    // Toggles the spam status of a mail
    @PATCH("mails/{id}/spam")
    Call<Void> setSpam(@Path("id") String mailId, @Body Map<String, Boolean> body);

    // Adds a label to a mail
    @POST("mails/{id}/labels")
    Call<Void> addLabelToMail(@Path("id") String mailId, @Body Map<String, String> body);

    // Removes a label from a mail
//    @DELETE("mails/{id}/labels")
    @HTTP(method = "DELETE", path = "mails/{id}/labels/", hasBody = true)
    Call<Void> removeLabelFromMail(@Path("id") String mailId, @Body Map<String, String> body);

    // Retrieves a single mail by ID
    @GET("mails/{id}")
    Call<MailFromServer> getMailById(@Path("id") String mailId);

    // Retrieves all mails (any type)
    @GET("mails/allmails")
    Call<MailListResponse> getAllMails(@Query("limit") int limit,
                                       @Query("offset") int offset);

    // Retrieves received mails (inbox)
    @GET("mails/inbox")
    Call<MailListResponse> getInboxMails(@Query("limit") int limit,
                                         @Query("offset") int offset);

    // Retrieves sent mails
    @GET("mails/sent")
    Call<MailListResponse> getSentMails(@Query("limit") int limit,
                                        @Query("offset") int offset);

    // Retrieves draft mails
    @GET("mails/drafts")
    Call<MailListResponse> getDraftMails(@Query("limit") int limit,
                                         @Query("offset") int offset);

    // Retrieves spam mails
    @GET("mails/spam")
    Call<MailListResponse> getSpamMails(@Query("limit") int limit,
                                        @Query("offset") int offset);

    // Retrieves starred mails
    @GET("mails/starred")
    Call<MailListResponse> getStarredMails(@Query("limit") int limit,
                                           @Query("offset") int offset);

    // Retrieves mails associated with a specific label
    @GET("mails/labels-{labelId}")
    Call<MailListResponse> getMailsByLabel(@Path("labelId") String labelId,
                                           @Query("limit") int limit,
                                           @Query("offset") int offset);

    // Searches mails by query (subject, body, sender, etc.)
    @GET("mails/search-{query}")
    Call<MailListResponse> searchMails(@Path("query") String query,
                                       @Query("limit") int limit,
                                       @Query("offset") int offset);
}
