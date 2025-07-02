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

public interface MailWebService {

    // יצירת מייל חדש
    @POST("mails")
    Call<Void> createMail(@Body Map<String, Object> body);

    // שליחת טיוטה
    @PATCH("mails/{id}/send")
    Call<Void> sendDraft(@Path("id") String mailId, @Body Map<String, Object> body);

    // עדכון טיוטה קיימת
    @PATCH("mails/{id}")
    Call<Void> updateMail(@Path("id") String mailId, @Body Map<String, Object> body);

    // מחיקת מייל לפי מזהה
    @DELETE("mails/{id}")
    Call<Void> deleteMail(@Path("id") String mailId);

    // סימון/ביטול כוכב
    @PATCH("mails/{id}/star")
    Call<Void> toggleStar(@Path("id") String mailId);

    // סימון/ביטול ספאם
    @PATCH("mails/{id}/spam")
    Call<Void> setSpam(@Path("id") String mailId, @Body Map<String, Boolean> body);

    // הוספת תווית למייל
    @POST("mails/{id}/labels")
    Call<Void> addLabelToMail(@Path("id") String mailId, @Body Map<String, String> body);

    // הסרת תווית ממייל
    @DELETE("mails/{id}/labels")
    Call<Void> removeLabelFromMail(@Path("id") String mailId, @Body Map<String, String> body);

    // שליפת מייל לפי מזהה
    @GET("mails/{id}")
    Call<MailFromServer> getMailById(@Path("id") String mailId);

    // שליפת כל המיילים
    @GET("mails/allmails")
    Call<MailListResponse> getAllMails(@Query("limit") int limit,
                                           @Query("offset") int offset);

    // שליפת מיילים שהתקבלו
    @GET("mails/inbox")
    Call<MailListResponse> getInboxMails(@Query("limit") int limit,
                                             @Query("offset") int offset);

    // שליפת מיילים שנשלחו
    @GET("mails/sent")
    Call<MailListResponse> getSentMails(@Query("limit") int limit,
                                            @Query("offset") int offset);

    // שליפת טיוטות
    @GET("mails/drafts")
    Call<MailListResponse> getDraftMails(@Query("limit") int limit,
                                             @Query("offset") int offset);

    // שליפת מיילים שנחשבים ספאם
    @GET("mails/spam")
    Call<MailListResponse> getSpamMails(@Query("limit") int limit,
                                            @Query("offset") int offset);

    // שליפת מיילים עם כוכב
    @GET("mails/starred")
    Call<MailListResponse> getStarredMails(@Query("limit") int limit,
                                               @Query("offset") int offset);

    // שליפת מיילים לפי תווית
    @GET("mails/labels-{labelId}")
    Call<MailListResponse> getMailsByLabel(@Path("labelId") String labelId,
                                               @Query("limit") int limit,
                                               @Query("offset") int offset);

    // חיפוש מיילים לפי מחרוזת
    @GET("mails/search-{query}")
    Call<MailListResponse> searchMails(@Path("query") String query,
                                       @Query("limit") int limit,
                                       @Query("offset") int offset);
}
