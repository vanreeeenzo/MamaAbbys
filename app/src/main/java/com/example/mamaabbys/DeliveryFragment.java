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
import java.util.ArrayList;
import java.util.List;

public class DeliveryFragment extends Fragment implements DeliveryAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private DeliveryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delivery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerView = view.findViewById(R.id.deliveryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Sample data - replace with your actual data
        List<DeliveryItem> items = new ArrayList<>();
        items.add(new DeliveryItem("1", "Order #123", "Scheduled for 2:00 PM", R.drawable.ic_truck));
        items.add(new DeliveryItem("2", "Order #124", "Scheduled for 3:30 PM", R.drawable.ic_truck));
        items.add(new DeliveryItem("3", "Order #125", "Scheduled for 5:00 PM", R.drawable.ic_truck));
        
        adapter = new DeliveryAdapter(items, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(DeliveryItem item) {
        Toast.makeText(getContext(), "Clicked: " + item.getOrderNumber(), Toast.LENGTH_SHORT).show();
        // Add your navigation logic here
    }
} 