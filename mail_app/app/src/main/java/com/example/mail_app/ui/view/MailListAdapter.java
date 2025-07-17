package com.example.mail_app.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.utils.MailUtils;

import java.util.List;
import java.util.function.Consumer;

public class MailListAdapter extends RecyclerView.Adapter<MailListAdapter.MailViewHolder> {

    private List<FullMail> mails;
    private final Consumer<String> onToggleStar;
    private final OnMailClickListener listener;

    public MailListAdapter(List<FullMail> mails, OnMailClickListener listener) {
        this.mails = mails;
        this.listener = listener;
        this.onToggleStar = listener::onToggleStar; // ממפה ל־Consumer
    }

    public void setMails(List<FullMail> newMails) {
        this.mails = newMails;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mail_item, parent, false);
        return new MailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MailViewHolder holder, int position) {
        FullMail mail = mails.get(position);
        holder.bind(mail);
    }

    @Override
    public int getItemCount() {
        return mails.size();
    }

    class MailViewHolder extends RecyclerView.ViewHolder {

        TextView senderView, subjectView, dateView;
        ImageView starIcon;
        UserAvatarView avatarView;

        public MailViewHolder(@NonNull View itemView) {
            super(itemView);
            senderView = itemView.findViewById(R.id.mailSender);
            subjectView = itemView.findViewById(R.id.mailSubject);
            dateView = itemView.findViewById(R.id.mailDate);
            starIcon = itemView.findViewById(R.id.mailStar);
            avatarView = itemView.findViewById(R.id.mailAvatar);
        }

        public void bind(FullMail mail) {
            if (mail == null || mail.getMail() == null) return;
            Context context = itemView.getContext();

            int textColor = resolveThemeColor(context, R.attr.text_color);
            setupSender(mail, context, textColor);
            setupSubject(mail, textColor);
            setupAvatar(mail);
            setupDate(mail);
            setupStar(mail, context);
            setupLabels(mail, context, textColor);
            setupBody(mail);
            setupClickListeners(mail);
        }

        private int resolveThemeColor(Context context, int attrId) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(attrId, typedValue, true);
            return typedValue.data;
        }

        private void setupSender(FullMail mail, Context context, int textColor) {
            if (mail.getMail().isDraft()) {
                senderView.setText(context.getString(R.string.draft_label));
                senderView.setTextColor(ContextCompat.getColor(context, R.color.draft_red));
                senderView.setTypeface(null, Typeface.NORMAL);
            } else {
                senderView.setText(mail.getFromUser().getName());
                senderView.setTextColor(textColor);
                senderView.setTypeface(null, mail.getMail().isRead() ? Typeface.NORMAL : Typeface.BOLD);
            }
        }

        private void setupSubject(FullMail mail, int textColor) {
            subjectView.setText(mail.getMail().getSubject());
            subjectView.setTextColor(textColor);
            subjectView.setTypeface(null, mail.getMail().isRead() ? Typeface.NORMAL : Typeface.BOLD);
        }

        private void setupAvatar(FullMail mail) {
            String imageUrl = mail.getFromUser().getProfileImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                avatarView.setImageUrl(imageUrl);
            } else {
                avatarView.setImageRes(R.drawable.default_avatar);
            }
        }

        private void setupDate(FullMail mail) {
            dateView.setText(MailUtils.formatMailDate(mail.getMail().getSentAt()));
        }

        private void setupStar(FullMail mail, Context context) {
            if (mail.getMail().isSpam()) {
                starIcon.setVisibility(View.GONE);
            } else {
                starIcon.setVisibility(View.VISIBLE);
                boolean isStarred = mail.getMail().isStar();
                starIcon.setImageResource(isStarred ? R.drawable.baseline_star : R.drawable.outline_star);
                starIcon.setColorFilter(ContextCompat.getColor(context, isStarred ? R.color.star_yellow : R.color.gray));
                starIcon.setOnClickListener(v -> onToggleStar.accept(mail.getMail().getId()));
            }
        }

        private void setupLabels(FullMail mail, Context context, int textColor) {
            LinearLayout labelContainer = itemView.findViewById(R.id.mailLabelContainer);
            labelContainer.removeAllViews();

            int maxLabels = 3, added = 0, totalWidthPx = 0;
            int maxWidthPx = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.5);

            for (Label label : mail.getLabels()) {
                if (added >= maxLabels || totalWidthPx > maxWidthPx) break;

                TextView labelView = new TextView(context);
                labelView.setText(label.getName());
                labelView.setTextColor(textColor);
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

        private void setupBody(FullMail mail) {
            TextView bodyView = itemView.findViewById(R.id.mailBodyPreview);
            bodyView.setText(mail.getMail().getBody());
        }

        private void setupClickListeners(FullMail mail) {
            itemView.setOnClickListener(v -> listener.onClick(mail));
            itemView.setOnLongClickListener(v -> {
                listener.onLongClick(mail);
                return true;
            });
        }
    }

        public interface OnMailClickListener {
        void onClick(FullMail mail);
        void onLongClick(FullMail mail);
        void onToggleStar(String mailId);
    }
}
