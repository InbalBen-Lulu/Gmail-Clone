package com.example.mail_app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a public user (sender) associated with a mail.
 */
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

    /** Returns the user ID. */
    @NonNull public String getUserId() { return userId; }

    /** Returns the user's full name. */
    public String getName() { return name; }

    /** Returns the user's profile image URL. */
    public String getProfileImage() { return profileImage; }
}
