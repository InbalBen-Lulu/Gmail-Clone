package com.example.mail_app.data.dto;

import java.util.Date;

public class RegisterRequest {
    private String userId;
    private String password;
    private String name;
    private String gender;
    private Date birthDate;

    public RegisterRequest(String userId, String password, String name, String gender, Date birthDate) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public Date getBirthDate() { return birthDate; }
}
