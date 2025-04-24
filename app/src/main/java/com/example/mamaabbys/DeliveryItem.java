package com.example.mamaabbys;

public class DeliveryItem {
    private String id;
    private String orderNumber;
    private String schedule;
    private int iconResId;

    public DeliveryItem(String id, String orderNumber, String schedule, int iconResId) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.schedule = schedule;
        this.iconResId = iconResId;
    }

    public String getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getSchedule() {
        return schedule;
    }

    public int getIconResId() {
        return iconResId;
    }
}
