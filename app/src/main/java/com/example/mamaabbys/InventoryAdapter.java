package com.example.mamaabbys;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {
    private List<InventoryItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(InventoryItem item);


    }

    public InventoryAdapter(List<InventoryItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
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
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<InventoryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView stockInfo;
        ImageView itemIcon;

        InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            stockInfo = itemView.findViewById(R.id.stockInfo);
            itemIcon = itemView.findViewById(R.id.itemIcon);
        }
    }
} 