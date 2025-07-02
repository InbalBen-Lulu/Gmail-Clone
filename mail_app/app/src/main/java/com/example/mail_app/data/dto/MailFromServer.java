package com.example.mail_app.data.dto;

import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.PublicUser;

import java.util.Date;
import java.util.List;
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

    // Getters
    public String getId() { return id; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public Date getSentAt() { return sentAt; }
    public PublicUser getFromUser() { return from; }
    public List<String> getTo() { return to; }
    public List<Label> getLabels() { return labels; }
    public boolean isStar() { return isStar; }
    public boolean isDraft() { return isDraft; }
    public boolean isSpam() { return isSpam; }
    public boolean isRead() { return isRead; }
    public String getType() { return type; }

    // Setters
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

    public FullMail toFullMail() {
        FullMail fullMail = new FullMail();

        // יצירת mail entity
        Mail mail = new Mail(
                this.getId(),
                this.getFromUser().getUserId(), // פה את לוקחת רק את ה־userId
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

        // הגדרת מזהי נמענים
        fullMail.setToUserIds(this.getTo()); // בהנחה שזה List<String>

        // הגדרת תוויות (אם יש)
        fullMail.setLabels(this.getLabels()); // בהנחה שזה List<Label>

        return fullMail;
    }

}
