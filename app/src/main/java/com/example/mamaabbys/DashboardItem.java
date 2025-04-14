package com.example.mamaabbys;

public class DashboardItem {
    private String id;
    private String title;
    private String description;
    private int iconResId;

    public DashboardItem(String id, String title, String description, int iconResId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResId() {
        return iconResId;
    }
} 