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

public class SalesFragment extends Fragment implements SalesAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private SalesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerView = view.findViewById(R.id.salesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Sample data - replace with your actual data
        List<SalesItem> items = new ArrayList<>();
        items.add(new SalesItem("1", "Today's Sales", "$1,234", R.drawable.ic_chart));
        items.add(new SalesItem("2", "Weekly Sales", "$8,567", R.drawable.ic_chart));
        items.add(new SalesItem("3", "Monthly Sales", "$32,456", R.drawable.ic_chart));
        
        adapter = new SalesAdapter(items, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(SalesItem item) {
        Toast.makeText(getContext(), "Clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        // Add your navigation logic here
    }
} 