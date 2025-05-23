package com.example.mamaabbys;

public class Delivery {
    private String id;
    private String deliveryName;
    private String orderDescription;
    private String deliveryDate;
    private String deliveryTime;
    private String location;
    private String status;
    private boolean isDone;

    public Delivery(String deliveryName, String orderDescription, String deliveryDate, String deliveryTime, String location) {
        this.deliveryName = deliveryName;
        this.orderDescription = orderDescription;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
        this.location = location;
        this.status = "Pending"; // Default status
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDone() {
        return "Done".equalsIgnoreCase(status);
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}