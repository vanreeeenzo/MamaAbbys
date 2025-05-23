package com.example.mamaabbys;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddDeliveryActivity extends AppCompatActivity {

    private TextInputEditText nameInput;
    private TextInputEditText orderInput;
    private TextInputEditText dateInput;
    private TextInputEditText timeInput;
    private TextInputEditText locationInput;
    private MyDataBaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery);

        // Initialize views
        nameInput = findViewById(R.id.nameInput);
        orderInput = findViewById(R.id.orderInput);
        dateInput = findViewById(R.id.dateInput);
        timeInput = findViewById(R.id.timeInput);
        locationInput = findViewById(R.id.locationInput);
        MaterialButton submitButton = findViewById(R.id.submitButton);

        // Initialize database helper and session manager
        dbHelper = new MyDataBaseHelper(this);
        sessionManager = new SessionManager(this);

        // Set up date picker
        dateInput.setOnClickListener(v -> showDatePicker());

        // Set up time picker
        timeInput.setOnClickListener(v -> showTimePicker());

        // Set up submit button
        submitButton.setOnClickListener(v -> submitDelivery());

        // Force CAPSLOCK for all text fields
        setupTextWatchers();
    }

    private void setupTextWatchers() {
        TextWatcher capsWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String upper = s.toString().toUpperCase();
                if (!upper.equals(s.toString())) {
                    s.replace(0, s.length(), upper);
                }
            }
        };

        nameInput.addTextChangedListener(capsWatcher);
        orderInput.addTextChangedListener(capsWatcher);
        locationInput.addTextChangedListener(capsWatcher);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                dateInput.setText(date);
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                timeInput.setText(time);
            },
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            true
        );
        timePickerDialog.show();
    }

    private void submitDelivery() {
        String name = nameInput.getText().toString().trim();
        String order = orderInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty()) {
            nameInput.setError("Please enter delivery name");
            return;
        }
        if (order.isEmpty()) {
            orderInput.setError("Please enter order description");
            return;
        }
        if (date.isEmpty()) {
            dateInput.setError("Please select delivery date");
            return;
        }
        if (time.isEmpty()) {
            timeInput.setError("Please select delivery time");
            return;
        }
        if (location.isEmpty()) {
            locationInput.setError("Please enter delivery location");
            return;
        }

        // Get user ID from session
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create delivery object
        Delivery delivery = new Delivery(name, order, date, time, location);

        // Add delivery to database
        try {
            if (dbHelper.addDelivery(delivery, userId)) {
                Toast.makeText(this, "Delivery added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add delivery. Please try again.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
