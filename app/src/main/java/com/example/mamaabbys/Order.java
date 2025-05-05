package com.example.mamaabbys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private String id;
    private String date;
    private String time;
    private double total;
    private String status;
    private List<OrderItem> items;

    public Order(String id, String date, String time, double total, String status) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.total = total;
        this.status = status;
        this.items = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public double getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public static class OrderItem implements Serializable {
        private String productId;
        private String productName;
        private int quantity;
        private double price;
        private double total;

        public OrderItem(String productId, String productName, int quantity, double price, double total) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.total = total;
        }

        public String getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public double getTotal() {
            return total;
        }
    }
} 