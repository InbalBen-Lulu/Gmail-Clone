package com.example.mail_app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Mail {
    @PrimaryKey
    @NonNull
    private String id;

    private String from;
    private String subject;
    private String body;
    private Date sentAt;

    // MailStatus
    private String type;       // "sent" or "received"
    private boolean isDraft;
    private boolean isSpam;
    private boolean isStar;
    private boolean isRead;

    // Constructor
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

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }

    public boolean isSpam() {
        return isSpam;
    }

    public void setSpam(boolean spam) {
        isSpam = spam;
    }

    public boolean isStar() {
        return isStar;
    }

    public void setStar(boolean star) {
        isStar = star;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
