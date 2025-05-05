// File: AddInventory.java
package com.example.mamaabbys;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class AddInventory extends AppCompatActivity {
    private Spinner categorySpinner, productSpinner;
    private TextInputEditText quantityInput;
    private MaterialButton saveButton;
    private MyDataBaseHelper myDB;
    private String selectedCategory, selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);

        // Initialize database helper
        myDB = new MyDataBaseHelper(this);

        // Initialize views
        categorySpinner = findViewById(R.id.categorySpinner);
        productSpinner = findViewById(R.id.productSpinner);
        quantityInput = findViewById(R.id.quantityInput);
        saveButton = findViewById(R.id.saveButton);

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
        List<String> products = myDB.getProductsByCategory(category);
        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, products);
        productSpinner.setAdapter(productAdapter);
    }

    private void saveInventoryItem() {
        if (selectedCategory == null || selectedProduct == null) {
            Toast.makeText(this, "Please select both category and product", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantityStr = quantityInput.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
            myDB.addInventory(selectedProduct, selectedCategory, quantity);
            
            // Clear inputs
            quantityInput.setText("");
            categorySpinner.setSelection(0);
            productSpinner.setSelection(0);
            
            Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
        }
    }
}
