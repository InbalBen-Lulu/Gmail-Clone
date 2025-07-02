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

    @NonNull public String getId() { return id; }

    public String getUserId() { return userId; }

    public String getName() { return name; }

    public String getColor() { return color; }

}
