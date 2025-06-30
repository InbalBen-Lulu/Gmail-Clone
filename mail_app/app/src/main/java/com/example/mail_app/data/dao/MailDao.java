package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.mail_app.data.entity.*;

import java.util.List;

@Dao
public interface MailDao {
    @Insert
    void insertMail(Mail mail);

    @Update
    void updateMail(Mail mail);

    @Delete
    void deleteMail(Mail mail);

    @Query("SELECT * FROM Mail WHERE id = :mailId")
    Mail getMailById(String mailId);

    @Transaction
    @Query("SELECT * FROM Mail WHERE id = :mailId")
    MailWithRecipientsAndLabels getMailWithRecipientsAndLabels(String mailId);

    @Transaction
    @Query("SELECT * FROM Mail")
    List<MailWithRecipientsAndLabels> getAllMailsWithRecipientsAndLabels();
}
