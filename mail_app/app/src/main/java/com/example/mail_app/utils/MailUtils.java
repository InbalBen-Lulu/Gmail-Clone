package com.example.mail_app.utils;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility functions related to mail display and formatting.
 */
public class MailUtils {

    /**
     * Formats a Date object to a human-readable string depending on how recent it is:
     * - Today → returns hour and minute (e.g. "14:35")
     * - Same year → returns day and month (e.g. "21 Jul")
     * - Different year → returns full date (e.g. "21 Jul 2023")
     *
     * @param date Date to format
     * @return formatted date string
     */
    public static String formatMailDate(Date date) {
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        then.setTime(date);

        // Check if the date is today
        boolean isToday = now.get(Calendar.YEAR) == then.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR);

        if (isToday) {
            return new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date);
        }

        // Check if the date is within the same year
        boolean isSameYear = now.get(Calendar.YEAR) == then.get(Calendar.YEAR);
        SimpleDateFormat formatter = isSameYear
                ? new SimpleDateFormat("d MMM", Locale.ENGLISH)
                : new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);

        return formatter.format(date);
    }

    /**
     * Creates a rounded colored Drawable based on the given hex color.
     * Used to style label chips (tags).
     *
     * @param colorHex Hex string representing a color (e.g. "#FF0000")
     * @return Drawable with rounded corners and given color
     */
    public static Drawable getRoundedLabelDrawable(String colorHex) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(colorHex)); // Apply the given color
        drawable.setCornerRadius(10f); // Rounded corners
        return drawable;
    }
}
