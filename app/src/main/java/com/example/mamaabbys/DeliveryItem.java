package com.example.mamaabbys;

public class DeliveryItem {
    private String id;
    private String orderNumber;
    private String schedule;
    private String location;
    private int iconResId;
    private boolean isDone;

    public DeliveryItem(String id, String orderNumber, String schedule, String location, int iconResId) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.schedule = schedule;
        this.location = location;
        this.iconResId = iconResId;
        this.isDone = false;
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

    public String getLocation() {
        return location;
    }

    public int getIconResId() {
        return iconResId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        this.isDone = done;
    }
}
