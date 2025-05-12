package com.example.mamaabbys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements SettingsAdapter.OnSettingsItemClickListener {
    private RecyclerView settingsRecyclerView;
    private SettingsAdapter adapter;
    private List<SettingsItem> settingsItems;
    private MyDataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_settings);

            // Initialize database helper
            dbHelper = new MyDataBaseHelper(this);

            // Initialize views
            ImageButton backButton = findViewById(R.id.backButton);
            settingsRecyclerView = findViewById(R.id.settingsRecyclerView);

            // Setup back button with error handling
            backButton.setOnClickListener(v -> {
                try {
                    finish();
                } catch (Exception e) {
                    Log.e("SettingsActivity", "Error finishing activity: " + e.getMessage());
                    // Fallback to default back behavior
                    onBackPressed();
                }
            });

            // Setup RecyclerView
            settingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            settingsItems = new ArrayList<>();
            
            // Add settings items
            settingsItems.add(new SettingsItem("Account Settings", "Manage your account preferences"));
            settingsItems.add(new SettingsItem("Notifications", "Configure notification settings"));
            settingsItems.add(new SettingsItem("Edit Prices", "View and update product prices"));

            adapter = new SettingsAdapter(settingsItems, this);
            settingsRecyclerView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("SettingsActivity", "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Error initializing settings", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onSettingsItemClick(SettingsItem item) {
        try {
            if (item.getTitle().equals("Edit Prices")) {
                Intent intent = new Intent(SettingsActivity.this, EditPricesActivity.class);
                startActivity(intent);
                return;
            }
            // Handle other settings items if needed
        } catch (Exception e) {
            Log.e("SettingsActivity", "Error handling settings item click: " + e.getMessage());
            Toast.makeText(this, "Error opening settings option", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (dbHelper != null) {
                dbHelper.close();
            }
        } catch (Exception e) {
            Log.e("SettingsActivity", "Error closing database: " + e.getMessage());
        }
    }
} 