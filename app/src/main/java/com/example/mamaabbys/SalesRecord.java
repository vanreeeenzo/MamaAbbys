package com.example.mamaabbys;

public class SalesRecord {
    private String id;
    private String productId;
    private String productName;
    private int quantity;
    private double totalAmount;
    private String saleDate;

    public SalesRecord(String id, String productId, String productName, int quantity, double totalAmount, String saleDate) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.saleDate = saleDate;
    }

    public String getId() {
        return id;
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getSaleDate() {
        return saleDate;
    }
} 