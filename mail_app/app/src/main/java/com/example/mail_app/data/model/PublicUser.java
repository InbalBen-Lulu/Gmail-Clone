package com.example.mail_app.data.model;

public class PublicUser {
    private String userId;
    private String name;
    private String profileImage;
    private boolean hasCustomImage;

    public PublicUser(String userId, String name, String profileImage, boolean hasCustomImage) {
        this.userId = userId;
        this.name = name;
        this.profileImage = profileImage;
        this.hasCustomImage = hasCustomImage;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public boolean hasCustomImage() { return hasCustomImage; }
    public void setHasCustomImage(boolean hasCustomImage) { this.hasCustomImage = hasCustomImage; }
}
