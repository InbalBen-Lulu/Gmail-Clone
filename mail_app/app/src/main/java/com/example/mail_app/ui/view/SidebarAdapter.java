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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mail_app.R;

import java.util.List;

/**
 * RecyclerView Adapter for displaying sidebar items.
 * Supports both SidebarItem (categories) and LabelSidebarItem (user labels).
 * Handles selection state, click callbacks, and theming.
 */
public class SidebarAdapter extends RecyclerView.Adapter<SidebarAdapter.SidebarViewHolder> {

    /**
     * Listener interface for sidebar item clicks.
     */
    public interface OnItemClickListener {
        void onCategoryClick(SidebarItem item);
        void onLabelClick(LabelSidebarItem label);
    }

    private final List<Object> items;
    private final OnItemClickListener listener;
    private int selectedPosition = 0;

    /**
     * Constructs the adapter with initial items and a click listener.
     *
     * @param items    List of sidebar items (categories and labels).
     * @param listener Callback for item click events.
     */
    public SidebarAdapter(List<Object> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // Inflate sidebar item view.
    @Override
    @NonNull
    public SidebarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_sidebar_item, parent, false);
        return new SidebarViewHolder(view);
    }

    // Bind item data to view, handle selection state and colors.
    @Override
    public void onBindViewHolder(SidebarViewHolder holder, int position) {
        Object item = items.get(position);
        Context context = holder.itemView.getContext();

        if (item instanceof SidebarItem) {
            SidebarItem cat = (SidebarItem) item;
            holder.title.setText(cat.getTitle());
            holder.icon.setImageResource(cat.getIconRes());
            holder.icon.setImageTintList(ColorStateList.valueOf(getThemeColor(context, R.attr.color_label)));
        } else if (item instanceof LabelSidebarItem) {
            LabelSidebarItem label = (LabelSidebarItem) item;
            holder.title.setText(label.getName());
            holder.icon.setImageResource(R.drawable.outline_label_24);
            try {
                holder.icon.setImageTintList(ColorStateList.valueOf(android.graphics.Color.parseColor(label.getColorHex())));
            } catch (IllegalArgumentException e) {
                holder.icon.setImageTintList(ColorStateList.valueOf(getThemeColor(context, R.attr.color_label)));
            }
        }

        int selectedColor = getThemeColor(context, R.attr.menu_item_select_bg);
        int defaultColor = getThemeColor(context, R.attr.menu_item_bg);
        holder.container.setBackgroundTintList(ColorStateList.valueOf(
                position == selectedPosition ? selectedColor : defaultColor
        ));

        holder.container.setOnClickListener(v -> {
            int prevPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prevPosition);
            notifyItemChanged(selectedPosition);

            if (item instanceof SidebarItem) {
                listener.onCategoryClick((SidebarItem) item);
            } else if (item instanceof LabelSidebarItem) {
                listener.onLabelClick((LabelSidebarItem) item);
            }
        });
    }

    // Return number of items.
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Updates the sidebar items and resets selection.
     *
     * @param newItems New list of sidebar items.
     */
    public void updateItems(List<Object> newItems) {
        items.clear();
        items.addAll(newItems);
        selectedPosition = 0;  // default selection
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for a sidebar item (category or label).
     */
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

    /**
     * Helper method to get theme-defined color by attribute.
     *
     * @param context Context to resolve theme.
     * @param attr    Attribute resource ID.
     * @return Resolved color value.
     */
    private int getThemeColor(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}
