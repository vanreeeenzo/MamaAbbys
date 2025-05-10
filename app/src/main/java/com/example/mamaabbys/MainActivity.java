package com.example.mamaabbys;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY_CURRENT_POSITION = "current_position";
    
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private DashboardPagerAdapter pagerAdapter;
    private FloatingActionButton fab;
    private MaterialButton deleteAllButton;
    private MyDataBaseHelper dbHelper;
    private ImageButton notificationButton;
    private TextView notificationBadge;
    private BroadcastReceiver notificationReadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initializeViews();
            setupViewPager();
            setupClickListeners();
            setupPageChangeListener();
            setupNotificationButton();
            setupNotificationReceiver();
            
            // Initialize Firebase in background with proper error handling
            initializeFirebase();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Error initializing app", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeFirebase() {
        new Thread(() -> {
            try {
                FirebaseDatabase.getInstance().getReference()
                    .child("Inventory")
                    .child("Meat")
                    .setValue("TJ Hotdog")
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Firebase operation failed: " + task.getException());
                        }
                    });
            } catch (Exception e) {
                Log.e(TAG, "Error initializing Firebase: " + e.getMessage());
            }
        }).start();
    }

    private void initializeViews() {
        try {
            notificationButton = findViewById(R.id.notificationButton);
            notificationBadge = findViewById(R.id.notificationBadge);
            ImageButton settingsButton = findViewById(R.id.settingsButton);
            viewPager = findViewById(R.id.viewPager);
            tabLayout = findViewById(R.id.tabLayout);
            fab = findViewById(R.id.quickActionsBar);
            deleteAllButton = findViewById(R.id.deleteAllButton);
            dbHelper = new MyDataBaseHelper(this);

            // Setup settings button click listener
            settingsButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error starting SettingsActivity: " + e.getMessage());
                    Toast.makeText(this, "Error opening settings", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            throw e;
        }
    }

    private void setupViewPager() {
        pagerAdapter = new DashboardPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Inventory");
                    tab.setIcon(R.drawable.ic_package);
                    break;
                case 1:
                    tab.setText("Sales");
                    tab.setIcon(R.drawable.ic_chart);
                    break;
                case 2:
                    tab.setText("Delivery");
                    tab.setIcon(R.drawable.ic_truck);
                    break;
            }
        }).attach();
    }

    private void setupClickListeners() {
        fab.setOnClickListener(v -> {
            int currentPosition = viewPager.getCurrentItem();
            if (currentPosition == 0) {
                startActivity(new Intent(MainActivity.this, AddInventory.class));
            } else if (currentPosition == 2) { // Delivery tab
                startActivity(new Intent(MainActivity.this, AddDeliveryActivity.class));
            }
        });

        deleteAllButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void setupPageChangeListener() {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateUIForPosition(position);
            }
        });
    }

    private void updateUIForPosition(int position) {
        if (position == 0) {
            fab.show();
            deleteAllButton.setVisibility(View.VISIBLE);
        } else if (position == 2) { // Delivery tab
            fab.hide();
            deleteAllButton.setVisibility(View.GONE);
        } else {
            fab.hide();
            deleteAllButton.setVisibility(View.GONE);
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Delete All Items")
            .setMessage("Are you sure you want to delete all inventory items? This action cannot be undone.")
            .setPositiveButton("Delete", (dialog, which) -> {
                try {
                    dbHelper.deleteAllInventory();
                    refreshInventoryFragment();
                    Toast.makeText(this, "All items deleted successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, "Error deleting inventory: " + e.getMessage());
                    Toast.makeText(this, "Error deleting items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void refreshInventoryFragment() {
        try {
            Fragment currentFragment = getSupportFragmentManager()
                .findFragmentByTag("f" + viewPager.getCurrentItem());
            
            if (currentFragment instanceof InventoryFragment) {
                ((InventoryFragment) currentFragment).loadInventoryData();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing inventory fragment: " + e.getMessage());
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, viewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int position = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
        viewPager.setCurrentItem(position, false);
        updateUIForPosition(position);
    }

    private void setupNotificationButton() {
        if (notificationButton != null) {
            notificationButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error starting NotificationActivity: " + e.getMessage());
                    Toast.makeText(this, "Error opening notifications", Toast.LENGTH_SHORT).show();
                }
            });
            
            // Check for unread notifications initially
            updateNotificationBadge();
        }
    }

    private void updateNotificationBadge() {
        try {
            if (notificationBadge == null || dbHelper == null) {
                return;
            }

            // Get all notifications from database
            List<Delivery> deliveries = dbHelper.getAllDeliveries();
            boolean hasUnreadNotifications = false;

            for (Delivery delivery : deliveries) {
                // Skip if notification was previously deleted
                if (dbHelper.isNotificationDeleted(delivery.getId())) {
                    continue;
                }

                // Check if notification is unread
                if (!dbHelper.isNotificationRead(delivery.getId())) {
                    hasUnreadNotifications = true;
                    break;
                }
            }

            // Update badge visibility
            if (notificationBadge != null) {
                notificationBadge.setVisibility(hasUnreadNotifications ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating notification badge: " + e.getMessage());
        }
    }

    private void setupNotificationReceiver() {
        try {
            notificationReadReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if ("com.example.mamaabbys.NOTIFICATION_READ".equals(intent.getAction())) {
                        updateNotificationBadge();
                    }
                }
            };
            
            IntentFilter filter = new IntentFilter("com.example.mamaabbys.NOTIFICATION_READ");
            registerReceiver(notificationReadReceiver, filter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up notification receiver: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            // Update badge when returning to MainActivity
            updateNotificationBadge();
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (notificationReadReceiver != null) {
                unregisterReceiver(notificationReadReceiver);
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage());
        }
    }
}

