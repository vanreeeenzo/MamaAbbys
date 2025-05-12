package com.example.mamaabbys;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_settings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingsItem item = settingsItems.get(position);
        holder.titleText.setText(item.getTitle());
        holder.descriptionText.setText(item.getDescription());

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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView descriptionText;

        ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.settingsTitle);
            descriptionText = itemView.findViewById(R.id.settingsDescription);
        }
    }
} 