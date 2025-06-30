package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.mail_app.data.entity.Label;

import java.util.List;

@Dao
public interface LabelDao {
    @Insert
    void insertLabel(Label label);

    @Update
    void updateLabel(Label label);

    @Delete
    void deleteLabel(Label label);

    @Query("SELECT * FROM Label WHERE userId = :userId")
    List<Label> getLabelsForUser(String userId);
}
