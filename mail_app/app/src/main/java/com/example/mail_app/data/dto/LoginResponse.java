package com.example.mail_app.data.dto;

import com.example.mail_app.data.entity.LoggedInUser;

public class LoginResponse {
    private String token;
    private LoggedInUser user;

    public String getToken() { return token; }
    public LoggedInUser getUser() { return user; }
}
