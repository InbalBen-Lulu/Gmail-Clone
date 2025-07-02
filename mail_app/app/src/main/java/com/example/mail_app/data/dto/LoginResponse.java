package com.example.mail_app.data.dto;

import com.example.mail_app.data.entity.LoggedInUser;

/**
 * DTO representing the server response to a login request.
 * Includes the JWT token and the user data.
 */
public class LoginResponse {
    private String token;
    private LoggedInUser user;

    public String getToken() {
        return token;
    }

    public LoggedInUser getUser() {
        return user;
    }
}
