package com.example.mamaabbys;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<NotificationItem> notifications;
    private OnNotificationDeleteListener deleteListener;

    public interface OnNotificationDeleteListener {
        void onDeleteNotification(NotificationItem notification);
    }

    public NotificationAdapter(List<NotificationItem> notifications, OnNotificationDeleteListener listener) {
        this.notifications = notifications;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem notification = notifications.get(position);
        holder.titleTextView.setText(notification.getTitle());
        holder.messageTextView.setText(notification.getMessage());
        holder.orderDetailsTextView.setText(notification.getOrderDetails());
        holder.dateTimeTextView.setText(notification.getDeliveryDate() + " at " + notification.getDeliveryTime());

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteNotification(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateNotifications(List<NotificationItem> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView messageTextView;
        TextView orderDetailsTextView;
        TextView dateTimeTextView;
        ImageButton deleteButton;

        NotificationViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notificationTitle);
            messageTextView = itemView.findViewById(R.id.notificationMessage);
            orderDetailsTextView = itemView.findViewById(R.id.orderDetails);
            dateTimeTextView = itemView.findViewById(R.id.deliveryDateTime);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 