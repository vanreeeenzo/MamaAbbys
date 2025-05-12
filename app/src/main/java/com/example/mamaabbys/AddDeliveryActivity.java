package com.example.mamaabbys;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddDeliveryActivity extends AppCompatActivity {

    EditText editTextOrder, editTextDate, editTextTime, editTextLocation;
    Button btnSaveDelivery;
    ImageButton btnClose;
    MyDataBaseHelper myDB;
    private Calendar selectedDate;
    private Calendar currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_delivery);

        myDB = new MyDataBaseHelper(this);
        editTextOrder = findViewById(R.id.editTextOrder);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextLocation = findViewById(R.id.editTextLocation);
        btnSaveDelivery = findViewById(R.id.btnSaveDelivery);
        btnClose = findViewById(R.id.btnClose);

        currentDate = Calendar.getInstance();
        selectedDate = Calendar.getInstance();

        editTextDate.setOnClickListener(v -> showDatePicker());
        editTextTime.setOnClickListener(v -> showTimePicker());

        btnClose.setOnClickListener(v -> finish());

        btnSaveDelivery.setOnClickListener(v -> {
            String order = editTextOrder.getText().toString().trim();
            String date = editTextDate.getText().toString().trim();
            String time = editTextTime.getText().toString().trim();
            String location = editTextLocation.getText().toString().trim();

            if (order.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate if the selected date is in the future
            if (!isValidDeliveryDate(date, time)) {
                Toast.makeText(this, "Please select a future date and time for delivery", Toast.LENGTH_SHORT).show();
                return;
            }

            Delivery delivery = new Delivery(order, date, time, location);
            boolean success = myDB.addDelivery(delivery);
            if (success) {
                Toast.makeText(this, "Delivery saved successfully", Toast.LENGTH_SHORT).show();
                clearFields();
                finish();
            } else {
                Toast.makeText(this, "Failed to save delivery", Toast.LENGTH_SHORT).show();
            }
        });

        // Force CAPSLOCK for Order Details
        editTextOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String upper = s.toString().toUpperCase();
                if (!upper.equals(s.toString())) {
                    editTextOrder.setText(upper);
                    editTextOrder.setSelection(upper.length());
                }
            }
        });
        // Force CAPSLOCK for Location
        editTextLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String upper = s.toString().toUpperCase();
                if (!upper.equals(s.toString())) {
                    editTextLocation.setText(upper);
                    editTextLocation.setSelection(upper.length());
                }
            }
        });
    }

    private boolean isValidDeliveryDate(String date, String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date deliveryDateTime = sdf.parse(date + " " + time);
            Date currentDateTime = new Date();

            return deliveryDateTime != null && deliveryDateTime.after(currentDateTime);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    selectedDate.set(year, month, day);
                    editTextDate.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day));
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH));

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hour, minute) -> {
                    selectedDate.set(Calendar.HOUR_OF_DAY, hour);
                    selectedDate.set(Calendar.MINUTE, minute);
                    editTextTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                },
                currentDate.get(Calendar.HOUR_OF_DAY),
                currentDate.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    private void clearFields() {
        editTextOrder.setText("");
        editTextDate.setText("");
        editTextTime.setText("");
        editTextLocation.setText("");
    }
}
