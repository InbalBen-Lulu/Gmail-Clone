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
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getProfileImage() { return profileImage; }
}
