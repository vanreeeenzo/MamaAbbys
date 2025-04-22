package com.example.mamaabbys;

import android.content.Intent;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class DeliveryFragment extends Fragment implements DeliveryAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private DeliveryAdapter adapter;
    private MyDataBaseHelper myDB;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delivery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        myDB = new MyDataBaseHelper(getContext());
        recyclerView = view.findViewById(R.id.deliveryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Setup SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadDeliveries);
        
        // Setup Floating Action Button
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddDeliveryActivity.class);
            startActivity(intent);
        });
        
        // Load initial deliveries
        loadDeliveries();
    }

    private void loadDeliveries() {
        List<DeliveryItem> items = new ArrayList<>();
        List<Delivery> deliveries = myDB.getAllDeliveries();
        
        for (Delivery delivery : deliveries) {
            String schedule = "Scheduled for " + delivery.getDeliveryDate() + " at " + delivery.getDeliveryTime();
            items.add(new DeliveryItem(
                delivery.getOrderDescription(),
                delivery.getOrderDescription(),
                schedule,
                R.drawable.ic_truck
            ));
        }
        
        if (adapter == null) {
            adapter = new DeliveryAdapter(items, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateItems(items);
        }
        
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onItemClick(DeliveryItem item) {
        Toast.makeText(getContext(), "Delivery: " + item.getOrderNumber(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDeliveries(); // Refresh the list when returning from AddDeliveryActivity
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myDB != null) {
            myDB.close();
        }
    }
} 