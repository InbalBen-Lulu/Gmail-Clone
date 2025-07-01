package com.example.mail_app.data.dto;

import com.example.mail_app.data.entity.Label;
import java.util.Date;
import java.util.List;

public class MailFromServer {
    public String id;
    public String subject;
    public String body;
    public Date sentAt;
    public FromUser from;
    public List<String> to;
    public List<Label> labels;
    public boolean isStar;
    public boolean isDraft;
    public boolean isSpam;
    public boolean isRead;
    public String type;

    public static class FromUser {
        public String userId;
        public String name;
        public String profileImage;
    }
}