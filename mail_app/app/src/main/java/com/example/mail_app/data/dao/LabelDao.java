package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;
import androidx.room.OnConflictStrategy;

import com.example.mail_app.data.entity.Label;

import java.util.List;

@Dao
public interface LabelDao {
    @Query("SELECT * FROM Label")
    List<Label> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Label> labels);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Label label);

    @Update
    void update(Label label);

    @Delete
    void delete(Label label);

    @Query("DELETE FROM Label")
    void clear();
}
