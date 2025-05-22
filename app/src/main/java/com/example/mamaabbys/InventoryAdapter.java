package com.example.mamaabbys;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {
    private List<InventoryItem> items;
    private OnItemClickListener listener;
    private OnSellClickListener sellListener;
    private Set<String> selectedItems;
    private boolean isSelectionMode;

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
        this.selectedItems = new HashSet<>();
        this.isSelectionMode = false;
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
        
        // Handle selection mode
        holder.itemCheckBox.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);
        holder.itemCheckBox.setChecked(selectedItems.contains(item.getId()));
        
        // Set text color and button state based on stock status
        if (item.isOutOfStock()) {
            holder.itemName.setTextColor(Color.RED);
            holder.stockInfo.setTextColor(Color.RED);
            holder.sellButton.setEnabled(false);
            holder.sellButton.setAlpha(0.5f);
        } else if (item.isLowStock()) {
            holder.itemName.setTextColor(Color.parseColor("#FFA500"));
            holder.stockInfo.setTextColor(Color.parseColor("#FFA500"));
            holder.sellButton.setEnabled(true);
            holder.sellButton.setAlpha(1.0f);
        } else {
            holder.itemName.setTextColor(Color.BLACK);
            holder.stockInfo.setTextColor(Color.GRAY);
            holder.sellButton.setEnabled(true);
            holder.sellButton.setAlpha(1.0f);
        }
        
        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                toggleItemSelection(item.getId());
                holder.itemCheckBox.setChecked(selectedItems.contains(item.getId()));
            } else if (listener != null) {
                listener.onItemClick(item);
            }
        });

        // Handle checkbox click directly
        holder.itemCheckBox.setOnClickListener(v -> {
            if (isSelectionMode) {
                toggleItemSelection(item.getId());
            }
        });

        holder.sellButton.setOnClickListener(v -> {
            if (!item.isOutOfStock() && sellListener != null) {
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

    public void setSelectionMode(boolean enabled) {
        if (isSelectionMode != enabled) {
            isSelectionMode = enabled;
            if (!enabled) {
                // Clear selection when exiting selection mode
                selectedItems.clear();
            }
            notifyDataSetChanged();
        }
    }

    public boolean isSelectionMode() {
        return isSelectionMode;
    }

    public Set<String> getSelectedItems() {
        return new HashSet<>(selectedItems);
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    private void toggleItemSelection(String itemId) {
        if (selectedItems.contains(itemId)) {
            selectedItems.remove(itemId);
        } else {
            selectedItems.add(itemId);
        }
        notifyDataSetChanged();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView stockInfo;
        ImageView itemIcon;
        MaterialButton sellButton;
        CheckBox itemCheckBox;

        InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            stockInfo = itemView.findViewById(R.id.stockInfo);
            itemIcon = itemView.findViewById(R.id.itemIcon);
            sellButton = itemView.findViewById(R.id.sellButton);
            itemCheckBox = itemView.findViewById(R.id.itemCheckBox);
        }
    }
} 