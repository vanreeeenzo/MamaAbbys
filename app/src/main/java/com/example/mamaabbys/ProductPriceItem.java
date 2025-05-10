package com.example.mamaabbys;

public class ProductPriceItem {
    private String id;
    private String name;
    private String category;
    private float price;

    public ProductPriceItem(String id, String name, String category, float price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
} 