package com.example.mamaabbys;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DashboardTabFragment extends Fragment implements InventorySearchManager.SearchListener {
    private RecyclerView recyclerView;
    private DashboardAdapter adapter;
    private int tabPosition;
    private InventorySearchManager searchManager;
    private List<DashboardItem> currentItems;

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

        // Initialize search functionality if this is the inventory tab
        if (tabPosition == 0) {
            View rootView = getActivity().findViewById(android.R.id.content);
            EditText searchEditText = rootView.findViewById(R.id.searchEditText);
            Spinner categoriesSpinner = rootView.findViewById(R.id.categoriesSpinner);
            Spinner productsSpinner = rootView.findViewById(R.id.productsSpinner);
            
            searchManager = new InventorySearchManager(getContext(), searchEditText, 
                                                     categoriesSpinner, productsSpinner);
            searchManager.setSearchListener(this);
        }

        // Set click listener for items
        adapter.setOnItemClickListener(item -> {
            switch (tabPosition) {
                case 0:
                    handleInventoryItemClick(item);
                    break;
                case 1:
                    handleSalesItemClick(item);
                    break;
                case 2:
                    handleDeliveryItemClick(item);
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
                items = DashboardDataProvider.getInventoryItems();
                break;
            case 1:
                items = DashboardDataProvider.getSalesItems();
                break;
            case 2:
                items = DashboardDataProvider.getDeliveryItems();
                break;
            default:
                items = java.util.Collections.emptyList();
        }
        currentItems = items;
        
        if (tabPosition == 0 && searchManager != null) {
            searchManager.setItems(items);
        } else {
            updateData(items);
        }
    }

    private void handleInventoryItemClick(DashboardItem item) {
        // Handle inventory item clicks
    }

    private void handleSalesItemClick(DashboardItem item) {
        // Handle sales item clicks
    }

    private void handleDeliveryItemClick(DashboardItem item) {
        // Handle delivery item clicks
    }

    @Override
    public void onSearchResults(List<DashboardItem> filteredItems) {
        updateData(filteredItems);
    }

    public void updateData(List<DashboardItem> newData) {
        adapter.updateData(newData);
    }

    public void refreshData() {
        loadData();
    }
} 