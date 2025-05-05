package com.example.mamaabbys;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(List<Order> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderDate.setText(order.getDate() + " " + order.getTime());
        holder.orderTotal.setText(String.format("₱%.2f", order.getTotal()));
        holder.orderStatus.setText(order.getStatus());
        
        // Build items summary
        StringBuilder itemsSummary = new StringBuilder();
        for (Order.OrderItem item : order.getItems()) {
            if (itemsSummary.length() > 0) {
                itemsSummary.append(", ");
            }
            itemsSummary.append(item.getQuantity()).append("x ").append(item.getProductName());
        }
        holder.orderItems.setText(itemsSummary.toString());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderDate;
        TextView orderTotal;
        TextView orderStatus;
        TextView orderItems;

        OrderViewHolder(View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderItems = itemView.findViewById(R.id.orderItems);
        }
    }
} 