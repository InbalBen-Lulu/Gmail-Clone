package com.example.mail_app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"mailId", "userId"})
public class MailRecipientCrossRef {
    @NonNull
    private String mailId;

    @NonNull
    private String userId;

    public MailRecipientCrossRef(@NonNull String mailId, @NonNull String userId) {
        this.mailId = mailId;
        this.userId = userId;
    }

    @NonNull
    public String getMailId() {
        return mailId;
    }

    public void setMailId(@NonNull String mailId) {
        this.mailId = mailId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }
}
