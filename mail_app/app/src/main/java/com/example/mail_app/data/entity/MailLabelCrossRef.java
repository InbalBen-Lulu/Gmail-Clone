package com.example.mail_app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * Cross-reference entity connecting a mail with a label (many-to-many).
 */
@Entity(
        tableName = "mail_label_cross_ref",
        primaryKeys = {"mailId", "labelId"}
)
public class MailLabelCrossRef {
    @NonNull
    private String mailId;

    @NonNull
    private String labelId;

    public MailLabelCrossRef(@NonNull String mailId, @NonNull String labelId) {
        this.mailId = mailId;
        this.labelId = labelId;
    }

    /** Returns the mail ID. */
    @NonNull
    public String getMailId() {
        return mailId;
    }

    public void setMailId(@NonNull String mailId) {
        this.mailId = mailId;
    }

    /** Returns the label ID. */
    @NonNull
    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(@NonNull String labelId) {
        this.labelId = labelId;
    }
}
