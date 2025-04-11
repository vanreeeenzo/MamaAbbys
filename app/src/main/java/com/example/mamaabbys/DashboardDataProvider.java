package com.example.mamaabbys;

import java.util.ArrayList;
import java.util.List;

public class DashboardDataProvider {
    public static List<DashboardItem> getOverviewItems() {
        List<DashboardItem> items = new ArrayList<>();
        items.add(new DashboardItem("1", "Daily Tasks", "View your daily tasks", R.drawable.ic_tasks));
        items.add(new DashboardItem("2", "Appointments", "Upcoming appointments", R.drawable.ic_calendar));
        items.add(new DashboardItem("3", "Messages", "Unread messages", R.drawable.ic_message));
        return items;
    }

    public static List<DashboardItem> getTasksItems() {
        List<DashboardItem> items = new ArrayList<>();
        items.add(new DashboardItem("4", "Pending Tasks", "Tasks waiting for approval", R.drawable.ic_pending));
        items.add(new DashboardItem("5", "Completed Tasks", "Recently completed tasks", R.drawable.ic_completed));
        items.add(new DashboardItem("6", "Overdue Tasks", "Tasks past due date", R.drawable.ic_overdue));
        return items;
    }

    public static List<DashboardItem> getReportsItems() {
        List<DashboardItem> items = new ArrayList<>();
        items.add(new DashboardItem("7", "Daily Report", "Today's activity summary", R.drawable.ic_report));
        items.add(new DashboardItem("8", "Weekly Report", "This week's summary", R.drawable.ic_chart));
        items.add(new DashboardItem("9", "Monthly Report", "Monthly performance", R.drawable.ic_stats));
        return items;
    }
} 