package com.example.mail_app.utils;


import android.app.Activity;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class UiUtils {

    /**
     * Displays a Snackbar message at the bottom of the screen.
     *
     * @param activity The activity to use for finding root view
     * @param message The message to display
     */
    public static void showMessage(Activity activity, String message) {
        View rootView = activity.findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }
}