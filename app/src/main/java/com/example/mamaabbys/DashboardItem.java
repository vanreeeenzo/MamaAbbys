package com.example.mamaabbys;

public class DashboardItem {
    private String id;
    private String title;
    private String description;
    private String category;
    private int iconResId;

    public DashboardItem(String id, String title, String description, String category, int iconResId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public int getIconResId() {
        return iconResId;
    }
} 