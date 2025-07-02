package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;

import com.example.mail_app.data.entity.*;

import java.util.List;

@Dao
public interface MailDao {

    @Query("DELETE FROM Mail")
    void clear();

    @Query("SELECT * FROM Mail WHERE id = :mailId")
    Mail getMailById(String mailId);

    @Transaction
    @Query("SELECT * FROM Mail WHERE id = :mailId")
    MailWithRecipientsAndLabels getMailWithRecipientsAndLabels(String mailId);

    // All mail
    @Transaction
    @Query("SELECT * FROM Mail")
    List<MailWithRecipientsAndLabels> getAllMailsWithRecipientsAndLabels();

    // Inbox (not draft/spam, type == 'received')
    @Transaction
    @Query("SELECT * FROM Mail WHERE isDraft = 0 AND isSpam = 0 AND type = 'received'")
    List<MailWithRecipientsAndLabels> getInboxMails();

    // Sent (not draft, type == 'sent')
    @Transaction
    @Query("SELECT * FROM Mail WHERE isDraft = 0 AND type = 'sent'")
    List<MailWithRecipientsAndLabels> getSentMails();

    // Drafts
    @Transaction
    @Query("SELECT * FROM Mail WHERE isDraft = 1")
    List<MailWithRecipientsAndLabels> getDraftMails();

    // Spam
    @Transaction
    @Query("SELECT * FROM Mail WHERE isSpam = 1")
    List<MailWithRecipientsAndLabels> getSpamMails();

    // Starred
    @Transaction
    @Query("SELECT * FROM Mail WHERE isStar = 1")
    List<MailWithRecipientsAndLabels> getStarredMails();

    // Filter by label
    @Transaction
    @Query("""
        SELECT Mail.* FROM Mail
        INNER JOIN MailLabelCrossRef ON Mail.id = MailLabelCrossRef.mailId
        WHERE MailLabelCrossRef.labelId = :labelId
    """)
    List<MailWithRecipientsAndLabels> getMailsByLabel(String labelId);

    // Search by subject, body, recipient name/email
    @Transaction
    @Query("""
        SELECT DISTINCT Mail.* FROM Mail
        LEFT JOIN MailRecipientCrossRef ON Mail.id = MailRecipientCrossRef.mailId
        LEFT JOIN users ON MailRecipientCrossRef.userId = users.userId
        WHERE
            Mail.subject LIKE '%' || :query || '%' OR
            Mail.body LIKE '%' || :query || '%' OR
            users.name LIKE '%' || :query || '%' OR
            users.userId LIKE '%' || :query || '%'
    """)
    List<MailWithRecipientsAndLabels> searchMails(String query);
}
