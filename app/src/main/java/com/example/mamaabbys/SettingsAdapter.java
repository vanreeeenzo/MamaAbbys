package com.example.mamaabbys;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {
    private List<SettingsItem> settingsItems;
    private OnSettingsItemClickListener listener;

    public interface OnSettingsItemClickListener {
        void onSettingsItemClick(SettingsItem item);
    }

    public SettingsAdapter(List<SettingsItem> settingsItems, OnSettingsItemClickListener listener) {
        this.settingsItems = settingsItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_settings, parent, false);
        return new SettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        SettingsItem item = settingsItems.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(item.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSettingsItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return settingsItems.size();
    }

    static class SettingsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        SettingsViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.settingsTitle);
            descriptionTextView = itemView.findViewById(R.id.settingsDescription);
        }
    }
} 