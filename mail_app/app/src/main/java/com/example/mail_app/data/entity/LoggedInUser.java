package com.example.mail_app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Represents the currently logged-in user in the app.
 */
@Entity(tableName = "logged_in_user")
public class LoggedInUser {
    @PrimaryKey
    @NonNull
    private String userId;
    private String name;
    private String gender;
    private Date birthDate;
    private String profileImage;
    private boolean hasCustomImage;

    public LoggedInUser(@NonNull String userId, String name, String gender, Date birthDate,
                        String profileImage, boolean hasCustomImage) {
        this.userId = userId;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
        this.hasCustomImage = hasCustomImage;
    }

    /** Returns the user ID. */
    @NonNull public String getUserId() { return userId; }

    /** Returns the user's name. */
    public String getName() { return name; }

    /** Returns the user's gender. */
    public String getGender() { return gender; }

    /** Returns the user's birth date. */
    public Date getBirthDate() { return birthDate; }

    /** Returns the user's profile image URL. */
    public String getProfileImage() { return profileImage; }

    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    /** Returns true if the user has a custom image. */
    public boolean hasCustomImage() { return hasCustomImage; }

    public void setHasCustomImage(boolean hasCustomImage) { this.hasCustomImage = hasCustomImage; }
}
