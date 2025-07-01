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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMail(Mail mail);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<Mail> mails);

    @Update
    void updateMail(Mail mail);

    @Delete
    void deleteMail(Mail mail);

    @Query("DELETE FROM Mail")
    void clear();

    @Query("SELECT * FROM Mail WHERE id = :mailId")
    Mail getMailById(String mailId);

    @Transaction
    @Query("SELECT * FROM Mail WHERE id = :mailId")
    MailWithRecipientsAndLabels getMailWithRecipientsAndLabels(String mailId);

    @Transaction
    @Query("SELECT * FROM Mail")
    List<MailWithRecipientsAndLabels> getAllMailsWithRecipientsAndLabels();
}