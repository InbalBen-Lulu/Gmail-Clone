package com.example.mail_app.data.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class MailWithRecipientsAndLabels {
    @Embedded
    private Mail mail;

    @Relation(
            parentColumn = "id",
            entityColumn = "userId",
            associateBy = @Junction(
                    value = MailRecipientCrossRef.class,
                    parentColumn = "mailId",
                    entityColumn = "userId"
            )
    )
    private List<PublicUser> recipients;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = MailLabelCrossRef.class,
                    parentColumn = "mailId",
                    entityColumn = "labelId"
            )
    )
    private List<Label> labels;

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public List<PublicUser> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<PublicUser> recipients) {
        this.recipients = recipients;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }
}
