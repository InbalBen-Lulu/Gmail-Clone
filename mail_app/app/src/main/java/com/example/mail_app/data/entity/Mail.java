package com.example.mail_app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Represents a mail entity stored locally in Room.
 */
@Entity(tableName = "mail")
public class Mail {
    @PrimaryKey
    @NonNull
    private String id;

    private String from;
    private String subject;
    private String body;
    private Date sentAt;

    private String type;
    private boolean isDraft;
    private boolean isSpam;
    private boolean isStar;
    private boolean isRead;

    public Mail(@NonNull String id, String from, String subject, String body, Date sentAt,
                String type, boolean isDraft, boolean isSpam,
                boolean isStar, boolean isRead) {
        this.id = id;
        this.from = from;
        this.subject = subject;
        this.body = body;
        this.sentAt = sentAt;
        this.type = type;
        this.isDraft = isDraft;
        this.isSpam = isSpam;
        this.isStar = isStar;
        this.isRead = isRead;
    }

    /** Returns the mail ID. */
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    /** Returns the sender user ID. */
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    /** Returns the subject of the mail. */
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    /** Returns the body of the mail. */
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    /** Returns the send time of the mail. */
    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    /** Returns the mail type (sent/received). */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /** Returns true if the mail is a draft. */
    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }

    /** Returns true if the mail is marked as spam. */
    public boolean isSpam() {
        return isSpam;
    }

    public void setSpam(boolean spam) {
        isSpam = spam;
    }

    /** Returns true if the mail is starred. */
    public boolean isStar() {
        return isStar;
    }

    public void setStar(boolean star) {
        isStar = star;
    }

    /** Returns true if the mail has been read. */
    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
