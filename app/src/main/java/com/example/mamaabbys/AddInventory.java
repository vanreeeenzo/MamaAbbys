package com.example.mamaabbys;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AddInventory extends AppCompatActivity {

    EditText product_name_input, product_qty_input, product_price, product_min_threshold;
    Spinner productNames;
    ArrayAdapter<String> adapter;
    List<String> itemList;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_inventory);

        product_name_input = findViewById(R.id.product_name_input);
        product_qty_input = findViewById(R.id.product_qty_input);
        product_price = findViewById(R.id.product_price);
        product_min_threshold = findViewById(R.id.product_min_threshold);
        productNames = findViewById(R.id.productNames);
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDataBaseHelper myDB = new MyDataBaseHelper(AddInventory.this);
                myDB.addInventory(product_name_input.getText().toString().trim(),
                        Integer.valueOf(product_qty_input.getText().toString().trim()),
                        Float.valueOf(product_price.getText().toString().trim()),
                        Integer.valueOf(product_min_threshold.getText().toString().trim()));
            }
        });

        // Initialize the list of items for the spinner
        itemList = new ArrayList<>();
        itemList.add("Hotdog");
        itemList.add("Banana");
        itemList.add("Cherry");
        itemList.add("Date");
        itemList.add("Elderberry");

        // Initialize the ArrayAdapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemList);

        // Set the ArrayAdapter to the Spinner
        productNames.setAdapter(adapter);

        // Set a listener for item selection in the Spinner
        productNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Toast.makeText(AddInventory.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set a TextWatcher for the EditText to filter the Spinner items
        product_name_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSpinnerItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void filterSpinnerItems(String searchText) {
        List<String> filteredList = new ArrayList<>();
        for (String item : itemList) {
            if (item.toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(item);
            }
        }

        // Update the ArrayAdapter with the filtered list
        ArrayAdapter<String> filteredAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filteredList);
        productNames.setAdapter(filteredAdapter);

        // Optionally, if no match is found, you might want to display a message
        if (filteredList.isEmpty() && !searchText.isEmpty()) {
            // You could add a "No match found" item to the spinner temporarily
            List<String> noMatchList = new ArrayList<>();
            noMatchList.add("No match found");
            ArrayAdapter<String> noMatchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, noMatchList);
            productNames.setAdapter(noMatchAdapter);
        } else if (searchText.isEmpty()) {
            // If the EditText is empty, reset to the original list
            productNames.setAdapter(adapter);
        }

    }
}