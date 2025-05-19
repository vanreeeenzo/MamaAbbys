package com.example.mamaabbys;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.List;

public class EditPricesActivity extends AppCompatActivity {
    private RecyclerView productsRecyclerView;
    private EditPricesAdapter adapter;
    private MyDataBaseHelper dbHelper;
    private MaterialButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_prices);

        // Initialize database helper
        dbHelper = new MyDataBaseHelper(this);

        // Initialize views
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        saveButton = findViewById(R.id.saveButton);

        // Setup RecyclerView
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load all products
        loadAllProducts();

        // Save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUpdatedPrices();
            }
        });

        // Initialize close button
        MaterialButton closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            // Check if there are unsaved changes
            if (hasUnsavedChanges()) {
                showUnsavedChangesDialog();
            } else {
                finish();
            }
        });
    }

    private void loadAllProducts() {
        List<String> categories = dbHelper.getAllCategories();
        List<Product> productList = new ArrayList<>();
        
        for (String category : categories) {
            List<String> products = dbHelper.getProductsByCategory(category);
            for (String productName : products) {
                String fullProductName = category + " - " + productName;
                float price = dbHelper.getProductPrice(fullProductName);
                productList.add(new Product(null, productName, category, price));
            }
        }
        
        adapter = new EditPricesAdapter(productList);
        productsRecyclerView.setAdapter(adapter);
    }

    private void saveUpdatedPrices() {
        if (adapter == null) {
            Toast.makeText(this, "No products to update", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Product> updatedProducts = adapter.getUpdatedProducts();
        if (updatedProducts.isEmpty()) {
            Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean allSuccess = true;
        StringBuilder errorMessage = new StringBuilder();

        for (Product product : updatedProducts) {
            String fullProductName = product.getCategory() + " - " + product.getName();
            if (product.getPrice() <= 0) {
                errorMessage.append("Invalid price for ").append(product.getName()).append("\n");
                allSuccess = false;
                continue;
            }

            boolean success = dbHelper.updatePredefinedPrice(fullProductName, product.getPrice());
            if (!success) {
                errorMessage.append("Failed to update ").append(product.getName()).append("\n");
                allSuccess = false;
            }
        }

        if (allSuccess) {
            Toast.makeText(this, "Prices updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update some prices:\n" + errorMessage.toString(), 
                Toast.LENGTH_LONG).show();
        }
    }

    private boolean hasUnsavedChanges() {
        // Implement your logic to check for unsaved changes
        // For example, compare current values with original values
        return false; // Return true if there are unsaved changes
    }

    private void showUnsavedChangesDialog() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Unsaved Changes")
            .setMessage("You have unsaved changes. Do you want to discard them?")
            .setPositiveButton("Discard", (dialog, which) -> finish())
            .setNegativeButton("Cancel", null)
            .show();
    }
} 