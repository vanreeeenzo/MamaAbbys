package com.example.mamaabbys;

import android.os.Parcel;
import android.os.Parcelable;

public class InventoryItem implements Parcelable {
    private String id;
    private String name;
    private String stockInfo;
    private int iconResId;
    private String category;
    private int quantity;
    private float price;
    private int minThreshold;

    // No-argument constructor
    public InventoryItem() {
        this.iconResId = R.drawable.ic_package; // Default icon
    }

    public InventoryItem(String id, String name, String stockInfo, int iconResId, String category) {
        this.id = id;
        this.name = name;
        this.stockInfo = stockInfo;
        this.iconResId = iconResId;
        this.category = category;
    }

    protected InventoryItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        stockInfo = in.readString();
        iconResId = in.readInt();
        category = in.readString();
        quantity = in.readInt();
        price = in.readFloat();
        minThreshold = in.readInt();
    }

    public static final Creator<InventoryItem> CREATOR = new Creator<InventoryItem>() {
        @Override
        public InventoryItem createFromParcel(Parcel in) {
            return new InventoryItem(in);
        }

        @Override
        public InventoryItem[] newArray(int size) {
            return new InventoryItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStockInfo() {
        return stockInfo;
    }

    public void setStockInfo(String stockInfo) {
        this.stockInfo = stockInfo;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateStockInfo();
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
        updateStockInfo();
    }

    public int getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(int minThreshold) {
        this.minThreshold = minThreshold;
        updateStockInfo();
    }

    private void updateStockInfo() {
        this.stockInfo = String.format("In Stock: %d | Price: ₱%.2f", quantity, price);
        if (quantity <= minThreshold) {
            this.stockInfo += " (Low Stock!)";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(stockInfo);
        dest.writeInt(iconResId);
        dest.writeString(category);
        dest.writeInt(quantity);
        dest.writeFloat(price);
        dest.writeInt(minThreshold);
    }
} 