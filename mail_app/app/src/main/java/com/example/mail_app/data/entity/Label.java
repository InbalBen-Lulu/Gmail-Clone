package com.example.mail_app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Label {
    @PrimaryKey
    @NonNull
    private String id;

    private String userId;
    private String name;
    private String color;

    public Label(@NonNull String id, String userId, String name, String color) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.color = color;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
