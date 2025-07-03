package com.example.mail_app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a label entity associated with a user.
 */
@Entity(tableName = "label")
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

    /** Returns the label ID. */
    @NonNull public String getId() { return id; }

    /** Returns the user ID who owns this label. */
    public String getUserId() { return userId; }

    /** Returns the name of the label. */
    public String getName() { return name; }

    /** Returns the color of the label. */
    public String getColor() { return color; }
}
