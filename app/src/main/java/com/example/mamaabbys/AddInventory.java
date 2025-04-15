// File: AddInventory.java
package com.example.mamaabbys;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddInventory extends AppCompatActivity {

    EditText product_qty_input, product_price, product_min_threshold;
    Spinner productNames;
    ArrayAdapter<String> adapter;
    List<String> itemList;
    Button addButton;
    String selectedProductName;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_inventory);

        product_qty_input = findViewById(R.id.product_qty_input);
        product_price = findViewById(R.id.product_price);
        product_min_threshold = findViewById(R.id.product_min_threshold);
        productNames = findViewById(R.id.productNames);
        addButton = findViewById(R.id.addButton);

        // Set input filter for price to limit to 2 decimal places
        InputFilter decimalFilter = new InputFilter() {
            Pattern pattern = Pattern.compile("\\d+(\\.\\d{0,2})?");

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String result = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());
                Matcher matcher = pattern.matcher(result);
                if (!matcher.matches()) {
                    return "";
                }
                return null;
            }
        };
        product_price.setFilters(new InputFilter[]{decimalFilter});

        // Initialize the list of items for the spinner
        itemList = new ArrayList<>();
        itemList.add("Hotdog");
        itemList.add("Banana");
        itemList.add("Cherry");
        itemList.add("Date");
        itemList.add("Elderberry");

        // Initialize the ArrayAdapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemList);
        productNames.setAdapter(adapter);

        // Set a listener for item selection in the Spinner
        productNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProductName = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedProductName = null;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedProductName == null) {
                    Toast.makeText(AddInventory.this, "Please select a product", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    String priceText = product_price.getText().toString().trim();
                    float price = Float.parseFloat(priceText);
                    String formattedPrice = decimalFormat.format(price);
                    price = Float.parseFloat(formattedPrice);

                    int quantity = Integer.parseInt(product_qty_input.getText().toString().trim());
                    int threshold = Integer.parseInt(product_min_threshold.getText().toString().trim());

                    MyDataBaseHelper myDB = new MyDataBaseHelper(AddInventory.this);
                    myDB.addInventory(selectedProductName, quantity, price, threshold);

                    // Clear all inputs
                    product_qty_input.setText("");
                    product_price.setText("");
                    product_min_threshold.setText("");
                    productNames.setSelection(0);
                    selectedProductName = itemList.get(0); // Optional: set explicitly
                    Toast.makeText(AddInventory.this, "Item added successfully", Toast.LENGTH_SHORT).show();

                } catch (NumberFormatException e) {
                    Toast.makeText(AddInventory.this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
