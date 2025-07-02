package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mail_app.data.entity.MailLabelCrossRef;

@Dao
public interface MailLabelCrossRefDao {
    @Insert
    void insertMailLabelCrossRef(MailLabelCrossRef crossRef);

    @Query("DELETE FROM MailLabelCrossRef WHERE mailId = :mailId")
    void deleteByMailId(String mailId);
}
