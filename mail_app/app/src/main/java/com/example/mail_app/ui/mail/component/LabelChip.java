package com.example.mail_app.ui.mail.component;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mail_app.data.entity.Label;
import com.example.mail_app.utils.MailUtils;

import java.util.List;

public class LabelChip {

    /**
     * Adds up to 3 label chips to the given container, respecting width constraints.
     */
    public static void displayLabelChips(Context context, LinearLayout container, List<Label> labels) {
        container.removeAllViews();

        int maxLabels = 3;
        int added = 0;
        int totalWidthPx = 0;
        int maxWidthPx = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.5);

        for (Label label : labels) {
            if (added >= maxLabels || totalWidthPx > maxWidthPx) break;

            TextView chip = new TextView(context);
            chip.setText(label.getName());
            chip.setTextColor(resolveTextColor(context));
            chip.setTextSize(12f);
            chip.setTypeface(null, Typeface.NORMAL);
            chip.setPadding(24, 8, 24, 8);
            chip.setBackground(MailUtils.getRoundedLabelDrawable(label.getColor()));

            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMarginEnd(8);
            chip.setLayoutParams(params);

            container.addView(chip);
            chip.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            totalWidthPx += chip.getMeasuredWidth() + params.getMarginEnd();
            added++;
        }
    }

    private static int resolveTextColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColor, typedValue, true);
        return typedValue.data;
    }
}
