package com.example.mail_app.data.dto;

import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.PublicUser;

import java.util.Date;
import java.util.List;

/**
 * Data Transfer Object representing a mail fetched from the server.
 * Contains sender, recipients, labels, and mail metadata.
 */
public class MailFromServer {
    private String id;
    private String subject;
    private String body;
    private Date sentAt;
    private PublicUser from;
    private List<String> to;
    private List<Label> labels;
    private boolean isStar;
    private boolean isDraft;
    private boolean isSpam;
    private boolean isRead;
    private String type;

    // -------------------- Getters --------------------

    /** @return ID of the mail */
    public String getId() { return id; }

    /** @return Subject line of the mail */
    public String getSubject() { return subject; }

    /** @return Body content of the mail */
    public String getBody() { return body; }

    /** @return Date and time the mail was sent */
    public Date getSentAt() { return sentAt; }

    /** @return Sender user (PublicUser) */
    public PublicUser getFromUser() { return from; }

    /** @return List of recipient user IDs */
    public List<String> getTo() { return to; }

    /** @return List of labels attached to the mail */
    public List<Label> getLabels() { return labels; }

    /** @return True if the mail is starred */
    public boolean isStar() { return isStar; }

    /** @return True if the mail is a draft */
    public boolean isDraft() { return isDraft; }

    /** @return True if the mail is marked as spam */
    public boolean isSpam() { return isSpam; }

    /** @return True if the mail was read */
    public boolean isRead() { return isRead; }

    /** @return Mail type: 'received', 'sent', etc. */
    public String getType() { return type; }

    // -------------------- Setters --------------------

    public void setId(String id) { this.id = id; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setBody(String body) { this.body = body; }
    public void setSentAt(Date sentAt) { this.sentAt = sentAt; }
    public void setFrom(PublicUser from) { this.from = from; }
    public void setTo(List<String> to) { this.to = to; }
    public void setLabels(List<Label> labels) { this.labels = labels; }
    public void setStar(boolean star) { isStar = star; }
    public void setDraft(boolean draft) { isDraft = draft; }
    public void setSpam(boolean spam) { isSpam = spam; }
    public void setRead(boolean read) { isRead = read; }
    public void setType(String type) { this.type = type; }

    /**
     * Converts this DTO into a FullMail entity used in Room database.
     * Includes basic mail data, labels, and recipients.
     *
     * @return FullMail object containing local data representation
     */
    public FullMail toFullMail() {
        FullMail fullMail = new FullMail();

        Mail mail = new Mail(
                this.getId(),
                this.getFromUser().getUserId(),
                this.getSubject(),
                this.getBody(),
                this.getSentAt(),
                this.getType(),
                this.isDraft(),
                this.isSpam(),
                this.isStar(),
                this.isRead()
        );

        fullMail.setMail(mail);
        fullMail.setToUserIds(this.getTo());
        fullMail.setLabels(this.getLabels());
        fullMail.setFromUser(this.getFromUser());

        return fullMail;
    }
}
