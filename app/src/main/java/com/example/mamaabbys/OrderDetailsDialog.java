package com.example.mamaabbys;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderDetailsDialog extends DialogFragment {
    private static final String ARG_ORDER = "order";
    private Order order;

    public static OrderDetailsDialog newInstance(Order order) {
        OrderDetailsDialog dialog = new OrderDetailsDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORDER, order);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable(ARG_ORDER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_order_details, container, false);

        TextView orderDate = view.findViewById(R.id.orderDate);
        TextView orderTime = view.findViewById(R.id.orderTime);
        TextView orderTotal = view.findViewById(R.id.orderTotal);
        TextView orderStatus = view.findViewById(R.id.orderStatus);
        RecyclerView itemsRecyclerView = view.findViewById(R.id.itemsRecyclerView);

        orderDate.setText(order.getDate());
        orderTime.setText(order.getTime());
        orderTotal.setText(String.format("₱%.2f", order.getTotal()));
        orderStatus.setText(order.getStatus());

        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OrderItemsAdapter adapter = new OrderItemsAdapter(order.getItems());
        itemsRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private static class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder> {
        private List<Order.OrderItem> items;

        OrderItemsAdapter(List<Order.OrderItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_detail, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Order.OrderItem item = items.get(position);
            holder.productName.setText(item.getProductName());
            holder.quantity.setText(String.format("x%d", item.getQuantity()));
            holder.price.setText(String.format("₱%.2f", item.getPrice()));
            holder.total.setText(String.format("₱%.2f", item.getTotal()));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView productName;
            TextView quantity;
            TextView price;
            TextView total;

            ViewHolder(View view) {
                super(view);
                productName = view.findViewById(R.id.productName);
                quantity = view.findViewById(R.id.quantity);
                price = view.findViewById(R.id.price);
                total = view.findViewById(R.id.total);
            }
        }
    }
} 