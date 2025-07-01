package com.example.mail_app.data.remote;

import com.example.mail_app.data.dto.MailFromServer;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MailWebService {

    @POST("mails")
    Call<Void> createMail(@Body Map<String, Object> mail);

    @GET("mails/{id}")
    Call<MailFromServer> getMailById(@Path("id") String id);

    @PATCH("mails/{id}")
    Call<Void> updateMail(@Path("id") String id, @Body Map<String, Object> updated);

    @PATCH("mails/{id}/send")
    Call<Void> sendDraft(@Path("id") String id, @Body Map<String, Object> body);

    @PATCH("mails/{id}/star")
    Call<Void> toggleStar(@Path("id") String id);

    @PATCH("mails/{id}/spam")
    Call<Void> setSpam(@Path("id") String id, @Body Map<String, Boolean> body);

    @POST("mails/{id}/labels")
    Call<Void> addLabelToMail(@Path("id") String id, @Body Map<String, String> body);

    @DELETE("mails/{id}/labels")
    Call<Void> removeLabelFromMail(@Path("id") String id, @Body Map<String, String> body);

    @DELETE("mails/{id}")
    Call<Void> deleteMail(@Path("id") String id);

    // Filters
    @GET("mails/allmails")
    Call<List<MailFromServer>> getAllMails();

    @GET("mails/inbox")
    Call<List<MailFromServer>> getInboxMails();

    @GET("mails/sent")
    Call<List<MailFromServer>> getSentMails();

    @GET("mails/drafts")
    Call<List<MailFromServer>> getDraftMails();

    @GET("mails/spam")
    Call<List<MailFromServer>> getSpamMails();

    @GET("mails/starred")
    Call<List<MailFromServer>> getStarredMails();

    @GET("mails/labels-{labelId}")
    Call<List<MailFromServer>> getMailsByLabel(@Path("labelId") String labelId);

    @GET("mails/search-{query}")
    Call<List<MailFromServer>> searchMails(@Path("query") String query);
}