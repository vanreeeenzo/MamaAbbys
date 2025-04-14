package com.example.mamaabbys;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder> {
    private List<DeliveryItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DeliveryItem item);
    }

    public DeliveryAdapter(List<DeliveryItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delivery, parent, false);
        return new DeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        DeliveryItem item = items.get(position);
        holder.orderNumber.setText(item.getOrderNumber());
        holder.schedule.setText(item.getSchedule());
        holder.deliveryIcon.setImageResource(item.getIconResId());
        
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

    static class DeliveryViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber;
        TextView schedule;
        ImageView deliveryIcon;

        DeliveryViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.orderNumber);
            schedule = itemView.findViewById(R.id.schedule);
            deliveryIcon = itemView.findViewById(R.id.deliveryIcon);
        }
    }
} 