package com.example.mail_app.data.dto;

import java.util.Date;

/**
 * DTO used for registering a new user.
 */
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

    /** Returns the user ID. */
    public String getUserId() {
        return userId;
    }

    /** Returns the password. */
    public String getPassword() {
        return password;
    }

    /** Returns the user's name. */
    public String getName() {
        return name;
    }

    /** Returns the user's gender. */
    public String getGender() {
        return gender;
    }

    /** Returns the user's birth date. */
    public Date getBirthDate() {
        return birthDate;
    }
}
