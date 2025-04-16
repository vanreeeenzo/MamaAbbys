package com.example.mamaabbys;

import android.os.Parcel;
import android.os.Parcelable;

public class InventoryItem implements Parcelable {
    private String id;
    private String name;
    private String stockInfo;
    private int iconResId;

    public InventoryItem(String id, String name, String stockInfo, int iconResId) {
        this.id = id;
        this.name = name;
        this.stockInfo = stockInfo;
        this.iconResId = iconResId;
    }

    protected InventoryItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        stockInfo = in.readString();
        iconResId = in.readInt();
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

    public String getName() {
        return name;
    }

    public String getStockInfo() {
        return stockInfo;
    }

    public int getIconResId() {
        return iconResId;
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
    }
} 