package com.example.mamaabbys;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.List;
import java.util.ArrayList;

public class InventoryFragment extends Fragment implements InventoryAdapter.OnItemClickListener {
    private static final String TAG = "InventoryFragment";
    private static final String KEY_ITEMS = "inventory_items";
    
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyDataBaseHelper dbHelper;
    private List<InventoryItem> currentItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new MyDataBaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        
        if (savedInstanceState != null) {
            // Restore state if available
            currentItems = savedInstanceState.getParcelableArrayList(KEY_ITEMS);
            if (currentItems != null) {
                updateAdapter(currentItems);
            }
        }
        
        loadInventoryData();
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.inventoryRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new InventoryAdapter(null, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshInventoryData);
    }

    public void loadInventoryData() {
        try {
            List<InventoryItem> items = dbHelper.getAllInventoryItems();
            currentItems = items;
            updateAdapter(items);
            
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading inventory data: " + e.getMessage());
            showErrorToast("Error loading inventory: " + e.getMessage());
        }
    }

    private void updateAdapter(List<InventoryItem> items) {
        if (adapter != null) {
            adapter.updateItems(items);
        }
    }

    private void showErrorToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshInventoryData() {
        loadInventoryData();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentItems != null) {
            outState.putParcelableArrayList(KEY_ITEMS, new ArrayList<>(currentItems));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInventoryData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Override
    public void onItemClick(InventoryItem item) {
        Toast.makeText(getContext(), "Clicked: " + item.getName(), Toast.LENGTH_SHORT).show();
    }
} 