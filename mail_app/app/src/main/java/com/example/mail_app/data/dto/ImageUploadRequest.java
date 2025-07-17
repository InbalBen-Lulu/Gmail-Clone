package com.example.mail_app.data.dto;

/**
 * Data transfer object used to send a Base64-encoded profile image to the server.
 */
public class ImageUploadRequest {
    private String image;

    public ImageUploadRequest(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
