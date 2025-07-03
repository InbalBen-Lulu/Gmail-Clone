package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.mail_app.data.entity.Label;
import java.util.List;

/**
 * Data Access Object for managing label entities in the local Room database.
 */
@Dao
public interface LabelDao {

    /**
     * Retrieves all labels from the database.
     */
    @Query("SELECT * FROM label")
    List<Label> getAll();

    /**
     * Retrieves a label by its ID.
     */
    @Query("SELECT * FROM label WHERE id = :id")
    Label getById(String id);

    /**
     * Inserts a list of labels into the database (replaces on conflict).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Label> labels);

    /**
     * Deletes all labels from the database.
     */
    @Query("DELETE FROM label")
    void clear();
}
