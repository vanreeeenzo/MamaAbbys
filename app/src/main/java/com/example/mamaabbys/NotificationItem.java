package com.example.mamaabbys;

public class NotificationItem {
    private String id;
    private String title;
    private String message;
    private String deliveryDate;
    private String deliveryTime;
    private String orderDetails;
    private long timestamp;
    private boolean isRead;
    private boolean isDeliveryNotification;

    public NotificationItem(String id, String title, String message, String deliveryDate, String deliveryTime, String orderDetails, boolean isDeliveryNotification) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
        this.orderDetails = orderDetails;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
        this.isDeliveryNotification = isDeliveryNotification;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getDeliveryDate() { return deliveryDate; }
    public String getDeliveryTime() { return deliveryTime; }
    public String getOrderDetails() { return orderDetails; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    public boolean isDeliveryNotification() { return isDeliveryNotification; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }
    public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }
    public void setOrderDetails(String orderDetails) { this.orderDetails = orderDetails; }
    public void setRead(boolean read) { isRead = read; }
    public void setDeliveryNotification(boolean deliveryNotification) { isDeliveryNotification = deliveryNotification; }
} 