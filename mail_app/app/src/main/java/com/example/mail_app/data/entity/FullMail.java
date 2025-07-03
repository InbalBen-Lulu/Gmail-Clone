package com.example.mail_app.data.entity;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a full mail entity including sender, recipients, and labels.
 * Combines multiple Room relationships into a single structure.
 */
public class FullMail {

    @Embedded
    private Mail mail;

    @Relation(
            parentColumn = "from",
            entityColumn = "userId"
    )
    private PublicUser fromUser;

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

    /** Returns the mail entity. */
    public Mail getMail() { return mail; }

    public void setMail(Mail mail) { this.mail = mail; }

    /** Returns the sender of the mail. */
    public PublicUser getFromUser() { return fromUser; }

    public void setFromUser(PublicUser fromUser) { this.fromUser = fromUser; }

    /** Returns the list of recipient user IDs. */
    public List<String> getToUserIds() { return toUserIds; }

    public void setToUserIds(List<String> toUserIds) { this.toUserIds = toUserIds; }

    /** Returns the list of labels attached to the mail. */
    public List<Label> getLabels() { return labels; }

    public void setLabels(List<Label> labels) { this.labels = labels; }

    /** Returns cross-references to recipients for Room. */
    public List<MailRecipientCrossRef> getRecipientRefs() {
        List<MailRecipientCrossRef> refs = new ArrayList<>();
        if (toUserIds != null) {
            for (String userId : toUserIds) {
                refs.add(new MailRecipientCrossRef(mail.getId(), userId));
            }
        }
        return refs;
    }

    /** Returns cross-references to labels for Room. */
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
