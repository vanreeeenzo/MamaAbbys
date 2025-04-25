package com.example.mamaabbys;

public class DeliveryItem {
    private String id;
    private String orderNumber;
    private String schedule;
    private int iconResId;
    private boolean isDone;

    public DeliveryItem(String id, String orderNumber, String schedule, int iconResId) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.schedule = schedule;
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
