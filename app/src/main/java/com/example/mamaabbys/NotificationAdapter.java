package com.example.mamaabbys;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<NotificationItem> notifications;
    private OnNotificationDeleteListener deleteListener;
    private OnNotificationReadListener readListener;

    public interface OnNotificationDeleteListener {
        void onDeleteNotification(NotificationItem notification);
    }

    public interface OnNotificationReadListener {
        void onMarkAsRead(NotificationItem notification);
    }

    public NotificationAdapter(List<NotificationItem> notifications, 
                             OnNotificationDeleteListener deleteListener,
                             OnNotificationReadListener readListener) {
        this.notifications = notifications;
        this.deleteListener = deleteListener;
        this.readListener = readListener;
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

        // Set background color based on read status
        if (notification.isRead()) {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            holder.unreadBadge.setVisibility(View.GONE);
            holder.markAsReadButton.setVisibility(View.GONE);
        } else {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
            holder.unreadBadge.setVisibility(View.VISIBLE);
            holder.markAsReadButton.setVisibility(View.VISIBLE);
        }

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteNotification(notification);
            }
        });

        holder.markAsReadButton.setOnClickListener(v -> {
            if (readListener != null) {
                readListener.onMarkAsRead(notification);
                // Update badge visibility immediately
                holder.unreadBadge.setVisibility(View.GONE);
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
        CardView cardView;
        View unreadBadge;
        TextView titleTextView;
        TextView messageTextView;
        TextView orderDetailsTextView;
        TextView dateTimeTextView;
        ImageButton deleteButton;
        Button markAsReadButton;

        NotificationViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            unreadBadge = itemView.findViewById(R.id.unreadBadge);
            titleTextView = itemView.findViewById(R.id.notificationTitle);
            messageTextView = itemView.findViewById(R.id.notificationMessage);
            orderDetailsTextView = itemView.findViewById(R.id.orderDetails);
            dateTimeTextView = itemView.findViewById(R.id.deliveryDateTime);
            deleteButton = itemView.findViewById(R.id.btnDeleteNotification);
            markAsReadButton = itemView.findViewById(R.id.btnMarkAsRead);
        }
    }
} 