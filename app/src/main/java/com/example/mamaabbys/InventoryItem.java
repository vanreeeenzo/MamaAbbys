package com.example.mamaabbys;

public class InventoryItem {
    private String id;
    private String name;
    private String stockInfo;
    private int iconResId;

    public InventoryItem(String id, String name, String stockInfo, int iconResId) {
        this.id = id;
        this.name = name;
        this.stockInfo = stockInfo;
        this.iconResId = iconResId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStockInfo() {
        return stockInfo;
    }

    public int getIconResId() {
        return iconResId;
    }
} 