package com.example.mail_app.utils;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

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
            return new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date);
        }

        boolean isSameYear = now.get(Calendar.YEAR) == then.get(Calendar.YEAR);
        SimpleDateFormat formatter = isSameYear
                ? new SimpleDateFormat("d MMM", Locale.ENGLISH)
                : new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);

        return formatter.format(date);
    }

    public static Drawable getRoundedLabelDrawable(String colorHex) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(colorHex));
        drawable.setCornerRadius(10f);
        return drawable;
    }

}
