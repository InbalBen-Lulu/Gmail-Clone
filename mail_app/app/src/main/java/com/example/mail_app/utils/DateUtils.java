package com.example.mail_app.utils;

import android.content.Context;

import com.example.mail_app.R;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Utility class for validating and comparing dates.
 */
public class DateUtils {

    /**
     * Checks if a date is valid, exists on the calendar, and is not in the future.
     *
     * @param context   Android context (to access string arrays).
     * @param day       Day of the month (1–31).
     * @param monthStr  Month name as shown in the spinner (e.g., "January").
     * @param year      Year (e.g., 1995).
     * @return true if the date is valid and not in the future; false otherwise.
     */
    public static boolean isValidDate(Context context, int day, String monthStr, int year) {
        // Get the month index from the array (0-based)
        String[] monthsArray = context.getResources().getStringArray(R.array.months_array);
        int month = Arrays.asList(monthsArray).indexOf(monthStr);

        // Basic range checks
        if (day < 1 || day > 31 || month < 0 || month > 11 || year < 1900) {
            return false;
        }

        // Validate actual calendar date (e.g., no Feb 30)
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        try {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.getTime(); // will throw exception if date is invalid
        } catch (Exception e) {
            return false;
        }

        // Prepare "today" calendar (midnight, no time)
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        // Set the input date also to midnight
        Calendar inputDate = Calendar.getInstance();
        inputDate.setLenient(false);
        inputDate.set(Calendar.YEAR, year);
        inputDate.set(Calendar.MONTH, month);
        inputDate.set(Calendar.DAY_OF_MONTH, day);
        inputDate.set(Calendar.HOUR_OF_DAY, 0);
        inputDate.set(Calendar.MINUTE, 0);
        inputDate.set(Calendar.SECOND, 0);
        inputDate.set(Calendar.MILLISECOND, 0);

        // Ensure date is not in the future
        return !inputDate.after(today);
    }
}
