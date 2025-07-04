package com.example.mail_app.utils;

import android.content.Context;
import com.example.mail_app.R;

/**
 * Utility for building email addresses from user IDs.
 */
public class EmailUtils {

    /**
     * Builds a full email address using the given user ID and domain from strings.xml.
     */
    public static String buildEmail(Context context, String userId) {
        String domain = context.getString(R.string.email_domain);
        return userId + domain;
    }
}
