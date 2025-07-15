package com.example.mail_app.ui.view;

/**
 * Represents a fixed category item in the sidebar (e.g., Inbox, Starred).
 * Includes display title and icon resource ID.
 */
public class SidebarItem {
    private final String title;
    private final int iconRes;

    public SidebarItem(String title, int iconRes) {
        this.title = title;
        this.iconRes = iconRes;
    }

    public String getTitle() { return title; }
    public int getIconRes() { return iconRes; }
}


