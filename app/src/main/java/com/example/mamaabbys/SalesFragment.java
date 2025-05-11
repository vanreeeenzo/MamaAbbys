package com.example.mamaabbys;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SalesFragment extends Fragment implements OrderAdapter.OnOrderClickListener {
    private static final String TAG = "SalesFragment";
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyDataBaseHelper dbHelper;
    private List<Order> currentOrders;
    private TextView todayRevenueText;
    private TextView weeklyRevenueText;
    private TextView monthlyRevenueText;
    private ChipGroup filterChipGroup;
    private Chip todayChip;
    private Chip weeklyChip;
    private Chip monthlyChip;
    private String currentFilter = "today"; // Default filter

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new MyDataBaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales, container, false);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.ordersRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        todayRevenueText = view.findViewById(R.id.todayRevenueText);
        weeklyRevenueText = view.findViewById(R.id.weeklyRevenueText);
        monthlyRevenueText = view.findViewById(R.id.monthlyRevenueText);
        filterChipGroup = view.findViewById(R.id.filterChipGroup);
        todayChip = view.findViewById(R.id.todayChip);
        weeklyChip = view.findViewById(R.id.weeklyChip);
        monthlyChip = view.findViewById(R.id.monthlyChip);
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        
        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
        
        // Setup filter chips
        setupFilterChips();
        
        return view;
    }

    private void setupFilterChips() {
        filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == todayChip.getId()) {
                currentFilter = "today";
            } else if (checkedId == weeklyChip.getId()) {
                currentFilter = "weekly";
            } else if (checkedId == monthlyChip.getId()) {
                currentFilter = "monthly";
            }
            loadData();
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                List<Order> filteredOrders;
                double todayRevenue = dbHelper.getTodayRevenue();
                double weeklyRevenue = dbHelper.getWeeklyRevenue();
                double monthlyRevenue = dbHelper.getMonthlyRevenue();
                
                // Get filtered orders based on selected chip
                switch (currentFilter) {
                    case "today":
                        filteredOrders = dbHelper.getOrdersByDate(getTodayDate());
                        break;
                    case "weekly":
                        filteredOrders = dbHelper.getOrdersByDateRange(getWeekStartDate(), getTodayDate());
                        break;
                    case "monthly":
                        filteredOrders = dbHelper.getOrdersByDateRange(getMonthStartDate(), getTodayDate());
                        break;
                    default:
                        filteredOrders = dbHelper.getAllOrders();
                }
                
                requireActivity().runOnUiThread(() -> {
                    currentOrders = filteredOrders;
                    adapter.updateOrders(filteredOrders);
                    
                    // Format and display revenue information
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                    todayRevenueText.setText("Today's Revenue: " + currencyFormat.format(todayRevenue));
                    weeklyRevenueText.setText("Weekly Revenue: " + currencyFormat.format(weeklyRevenue));
                    monthlyRevenueText.setText("Monthly Revenue: " + currencyFormat.format(monthlyRevenue));
                    
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private String getTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private String getWeekStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private String getMonthStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    @Override
    public void onOrderClick(Order order) {
        // Show order details dialog
        OrderDetailsDialog dialog = OrderDetailsDialog.newInstance(order);
        dialog.show(getChildFragmentManager(), "OrderDetails");
    }
} 