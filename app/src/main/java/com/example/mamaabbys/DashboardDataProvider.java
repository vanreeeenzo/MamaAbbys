package com.example.mamaabbys;

import java.util.ArrayList;
import java.util.List;

public class DashboardDataProvider {
    public static List<DashboardItem> getOverviewItems() {
        List<DashboardItem> items = new ArrayList<>();
        items.add(new DashboardItem("1", "Daily Tasks", "View your daily tasks", "Overview", R.drawable.ic_tasks));
        items.add(new DashboardItem("2", "Appointments", "Upcoming appointments", "Overview", R.drawable.ic_calendar));
        items.add(new DashboardItem("3", "Messages", "Unread messages", "Overview", R.drawable.ic_message));
        return items;
    }

    public static List<DashboardItem> getTasksItems() {
        List<DashboardItem> items = new ArrayList<>();
        items.add(new DashboardItem("4", "Pending Tasks", "Tasks waiting for approval", "Tasks", R.drawable.ic_pending));
        items.add(new DashboardItem("5", "Completed Tasks", "Recently completed tasks", "Tasks", R.drawable.ic_completed));
        items.add(new DashboardItem("6", "Overdue Tasks", "Tasks past due date", "Tasks", R.drawable.ic_overdue));
        return items;
    }

    public static List<DashboardItem> getReportsItems() {
        List<DashboardItem> items = new ArrayList<>();
        items.add(new DashboardItem("7", "Daily Report", "Today's activity summary", "Reports", R.drawable.ic_report));
        items.add(new DashboardItem("8", "Weekly Report", "This week's summary", "Reports", R.drawable.ic_chart));
        items.add(new DashboardItem("9", "Monthly Report", "Monthly performance", "Reports", R.drawable.ic_stats));
        return items;
    }

    public static List<DashboardItem> getInventoryItems() {
        List<DashboardItem> items = new ArrayList<>();
        items.add(new DashboardItem("1", "Rice", "25kg of rice", "Grains", R.drawable.ic_package));
        items.add(new DashboardItem("2", "Beans", "50kg of beans", "Grains", R.drawable.ic_package));
        items.add(new DashboardItem("3", "Cooking Oil", "20L vegetable oil", "Oils", R.drawable.ic_package));
        items.add(new DashboardItem("4", "Sugar", "5kg refined sugar", "Sweeteners", R.drawable.ic_package));
        items.add(new DashboardItem("5", "Salt", "2kg iodized salt", "Seasonings", R.drawable.ic_package));
        return items;
    }

    public static List<DashboardItem> getSalesItems() {
        List<DashboardItem> items = new ArrayList<>();
        items.add(new DashboardItem("6", "Today's Sales", "View today's sales summary", "Reports", R.drawable.ic_chart));
        items.add(new DashboardItem("7", "Weekly Sales", "View weekly sales report", "Reports", R.drawable.ic_chart));
        items.add(new DashboardItem("8", "Monthly Sales", "View monthly sales analysis", "Reports", R.drawable.ic_chart));
        return items;
    }

    public static List<DashboardItem> getDeliveryItems() {
        List<DashboardItem> items = new ArrayList<>();
        items.add(new DashboardItem("9", "Pending Deliveries", "View pending deliveries", "Delivery", R.drawable.ic_truck));
        items.add(new DashboardItem("10", "Completed Deliveries", "View completed deliveries", "Delivery", R.drawable.ic_truck));
        items.add(new DashboardItem("11", "Schedule Delivery", "Schedule new delivery", "Delivery", R.drawable.ic_truck));
        return items;
    }
} 