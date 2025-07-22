package com.example.mail_app.ui.mail.component;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.utils.MailUtils;

import java.util.List;

/**
 * Utility class for displaying label chips (colored tags) in mail views.
 * Can either limit the number of displayed chips (for compact layouts like mail list),
 * or show all of them with full horizontal scroll (e.g. mail details view).
 */
public class LabelChip {

    /**
     * Displays label chips inside the given container.
     *
     * @param context   Context used for resolving theme attributes and screen dimensions
     * @param container LinearLayout container to which chips will be added
     * @param labels    List of labels to display
     * @param showAll   If true, displays all labels (no limit). If false, shows up to 3 chips,
     *                  and stops if total width exceeds 50% of screen.
     */
    public static void displayLabelChips(Context context, LinearLayout container, List<Label> labels, boolean showAll) {
        container.removeAllViews();

        int added = 0;
        int totalWidthPx = 0;
        int maxLabels = 3; // max chips shown when showAll=false
        int maxWidthPx = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.5); // 50% of screen

        for (Label label : labels) {
            // When not showing all labels, enforce limits
            if (!showAll && (added >= maxLabels || totalWidthPx > maxWidthPx)) break;

            // Create a styled TextView to represent the chip
            TextView chip = new TextView(context);
            chip.setText(label.getName());
            chip.setTextColor(resolveTextColor(context));
            chip.setTextSize(12f);
            chip.setTypeface(null, Typeface.NORMAL);
            chip.setPadding(24, 8, 24, 8);
            chip.setBackground(MailUtils.getRoundedLabelDrawable(label.getColor())); // round background with color

            // Set spacing between chips
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMarginEnd(8);
            chip.setLayoutParams(params);

            // Add chip to container
            container.addView(chip);
            chip.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            totalWidthPx += chip.getMeasuredWidth() + params.getMarginEnd();
            added++;
        }
    }

    /**
     * Resolves the text color from the current theme's `text_color` attribute.
     *
     * @param context Context for theme access
     * @return int color value
     */
    private static int resolveTextColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.text_color, typedValue, true);
        return typedValue.data;
    }
}
