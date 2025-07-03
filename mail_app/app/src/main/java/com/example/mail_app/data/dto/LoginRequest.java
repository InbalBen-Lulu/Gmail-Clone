package com.example.mail_app.data.dto;

/**
 * Data Transfer Object (DTO) used for sending login credentials to the server.
 */
public class LoginRequest {
    private String userId;
    private String password;

    public LoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

}
