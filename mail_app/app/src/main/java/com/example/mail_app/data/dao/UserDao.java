package com.example.mail_app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.mail_app.data.entity.User;
import com.example.mail_app.data.model.PublicUser;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserById(String userId);

    @Query("SELECT userId, name, profileImage, hasCustomImage FROM users WHERE userId = :userId")
    PublicUser getPublicUserById(String userId);

    @Query("SELECT userId, name, profileImage, hasCustomImage FROM users")
    List<PublicUser> getAllPublicUsers();
}
