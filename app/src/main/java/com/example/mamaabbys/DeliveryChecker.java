package com.example.mamaabbys;

import android.Manifest;
import android.content.Context;

import androidx.annotation.RequiresPermission;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeliveryChecker {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public static void checkDeliveries(Context context, List<Delivery> deliveries) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();

        for (Delivery delivery : deliveries) {
            try {
                Date deliveryDate = sdf.parse(delivery.getDeliveryDate());
                if (deliveryDate != null) {
                    long diffInMillis = deliveryDate.getTime() - today.getTime();
                    long daysLeft = diffInMillis / (1000 * 60 * 60 * 24);

                    String message = null;

                    if (daysLeft == 7) {
                        message = "Your delivery is scheduled for " + delivery.getDeliveryDate() +
                                ".\nOrder Details: " + delivery.getOrderDescription();
                    } else if (daysLeft == 3) {
                        message = "Reminder: Your delivery is coming in 3 days (" + delivery.getDeliveryDate() + ").\n" +
                                "Order Details: " + delivery.getOrderDescription();
                    } else if (daysLeft == 0) {
                        message = "Today is the delivery day!\nOrder Details: " + delivery.getOrderDescription();
                    }

                    if (message != null) {
                        NotificationHelper.showNotification(context, delivery.getOrderDescription(), message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
