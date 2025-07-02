package com.example.mail_app.auth;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * AuthManager handles secure storage and retrieval of authentication data (token and userId)
 * using SharedPreferences.
 */
public class AuthManager {
    private static final String PREFS_NAME = "auth";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";

    /**
     * Stores the JWT token in SharedPreferences.
     */
    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    /**
     * Retrieves the JWT token from SharedPreferences.
     */
    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    /**
     * Stores the logged-in user ID in SharedPreferences.
     */
    public static void saveUserId(Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_ID, userId).apply();
    }

    /**
     * Retrieves the user ID from SharedPreferences.
     */
    public static String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_ID, null);
    }

    /**
     * Removes the user ID from SharedPreferences.
     */
    public static void clearUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_USER_ID).apply();
    }

    /**
     * Clears all authentication data (used for logout).
     */
    public static void clearAll(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
