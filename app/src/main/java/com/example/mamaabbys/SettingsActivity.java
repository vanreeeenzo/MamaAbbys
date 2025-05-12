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
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_settings);

            sessionManager = new SessionManager(this);
            dbHelper = new MyDataBaseHelper(this);
            settingsRecyclerView = findViewById(R.id.settingsRecyclerView);
            settingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Initialize settings items
            settingsItems = new ArrayList<>();
            settingsItems.add(new SettingsItem("Edit Prices", "Manage product prices"));
            settingsItems.add(new SettingsItem("Logout", "Sign out from your account"));

            adapter = new SettingsAdapter(settingsItems, this);
            settingsRecyclerView.setAdapter(adapter);

            // Setup back button with error handling
            ImageButton backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> {
                try {
                    finish();
                } catch (Exception e) {
                    Log.e("SettingsActivity", "Error finishing activity: " + e.getMessage());
                    // Fallback to default back behavior
                    onBackPressed();
                }
            });
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
                Intent intent = new Intent(this, EditPricesActivity.class);
                startActivity(intent);
            } else if (item.getTitle().equals("Logout")) {
                sessionManager.logout();
                Intent intent = new Intent(this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
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