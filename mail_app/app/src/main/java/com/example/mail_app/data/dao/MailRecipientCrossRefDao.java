package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;

import com.example.mail_app.data.entity.MailRecipientCrossRef;

@Dao
public interface MailRecipientCrossRefDao {
    @Insert
    void insertMailRecipientCrossRef(MailRecipientCrossRef crossRef);

    @Delete
    void deleteMailRecipientCrossRef(MailRecipientCrossRef crossRef);
}
