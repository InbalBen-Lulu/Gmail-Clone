package com.example.mail_app.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MailUtils {
    public static String formatMailDate(Date date) {
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        then.setTime(date);

        boolean isToday = now.get(Calendar.YEAR) == then.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR);

        if (isToday) {
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
        }

        boolean isSameYear = now.get(Calendar.YEAR) == then.get(Calendar.YEAR);
        SimpleDateFormat formatter = isSameYear
                ? new SimpleDateFormat("d MMM", Locale.getDefault())
                : new SimpleDateFormat("d MMM yyyy", Locale.getDefault());

        return formatter.format(date);
    }
}
