package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mail_app.data.entity.Label;

import java.util.List;

@Dao
public interface LabelDao {
    @Query("SELECT * FROM label")
    List<Label> getAll();

    @Query("SELECT * FROM label WHERE id = :id")
    Label getById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Label> labels);

    @Query("DELETE FROM label")
    void clear();
}

