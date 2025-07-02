package com.example.mail_app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

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

    // Getters + Setters
    @NonNull public String getUserId() { return userId; }

    public String getName() { return name; }

    public String getGender() { return gender; }

    public Date getBirthDate() { return birthDate; }

    public String getProfileImage() { return profileImage; }

    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public boolean hasCustomImage() { return hasCustomImage; }

    public void setHasCustomImage(boolean hasCustomImage) { this.hasCustomImage = hasCustomImage; }
}
