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
    private boolean isStockNotification;
    private String productName;
    private int currentStock;
    private int minThreshold;

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
        this.isStockNotification = false;
    }

    // Constructor for stock notifications
    public NotificationItem(String id, String title, String message, String productName, int currentStock, int minThreshold) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
        this.isDeliveryNotification = false;
        this.isStockNotification = true;
        this.productName = productName;
        this.currentStock = currentStock;
        this.minThreshold = minThreshold;
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
    public boolean isStockNotification() { return isStockNotification; }
    public String getProductName() { return productName; }
    public int getCurrentStock() { return currentStock; }
    public int getMinThreshold() { return minThreshold; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }
    public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }
    public void setOrderDetails(String orderDetails) { this.orderDetails = orderDetails; }
    public void setRead(boolean read) { isRead = read; }
    public void setDeliveryNotification(boolean deliveryNotification) { isDeliveryNotification = deliveryNotification; }
    public void setStockNotification(boolean stockNotification) { isStockNotification = stockNotification; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }
    public void setMinThreshold(int minThreshold) { this.minThreshold = minThreshold; }
} 