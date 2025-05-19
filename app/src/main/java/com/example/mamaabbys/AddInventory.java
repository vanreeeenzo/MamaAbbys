// File: AddInventory.java
package com.example.mamaabbys;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.List;

public class AddInventory extends AppCompatActivity {
    private Spinner categorySpinner, productSpinner;
    private TextInputEditText quantityInput;
    private MaterialButton saveButton;
    private MyDataBaseHelper myDB;
    private String selectedCategory, selectedProduct;
    private android.os.Handler mainHandler;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);

        // Initialize handler for main thread
        mainHandler = new android.os.Handler(getMainLooper());

        // Initialize database helper and session manager
        myDB = new MyDataBaseHelper(this);
        sessionManager = new SessionManager(this);

        // Initialize views
        categorySpinner = findViewById(R.id.categorySpinner);
        productSpinner = findViewById(R.id.productSpinner);
        quantityInput = findViewById(R.id.quantityInput);
        saveButton = findViewById(R.id.saveButton);

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

        // Setup category spinner
        List<String> categories = myDB.getAllCategories();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(categoryAdapter);

        // Category selection listener
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (String) parent.getItemAtPosition(position);
                updateProductList(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
            }
        });

        // Product selection listener
        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProduct = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedProduct = null;
            }
        });

        // Save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInventoryItem();
            }
        });
    }

    private void updateProductList(String category) {
        if (category == null || category.equals("All Categories")) {
            return;
        }
        List<String> products = myDB.getProductsByCategory(category);
        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, products);
        productSpinner.setAdapter(productAdapter);
    }

    private void showToast(final String message) {
        mainHandler.post(() -> {
            Toast.makeText(AddInventory.this, message, Toast.LENGTH_SHORT).show();
            Log.d("AddInventory", "Toast message: " + message);
        });
    }

    private void saveInventoryItem() {
        if (selectedCategory == null || selectedCategory.equals("All Categories")) {
            showToast("Please select a valid category");
            return;
        }

        if (selectedProduct == null) {
            showToast("Please select a product");
            return;
        }

        String quantityStr = quantityInput.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            showToast("Please enter quantity");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                showToast("Quantity must be greater than 0");
                return;
            }

            // Get current user ID
            int userId = sessionManager.getUserId();
            if (userId == -1) {
                showToast("User session expired. Please login again.");
                finish();
                return;
            }

            // Disable save button to prevent multiple clicks
            saveButton.setEnabled(false);

            // Perform database operation in a background thread
            new Thread(() -> {
                try {
                    Log.d("AddInventory", "Attempting to add inventory item: " + selectedProduct + 
                        " in category: " + selectedCategory + " with quantity: " + quantity);
                    
                    boolean success = myDB.addInventory(selectedProduct, selectedCategory, quantity, userId);
                    
                    runOnUiThread(() -> {
                        if (success) {
                            Log.d("AddInventory", "Successfully added inventory item");
                            // Clear inputs
                            quantityInput.setText("");
                            categorySpinner.setSelection(0);
                            productSpinner.setSelection(0);
                            showToast("Item added successfully");
                            finish(); // Close the activity after successful addition
                        } else {
                            Log.e("AddInventory", "Failed to add inventory item");
                            showToast("Failed to add item. Please try again.");
                            saveButton.setEnabled(true); // Re-enable save button on error
                        }
                    });
                } catch (Exception e) {
                    Log.e("AddInventory", "Error adding inventory item: " + e.getMessage(), e);
                    runOnUiThread(() -> {
                        showToast("Error: " + e.getMessage());
                        saveButton.setEnabled(true); // Re-enable save button on error
                    });
                }
            }).start();
        } catch (NumberFormatException e) {
            Log.e("AddInventory", "Invalid quantity format: " + e.getMessage());
            showToast("Please enter a valid quantity");
        }
    }

    private boolean hasUnsavedChanges() {
        // Check if any field has been modified
        return !quantityInput.getText().toString().trim().isEmpty() ||
               (categorySpinner.getSelectedItemPosition() != 0) ||
               (productSpinner.getSelectedItemPosition() != 0);
    }

    private void showUnsavedChangesDialog() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Unsaved Changes")
            .setMessage("You have unsaved changes. Do you want to discard them?")
            .setPositiveButton("Discard", (dialog, which) -> finish())
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDB != null) {
            myDB.close();
        }
    }
}
