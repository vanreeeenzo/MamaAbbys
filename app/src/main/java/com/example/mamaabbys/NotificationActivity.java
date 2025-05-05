package com.example.mamaabbys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity implements 
    NotificationAdapter.OnNotificationDeleteListener,
    NotificationAdapter.OnNotificationReadListener {

    private MyDataBaseHelper myDB;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationItem> notificationItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        myDB = new MyDataBaseHelper(this);
        notificationItems = new ArrayList<>();

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Setup RecyclerView
        recyclerView = findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationItems, this, this);
        recyclerView.setAdapter(adapter);

        // Load and check deliveries
        checkAndNotifyDeliveries(myDB.getAllDeliveries());
    }

    private void checkAndNotifyDeliveries(List<Delivery> deliveries) {
        notificationItems.clear();
        for (Delivery delivery : deliveries) {
            try {
                // Skip if notification was previously deleted
                if (myDB.isNotificationDeleted(delivery.getId())) {
                    continue;
                }

                long daysLeft = calculateDaysLeft(delivery);
                String notificationTitle = "";
                String notificationMessage = "";
                String orderDetails = "Order: " + delivery.getOrderDescription() + "\nLocation: " + delivery.getLocation();

                if (daysLeft == 7) {
                    notificationTitle = "Delivery in 7 days";
                    notificationMessage = "Your order is scheduled for delivery in 7 days on " + delivery.getDeliveryDate() + " at " + delivery.getDeliveryTime() + "\nLocation: " + delivery.getLocation();
                } else if (daysLeft == 3) {
                    notificationTitle = "Delivery in 3 days";
                    notificationMessage = "Your order is scheduled for delivery in 3 days on " + delivery.getDeliveryDate() + " at " + delivery.getDeliveryTime() + "\nLocation: " + delivery.getLocation();
                } else if (daysLeft == 0) {
                    notificationTitle = "Delivery today!";
                    notificationMessage = "Your order is scheduled for delivery today at " + delivery.getDeliveryTime() + "\nLocation: " + delivery.getLocation();
                }

                if (!notificationTitle.isEmpty() && !notificationMessage.isEmpty()) {
                    boolean isRead = myDB.isNotificationRead(delivery.getId());
                    NotificationItem notificationItem = new NotificationItem(
                        delivery.getId(),
                        notificationTitle,
                        notificationMessage,
                        delivery.getDeliveryDate(),
                        delivery.getDeliveryTime(),
                        orderDetails,
                        true // This is a delivery notification
                    );
                    notificationItem.setRead(isRead);
                    notificationItems.add(notificationItem);
                    
                    // Show system notification
                    NotificationHelper.showNotification(this, notificationTitle, notificationMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Sort notifications: unread first, then read
        notificationItems.sort((a, b) -> {
            if (a.isRead() == b.isRead()) {
                // If both are read or both are unread, sort by timestamp (newest first)
                return Long.compare(b.getTimestamp(), a.getTimestamp());
            }
            // Unread notifications come first
            return Boolean.compare(a.isRead(), b.isRead());
        });

        adapter.updateNotifications(notificationItems);
    }

    private long calculateDaysLeft(Delivery delivery) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date deliveryDate = sdf.parse(delivery.getDeliveryDate());
            
            // Get today's date at midnight
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // Set delivery date to midnight
            Calendar deliveryCalendar = Calendar.getInstance();
            deliveryCalendar.setTime(deliveryDate);
            deliveryCalendar.set(Calendar.HOUR_OF_DAY, 0);
            deliveryCalendar.set(Calendar.MINUTE, 0);
            deliveryCalendar.set(Calendar.SECOND, 0);
            deliveryCalendar.set(Calendar.MILLISECOND, 0);

            // Calculate difference in days
            long diffInMillis = deliveryCalendar.getTimeInMillis() - today.getTimeInMillis();
            long daysLeft = diffInMillis / (1000 * 60 * 60 * 24);

            // Only return positive days or 0
            return Math.max(0, daysLeft);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Long.MAX_VALUE;
    }

    @Override
    public void onDeleteNotification(NotificationItem notification) {
        // Mark notification as deleted in database
        if (myDB.markNotificationAsDeleted(notification.getId())) {
            // Remove from current list
            notificationItems.remove(notification);
            adapter.updateNotifications(notificationItems);
        }
    }

    @Override
    public void onMarkAsRead(NotificationItem notification) {
        // Mark notification as read in database
        if (myDB.markNotificationAsRead(notification.getId())) {
            // Update local state
            notification.setRead(true);
            // Re-sort the list
            notificationItems.sort((a, b) -> {
                if (a.isRead() == b.isRead()) {
                    return Long.compare(b.getTimestamp(), a.getTimestamp());
                }
                return Boolean.compare(a.isRead(), b.isRead());
            });
            adapter.updateNotifications(notificationItems);
            
            // Broadcast that a notification was marked as read
            Intent intent = new Intent("com.example.mamaabbys.NOTIFICATION_READ");
            sendBroadcast(intent);
        }
    }
}
