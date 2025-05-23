package com.example.mamaabbys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.Toast;
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
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        myDB = new MyDataBaseHelper(this);
        notificationItems = new ArrayList<>();
        sessionManager = new SessionManager(this);

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Setup RecyclerView
        recyclerView = findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationItems, this, this);
        recyclerView.setAdapter(adapter);

        // Load notifications including both delivery and stock notifications
        loadAllNotifications();
    }

    private void loadAllNotifications() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        notificationItems.clear();

        // Load delivery notifications
        loadDeliveryNotifications(userId);

        // Load stock notifications
        loadStockNotifications(userId);

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

    private void loadDeliveryNotifications(int userId) {
        List<Delivery> deliveries = myDB.getAllDeliveries(userId);

        for (Delivery delivery : deliveries) {
            // Skip if notification was previously deleted
            if (myDB.isNotificationDeleted(delivery.getId(), userId)) {
                continue;
            }

            long daysLeft = calculateDaysLeft(delivery);
            String notificationTitle = "";
            String notificationMessage = "";
            String orderDetails = "Order: " + delivery.getOrderDescription() + "\nLocation: " + delivery.getLocation();

            if (daysLeft == 7) {
                notificationTitle = "Delivery To: " + delivery.getDeliveryName() + " (7 days)";
                notificationMessage = "Your order is scheduled for delivery in 7 days on " + delivery.getDeliveryDate() + " at " + delivery.getDeliveryTime() + "\nLocation: " + delivery.getLocation();
            } else if (daysLeft == 3) {
                notificationTitle = "Delivery To: " + delivery.getDeliveryName() + " (3 days)";
                notificationMessage = "Your order is scheduled for delivery in 3 days on " + delivery.getDeliveryDate() + " at " + delivery.getDeliveryTime() + "\nLocation: " + delivery.getLocation();
            } else if (daysLeft == 0) {
                notificationTitle = "Delivery To: " + delivery.getDeliveryName() + " (Today)";
                notificationMessage = "Your order is scheduled for delivery today at " + delivery.getDeliveryTime() + "\nLocation: " + delivery.getLocation();
            }

            if (!notificationTitle.isEmpty() && !notificationMessage.isEmpty()) {
                boolean isRead = myDB.isNotificationRead(delivery.getId(), userId);
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

                // Show system notification only if not read and not deleted
                if (!isRead) {
                    NotificationHelper.showNotification(this, notificationTitle, notificationMessage);
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void loadStockNotifications(int userId) {
        List<InventoryItem> inventoryItems = myDB.getAllInventoryItems(userId);

        for (InventoryItem item : inventoryItems) {
            String stockInfo = item.getStockInfo();

            if (stockInfo.contains("Out of Stock!")) {
                createStockNotificationIfNeeded(item, "out", userId);
            } else if (stockInfo.contains("Low Stock!")) {
                createStockNotificationIfNeeded(item, "low", userId);
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void createStockNotificationIfNeeded(InventoryItem item, String type, int userId) {
        String notificationId = "stock_" + type + "_" + item.getId();

        // Skip if notification was previously deleted
        if (myDB.isNotificationDeleted(notificationId, userId)) {
            return;
        }

        String title = type.equals("out") ? "Out of Stock Alert" : "Low Stock Alert";
        String message;

        if (type.equals("out")) {
            message = item.getName() + " is out of stock! Please restock soon.";
        } else {
            message = item.getName() + " is running low on stock! Current stock: ";
        }

        NotificationItem notificationItem = new NotificationItem(
                notificationId,
                title,
                message,
                item.getName(),
                item.getQuantity(),
                item.getMinThreshold()
        );

        // Check if this notification was already read
        boolean isRead = myDB.isNotificationRead(notificationId, userId);
        notificationItem.setRead(isRead);

        notificationItems.add(notificationItem);

        // Show system notification only if not read
        if (!isRead) {
            NotificationHelper.showStockNotification(this, title, message);
        }
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
        deleteNotification(notification);
    }

    @Override
    public void onMarkAsRead(NotificationItem notification) {
        markAsRead(notification);
    }

    private void deleteNotification(NotificationItem notification) {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (myDB.markNotificationAsDeleted(notification.getId(), userId)) {
            notificationItems.remove(notification);
            adapter.updateNotifications(notificationItems);
            Toast.makeText(this, "Notification deleted", Toast.LENGTH_SHORT).show();

            // Broadcast that a notification was deleted
            Intent intent = new Intent("com.example.mamaabbys.NOTIFICATION_DELETED");
            sendBroadcast(intent);
        } else {
            Toast.makeText(this, "Failed to delete notification", Toast.LENGTH_SHORT).show();
        }
    }

    private void markAsRead(NotificationItem notification) {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (myDB.markNotificationAsRead(notification.getId(), userId)) {
            notification.setRead(true);

            // Re-sort the list to move read notification to the bottom
            notificationItems.sort((a, b) -> {
                if (a.isRead() == b.isRead()) {
                    return Long.compare(b.getTimestamp(), a.getTimestamp());
                }
                return Boolean.compare(a.isRead(), b.isRead());
            });

            adapter.updateNotifications(notificationItems);
            Toast.makeText(this, "Marked as read", Toast.LENGTH_SHORT).show();

            // Broadcast that a notification was marked as read
            Intent intent = new Intent("com.example.mamaabbys.NOTIFICATION_READ");
            sendBroadcast(intent);
        } else {
            Toast.makeText(this, "Failed to mark as read", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh notifications when returning to this activity
        loadAllNotifications();
    }

    // Method to get unread notification count for badge
    public int getUnreadNotificationCount() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            return 0;
        }

        int unreadCount = 0;

        // Count unread delivery notifications
        List<Delivery> deliveries = myDB.getAllDeliveries(userId);
        for (Delivery delivery : deliveries) {
            if (!myDB.isNotificationDeleted(delivery.getId(), userId)) {
                long daysLeft = calculateDaysLeft(delivery);
                if (daysLeft == 7 || daysLeft == 3 || daysLeft == 0) {
                    if (!myDB.isNotificationRead(delivery.getId(), userId)) {
                        unreadCount++;
                    }
                }
            }
        }

        // Count unread stock notifications
        List<InventoryItem> inventoryItems = myDB.getAllInventoryItems(userId);
        for (InventoryItem item : inventoryItems) {
            String stockInfo = item.getStockInfo();

            if (stockInfo.contains("Out of Stock!")) {
                String notificationId = "stock_out_" + item.getId();
                if (!myDB.isNotificationDeleted(notificationId, userId) &&
                        !myDB.isNotificationRead(notificationId, userId)) {
                    unreadCount++;
                }
            } else if (stockInfo.contains("Low Stock!")) {
                String notificationId = "stock_low_" + item.getId();
                if (!myDB.isNotificationDeleted(notificationId, userId) &&
                        !myDB.isNotificationRead(notificationId, userId)) {
                    unreadCount++;
                }
            }
        }

        return unreadCount;
    }
}