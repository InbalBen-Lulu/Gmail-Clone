package com.example.mail_app.utils;

import android.app.Activity;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

/**
 * Utility functions for showing simple UI messages using Snackbars.
 */
public class UiUtils {

    /**
     * Displays a Snackbar message at the bottom of the screen.
     *
     * @param activity The activity to use for finding the root view.
     * @param message  The message to display.
     */
    public static void showMessage(Activity activity, String message) {
        View rootView = activity.findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Displays a Snackbar message and runs a callback when it's dismissed.
     *
     * @param activity   The activity to use for finding the root view.
     * @param message    The message to display.
     * @param onDismiss  Optional runnable to execute when the Snackbar is dismissed.
     */
    public static void showMessage(Activity activity, String message, @Nullable Runnable onDismiss) {
        View rootView = activity.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);

        // Add optional callback on dismissal
        if (onDismiss != null) {
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    onDismiss.run();
                }
            });
        }

        snackbar.show();
    }
}
