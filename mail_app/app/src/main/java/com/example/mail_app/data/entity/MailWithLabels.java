package com.example.mail_app.data.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class MailWithLabels {
    @Embedded
    private Mail mail;

    @Relation(
            parentColumn = "id", // Mail.id
            entityColumn = "id", // Label.id
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

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }
}
