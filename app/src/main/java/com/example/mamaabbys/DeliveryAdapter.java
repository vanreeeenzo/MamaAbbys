package com.example.mamaabbys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

    public void markAsDone(DeliveryItem item) {
        items.remove(item);
        item.setDone(true);
        items.add(item);

        items.sort((a, b) -> Boolean.compare(a.isDone(), b.isDone()));
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
        holder.name.setText(item.getName());
        holder.schedule.setText(item.getSchedule());
        holder.location.setText("Location: " + item.getLocation());
        holder.deliveryIcon.setImageResource(item.getIconResId());

        // Show/hide overdue badge
        boolean isOverdue = item.isOverdue();
        android.util.Log.d("DeliveryAdapter", "Item: " + item.getOrderNumber() + 
            ", Schedule: " + item.getSchedule() + 
            ", IsOverdue: " + isOverdue + 
            ", IsDone: " + item.isDone());
            
        if (isOverdue) {
            holder.overdueBadge.setVisibility(View.VISIBLE);
            // Show notification for overdue delivery
            NotificationHelper.showNotification(
                context,
                "Overdue Delivery",
                "Delivery for order " + item.getOrderNumber() + " is overdue! Scheduled for " + item.getSchedule()
            );
        } else {
            holder.overdueBadge.setVisibility(View.GONE);
        }

        if (item.isDone()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
            holder.btnMarkAsDone.setVisibility(View.GONE);
            holder.btnCancelDelivery.setText("Delete");
            holder.btnCancelDelivery.setOnClickListener(v -> {
                if (listener != null) listener.onCancelClicked(item);
            });
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.btnMarkAsDone.setVisibility(View.VISIBLE);
            holder.btnCancelDelivery.setText("Cancel");
            holder.btnCancelDelivery.setOnClickListener(v -> {
                if (listener != null) listener.onCancelClicked(item);
            });
        }

        holder.btnMarkAsDone.setOnClickListener(v -> {
            if (listener != null) listener.onMarkAsDoneClicked(item);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class DeliveryViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, name, schedule, location, overdueBadge;
        ImageView deliveryIcon;
        Button btnMarkAsDone, btnCancelDelivery;

        DeliveryViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.orderNumber);
            name = itemView.findViewById(R.id.name);
            schedule = itemView.findViewById(R.id.schedule);
            location = itemView.findViewById(R.id.location);
            deliveryIcon = itemView.findViewById(R.id.deliveryIcon);
            btnMarkAsDone = itemView.findViewById(R.id.btnMarkAsDone);
            btnCancelDelivery = itemView.findViewById(R.id.btnCancelDelivery);
            overdueBadge = itemView.findViewById(R.id.overdueBadge);
        }
    }
}
