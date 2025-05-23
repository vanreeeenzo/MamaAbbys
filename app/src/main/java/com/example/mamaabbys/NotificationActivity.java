package com.example.mamaabbys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

        // Load and check deliveries and stock
        loadNotifications();
        checkStockStatus();
    }

    private void loadNotifications() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Delivery> deliveries = myDB.getAllDeliveries(userId);
        notificationItems.clear();

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
                
                // Show system notification
                NotificationHelper.showNotification(this, notificationTitle, notificationMessage);
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

    private void checkStockStatus() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<InventoryItem> inventoryItems = myDB.getAllInventoryItems(userId);
        for (InventoryItem item : inventoryItems) {
            String stockInfo = item.getStockInfo();
            if (stockInfo.contains("Out of Stock!")) {
                createOutOfStockNotification(item);
            } else if (stockInfo.contains("Low Stock!")) {
                createLowStockNotification(item);
            }
        }
    }

    private void createOutOfStockNotification(InventoryItem item) {
        String notificationId = "stock_" + item.getId();
        if (myDB.isNotificationDeleted(notificationId, sessionManager.getUserId())) {
            return;
        }

        String title = "Out of Stock Alert";
        String message = item.getName() + " is out of stock! Please restock soon.";
        
        NotificationItem notificationItem = new NotificationItem(
            notificationId,
            title,
            message,
            item.getName(),
            item.getQuantity(),
            item.getMinThreshold()
        );
        
        notificationItems.add(notificationItem);
        NotificationHelper.showStockNotification(this, title, message);
    }

    private void createLowStockNotification(InventoryItem item) {
        String notificationId = "stock_" + item.getId();
        if (myDB.isNotificationDeleted(notificationId, sessionManager.getUserId())) {
            return;
        }

        String title = "Low Stock Alert";
        String message = item.getName() + " is running low on stock! Current stock: " + 
                        item.getQuantity() + " (Minimum threshold: " + item.getMinThreshold() + ")";
        
        NotificationItem notificationItem = new NotificationItem(
            notificationId,
            title,
            message,
            item.getName(),
            item.getQuantity(),
            item.getMinThreshold()
        );
        
        notificationItems.add(notificationItem);
        NotificationHelper.showStockNotification(this, title, message);
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
            adapter.updateNotifications(notificationItems);
            Toast.makeText(this, "Marked as read", Toast.LENGTH_SHORT).show();
            
            // Broadcast that a notification was marked as read
            Intent intent = new Intent("com.example.mamaabbys.NOTIFICATION_READ");
            sendBroadcast(intent);
        } else {
            Toast.makeText(this, "Failed to mark as read", Toast.LENGTH_SHORT).show();
        }
    }
}
