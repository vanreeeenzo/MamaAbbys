package com.example.mamaabbys;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class EditPricesActivity extends AppCompatActivity implements ProductPriceAdapter.OnPriceChangeListener {
    private RecyclerView productsRecyclerView;
    private ProductPriceAdapter adapter;
    private List<ProductPriceItem> allProductItems; // Store all products
    private List<ProductPriceItem> filteredProductItems; // Store filtered products
    private MyDataBaseHelper dbHelper;
    private TextInputEditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_prices);

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        dbHelper = new MyDataBaseHelper(this);

        // Setup back button
        backButton.setOnClickListener(v -> finish());

        // Setup RecyclerView
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allProductItems = new ArrayList<>();
        filteredProductItems = new ArrayList<>();
        adapter = new ProductPriceAdapter(filteredProductItems, this);
        productsRecyclerView.setAdapter(adapter);

        // Setup search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Load products
        loadProducts();
    }

    private void loadProducts() {
        allProductItems.clear();
        List<String> categories = dbHelper.getAllCategories();
        
        // Skip "All Categories" option
        for (int i = 1; i < categories.size(); i++) {
            String category = categories.get(i);
            List<String> products = dbHelper.getProductsByCategory(category);
            
            for (String productName : products) {
                String fullProductName = category + " - " + productName;
                float price = dbHelper.getProductPrice(fullProductName);
                
                // Create a unique ID for the product
                String id = category + "_" + productName.replaceAll("\\s+", "_");
                
                allProductItems.add(new ProductPriceItem(
                    id,
                    productName,
                    category,
                    price
                ));
            }
        }
        // Initially show all products
        filteredProductItems.clear();
        filteredProductItems.addAll(allProductItems);
        adapter.notifyDataSetChanged();
    }

    private void filterProducts(String query) {
        filteredProductItems.clear();
        if (query.isEmpty()) {
            filteredProductItems.addAll(allProductItems);
        } else {
            for (ProductPriceItem item : allProductItems) {
                if (item.getName().toLowerCase().contains(query.toLowerCase()) ||
                    item.getCategory().toLowerCase().contains(query.toLowerCase())) {
                    filteredProductItems.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPriceChanged(ProductPriceItem item) {
        if (item.getPrice() <= 0) {
            Toast.makeText(this, "Price must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullProductName = item.getCategory() + " - " + item.getName();
        boolean success = dbHelper.updatePredefinedPrice(fullProductName, item.getPrice());
        if (success) {
            Toast.makeText(this, "Price updated successfully", Toast.LENGTH_SHORT).show();
            // Update the price in the original list
            for (ProductPriceItem originalItem : allProductItems) {
                if (originalItem.getId().equals(item.getId())) {
                    originalItem.setPrice(item.getPrice());
                    break;
                }
            }
        } else {
            Toast.makeText(this, "Failed to update price", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
} 