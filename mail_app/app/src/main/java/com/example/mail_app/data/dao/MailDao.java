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

@Dao
public interface MailDao {

    // שליפת מייל לפי ID כולל נמענים ותוויות
    @Transaction
    @Query("SELECT * FROM mail WHERE id = :mailId")
    FullMail getMailById(String mailId);

    // שליפת כל המיילים כולל TO + LABELS
    @Transaction
    @Query("SELECT * FROM mail WHERE isSpam = 0")
    List<FullMail> getAllMails();

    // שליפת מיילים לפי מצב (inbox, sent, drafts וכו')
    @Transaction
    @Query("SELECT * FROM mail WHERE type = 'received' AND isSpam = 0")
    List<FullMail> getInboxMails();

    @Transaction
    @Query("SELECT * FROM mail WHERE type = 'sent' AND isDraft = 0")
    List<FullMail> getSentMails();

    @Transaction
    @Query("SELECT * FROM mail WHERE isDraft = 1")
    List<FullMail> getDraftMails();

    @Transaction
    @Query("SELECT * FROM mail WHERE isSpam = 1")
    List<FullMail> getSpamMails();

    @Transaction
    @Query("SELECT * FROM mail WHERE isStar = 1")
    List<FullMail> getStarredMails();

    // חיפוש טקסטואלי לפי subject או body
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

    // שליפת מיילים לפי labelId
    @Transaction
    @Query("SELECT * FROM mail " +
            "INNER JOIN mail_label_cross_ref ON mail.id = mail_label_cross_ref.mailId " +
            "WHERE mail_label_cross_ref.labelId = :labelId")
    List<FullMail> getMailsByLabel(String labelId);

    // הוספת מייל (רק את הישות עצמה)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMail(Mail mail);

    // עדכון מייל (רק את הישות עצמה)
    @Update
    void updateMail(Mail mail);

    // מחיקת מייל לפי מזהה
    @Query("DELETE FROM mail WHERE id = :mailId")
    void deleteMailById(String mailId);

    // מחיקת הנמענים (TO) של מייל
    @Query("DELETE FROM mail_recipient_cross_ref WHERE mailId = :mailId")
    void deleteRecipientsByMailId(String mailId);

    // מחיקת התוויות של מייל
    @Query("DELETE FROM mail_label_cross_ref WHERE mailId = :mailId")
    void deleteLabelsByMailId(String mailId);

    // שליחת טיוטה → שינוי isDraft=false
    @Query("UPDATE mail SET isDraft = 0, sentAt = :timestamp WHERE id = :mailId")
    void sendDraftMail(String mailId, java.util.Date timestamp);

    // סימון/ביטול כוכב
    @Query("UPDATE mail SET isStar = NOT isStar WHERE id = :mailId")
    void toggleStar(String mailId);

    // סימון ספאם
    @Query("UPDATE mail SET isSpam = NOT isSpam WHERE id = :mailId")
    void setSpam(String mailId);

    // סימון קריאה
    @Query("UPDATE mail SET isRead = 1 WHERE id = :mailId")
    void markAsRead(String mailId);

    // הוספת נמענים (to)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipients(List<MailRecipientCrossRef> recipients);

    // הוספת תווית
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLabelToMail(MailLabelCrossRef labelRef);

    // הסרת תווית
    @Query("DELETE FROM mail_label_cross_ref WHERE mailId = :mailId AND labelId = :labelId")
    void removeLabelFromMail(String mailId, String labelId);

    @Query("SELECT id FROM mail WHERE isSpam = 0 AND type = 'received' ORDER BY sentAt DESC LIMIT :limit")
    List<String> getRecentInboxMailIds(int limit);

    @Query("SELECT id FROM mail WHERE isDraft = 1 ORDER BY sentAt DESC LIMIT :limit")
    List<String> getRecentDraftMailIds(int limit);

    @Query("SELECT id FROM mail WHERE isSpam = 1 ORDER BY sentAt DESC LIMIT :limit")
    List<String> getRecentSpamMailIds(int limit);

    @Query("SELECT id FROM mail WHERE isStar = 1 AND isSpam = 0 ORDER BY sentAt DESC LIMIT :limit")

    List<String> getRecentStarredMailIds(int limit);

    @Query("SELECT id FROM mail WHERE type = 'sent' AND isDraft = 0 ORDER BY sentAt DESC LIMIT :limit")
    List<String> getRecentSentMailIds(int limit);

    @Query("DELETE FROM mail WHERE id NOT IN (:ids)")
    void deleteMailsNotIn(List<String> ids);

    @Query("DELETE FROM mail")
    void clearAllMails();
}
