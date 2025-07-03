package com.example.mail_app.data.dto;

import java.util.ArrayList;
import java.util.List;

public class MailListResponse {
    private int total;
    private List<MailFromServer> mails;

    public int getTotal() {
        return total;
    }

    public List<MailFromServer> getMails() {
        return mails;
    }

    public List<String> extractMailIds() {
        List<String> ids = new ArrayList<>();
        for (MailFromServer mail : mails) {
            ids.add(mail.getId());
        }
        return ids;
    }
}
