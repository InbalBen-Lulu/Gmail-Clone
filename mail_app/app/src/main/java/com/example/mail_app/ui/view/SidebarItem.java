package com.example.mail_app.ui.view;

public class SidebarItem {
    private final String title;
    private final int iconRes;
    private final String customColorHex; // nullable, לדוגמה "#FF0000"

    public SidebarItem(String title, int iconRes) {
        this(title, iconRes, null);
    }

    public SidebarItem(String title, int iconRes, String customColorHex) {
        this.title = title;
        this.iconRes = iconRes;
        this.customColorHex = customColorHex;
    }

    public String getTitle() {
        return title;
    }

    public int getIconRes() {
        return iconRes;
    }

    public String getCustomColorHex() {
        return customColorHex;
    }
}

