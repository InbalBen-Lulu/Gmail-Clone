package com.example.mail_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.MailLabelCrossRef;
import com.example.mail_app.data.entity.MailRecipientCrossRef;

import java.util.List;

@Dao
public interface MailDao {

    // Retrieves a full mail (with recipients and labels) by ID
    @Transaction
    @Query("SELECT * FROM mail WHERE id = :mailId")
    FullMail getMailById(String mailId);

    // Returns a LiveData-wrapped FullMail object for the given mail ID
    @Transaction
    @Query("SELECT * FROM mail WHERE id = :mailId")
    LiveData<FullMail> getLiveMailById(String mailId);

    // Retrieves all non-spam mails with recipients and labels
    @Transaction
    @Query("SELECT * FROM mail WHERE isSpam = 0")
    List<FullMail> getAllMails();

    // Retrieves inbox mails (received and not spam)
    @Transaction
    @Query("SELECT * FROM mail WHERE type = 'received' AND isSpam = 0")
    List<FullMail> getInboxMails();

    // Retrieves sent mails (not drafts)
    @Transaction
    @Query("SELECT * FROM mail WHERE type = 'sent' AND isDraft = 0")
    List<FullMail> getSentMails();

    // Retrieves draft mails
    @Transaction
    @Query("SELECT * FROM mail WHERE isDraft = 1")
    List<FullMail> getDraftMails();

    // Retrieves spam mails
    @Transaction
    @Query("SELECT * FROM mail WHERE isSpam = 1")
    List<FullMail> getSpamMails();

    // Retrieves starred mails
    @Transaction
    @Query("SELECT * FROM mail WHERE isStar = 1")
    List<FullMail> getStarredMails();

    // Full-text search across subject, body, sender, sender name, and recipients
    @Transaction
    @Query("SELECT DISTINCT * FROM mail " +
            "LEFT JOIN public_users AS from_user ON \"from\" = from_user.userId " +
            "LEFT JOIN mail_recipient_cross_ref ON id = mail_recipient_cross_ref.mailId " +
            "WHERE subject LIKE '%' || :query || '%' OR " +
            "body LIKE '%' || :query || '%' OR " +
            "\"from\" LIKE '%' || :query || '%' OR " +
            "from_user.name LIKE '%' || :query || '%' OR " +
            "mail_recipient_cross_ref.userId LIKE '%' || :query || '%'")
    List<FullMail> searchMails(String query);

    // Retrieves mails associated with a specific label ID
    @Transaction
    @Query("SELECT * FROM mail " +
            "INNER JOIN mail_label_cross_ref ON mail.id = mail_label_cross_ref.mailId " +
            "WHERE mail_label_cross_ref.labelId = :labelId")
    List<FullMail> getMailsByLabel(String labelId);

    // Inserts a single mail object into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMail(Mail mail);

    // Deletes a mail by its ID
    @Query("DELETE FROM mail WHERE id = :mailId")
    void deleteMailById(String mailId);

    // Deletes all recipients (TO) related to a mail
    @Query("DELETE FROM mail_recipient_cross_ref WHERE mailId = :mailId")
    void deleteRecipientsByMailId(String mailId);

    // Deletes all labels linked to a mail
    @Query("DELETE FROM mail_label_cross_ref WHERE mailId = :mailId")
    void deleteLabelsByMailId(String mailId);

    // Toggles the isStar flag of a mail (on/off)
    @Query("UPDATE mail SET isStar = NOT isStar WHERE id = :mailId")
    void toggleStar(String mailId);

    // Toggles the isSpam flag of a mail (on/off)
    @Query("UPDATE mail SET isSpam = NOT isSpam WHERE id = :mailId")
    void setSpam(String mailId);

    // Marks a mail as read (isRead = 1)
    @Query("UPDATE mail SET isRead = 1 WHERE id = :mailId")
    void markAsRead(String mailId);

    // Inserts a list of recipients (TO) for a mail
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipients(List<MailRecipientCrossRef> recipients);

    // Adds a label to a mail
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLabelToMail(MailLabelCrossRef labelRef);

    // Removes a specific label from a mail
    @Query("DELETE FROM mail_label_cross_ref WHERE mailId = :mailId AND labelId = :labelId")
    void removeLabelFromMail(String mailId, String labelId);

    // Deletes all mails from the table
    @Query("DELETE FROM mail")
    void clearAllMails();
}
