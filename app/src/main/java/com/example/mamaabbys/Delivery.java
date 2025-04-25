package com.example.mamaabbys;

public class Delivery {
    private String id;
    private String orderDescription;
    private String deliveryDate;
    private String deliveryTime;
    private String status;
    private boolean isDone;

    public Delivery(String orderDescription, String deliveryDate, String deliveryTime) {
        this.orderDescription = orderDescription;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
        this.status = "Pending"; // Default status

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}