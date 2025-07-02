package com.example.mail_app.data.entity;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

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

    @Relation(
            parentColumn = "id",
            entityColumn = "mailId"
    )
    private List<MailRecipientCrossRef> recipientRefs;

    @Ignore
    private List<String> toUserIds;

    public Mail getMail() { return mail; }
    public void setMail(Mail mail) { this.mail = mail; }

    public PublicUser getFromUser() { return fromUser; }
    public void setFromUser(PublicUser fromUser) { this.fromUser = fromUser; }

    public List<Label> getLabels() { return labels; }
    public void setLabels(List<Label> labels) { this.labels = labels; }

    public List<MailRecipientCrossRef> getRecipientRefs() { return recipientRefs; }
    public void setRecipientRefs(List<MailRecipientCrossRef> recipientRefs) { this.recipientRefs = recipientRefs; }

    public List<String> getToUserIds() {
        if (toUserIds == null && recipientRefs != null) {
            toUserIds = new ArrayList<>();
            for (MailRecipientCrossRef ref : recipientRefs) {
                toUserIds.add(ref.getUserId());
            }
        }
        return toUserIds;
    }

    public void setToUserIds(List<String> toUserIds) { this.toUserIds = toUserIds; }

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
