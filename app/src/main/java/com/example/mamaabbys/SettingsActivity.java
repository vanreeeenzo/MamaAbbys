package com.example.mamaabbys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements SettingsAdapter.OnSettingsItemClickListener {
    private RecyclerView settingsRecyclerView;
    private SettingsAdapter adapter;
    private List<SettingsItem> settingsItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        settingsRecyclerView = findViewById(R.id.settingsRecyclerView);

        // Setup back button
        backButton.setOnClickListener(v -> finish());

        // Setup RecyclerView
        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        settingsItems = new ArrayList<>();
        
        // Add settings items
        settingsItems.add(new SettingsItem("Account Settings", "Manage your account preferences"));
        settingsItems.add(new SettingsItem("Notifications", "Configure notification settings"));
        settingsItems.add(new SettingsItem("Appearance", "Customize app theme and display"));
        settingsItems.add(new SettingsItem("Edit Prices", "Update product prices"));
        settingsItems.add(new SettingsItem("About", "App information and version"));

        adapter = new SettingsAdapter(settingsItems, this);
        settingsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onSettingsItemClick(SettingsItem item) {
        if (item.getTitle().equals("Edit Prices")) {
            Intent intent = new Intent(this, EditPricesActivity.class);
            startActivity(intent);
        }
    }
} 