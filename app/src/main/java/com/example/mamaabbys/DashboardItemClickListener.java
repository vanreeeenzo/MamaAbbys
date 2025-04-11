package com.example.mamaabbys;

import android.content.Context;
import android.widget.Toast;

public class DashboardItemClickListener {
    private final Context context;

    public DashboardItemClickListener(Context context) {
        this.context = context;
    }

    public void handleItemClick(DashboardItem item, int tabPosition) {
        String message;
        switch (tabPosition) {
            case 0: // Overview
                message = "Overview: " + item.getTitle();
                break;
            case 1: // Tasks
                message = "Tasks: " + item.getTitle();
                break;
            case 2: // Reports
                message = "Reports: " + item.getTitle();
                break;
            default:
                message = "Unknown tab: " + item.getTitle();
        }
        showToast(message);
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
} 