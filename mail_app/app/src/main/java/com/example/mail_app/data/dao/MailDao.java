package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.MailLabelCrossRef;
import com.example.mail_app.data.entity.MailRecipientCrossRef;
import java.util.List;

/**
 * DAO for managing mail data, including relationships with recipients and labels.
 * Supports filtering, searching, insertion, and cleanup logic for Room database.
 */
@Dao
public interface MailDao {

    /**
     * Retrieves a mail by its ID, including recipients and labels.
     */
    @Transaction
    @Query("SELECT * FROM mail WHERE id = :mailId")
    FullMail getMailById(String mailId);

    /**
     * Retrieves all non-spam mails from the database.
     */
    @Transaction
    @Query("SELECT * FROM mail WHERE isSpam = 0")
    List<FullMail> getAllMails();

    /**
     * Retrieves received (inbox) mails that are not spam.
     */
    @Transaction
    @Query("SELECT * FROM mail WHERE type = 'received' AND isSpam = 0")
    List<FullMail> getInboxMails();

    /**
     * Retrieves all sent mails that are not drafts.
     */
    @Transaction
    @Query("SELECT * FROM mail WHERE type = 'sent' AND isDraft = 0")
    List<FullMail> getSentMails();

    /**
     * Retrieves all draft mails.
     */
    @Transaction
    @Query("SELECT * FROM mail WHERE isDraft = 1")
    List<FullMail> getDraftMails();

    /**
     * Retrieves all spam mails.
     */
    @Transaction
    @Query("SELECT * FROM mail WHERE isSpam = 1")
    List<FullMail> getSpamMails();

    /**
     * Retrieves all starred (non-spam) mails.
     */
    @Transaction
    @Query("SELECT * FROM mail WHERE isStar = 1")
    List<FullMail> getStarredMails();

    /**
     * Searches mails by subject, body, sender, sender name, or recipient ID.
     */
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

    /**
     * Retrieves all mails with a specific label ID.
     */
    @Transaction
    @Query("SELECT * FROM mail " +
            "INNER JOIN mail_label_cross_ref ON mail.id = mail_label_cross_ref.mailId " +
            "WHERE mail_label_cross_ref.labelId = :labelId")
    List<FullMail> getMailsByLabel(String labelId);

    /**
     * Inserts a single mail into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMail(Mail mail);

    /**
     * Updates an existing mail.
     */
    @Update
    void updateMail(Mail mail);

    /**
     * Deletes a mail by its ID.
     */
    @Query("DELETE FROM mail WHERE id = :mailId")
    void deleteMailById(String mailId);

    /**
     * Deletes all recipients (TO users) for a specific mail.
     */
    @Query("DELETE FROM mail_recipient_cross_ref WHERE mailId = :mailId")
    void deleteRecipientsByMailId(String mailId);

    /**
     * Deletes all labels from a mail.
     */
    @Query("DELETE FROM mail_label_cross_ref WHERE mailId = :mailId")
    void deleteLabelsByMailId(String mailId);

    /**
     * Sends a draft mail by marking it as not a draft and adding a send timestamp.
     */
    @Query("UPDATE mail SET isDraft = 0, sentAt = :timestamp WHERE id = :mailId")
    void sendDraftMail(String mailId, java.util.Date timestamp);

    /**
     * Toggles the star status (isStar) of a mail.
     */
    @Query("UPDATE mail SET isStar = NOT isStar WHERE id = :mailId")
    void toggleStar(String mailId);

    /**
     * Toggles the spam status (isSpam) of a mail.
     */
    @Query("UPDATE mail SET isSpam = NOT isSpam WHERE id = :mailId")
    void setSpam(String mailId);

    /**
     * Marks a mail as read.
     */
    @Query("UPDATE mail SET isRead = 1 WHERE id = :mailId")
    void markAsRead(String mailId);

    /**
     * Inserts recipients of a mail (TO users).
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRecipients(List<MailRecipientCrossRef> recipients);

    /**
     * Inserts a label attached to a mail.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertLabelToMail(MailLabelCrossRef labelRef);

    /**
     * Removes a specific label from a mail.
     */
    @Query("DELETE FROM mail_label_cross_ref WHERE mailId = :mailId AND labelId = :labelId")
    void removeLabelFromMail(String mailId, String labelId);

    /**
     * Clears all mails from the database.
     */
    @Query("DELETE FROM mail")
    void clearAllMails();
}
