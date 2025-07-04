package com.example.mail_app.app.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.PublicUser;
import com.example.mail_app.ui.view.UserAvatarView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying a list of mails in the RecyclerView.
 */
public class MailAdapter extends RecyclerView.Adapter<MailAdapter.MailViewHolder> {
    private List<FullMail> mailList = new ArrayList<>();

    public void setMailList(List<FullMail> mails) {
        this.mailList = mails;
        notifyDataSetChanged(); // In production use DiffUtil
    }

    @NonNull
    @Override
    public MailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mail_item, parent, false);
        return new MailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MailViewHolder holder, int position) {
        FullMail fullMail = mailList.get(position);
        Mail mail = fullMail.getMail();
        PublicUser from = fullMail.getFromUser();

        // Set subject, body, sender name, etc.
        holder.subject.setText(mail.getSubject());
        holder.body.setText(mail.getBody());
        holder.time.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(mail.getSentAt()));

        if (from != null) {
            String initial = from.getName().isEmpty() ? "?" : from.getName().substring(0, 1).toUpperCase();
            holder.senderIcon.setText(initial);
        }

        // Set star icon
        holder.star.setImageResource(mail.isStar() ? R.drawable.ic_star_filled : R.drawable.ic_star_border);

        // Labels
        holder.labelContainer.removeAllViews();
        for (Label label : fullMail.getLabels()) {
            TextView labelView = createLabelView(holder.itemView.getContext(), label);
            holder.labelContainer.addView(labelView);
        }
    }

    @Override
    public int getItemCount() {
        return mailList.size();
    }

    static class MailViewHolder extends RecyclerView.ViewHolder {
        TextView subject, body, time, senderIcon;
        ImageView star;
        LinearLayout labelContainer;

        public MailViewHolder(@NonNull View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.mailSubject);
            body = itemView.findViewById(R.id.mailBody);
            time = itemView.findViewById(R.id.mailTime);
            senderIcon = itemView.findViewById(R.id.senderIcon);
            star = itemView.findViewById(R.id.mailStar);
            labelContainer = itemView.findViewById(R.id.mailLabels);
        }
    }

    private TextView createLabelView(Context context, Label label) {
        TextView view = new TextView(context);
        view.setText(label.getName());
        view.setTextColor(Color.WHITE);
        view.setTextSize(12f);
        view.setPadding(20, 4, 20, 4);
        view.setBackground(createRoundedColorDrawable(label.getColor()));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(8, 0, 0, 0);
        view.setLayoutParams(lp);
        return view;
    }

    private Drawable createRoundedColorDrawable(String hexColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(50);
        try {
            drawable.setColor(Color.parseColor(hexColor));
        } catch (Exception e) {
            drawable.setColor(Color.GRAY);
        }
        return drawable;
    }
}
