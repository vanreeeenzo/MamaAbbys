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
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class SalesFragment extends Fragment implements OrderAdapter.OnOrderClickListener {
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyDataBaseHelper dbHelper;
    private List<Order> currentOrders;
    private TextView todayRevenueText;
    private TextView weeklyRevenueText;
    private TextView monthlyRevenueText;

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
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        
        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            List<Order> orders = dbHelper.getAllOrders();
            double todayRevenue = dbHelper.getTodayRevenue();
            double weeklyRevenue = dbHelper.getWeeklyRevenue();
            double monthlyRevenue = dbHelper.getMonthlyRevenue();
            
            requireActivity().runOnUiThread(() -> {
                currentOrders = orders;
                adapter.updateOrders(orders);
                
                // Format and display revenue information
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                todayRevenueText.setText("Today's Revenue: " + currencyFormat.format(todayRevenue));
                weeklyRevenueText.setText("Weekly Revenue: " + currencyFormat.format(weeklyRevenue));
                monthlyRevenueText.setText("Monthly Revenue: " + currencyFormat.format(monthlyRevenue));
                
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }).start();
    }

    @Override
    public void onOrderClick(Order order) {
        // Show order details dialog
        OrderDetailsDialog dialog = OrderDetailsDialog.newInstance(order);
        dialog.show(getChildFragmentManager(), "OrderDetails");
    }
} 