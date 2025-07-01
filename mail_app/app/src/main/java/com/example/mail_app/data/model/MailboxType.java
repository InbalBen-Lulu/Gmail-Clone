package com.example.mail_app.data.model;

public enum MailboxType {
    INBOX("inbox"),
    SENT("sent"),
    DRAFTS("drafts"),
    SPAM("spam"),
    STARRED("starred"),
    ALL("allmails");

    private final String path;

    MailboxType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}