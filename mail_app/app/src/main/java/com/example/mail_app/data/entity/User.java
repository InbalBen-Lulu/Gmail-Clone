package com.example.mail_app.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import java.util.Date;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    private String userId;

    private String name;
    private String password;
    private String gender;
    private Date birthDate;
    private String profileImage;
    private boolean hasCustomImage;

    public User() {}

    public User(String userId, String name, String password, String gender, Date birthDate, String profileImage, boolean hasCustomImage) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
        this.hasCustomImage = hasCustomImage;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public boolean hasCustomImage() { return hasCustomImage; }
    public void setHasCustomImage(boolean hasCustomImage) { this.hasCustomImage = hasCustomImage; }
}
