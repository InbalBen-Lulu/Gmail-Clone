package com.example.mail_app.data.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO representing a paginated mail list response from the server.
 * Includes total count and list of mails.
 */
public class MailListResponse {
    private int total;
    private List<MailFromServer> mails;

    /** @return Total number of mails available on the server */
    public int getTotal() {
        return total;
    }

    /** @return List of mails in the current page */
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
