package com.example.mamaabbys;

public class Delivery {
    private String orderDescription;
    private String deliveryDate;
    private String deliveryTime;

    public Delivery(String orderDescription, String deliveryDate, String deliveryTime) {
        this.orderDescription = orderDescription;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
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
}
