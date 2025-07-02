package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mail_app.data.entity.PublicUser;

import java.util.List;

@Dao
public interface PublicUserDao {

    @Query("SELECT * FROM public_users WHERE userId = :userId")
    PublicUser getById(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PublicUser user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PublicUser> users);

    @Query("DELETE FROM public_users")
    void clearAllUesrs();
}
