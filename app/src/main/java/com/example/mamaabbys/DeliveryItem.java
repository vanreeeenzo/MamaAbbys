package com.example.mamaabbys;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DeliveryItem {
    private String id;
    private String name;
    private String orderNumber;
    private String schedule;
    private String location;
    private int iconResId;
    private boolean isDone;

    public DeliveryItem(String id, String name, String orderNumber, String schedule, String location, int iconResId) {
        this.id = id;
        this.name = name;
        this.orderNumber = orderNumber;
        this.schedule = schedule;
        this.location = location;
        this.iconResId = iconResId;
        this.isDone = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getLocation() {
        return location;
    }

    public int getIconResId() {
        return iconResId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        this.isDone = done;
    }

    public boolean isOverdue() {
        try {
            // Extract date and time from schedule string (format: "Scheduled for yyyy-MM-dd at HH:mm")
            String[] parts = schedule.split("for ")[1].split(" at ");
            String dateStr = parts[0];
            String timeStr = parts[1];
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date deliveryDateTime = sdf.parse(dateStr + " " + timeStr);
            Date currentDateTime = new Date();
            
            // Return true if delivery date/time is before current date/time and not done
            return deliveryDateTime.before(currentDateTime) && !isDone;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
