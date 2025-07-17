package com.example.mail_app.ui.view;

/**
 * Represents a user-defined label item in the sidebar.
 * Includes label ID, name, and color (hex code).
 */
public class LabelSidebarItem {
    private final String id;
    private final String name;
    private final String colorHex;

    public LabelSidebarItem(String id, String name, String colorHex) {
        this.id = id;
        this.name = name;
        this.colorHex = colorHex;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getColorHex() { return colorHex; }
}

