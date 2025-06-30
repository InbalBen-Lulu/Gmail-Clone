package com.example.mail_app.data.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class MailWithRecipients {
    @Embedded
    private Mail mail;

    @Relation(
            parentColumn = "id",
            entityColumn = "userId",
            associateBy = @Junction(MailRecipientCrossRef.class)
    )
    private List<User> recipients;

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public List<User> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<User> recipients) {
        this.recipients = recipients;
    }
}
