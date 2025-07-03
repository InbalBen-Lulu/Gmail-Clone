package com.example.mail_app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * Cross-reference entity connecting a mail to a recipient (many-to-many).
 */
@Entity(
        tableName = "mail_recipient_cross_ref",
        primaryKeys = {"mailId", "userId"}
)
public class MailRecipientCrossRef {
    @NonNull
    private String mailId;

    @NonNull
    private String userId;

    public MailRecipientCrossRef(@NonNull String mailId, @NonNull String userId) {
        this.mailId = mailId;
        this.userId = userId;
    }

    /** Returns the mail ID. */
    @NonNull
    public String getMailId() {
        return mailId;
    }

    public void setMailId(@NonNull String mailId) {
        this.mailId = mailId;
    }

    /** Returns the recipient user ID. */
    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }
}
