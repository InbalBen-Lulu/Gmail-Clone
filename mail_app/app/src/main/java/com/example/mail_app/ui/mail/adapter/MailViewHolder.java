package com.example.mail_app.ui.mail.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.ui.mail.component.LabelChip;
import com.example.mail_app.ui.view.UserAvatarView;
import com.example.mail_app.utils.MailUtils;
import com.example.mail_app.utils.ThemeUtils;

import java.util.function.Consumer;

/**
 * ViewHolder class for displaying a single mail item inside the RecyclerView.
 * Binds mail data to the views and handles click interactions.
 */
public class MailViewHolder extends RecyclerView.ViewHolder {

    private final TextView senderView, subjectView, dateView, bodyView;
    private final ImageView starIcon;
    private final UserAvatarView avatarView;
    private final LinearLayout labelContainer;

    /**
     * Constructor initializes all views used to display the mail.
     */
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

    /**
     * Binds a FullMail object to the UI, including avatar, subject, sender, labels, etc.
     * Also handles click and long-click events.
     */
    public void bind(FullMail mail, String selectedMailId, MailListAdapter.OnMailClickListener listener, Consumer<String> onToggleStar) {
        Context context = itemView.getContext();
        if (mail == null || mail.getMail() == null) return;

        // Highlight background if mail is selected
        itemView.setBackgroundColor(
                mail.getMail().getId().equals(selectedMailId)
                        ? ContextCompat.getColor(context, R.color.selected_blue)
                        : Color.TRANSPARENT
        );

        // Sender: show "Draft" label if mail is a draft, otherwise sender's name
        if (mail.getMail().isDraft()) {
            senderView.setText(context.getString(R.string.draft_label));
            senderView.setTextColor(ContextCompat.getColor(context, R.color.draft_red));
            senderView.setTypeface(null, Typeface.NORMAL);
        } else {
            senderView.setText(mail.getFromUser().getName());
            senderView.setTextColor(ThemeUtils.resolveThemeColor(context, R.attr.text_color));
            senderView.setTypeface(null, mail.getMail().isRead() ? Typeface.NORMAL : Typeface.BOLD);
        }

        // Subject text and style
        subjectView.setText(mail.getMail().getSubject());
        subjectView.setTextColor(ThemeUtils.resolveThemeColor(context, R.attr.text_color));
        subjectView.setTypeface(null, mail.getMail().isRead() ? Typeface.NORMAL : Typeface.BOLD);

        // Mail body preview
        bodyView.setText(mail.getMail().getBody());

        // Sent date
        dateView.setText(MailUtils.formatMailDate(mail.getMail().getSentAt()));

        // Sender avatar (profile image or default)
        String imageUrl = mail.getFromUser().getProfileImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            avatarView.setImageUrl(imageUrl);
        } else {
            avatarView.setImageRes(R.drawable.default_avatar);
        }

        // Star icon (visible only if not spam)
        if (mail.getMail().isSpam()) {
            starIcon.setVisibility(View.GONE);
        } else {
            starIcon.setVisibility(View.VISIBLE);
            boolean isStarred = mail.getMail().isStar();
            starIcon.setImageResource(isStarred ? R.drawable.baseline_star : R.drawable.outline_star);
            starIcon.setColorFilter(ContextCompat.getColor(context, isStarred ? R.color.star_yellow : R.color.gray));
            starIcon.setOnClickListener(v -> onToggleStar.accept(mail.getMail().getId()));
        }

        // Display label chips under the mail
        setupLabels(mail, context);

        // Handle mail click (open or edit)
        itemView.setOnClickListener(v -> listener.onClick(mail));

        // Handle long-click (enter selection mode)
        itemView.setOnLongClickListener(v -> {
            listener.onLongClick(mail);
            return true;
        });
    }

    /**
     * Displays all label chips associated with the mail in the label container.
     */
    private void setupLabels(FullMail mail, Context context) {
        LabelChip.displayLabelChips(context, labelContainer, mail.getLabels(), false);
    }
}
