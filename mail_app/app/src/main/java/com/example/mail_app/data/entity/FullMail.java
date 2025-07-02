package com.example.mail_app.data.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class FullMail {

    @Embedded
    private Mail mail;

    // שדה from כ־PublicUser
    @Relation(
            parentColumn = "from",
            entityColumn = "userId"
    )
    private PublicUser fromUser;

    // תוויות
    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @androidx.room.Junction(
                    value = MailLabelCrossRef.class,
                    parentColumn = "mailId",
                    entityColumn = "labelId"
            )
    )
    private List<Label> labels;

    private List<String> toUserIds;

    // --- Getters & Setters ---

    public Mail getMail() { return mail; }

    public void setMail(Mail mail) { this.mail = mail; }

    public PublicUser getFromUser() { return fromUser; }

    public void setFromUser(PublicUser fromUser) { this.fromUser = fromUser; }

    public List<String> getToUserIds() { return toUserIds; }

    public void setToUserIds(List<String> toUserIds) { this.toUserIds = toUserIds; }

    public List<Label> getLabels() { return labels; }

    public void setLabels(List<Label> labels) { this.labels = labels; }

    public List<MailRecipientCrossRef> getRecipientRefs() {
        List<MailRecipientCrossRef> refs = new ArrayList<>();
        if (toUserIds != null) {
            for (String userId : toUserIds) {
                refs.add(new MailRecipientCrossRef(mail.getId(), userId));
            }
        }
        return refs;
    }

    public List<MailLabelCrossRef> getLabelRefs() {
        List<MailLabelCrossRef> refs = new ArrayList<>();
        if (labels != null) {
            for (Label label : labels) {
                refs.add(new MailLabelCrossRef(mail.getId(), label.getId()));
            }
        }
        return refs;
    }
}
