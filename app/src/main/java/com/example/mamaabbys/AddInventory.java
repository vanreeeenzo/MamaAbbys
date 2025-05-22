// File: AddInventory.java
package com.example.mamaabbys;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.List;

public class AddInventory extends AppCompatActivity {
    private static final String TAG = "AddInventory";
    private Spinner categorySpinner, productSpinner;
    private TextInputEditText quantityInput;
    private MaterialButton saveButton;
    private ImageButton closeButton;
    private MyDataBaseHelper myDB;
    private String selectedCategory, selectedProduct;
    private android.os.Handler mainHandler;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_add_inventory);

            // Initialize handler for main thread
            mainHandler = new android.os.Handler(getMainLooper());

            // Initialize database helper and session manager
            myDB = new MyDataBaseHelper(this);
            sessionManager = new SessionManager(this);

            // Initialize views
            initializeViews();

            // Setup spinners
            setupSpinners();

            // Setup click listeners
            setupClickListeners();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            categorySpinner = findViewById(R.id.categorySpinner);
            productSpinner = findViewById(R.id.productSpinner);
            quantityInput = findViewById(R.id.quantityInput);
            saveButton = findViewById(R.id.saveButton);
            closeButton = findViewById(R.id.closeButton);

            if (categorySpinner == null || productSpinner == null || 
                quantityInput == null || saveButton == null || closeButton == null) {
                throw new RuntimeException("Failed to initialize views");
            }

            closeButton.setOnClickListener(v -> {
                if (hasUnsavedChanges()) {
                    showUnsavedChangesDialog();
                } else {
                    finish();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize views: " + e.getMessage());
        }
    }

    private void setupSpinners() {
        try {
            // Setup category spinner
            List<String> categories = myDB.getAllCategories();
            if (categories == null || categories.isEmpty()) {
                categories = new ArrayList<>();
                categories.add("No Categories Available");
            }
            
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

        } catch (Exception e) {
            Log.e(TAG, "Error setting up spinners: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading categories: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupClickListeners() {
        try {
            saveButton.setOnClickListener(v -> saveInventoryItem());
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
            Toast.makeText(this, "Error setting up buttons: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateProductList(String category) {
        try {
            if (category == null || category.equals("All Categories") || category.equals("No Categories Available")) {
                return;
            }
            List<String> products = myDB.getProductsByCategory(category);
            if (products == null || products.isEmpty()) {
                products = new ArrayList<>();
                products.add("No Products Available");
            }
            ArrayAdapter<String> productAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, products);
            productSpinner.setAdapter(productAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error updating product list: " + e.getMessage(), e);
            showToast("Error loading products: " + e.getMessage());
        }
    }

    private void showToast(final String message) {
        if (mainHandler != null) {
            mainHandler.post(() -> {
                Toast.makeText(AddInventory.this, message, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Toast message: " + message);
            });
        }
    }

    private void saveInventoryItem() {
        try {
            if (selectedCategory == null || selectedCategory.equals("All Categories") || 
                selectedCategory.equals("No Categories Available")) {
                showToast("Please select a valid category");
                return;
            }

            if (selectedProduct == null || selectedProduct.equals("No Products Available")) {
                showToast("Please select a product");
                return;
            }

            String quantityStr = quantityInput.getText().toString().trim();
            if (quantityStr.isEmpty()) {
                showToast("Please enter quantity");
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                showToast("Quantity must be greater than 0");
                return;
            }

            int userId = sessionManager.getUserId();
            if (userId == -1) {
                showToast("User session expired. Please login again.");
                finish();
                return;
            }

            saveButton.setEnabled(false);

            new Thread(() -> {
                try {
                    Log.d(TAG, "Attempting to add inventory item: " + selectedProduct + 
                        " in category: " + selectedCategory + " with quantity: " + quantity);
                    
                    boolean success = myDB.addInventory(selectedProduct, selectedCategory, quantity, userId);
                    
                    runOnUiThread(() -> {
                        if (success) {
                            Log.d(TAG, "Successfully added inventory item");
                            quantityInput.setText("");
                            categorySpinner.setSelection(0);
                            productSpinner.setSelection(0);
                            showToast("Item added successfully");
                            finish();
                        } else {
                            Log.e(TAG, "Failed to add inventory item");
                            showToast("Failed to add item. Please try again.");
                            saveButton.setEnabled(true);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error adding inventory item: " + e.getMessage(), e);
                    runOnUiThread(() -> {
                        showToast("Error: " + e.getMessage());
                        saveButton.setEnabled(true);
                    });
                }
            }).start();
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid quantity format: " + e.getMessage());
            showToast("Please enter a valid quantity");
        } catch (Exception e) {
            Log.e(TAG, "Error in saveInventoryItem: " + e.getMessage(), e);
            showToast("An error occurred: " + e.getMessage());
            saveButton.setEnabled(true);
        }
    }

    private boolean hasUnsavedChanges() {
        try {
            return !quantityInput.getText().toString().trim().isEmpty() ||
                   (categorySpinner.getSelectedItemPosition() != 0) ||
                   (productSpinner.getSelectedItemPosition() != 0);
        } catch (Exception e) {
            Log.e(TAG, "Error checking unsaved changes: " + e.getMessage());
            return false;
        }
    }

    private void showUnsavedChangesDialog() {
        try {
            new MaterialAlertDialogBuilder(this)
                .setTitle("Unsaved Changes")
                .setMessage("You have unsaved changes. Do you want to discard them?")
                .setPositiveButton("Discard", (dialog, which) -> finish())
                .setNegativeButton("Cancel", null)
                .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing unsaved changes dialog: " + e.getMessage());
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (myDB != null) {
                myDB.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage());
        }
    }
}
