package com.example.mamaabbys;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationDeleteListener {
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

        // Setup RecyclerView
        recyclerView = findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationItems, this);
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
                String orderDetails = "Order: " + delivery.getOrderDescription();

                if (daysLeft == 7) {
                    notificationTitle = "Delivery in 7 days";
                    notificationMessage = "Your order is scheduled for delivery in 7 days on " + delivery.getDeliveryDate() + " at " + delivery.getDeliveryTime();
                } else if (daysLeft == 3) {
                    notificationTitle = "Delivery in 3 days";
                    notificationMessage = "Your order is scheduled for delivery in 3 days on " + delivery.getDeliveryDate() + " at " + delivery.getDeliveryTime();
                } else if (daysLeft == 0) {
                    notificationTitle = "Delivery today!";
                    notificationMessage = "Your order is scheduled for delivery today at " + delivery.getDeliveryTime();
                }

                if (!notificationTitle.isEmpty() && !notificationMessage.isEmpty()) {
                    NotificationItem notificationItem = new NotificationItem(
                        delivery.getId(),
                        notificationTitle,
                        notificationMessage,
                        delivery.getDeliveryDate(),
                        delivery.getDeliveryTime(),
                        orderDetails
                    );
                    notificationItems.add(notificationItem);
                    
                    // Show system notification
                    NotificationHelper.showNotification(this, notificationTitle, notificationMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
}
