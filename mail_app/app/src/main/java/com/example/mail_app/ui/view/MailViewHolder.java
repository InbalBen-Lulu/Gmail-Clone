package com.example.mail_app.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.utils.MailUtils;

import java.util.function.Consumer;

public class MailViewHolder extends RecyclerView.ViewHolder {

    private final TextView senderView, subjectView, dateView, bodyView;
    private final ImageView starIcon;
    private final UserAvatarView avatarView;
    private final LinearLayout labelContainer;

    public MailViewHolder(View itemView) {
        super(itemView);
        senderView = itemView.findViewById(R.id.mailSender);
        subjectView = itemView.findViewById(R.id.mailSubject);
        dateView = itemView.findViewById(R.id.mailDate);
        bodyView = itemView.findViewById(R.id.mailBodyPreview);
        starIcon = itemView.findViewById(R.id.mailStar);
        avatarView = itemView.findViewById(R.id.mailAvatar);
        labelContainer = itemView.findViewById(R.id.mailLabelContainer);
    }

    public void bind(FullMail mail, String selectedMailId, MailListAdapter.OnMailClickListener listener, Consumer<String> onToggleStar) {
        Context context = itemView.getContext();
        if (mail == null || mail.getMail() == null) return;

        // background
        itemView.setBackgroundColor(
                mail.getMail().getId().equals(selectedMailId)
                        ? ContextCompat.getColor(context, R.color.selected_blue)
                        : Color.TRANSPARENT
        );

        // Sender
        if (mail.getMail().isDraft()) {
            senderView.setText(context.getString(R.string.draft_label));
            senderView.setTextColor(ContextCompat.getColor(context, R.color.draft_red));
            senderView.setTypeface(null, Typeface.NORMAL);
        } else {
            senderView.setText(mail.getFromUser().getName());
            senderView.setTextColor(resolveThemeColor(context, R.attr.text_color));
            senderView.setTypeface(null, mail.getMail().isRead() ? Typeface.NORMAL : Typeface.BOLD);
        }

        // Subject
        subjectView.setText(mail.getMail().getSubject());
        subjectView.setTextColor(resolveThemeColor(context, R.attr.text_color));
        subjectView.setTypeface(null, mail.getMail().isRead() ? Typeface.NORMAL : Typeface.BOLD);

        // Body
        bodyView.setText(mail.getMail().getBody());

        // Date
        dateView.setText(MailUtils.formatMailDate(mail.getMail().getSentAt()));

        // Avatar
        String imageUrl = mail.getFromUser().getProfileImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            avatarView.setImageUrl(imageUrl);
        } else {
            avatarView.setImageRes(R.drawable.default_avatar);
        }

        // Star
        if (mail.getMail().isSpam()) {
            starIcon.setVisibility(View.GONE);
        } else {
            starIcon.setVisibility(View.VISIBLE);
            boolean isStarred = mail.getMail().isStar();
            starIcon.setImageResource(isStarred ? R.drawable.baseline_star : R.drawable.outline_star);
            starIcon.setColorFilter(ContextCompat.getColor(context, isStarred ? R.color.star_yellow : R.color.gray));
            starIcon.setOnClickListener(v -> onToggleStar.accept(mail.getMail().getId()));
        }

        // Labels
        setupLabels(mail, context);

        // Clicks
        itemView.setOnClickListener(v -> listener.onClick(mail));
        itemView.setOnLongClickListener(v -> {
            listener.onLongClick(mail);
            return true;
        });
    }

    private void setupLabels(FullMail mail, Context context) {
        labelContainer.removeAllViews();
        int maxLabels = 3, added = 0, totalWidthPx = 0;
        int maxWidthPx = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.5);

        for (Label label : mail.getLabels()) {
            if (added >= maxLabels || totalWidthPx > maxWidthPx) break;

            TextView labelView = new TextView(context);
            labelView.setText(label.getName());
            labelView.setTextColor(resolveThemeColor(context, R.attr.text_color));
            labelView.setTextSize(12f);
            labelView.setPadding(24, 8, 24, 8);
            labelView.setBackground(MailUtils.getRoundedLabelDrawable(label.getColor()));

            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMarginEnd(8);
            labelView.setLayoutParams(params);

            labelContainer.addView(labelView);
            labelView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            totalWidthPx += labelView.getMeasuredWidth() + params.getMarginEnd();
            added++;
        }
    }

    private int resolveThemeColor(Context context, int attrId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrId, typedValue, true);
        return typedValue.data;
    }
}
