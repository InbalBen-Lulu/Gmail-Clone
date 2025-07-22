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

/**
 * RecyclerView.Adapter for displaying a list of mails using MailViewHolder.
 * Handles mail clicks, long-press selection, and toggling stars.
 */
public class MailListAdapter extends RecyclerView.Adapter<MailViewHolder> {

    private List<FullMail> mails;
    private final Consumer<String> onToggleStar;
    private final OnMailClickListener listener;

    private String selectedMailId = null; // ID of the currently selected mail (for highlight)

    /**
     * Constructor that sets up mail list and click listeners.
     */
    public MailListAdapter(List<FullMail> mails, OnMailClickListener listener) {
        this.mails = mails;
        this.listener = listener;
        this.onToggleStar = listener::onToggleStar;
    }

    /**
     * Replaces the adapter's data and refreshes the UI.
     */
    public void setMails(List<FullMail> newMails) {
        this.mails = newMails;
        notifyDataSetChanged();
    }

    /**
     * Inflates the mail item view and returns a new ViewHolder.
     */
    @NonNull
    @Override
    public MailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mail_item, parent, false);
        return new MailViewHolder(view);
    }

    /**
     * Binds the mail data to the given ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull MailViewHolder holder, int position) {
        FullMail mail = mails.get(position);
        holder.bind(mail, selectedMailId, listener, onToggleStar);
    }

    /**
     * Returns the number of mails currently displayed.
     */
    @Override
    public int getItemCount() {
        return mails.size();
    }

    /**
     * Listener interface for click, long-click, and star-toggle events on a mail.
     */
    public interface OnMailClickListener {
        void onClick(FullMail mail);
        void onLongClick(FullMail mail);
        void onToggleStar(String mailId);
    }

    /**
     * Sets the currently selected mail ID (for highlighting).
     */
    public void setSelectedMailId(String id) {
        this.selectedMailId = id;
        notifyDataSetChanged();
    }

    /**
     * Clears the selected mail (e.g. when ActionMode ends).
     */
    public void clearSelection() {
        this.selectedMailId = null;
        notifyDataSetChanged();
    }
}
