package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.mail_app.data.entity.LoggedInUser;

/**
 * DAO for accessing and modifying the logged-in user's data in Room.
 */
@Dao
public interface LoggedInUserDao {

    /**
     * Retrieves the currently logged-in user (only one allowed).
     */
    @Query("SELECT * FROM logged_in_user LIMIT 1")
    LoggedInUser get();

    /**
     * Inserts or updates the logged-in user in the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LoggedInUser user);

    /**
     * Clears the logged-in user (used on logout).
     */
    @Query("DELETE FROM logged_in_user")
    void clear();
}
