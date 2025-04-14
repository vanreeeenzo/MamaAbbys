package com.example.mamaabbys;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DashboardTabFragment extends Fragment {
    private RecyclerView recyclerView;
    private DashboardAdapter adapter;
    private int tabPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tabPosition = getArguments().getInt("tab_position", 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerView = view.findViewById(R.id.dashboardRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Initialize adapter with empty list
        adapter = new DashboardAdapter(java.util.Collections.emptyList());
        recyclerView.setAdapter(adapter);

        // Set click listener for items
        adapter.setOnItemClickListener(item -> {
            // Handle item click based on tab position
            switch (tabPosition) {
                case 0:
                    handleOverviewItemClick(item);
                    break;
                case 1:
                    handleTasksItemClick(item);
                    break;
                case 2:
                    handleReportsItemClick(item);
                    break;
            }
        });

        // Load data based on tab position
        loadData();
    }

    private void loadData() {
        List<DashboardItem> items;
        switch (tabPosition) {
            case 0:
                items = DashboardDataProvider.getOverviewItems();
                break;
            case 1:
                items = DashboardDataProvider.getTasksItems();
                break;
            case 2:
                items = DashboardDataProvider.getReportsItems();
                break;
            default:
                items = java.util.Collections.emptyList();
        }
        updateData(items);
    }

    private void handleOverviewItemClick(DashboardItem item) {
        // Handle overview tab item clicks
        // You can implement navigation or actions here
    }

    private void handleTasksItemClick(DashboardItem item) {
        // Handle tasks tab item clicks
        // You can implement navigation or actions here
    }

    private void handleReportsItemClick(DashboardItem item) {
        // Handle reports tab item clicks
        // You can implement navigation or actions here
    }

    public void updateData(List<DashboardItem> newData) {
        adapter.updateData(newData);
    }
} 