package com.example.mamaabbys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder> {
    private List<DeliveryItem> items;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(DeliveryItem item);
        void onMarkAsDoneClicked(DeliveryItem delivery);
        void onCancelClicked(DeliveryItem delivery);
    }

    public DeliveryAdapter(Context context, List<DeliveryItem> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    public void updateItems(List<DeliveryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delivery, parent, false);
        return new DeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        DeliveryItem item = items.get(position);
        holder.orderNumber.setText(item.getOrderNumber());
        holder.schedule.setText(item.getSchedule());
        holder.deliveryIcon.setImageResource(item.getIconResId());

        holder.btnMarkAsDone.setOnClickListener(v -> {
            if (listener != null) listener.onMarkAsDoneClicked(item);
        });

        holder.btnCancelDelivery.setOnClickListener(v -> {
            if (listener != null) listener.onCancelClicked(item);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class DeliveryViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, schedule;
        ImageView deliveryIcon;
        Button btnMarkAsDone, btnCancelDelivery;

        DeliveryViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.orderNumber);
            schedule = itemView.findViewById(R.id.schedule);
            deliveryIcon = itemView.findViewById(R.id.deliveryIcon);
            btnMarkAsDone = itemView.findViewById(R.id.btnMarkAsDone);
            btnCancelDelivery = itemView.findViewById(R.id.btnCancelDelivery);
        }
    }
}


