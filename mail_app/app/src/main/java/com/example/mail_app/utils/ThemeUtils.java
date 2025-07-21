package com.example.mail_app.utils;

import android.content.Context;
import android.util.TypedValue;

public class ThemeUtils {

    /**
     * Resolves a theme color attribute (like ?attr/gray_icon) into an actual color int.
     *
     * @param context Context with theme.
     * @param attrId  Attribute ID from R.attr (not R.color!).
     * @return Resolved color int, or fallback if not found.
     */
    public static int resolveThemeColor(Context context, int attrId) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attrId, typedValue, true)) {
            return typedValue.data;
        } else {
            return 0xFF888888; // fallback gray
        }
    }

}
