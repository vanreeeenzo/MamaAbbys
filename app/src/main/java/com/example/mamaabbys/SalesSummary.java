package com.example.mamaabbys;

public class SalesSummary {
    private double totalRevenue;
    private int totalOrders;
    private double averageOrderValue;

    public SalesSummary() {
        this.totalRevenue = 0.0;
        this.totalOrders = 0;
        this.averageOrderValue = 0.0;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
        // Recalculate average order value when total revenue changes
        calculateAverageOrderValue();
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
        // Recalculate average order value when total orders changes
        calculateAverageOrderValue();
    }

    public double getAverageOrderValue() {
        return averageOrderValue;
    }

    private void calculateAverageOrderValue() {
        if (totalOrders > 0) {
            this.averageOrderValue = totalRevenue / totalOrders;
        } else {
            this.averageOrderValue = 0.0;
        }
    }

    public void setAverageOrderValue(double averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
} 