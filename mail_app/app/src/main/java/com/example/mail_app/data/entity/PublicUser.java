package com.example.mail_app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "public_users")
public class PublicUser {
    @PrimaryKey
    @NonNull
    private String userId;
    private String name;
    private String profileImage;

    public PublicUser(@NonNull String userId, String name, String profileImage) {
        this.userId = userId;
        this.name = name;
        this.profileImage = profileImage;
    }

    // Getters + Setters
    @NonNull public String getUserId() { return userId; }

    public String getName() { return name; }

    public String getProfileImage() { return profileImage; }
}
