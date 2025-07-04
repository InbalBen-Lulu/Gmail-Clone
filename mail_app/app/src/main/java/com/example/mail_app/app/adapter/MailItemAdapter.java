package com.example.mail_app.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.utils.MailUtils;

import java.util.List;
import java.util.function.Consumer;

public class MailItemAdapter extends RecyclerView.Adapter<MailItemAdapter.MailViewHolder> {

    private final List<FullMail> mails;
    private final Consumer<FullMail> onStarClicked;

    public MailItemAdapter(List<FullMail> mails, Consumer<FullMail> onStarClicked) {
        this.mails = mails;
        this.onStarClicked = onStarClicked;
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

        public MailViewHolder(@NonNull View itemView) {
            super(itemView);
            senderView = itemView.findViewById(R.id.mailSender);
            subjectView = itemView.findViewById(R.id.mailSubject);
            dateView = itemView.findViewById(R.id.mailDate);
            starIcon = itemView.findViewById(R.id.mailStar);
        }

        public void bind(FullMail mail) {
            senderView.setText(mail.getFromUser().getName());
            subjectView.setText(mail.getMail().getSubject());
            dateView.setText(MailUtils.formatMailDate(mail.getMail().getSentAt()));

            boolean isStarred = mail.getMail().isStar();
            starIcon.setImageResource(isStarred ? R.drawable.baseline_star : R.drawable.outline_star);

            starIcon.setOnClickListener(v -> {
                onStarClicked.accept(mail); // update via ViewModel
            });
        }
    }
}
