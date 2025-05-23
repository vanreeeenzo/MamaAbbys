package com.example.mamaabbys;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationChannel;
import android.os.Build;
import androidx.activity.OnBackPressedCallback;
import android.app.NotificationManager;

import java.io.File;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY_CURRENT_POSITION = "current_position";
    
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private DashboardPagerAdapter pagerAdapter;
    private FloatingActionButton fab;
    private MaterialButton deleteAllButton;
    private MaterialButton cancelButton;
    private MyDataBaseHelper dbHelper;
    private ImageButton notificationButton;
    private TextView notificationBadge;
    private TextView welcomeText;
    private BroadcastReceiver notificationReadReceiver;
    private BroadcastReceiver refreshReceiver;
    private SessionManager sessionManager;
    private static final String CHANNEL_ID = "mamaabbys_channel_01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            // Set content view first
            setContentView(R.layout.activity_main);
            
            // Initialize session manager
            sessionManager = new SessionManager(this);
            
            // Initialize database with retry mechanism
            initializeDatabase();
            
            // Initialize views after database
            initializeViews();
            
            // Setup UI components
            setupViewPager();
            setupClickListeners();
            setupPageChangeListener();
            setupNotificationButton();
            setupNotificationReceiver();
            
            // Initialize Firebase in background
            initializeFirebase();
            
            // Initialize the refresh receiver
            initializeRefreshReceiver();
            
            // Update welcome text with username
            updateWelcomeText();
            
            Log.d(TAG, "MainActivity initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            handleInitializationError(e);
        }
    }

    private void updateWelcomeText() {
        String username = sessionManager.getUsername();
        if (welcomeText != null && username != null && !username.isEmpty()) {
            welcomeText.setText("Welcome back, " + username);
        }
    }

    private void initializeDatabase() {
        int maxRetries = 3;
        int retryCount = 0;
        Exception lastError = null;

        while (retryCount < maxRetries) {
            try {
                if (dbHelper != null) {
                    dbHelper.close();
                }
                dbHelper = new MyDataBaseHelper(this);
                if (dbHelper == null) {
                    throw new RuntimeException("Failed to initialize database");
                }
                // Test database connection
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                if (db == null) {
                    throw new RuntimeException("Failed to get readable database");
                }
                db.close();
                return; // Success
            } catch (Exception e) {
                lastError = e;
                retryCount++;
                Log.e(TAG, "Database initialization attempt " + retryCount + " failed: " + e.getMessage());
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(1000); // Wait 1 second before retrying
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        throw new RuntimeException("Failed to initialize database after " + maxRetries + " attempts: " + 
            (lastError != null ? lastError.getMessage() : "Unknown error"));
    }

    private void handleInitializationError(Exception e) {
        String errorMessage = "Error initializing app: " + e.getMessage();
        Log.e(TAG, errorMessage, e);
        
        // Try to clean up resources
        cleanupResources();
        
        // Show error dialog to user
        new AlertDialog.Builder(this)
            .setTitle("Initialization Error")
            .setMessage("The app encountered an error while starting up. Would you like to:\n\n" +
                       "1. Try again (Recommended)\n" +
                       "2. Clear app data and try again\n" +
                       "3. Close the app")
            .setPositiveButton("Try Again", (dialog, which) -> {
                recreate();
            })
            .setNeutralButton("Clear Data", (dialog, which) -> {
                clearAppData();
            })
            .setNegativeButton("Close", (dialog, which) -> {
                finish();
            })
            .setCancelable(false)
            .show();
    }

    private void cleanupResources() {
        try {
            if (dbHelper != null) {
                dbHelper.close();
                dbHelper = null;
            }
            if (notificationReadReceiver != null) {
                unregisterReceiver(notificationReadReceiver);
                notificationReadReceiver = null;
            }
            if (refreshReceiver != null) {
                unregisterReceiver(refreshReceiver);
                refreshReceiver = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during cleanup: " + e.getMessage());
        }
    }

    private void clearAppData() {
        try {
            // Clear database
            if (dbHelper != null) {
                dbHelper.close();
                dbHelper = null;
            }
            deleteDatabase(MyDataBaseHelper.DATABASE_NAME);
            
            // Clear shared preferences
            getSharedPreferences("app_preferences", MODE_PRIVATE).edit().clear().apply();
            
            // Clear cache
            File cacheDir = getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                deleteRecursive(cacheDir);
            }
            
            // Restart app
            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error clearing app data: " + e.getMessage());
            Toast.makeText(this, "Error clearing app data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    private void initializeRefreshReceiver() {
        try {
            refreshReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if ("com.example.mamaabbys.REFRESH_INVENTORY".equals(intent.getAction())) {
                        runOnUiThread(() -> refreshInventoryList());
                    }
                }
            };
            registerReceiver(refreshReceiver, new IntentFilter("com.example.mamaabbys.REFRESH_INVENTORY"));
        } catch (Exception e) {
            Log.e(TAG, "Error initializing refresh receiver: " + e.getMessage());
            // Don't throw, as this is not critical
        }
    }

    private void initializeFirebase() {
        new Thread(() -> {
            try {
                if (FirebaseDatabase.getInstance() != null) {
                    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                    FirebaseDatabase.getInstance().getReference()
                        .child("Inventory")
                        .child("Meat")
                        .setValue("TJ Hotdog")
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.e(TAG, "Firebase operation failed: " + task.getException());
                            }
                        });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error initializing Firebase: " + e.getMessage());
                // Don't show error to user as this is not critical
            }
        }).start();
    }

    private void initializeViews() {
        try {
            // Initialize all views first
            notificationButton = findViewById(R.id.notificationButton);
            notificationBadge = findViewById(R.id.notificationBadge);
            welcomeText = findViewById(R.id.welcomeText);
            ImageButton settingsButton = findViewById(R.id.settingsButton);
            viewPager = findViewById(R.id.viewPager);
            tabLayout = findViewById(R.id.tabLayout);
            fab = findViewById(R.id.quickActionsBar);
            deleteAllButton = findViewById(R.id.deleteAllButton);
            cancelButton = findViewById(R.id.cancelButton);

            if (notificationButton == null || notificationBadge == null || 
                settingsButton == null || viewPager == null || 
                tabLayout == null || fab == null || deleteAllButton == null ||
                welcomeText == null || cancelButton == null) {
                throw new RuntimeException("Failed to initialize views");
            }

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
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize views: " + e.getMessage());
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
            try {
                int currentPosition = viewPager.getCurrentItem();
                if (currentPosition == 0) {
                    Intent intent = new Intent(MainActivity.this, AddInventory.class);
                    startActivity(intent);
                } else if (currentPosition == 2) { // Delivery tab
                    Intent intent = new Intent(MainActivity.this, AddDeliveryActivity.class);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error launching activity: " + e.getMessage());
                Toast.makeText(MainActivity.this, "Error launching activity. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        deleteAllButton.setOnClickListener(v -> {
            InventoryFragment fragment = (InventoryFragment) getSupportFragmentManager()
                .findFragmentByTag("f" + viewPager.getCurrentItem());
            if (fragment != null) {
                InventoryAdapter adapter = fragment.getAdapter();
                if (adapter != null) {
                    if (adapter.isSelectionMode()) {
                        // If in selection mode, show delete confirmation for selected items
                        Set<String> selectedItems = adapter.getSelectedItems();
                        if (!selectedItems.isEmpty()) {
                            showDeleteSelectedItemsDialog(selectedItems);
                        } else {
                            Toast.makeText(this, "Please select items to delete", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Enter selection mode
                        adapter.setSelectionMode(true);
                        deleteAllButton.setText("Delete Selected");
                        cancelButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        cancelButton.setOnClickListener(v -> {
            InventoryFragment fragment = (InventoryFragment) getSupportFragmentManager()
                .findFragmentByTag("f" + viewPager.getCurrentItem());
            if (fragment != null) {
                InventoryAdapter adapter = fragment.getAdapter();
                if (adapter != null && adapter.isSelectionMode()) {
                    // Exit selection mode
                    adapter.setSelectionMode(false);
                    deleteAllButton.setText("Select");
                    cancelButton.setVisibility(View.GONE);
                }
            }
        });

        // Add back button press handler for selection mode
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InventoryFragment fragment = (InventoryFragment) getSupportFragmentManager()
                    .findFragmentByTag("f" + viewPager.getCurrentItem());
                if (fragment != null) {
                    InventoryAdapter adapter = fragment.getAdapter();
                    if (adapter != null && adapter.isSelectionMode()) {
                        // Exit selection mode
                        adapter.setSelectionMode(false);
                        deleteAllButton.setText("Select");
                        cancelButton.setVisibility(View.GONE);
                    } else {
                        // Let the system handle the back press
                        setEnabled(false);
                        getOnBackPressedDispatcher().onBackPressed();
                    }
                }
            }
        });
    }

    private void showDeleteSelectedItemsDialog(Set<String> selectedItems) {
        if (selectedItems == null || selectedItems.isEmpty()) {
            Toast.makeText(this, "Please select items to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
            .setTitle("Delete Selected Items")
            .setMessage("Are you sure you want to delete the selected items? This action cannot be undone.")
            .setPositiveButton("Delete", (dialog, which) -> {
                try {
                    // Delete items from database
                    dbHelper.deleteSelectedInventoryItems(selectedItems);
                    
                    // Refresh the inventory list
                    refreshInventoryFragment();
                    
                    // Exit selection mode
                    InventoryFragment fragment = (InventoryFragment) getSupportFragmentManager()
                        .findFragmentByTag("f" + viewPager.getCurrentItem());
                    if (fragment != null) {
                        InventoryAdapter adapter = fragment.getAdapter();
                        if (adapter != null) {
                            adapter.setSelectionMode(false);
                            deleteAllButton.setText("Select");
                            cancelButton.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error deleting selected items: " + e.getMessage(), e);
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("Failed to delete items:")) {
                        errorMessage = errorMessage.substring(errorMessage.indexOf(":") + 1).trim();
                    }
                    Toast.makeText(this, "Error deleting items: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void updateUIForPosition(int position) {
        if (position == 0) {
            fab.show();
            deleteAllButton.setVisibility(View.VISIBLE);
            deleteAllButton.setText("Select");
            cancelButton.setVisibility(View.GONE);
        } else if (position == 2) { // Delivery tab
            fab.hide();
            deleteAllButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
        } else {
            fab.hide();
            deleteAllButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
        }
    }

    private void setupPageChangeListener() {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateUIForPosition(position);
            }
        });
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
            int userId = sessionManager.getUserId();
            if (userId == -1) {
                Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_SHORT).show();
                return;
            }
            List<Delivery> deliveries = dbHelper.getAllDeliveries(userId);
            boolean hasUnreadNotifications = false;

            for (Delivery delivery : deliveries) {
                // Skip if notification was previously deleted
                if (dbHelper.isNotificationDeleted(delivery.getId(), userId)) {
                    continue;
                }

                // Check if notification is unread
                if (!dbHelper.isNotificationRead(delivery.getId(), userId)) {
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
            if (refreshReceiver != null) {
                unregisterReceiver(refreshReceiver);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage());
        }
    }

    private void refreshInventoryList() {
        try {
            // Get the current fragment from the ViewPager2
            Fragment currentFragment = getSupportFragmentManager()
                .findFragmentByTag("f" + viewPager.getCurrentItem());
            
            if (currentFragment instanceof InventoryFragment) {
                ((InventoryFragment) currentFragment).loadInventoryData();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing inventory list: " + e.getMessage());
        }
    }

    private void checkAndNotifyDeliveries(List<Delivery> deliveries) {
        if (deliveries == null || deliveries.isEmpty()) return;

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Delivery delivery : deliveries) {
            // Skip if notification was already deleted or read
            if (dbHelper.isNotificationDeleted(delivery.getId(), userId)) {
                continue;
            }
            if (!dbHelper.isNotificationRead(delivery.getId(), userId)) {
                showDeliveryNotification(delivery);
            }
        }
    }

    private void showDeliveryNotification(Delivery delivery) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Delivery Notifications",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for delivery updates");
            notificationManager.createNotificationChannel(channel);
        }

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_truck)
            .setContentTitle("New Delivery for " + delivery.getDeliveryName())
            .setContentText("Order: " + delivery.getOrderDescription() + 
                          "\nDate: " + delivery.getDeliveryDate() + 
                          "\nTime: " + delivery.getDeliveryTime() + 
                          "\nLocation: " + delivery.getLocation())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true);

        // Create intent for notification click
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("deliveryId", delivery.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        // Show notification
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}

