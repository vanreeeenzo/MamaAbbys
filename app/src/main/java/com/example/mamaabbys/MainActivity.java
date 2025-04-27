package com.example.mamaabbys;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupViewPager();
        setupClickListeners();
        setupPageChangeListener();
        setupNotificationButton();
        // Initialize Firebase (if needed)
        FirebaseDatabase.getInstance().getReference().child("Inventory").child("Meat").setValue("TJ Hotdog");
    }


    private void initializeViews() {
        notificationButton = findViewById(R.id.notificationButton);
        notificationBadge = findViewById(R.id.notificationBadge);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        fab = findViewById(R.id.quickActionsBar);
        deleteAllButton = findViewById(R.id.deleteAllButton);
        dbHelper = new MyDataBaseHelper(this);
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
        notificationButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
    }

}

