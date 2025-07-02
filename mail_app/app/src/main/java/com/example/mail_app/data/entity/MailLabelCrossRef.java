package com.example.mail_app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

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

    @NonNull
    public String getMailId() {
        return mailId;
    }

    public void setMailId(@NonNull String mailId) {
        this.mailId = mailId;
    }

    @NonNull
    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(@NonNull String labelId) {
        this.labelId = labelId;
    }
}
