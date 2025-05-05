package com.example.mamaabbys;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {
    private List<InventoryItem> items;
    private OnItemClickListener listener;
    private OnSellClickListener sellListener;

    public interface OnItemClickListener {
        void onItemClick(InventoryItem item);
    }

    public interface OnSellClickListener {
        void onSellClick(InventoryItem item);
    }

    public InventoryAdapter(List<InventoryItem> items, OnItemClickListener listener, OnSellClickListener sellListener) {
        this.items = new ArrayList<>(items);
        this.listener = listener;
        this.sellListener = sellListener;
        sortItems();
    }

    private void sortItems() {
        Collections.sort(items, (item1, item2) -> {
            // First sort by out of stock status
            boolean isOutOfStock1 = item1.isOutOfStock();
            boolean isOutOfStock2 = item2.isOutOfStock();
            if (isOutOfStock1 != isOutOfStock2) {
                return isOutOfStock1 ? -1 : 1;
            }
            // Then sort by low stock status
            boolean isLowStock1 = item1.isLowStock();
            boolean isLowStock2 = item2.isLowStock();
            if (isLowStock1 != isLowStock2) {
                return isLowStock1 ? -1 : 1;
            }
            // Finally sort by name
            return item1.getName().compareTo(item2.getName());
        });
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = items.get(position);
        holder.itemName.setText(item.getName());
        holder.stockInfo.setText(item.getStockInfo());
        holder.itemIcon.setImageResource(item.getIconResId());
        
        // Set text color based on stock status
        if (item.isOutOfStock()) {
            holder.itemName.setTextColor(Color.RED);
            holder.stockInfo.setTextColor(Color.RED);
            holder.sellButton.setEnabled(false);
        } else if (item.isLowStock()) {
            holder.itemName.setTextColor(Color.parseColor("#FFA500")); // Orange color for low stock
            holder.stockInfo.setTextColor(Color.parseColor("#FFA500"));
            holder.sellButton.setEnabled(true);
        } else {
            holder.itemName.setTextColor(Color.BLACK);
            holder.stockInfo.setTextColor(Color.GRAY);
            holder.sellButton.setEnabled(true);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        holder.sellButton.setOnClickListener(v -> {
            if (sellListener != null) {
                sellListener.onSellClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<InventoryItem> newItems) {
        this.items = new ArrayList<>(newItems);
        sortItems();
        notifyDataSetChanged();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView stockInfo;
        ImageView itemIcon;
        MaterialButton sellButton;

        InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            stockInfo = itemView.findViewById(R.id.stockInfo);
            itemIcon = itemView.findViewById(R.id.itemIcon);
            sellButton = itemView.findViewById(R.id.sellButton);
        }
    }
} 