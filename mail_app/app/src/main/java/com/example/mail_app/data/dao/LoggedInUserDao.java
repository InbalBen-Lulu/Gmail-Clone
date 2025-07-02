package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mail_app.data.entity.LoggedInUser;

@Dao
public interface LoggedInUserDao {

    @Query("SELECT * FROM logged_in_user LIMIT 1")
    LoggedInUser get();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LoggedInUser user);

    @Query("DELETE FROM logged_in_user")
    void clear();
}
