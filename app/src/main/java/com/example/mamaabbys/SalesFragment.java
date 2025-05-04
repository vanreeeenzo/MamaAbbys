package com.example.mamaabbys;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class SalesFragment extends Fragment implements SalesAdapter.OnItemClickListener, SalesEventBus.OnSaleListener {
    private RecyclerView recyclerView;
    private SalesAdapter adapter;
    private MyDataBaseHelper dbHelper;
    private NumberFormat currencyFormat;
    private MaterialButton deleteAllButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        dbHelper = new MyDataBaseHelper(requireContext());
        recyclerView = view.findViewById(R.id.salesRecyclerView);
        deleteAllButton = view.findViewById(R.id.deleteAllButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Initialize currency formatter
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(2);
        
        // Register for sales events
        SalesEventBus.getInstance().registerListener(this);
        
        // Set up delete all button click listener
        deleteAllButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        
        loadSalesData();
    }

    private void showDeleteConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete All Sales")
            .setMessage("Are you sure you want to delete all sales records? This action cannot be undone.")
            .setPositiveButton("Delete", (dialog, which) -> {
                deleteAllSales();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteAllSales() {
        dbHelper.deleteAllSales();
        Toast.makeText(getContext(), "All sales records have been deleted", Toast.LENGTH_SHORT).show();
        loadSalesData(); // Refresh the view
    }

    private void loadSalesData() {
        SalesSummary summary = dbHelper.getSalesSummary();
        List<SalesItem> items = new ArrayList<>();
        
        // Add summary items with formatted values
        double totalRevenue = summary.getTotalRevenue();
        int totalOrders = summary.getTotalOrders();
        double averageOrderValue = summary.getAverageOrderValue();
        
        items.add(new SalesItem("1", "Total Revenue", currencyFormat.format(totalRevenue), R.drawable.ic_chart));
        items.add(new SalesItem("2", "Total Orders", String.valueOf(totalOrders), R.drawable.ic_chart));
        items.add(new SalesItem("3", "Average Order Value", currencyFormat.format(averageOrderValue), R.drawable.ic_chart));
        
        // Add individual sales records
        List<SalesRecord> salesRecords = dbHelper.getRecentSales();
        for (SalesRecord record : salesRecords) {
            String title = record.getProductName() + " x" + record.getQuantity();
            String amount = currencyFormat.format(record.getTotalAmount());
            items.add(new SalesItem(record.getId(), title, amount, R.drawable.ic_chart));
        }
        
        if (adapter == null) {
            adapter = new SalesAdapter(items, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateItems(items);
        }
    }

    @Override
    public void onItemClick(SalesItem item) {
        Toast.makeText(getContext(), "Clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSalesData(); // Refresh data when returning to the fragment
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister from sales events
        SalesEventBus.getInstance().unregisterListener(this);
    }

    @Override
    public void onSaleMade() {
        // Refresh sales data when a new sale is made
        if (isAdded()) { // Check if fragment is still attached to activity
            loadSalesData();
        }
    }
} 