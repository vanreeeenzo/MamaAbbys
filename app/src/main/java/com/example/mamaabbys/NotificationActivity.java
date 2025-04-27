package com.example.mamaabbys;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {

    private MyDataBaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        myDB = new MyDataBaseHelper(this);


        List<Delivery> deliveries = myDB.getAllDeliveries();


        checkAndNotifyDeliveries(deliveries);
    }

    private void checkAndNotifyDeliveries(List<Delivery> deliveries) {
        for (Delivery delivery : deliveries) {
            try {

                long daysLeft = calculateDaysLeft(delivery);

                String notificationTitle = "";
                String notificationMessage = "";


                if (daysLeft == 7) {
                    notificationTitle = "Delivery in 7 days";
                    notificationMessage = "Your order " + delivery.getOrderDescription() + " is scheduled for delivery on " + delivery.getDeliveryDate() + " at " + delivery.getDeliveryTime();
                } else if (daysLeft == 3) {
                    notificationTitle = "Delivery in 3 days";
                    notificationMessage = "Your order " + delivery.getOrderDescription() + " is scheduled for delivery on " + delivery.getDeliveryDate() + " at " + delivery.getDeliveryTime();
                } else if (daysLeft == 1) {
                    notificationTitle = "Delivery tomorrow";
                    notificationMessage = "Your order " + delivery.getOrderDescription() + " is scheduled for delivery tomorrow at " + delivery.getDeliveryTime();
                } else if (daysLeft == 0) {
                    notificationTitle = "Delivery today!";
                    notificationMessage = "Your order " + delivery.getOrderDescription() + " is scheduled for delivery today at " + delivery.getDeliveryTime();
                }


                if (!notificationTitle.isEmpty() && !notificationMessage.isEmpty()) {

                    NotificationHelper.showNotification(this, notificationTitle, notificationMessage);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private long calculateDaysLeft(Delivery delivery) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date deliveryDate = sdf.parse(delivery.getDeliveryDate());
            Date today = new Date();

            if (deliveryDate != null) {
                long diffInMillis = deliveryDate.getTime() - today.getTime();
                return diffInMillis / (1000 * 60 * 60 * 24); // Return difference in days
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Long.MAX_VALUE;
    }
}
