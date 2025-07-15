package com.example.mail_app.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mail_app.R;

import java.util.List;

public class SidebarAdapter extends RecyclerView.Adapter<SidebarAdapter.SidebarViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final List<SidebarItem> items;
    private final OnItemClickListener listener;
    private int selectedPosition = 0;

    public SidebarAdapter(List<SidebarItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public SidebarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_sidebar_item, parent, false);
        return new SidebarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SidebarViewHolder holder, int position) {
        SidebarItem item = items.get(position);
        Context context = holder.itemView.getContext();

        holder.icon.setImageResource(item.getIconRes());
        holder.title.setText(item.getTitle());

        // Set icon tint
        String customColorHex = item.getCustomColorHex();
        int tintColor;
        if (customColorHex != null) {
            try {
                tintColor = android.graphics.Color.parseColor(customColorHex);
            } catch (IllegalArgumentException e) {
                tintColor = getThemeColor(context, R.attr.color_label);
            }
        } else {
            tintColor = getThemeColor(context, R.attr.color_label);
        }
        holder.icon.setImageTintList(ColorStateList.valueOf(tintColor));

        // Background color
        int selectedColor = getThemeColor(context, R.attr.menu_item_select_bg);
        int defaultColor = getThemeColor(context, R.attr.menu_item_bg);
        holder.container.setBackgroundTintList(ColorStateList.valueOf(
                position == selectedPosition ? selectedColor : defaultColor
        ));

        holder.container.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition == RecyclerView.NO_POSITION) return;

            int prevPosition = selectedPosition;
            selectedPosition = clickedPosition;

            notifyItemChanged(prevPosition);
            notifyItemChanged(clickedPosition);
            listener.onItemClick(clickedPosition);
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SidebarViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        ImageView icon;
        TextView title;

        SidebarViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.sidebar_item_container);
            icon = itemView.findViewById(R.id.sidebar_item_icon);
            title = itemView.findViewById(R.id.sidebar_item_title);
        }
    }

    private int getThemeColor(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}
