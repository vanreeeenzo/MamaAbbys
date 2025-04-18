package com.example.mamaabbys;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class InventorySearchManager {
    private EditText searchEditText;
    private Spinner categoriesSpinner;
    private Spinner nameSpinner;
    private List<DashboardItem> originalItems;
    private SearchListener searchListener;

    public interface SearchListener {
        void onSearchResults(List<DashboardItem> filteredItems);
    }

    public InventorySearchManager(Context context, EditText searchEditText, 
                                Spinner categoriesSpinner, Spinner nameSpinner) {
        this.searchEditText = searchEditText;
        this.categoriesSpinner = categoriesSpinner;
        this.nameSpinner = nameSpinner;
        this.originalItems = new ArrayList<>();
        
        setupSpinners(context);
        setupSearchListener();
    }

    private void setupSpinners(Context context) {
        // Setup Categories Spinner
        List<String> categories = new ArrayList<>();
        categories.add("All Categories");
        categories.add("Grains");
        categories.add("Oils");
        categories.add("Sweeteners");
        categories.add("Seasonings");
        categories.add("Reports");
        categories.add("Delivery");
        
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(categoryAdapter);

        // Setup Names Spinner
        List<String> names = new ArrayList<>();
        names.add("Name");
        // Names will be populated from inventory items
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, names);
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameSpinner.setAdapter(nameAdapter);

        // Add spinner listeners
        categoriesSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                performSearch();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        nameSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                performSearch();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                performSearch();
            }
        });
    }

    public void setItems(List<DashboardItem> items) {
        this.originalItems = new ArrayList<>(items);
        updateNameSpinner();
        performSearch();
    }

    private void updateNameSpinner() {
        Set<String> uniqueNames = new HashSet<>();
        uniqueNames.add("Name"); // Default option
        
        for (DashboardItem item : originalItems) {
            uniqueNames.add(item.getTitle());
        }
        
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) nameSpinner.getAdapter();
        adapter.clear();
        adapter.addAll(uniqueNames);
        adapter.notifyDataSetChanged();
    }

    public void setSearchListener(SearchListener listener) {
        this.searchListener = listener;
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().toLowerCase().trim();
        String selectedCategory = categoriesSpinner.getSelectedItem().toString();
        String selectedName = nameSpinner.getSelectedItem().toString();

        List<DashboardItem> filteredItems = new ArrayList<>();

        for (DashboardItem item : originalItems) {
            boolean matchesSearch = TextUtils.isEmpty(query) ||
                    item.getTitle().toLowerCase().contains(query) ||
                    item.getDescription().toLowerCase().contains(query);

            boolean matchesCategory = selectedCategory.equals("All Categories") ||
                    item.getCategory().equals(selectedCategory);

            boolean matchesName = selectedName.equals("Name") ||
                    item.getTitle().equals(selectedName);

            if (matchesSearch && matchesCategory && matchesName) {
                filteredItems.add(item);
            }
        }

        if (searchListener != null) {
            searchListener.onSearchResults(filteredItems);
        }
    }
} 