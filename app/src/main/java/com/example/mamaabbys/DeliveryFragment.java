package com.example.mamaabbys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryFragment extends Fragment implements DeliveryAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private DeliveryAdapter adapter;
    private MyDataBaseHelper myDB;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private Map<String, Boolean> doneMap = new HashMap<>();
    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB = new MyDataBaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery, container, false);
        recyclerView = view.findViewById(R.id.deliveryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter with empty list
        List<DeliveryItem> items = new ArrayList<>();
        adapter = new DeliveryAdapter(getContext(), items, this);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadDeliveries);

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddDeliveryActivity.class);
            startActivity(intent);
        });

        loadDeliveries();
        return view;
    }

    private void loadDeliveries() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(requireContext(), "User session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Delivery> deliveries = myDB.getAllDeliveries(userId);
        List<DeliveryItem> items = new ArrayList<>();
        
        for (Delivery delivery : deliveries) {
            String schedule = "Scheduled for " + delivery.getDeliveryDate() + " at " + delivery.getDeliveryTime();
            DeliveryItem item = new DeliveryItem(
                delivery.getId(),
                delivery.getOrderDescription(),
                schedule,
                delivery.getLocation(),
                R.drawable.ic_truck
            );
            item.setDone(delivery.isDone());
            items.add(item);
        }

        // Sort: done items at the bottom
        items.sort((a, b) -> Boolean.compare(a.isDone(), b.isDone()));
        adapter.updateItems(items);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void refreshDeliveries() {
        loadDeliveries();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDeliveries();

        // New code to check notifications
        if (myDB != null) {
            int userId = sessionManager.getUserId();
            if (userId != -1) {
                List<Delivery> deliveries = myDB.getAllDeliveries(userId);
                DeliveryChecker.checkDeliveries(getContext(), deliveries);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myDB != null) {
            myDB.close();
        }
    }

    private void checkAndNotifyDeliveries() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(requireContext(), "User session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Delivery> deliveries = myDB.getAllDeliveries(userId);

        for (Delivery delivery : deliveries) {
            // Check if delivery date is 7 days, 3 days, 1 day or today
            // If yes -> build a notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "your_channel_id")
                    .setSmallIcon(R.drawable.ic_truck)
                    .setContentTitle("Upcoming Delivery")
                    .setContentText("Your order " + delivery.getOrderDescription() + " is coming soon!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    @Override
    public void onItemClick(DeliveryItem item) {
        Toast.makeText(getContext(), "Delivery: " + item.getOrderNumber(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkAsDoneClicked(DeliveryItem delivery) {
        boolean isUpdated = myDB.updateDeliveryStatus(delivery.getId(), true);
        if (isUpdated) {
            doneMap.put(delivery.getId(), true);
            delivery.setDone(true);
            adapter.markAsDone(delivery);
        } else {
            Toast.makeText(getContext(), "Failed to mark delivery as done", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelClicked(DeliveryItem delivery) {
        boolean isDeleted = myDB.deleteDelivery(delivery.getId());
        if (isDeleted) {
            Toast.makeText(getContext(), "Delivery details deleted", Toast.LENGTH_SHORT).show();
            loadDeliveries();
        } else {
            Toast.makeText(getContext(), "Failed to delete delivery", Toast.LENGTH_SHORT).show();
        }
    }
}
