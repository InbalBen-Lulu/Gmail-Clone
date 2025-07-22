package com.example.mail_app.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Utility class for resolving theme-based color attributes into actual color values.
 */
public class ThemeUtils {

    /**
     * Resolves a theme color attribute (like ?attr/gray_icon) into an actual ARGB color int.
     *
     * @param context Context to access the current theme.
     * @param attrId  Attribute resource ID from R.attr (not from R.color).
     * @return The resolved color value, or a default gray if not found.
     */
    public static int resolveThemeColor(Context context, int attrId) {
        TypedValue typedValue = new TypedValue();

        // Try to resolve the attribute from the current theme
        if (context.getTheme().resolveAttribute(attrId, typedValue, true)) {
            return typedValue.data;
        } else {
            // Fallback color (light gray) in case resolution fails
            return 0xFF888888;
        }
    }
}
