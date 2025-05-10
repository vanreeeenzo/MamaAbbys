package com.example.mamaabbys;

public class SettingsItem {
    private String title;
    private String description;

    public SettingsItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
} 