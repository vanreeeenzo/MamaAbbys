package com.example.mamaabbys;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SalesViewHolder> {
    private List<SalesItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SalesItem item);
    }

    public SalesAdapter(List<SalesItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sales, parent, false);
        return new SalesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesViewHolder holder, int position) {
        SalesItem item = items.get(position);
        holder.salesTitle.setText(item.getTitle());
        holder.salesAmount.setText(item.getAmount());
        holder.salesIcon.setImageResource(item.getIconResId());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SalesViewHolder extends RecyclerView.ViewHolder {
        TextView salesTitle;
        TextView salesAmount;
        ImageView salesIcon;

        SalesViewHolder(@NonNull View itemView) {
            super(itemView);
            salesTitle = itemView.findViewById(R.id.salesTitle);
            salesAmount = itemView.findViewById(R.id.salesAmount);
            salesIcon = itemView.findViewById(R.id.salesIcon);
        }
    }
} 