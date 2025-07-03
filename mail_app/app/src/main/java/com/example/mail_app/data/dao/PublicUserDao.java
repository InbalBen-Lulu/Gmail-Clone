package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mail_app.data.entity.PublicUser;

import java.util.List;

/**
 * DAO for managing public users (senders) stored locally.
 */
@Dao
public interface PublicUserDao {

    /**
     * Retrieves a public user by user ID.
     */
    @Query("SELECT * FROM public_users WHERE userId = :userId")
    PublicUser getById(String userId);

    /**
     * Inserts a single public user into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PublicUser user);

    /**
     * Inserts a list of public users into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PublicUser> users);

    /**
     * Clears all public users from the database.
     */
    @Query("DELETE FROM public_users")
    void clearAllUesrs();
}
