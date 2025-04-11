package com.example.mamaabbys;

public class SalesItem {
    private String id;
    private String title;
    private String amount;
    private int iconResId;

    public SalesItem(String id, String title, String amount, int iconResId) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.iconResId = iconResId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public int getIconResId() {
        return iconResId;
    }
} 