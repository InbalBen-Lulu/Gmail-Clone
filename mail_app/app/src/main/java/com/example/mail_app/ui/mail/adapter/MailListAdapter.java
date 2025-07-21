package com.example.mail_app.ui.mail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;

import java.util.List;
import java.util.function.Consumer;

public class MailListAdapter extends RecyclerView.Adapter<MailViewHolder> {

    private List<FullMail> mails;
    private final Consumer<String> onToggleStar;
    private final OnMailClickListener listener;

    private String selectedMailId = null;

    public MailListAdapter(List<FullMail> mails, OnMailClickListener listener) {
        this.mails = mails;
        this.listener = listener;
        this.onToggleStar = listener::onToggleStar;
    }

    public void setMails(List<FullMail> newMails) {
        this.mails = newMails;
        notifyDataSetChanged();
    }

    @NonNull
    public MailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mail_item, parent, false);
        return new MailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MailViewHolder holder, int position) {
        FullMail mail = mails.get(position);
        holder.bind(mail, selectedMailId, listener, onToggleStar);
    }


    @Override
    public int getItemCount() {
        return mails.size();
    }


    public interface OnMailClickListener {
        void onClick(FullMail mail);
        void onLongClick(FullMail mail);
        void onToggleStar(String mailId);
    }

    public void setSelectedMailId(String id) {
        this.selectedMailId = id;
        notifyDataSetChanged();
    }

    public void clearSelection() {
        this.selectedMailId = null;
        notifyDataSetChanged();
    }

}
