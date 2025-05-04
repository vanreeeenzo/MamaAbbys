package com.example.mamaabbys;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryFragment extends Fragment implements InventoryAdapter.OnItemClickListener {
    private static final String TAG = "InventoryFragment";
    private static final String KEY_ITEMS = "inventory_items";
    
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyDataBaseHelper dbHelper;
    private List<InventoryItem> currentItems;
    private Spinner categoriesSpinner;
    private Spinner productsSpinner;
    private Map<String, List<String>> productCategories;
    private volatile boolean isLoading = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new MyDataBaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        
        initializeProductCategories();
        setupSpinners(view);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        
        if (savedInstanceState != null) {

            currentItems = savedInstanceState.getParcelableArrayList(KEY_ITEMS);
            if (currentItems != null) {
                updateAdapter(currentItems);
            }
        }
        
        loadInventoryData();
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.inventoryRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        
        // Add search functionality
        EditText searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String searchQuery = s.toString().toLowerCase().trim();
                filterInventoryItems(searchQuery);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new InventoryAdapter(
            new ArrayList<>(),
            this,
            item -> showSellDialog(item)
        );
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshInventoryData);
    }

    public void loadInventoryData() {
        if (isLoading) return;
        isLoading = true;
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        new Thread(() -> {
            try {
                List<InventoryItem> allItems = dbHelper.getAllInventoryItems();
                
                // Check if spinners are initialized and have selections
                if (categoriesSpinner == null || productsSpinner == null || 
                    categoriesSpinner.getSelectedItem() == null || 
                    productsSpinner.getSelectedItem() == null) {
                    // If spinners are not ready, show all items
                    currentItems = allItems;
                    requireActivity().runOnUiThread(() -> {
                        updateAdapter(allItems);
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        isLoading = false;
                    });
                    return;
                }

                String selectedCategory = categoriesSpinner.getSelectedItem().toString();
                String selectedProduct = productsSpinner.getSelectedItem().toString();

                List<InventoryItem> filteredItems = new ArrayList<>();

                // If "All Categories" or "All Products" is selected, show all items
                if (selectedCategory.equals("All Categories") || selectedProduct.equals("All Products")) {
                    currentItems = allItems;
                    requireActivity().runOnUiThread(() -> {
                        updateAdapter(allItems);
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        isLoading = false;
                    });
                    return;
                }

                for (InventoryItem item : allItems) {
                    // Check if the item matches both the selected category and product
                    if (item.getCategory().equals(selectedCategory) && 
                        item.getName().equals(selectedProduct)) {
                        filteredItems.add(item);
                    }
                }

                currentItems = filteredItems;
                requireActivity().runOnUiThread(() -> {
                    updateAdapter(filteredItems);
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    isLoading = false;
                });
            } catch (Exception e) {
                Log.e(TAG, "Error loading inventory data: " + e.getMessage());
                requireActivity().runOnUiThread(() -> {
                    showErrorToast("Error loading inventory: " + e.getMessage());
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    isLoading = false;
                });
            }
        }).start();
    }

    private void updateAdapter(List<InventoryItem> items) {
        if (adapter != null) {
            adapter.updateItems(items);
        }
    }

    private void showErrorToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshInventoryData() {
        loadInventoryData();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentItems != null) {
            outState.putParcelableArrayList(KEY_ITEMS, new ArrayList<>(currentItems));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInventoryData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Override
    public void onItemClick(InventoryItem item) {
        Toast.makeText(getContext(), "Clicked: " + item.getName(), Toast.LENGTH_SHORT).show();
    }

    private void initializeProductCategories() {
        productCategories = new HashMap<>();

        // Purefoods Products
        List<String> purefoodsProducts = new ArrayList<>();
        purefoodsProducts.add("TJ Classic 1 kilo");
        purefoodsProducts.add("TJ Cheesedog 1 kilo");
        purefoodsProducts.add("TJ Classic 250g");
        purefoodsProducts.add("Star Nuggets");
        purefoodsProducts.add("Crazy Cut Nuggets");
        purefoodsProducts.add("Chicken Breast Nuggets");
        purefoodsProducts.add("TJ Hotdog w/ Cheese 250 grams");
        purefoodsProducts.add("TJ balls 500 grams");
        purefoodsProducts.add("TJ Chicken Jumbo");
        purefoodsProducts.add("TJ Cocktail");
        purefoodsProducts.add("TJ Cheesedog (Small)");
        purefoodsProducts.add("TJ Classic (Small)");
        productCategories.put("Purefoods", purefoodsProducts);

        // Virginia Products
        List<String> virginiaProducts = new ArrayList<>();
        virginiaProducts.add("Virginia Classic 250 grams");
        virginiaProducts.add("Virginia Chicken Hotdog 250 grams (Blue)");
        virginiaProducts.add("Virginia Classic 500 grams");
        virginiaProducts.add("Virginia Chicken Hotdog w/ Cheese (Jumbo)");
        virginiaProducts.add("Virginia Classic 1 kilo");
        virginiaProducts.add("Virginia w/ Cheese 1 kilo");
        virginiaProducts.add("Chicken Longganisa");
        productCategories.put("Virginia Products", virginiaProducts);

        // Big Shot Products
        List<String> bigShotProducts = new ArrayList<>();
        bigShotProducts.add("Big shot balls 500 grams");
        bigShotProducts.add("Big shot Classic 1 kilo");
        bigShotProducts.add("Big shot w/ Cheese 1 kilo");
        productCategories.put("Big Shot Products", bigShotProducts);

        // Beefies Products
        List<String> beefiesProducts = new ArrayList<>();
        beefiesProducts.add("Beefies Classic 250 grams");
        beefiesProducts.add("Beefies w/ Cheese 250 grams");
        beefiesProducts.add("Beefies Classic 1 kilo");
        beefiesProducts.add("Beefies w/ Cheese 1 kilo");
        productCategories.put("Beefies Products", beefiesProducts);
    }

    private void setupSpinners(View view) {
        categoriesSpinner = view.findViewById(R.id.categoriesSpinner);
        productsSpinner = view.findViewById(R.id.productsSpinner);

        // Setup Categories Spinner
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dbHelper.getAllCategories()
        );
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(categoriesAdapter);

        // Setup Products Spinner
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                List<String> products = new ArrayList<>();
                if (selectedCategory.equals("All Categories")) {
                    products.clear();
                    products.add("All Products");
                } else {
                    products = productCategories.get(selectedCategory);
                }
                ArrayAdapter<String> productsAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    products
                );
                productsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                productsSpinner.setAdapter(productsAdapter);
                loadInventoryData(); // Reload data when category changes
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Add listener for product selection
        productsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadInventoryData(); // Reload data when product changes
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void filterInventoryItems(String searchQuery) {
        if (currentItems == null) return;

        new Thread(() -> {
            List<InventoryItem> filteredList = new ArrayList<>();
            for (InventoryItem item : currentItems) {
                if (item.getName().toLowerCase().contains(searchQuery) ||
                    item.getCategory().toLowerCase().contains(searchQuery)) {
                    filteredList.add(item);
                }
            }
            requireActivity().runOnUiThread(() -> updateAdapter(filteredList));
        }).start();
    }

    private void showSellDialog(InventoryItem item) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_sell_quantity, null);
        EditText quantityInput = dialogView.findViewById(R.id.quantityInput);

        new AlertDialog.Builder(requireContext())
            .setTitle("Sell " + item.getName())
            .setView(dialogView)
            .setPositiveButton("Sell", (dialog, which) -> {
                String quantityStr = quantityInput.getText().toString().trim();
                if (!quantityStr.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        if (quantity > 0) {
                            new Thread(() -> {
                                String resultMessage = dbHelper.sellInventoryItem(item.getId(), quantity);
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), resultMessage, Toast.LENGTH_SHORT).show();
                                    loadInventoryData();
                                });
                            }).start();
                        } else {
                            Toast.makeText(getContext(), "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
} 