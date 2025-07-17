package com.example.mail_app.ui.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
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
            if (mail == null || mail.getMail() == null) {
                Log.e("MailAdapter", "Mail או mail.getMail() ריק!!");
                return;
            }
            Log.d("MailAdapter", "subject: " + mail.getMail().getSubject());
            senderView.setText(mail.getFromUser().getName());
            subjectView.setText(mail.getMail().getSubject());
            dateView.setText(MailUtils.formatMailDate(mail.getMail().getSentAt()));

            String imageUrl = mail.getFromUser().getProfileImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                avatarView.setImageUrl(imageUrl);
            } else {
                avatarView.setImageRes(R.drawable.default_avatar);
            }

            boolean isStarred = mail.getMail().isStar();
            starIcon.setImageResource(isStarred ? R.drawable.baseline_star : R.drawable.outline_star);
            starIcon.setColorFilter(
                    itemView.getContext().getResources().getColor(
                            isStarred ? R.color.star_yellow : R.color.gray,
                            null
                    )
            );

            starIcon.setOnClickListener(v -> onToggleStar.accept(mail.getMail().getId()));

            // מאזינים ללחיצה ולחיצה ארוכה
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
