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

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadDeliveries);

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddDeliveryActivity.class);
            startActivity(intent);
        });

        loadDeliveries();
    }

    private void loadDeliveries() {
        List<DeliveryItem> items = new ArrayList<>();
        List<Delivery> deliveries = myDB.getAllDeliveries();

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

        if (adapter == null) {
            adapter = new DeliveryAdapter(getContext(), items, this);
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

    @Override
    public void onResume() {
        super.onResume();
        loadDeliveries();

        // New code to check notifications
        if (myDB != null) {
            List<Delivery> deliveries = myDB.getAllDeliveries();
            DeliveryChecker.checkDeliveries(getContext(), deliveries);
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
        List<Delivery> deliveries = myDB.getAllDeliveries();

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

}
