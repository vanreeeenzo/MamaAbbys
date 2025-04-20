package com.example.mamaabbys;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddDeliveryActivity extends AppCompatActivity {

    EditText editTextOrder, editTextDate, editTextTime;
    Button btnSaveDelivery;
    MyDataBaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_delivery);

        editTextOrder = findViewById(R.id.editTextOrder);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        btnSaveDelivery = findViewById(R.id.btnSaveDelivery);

        myDB = new MyDataBaseHelper(this);

        editTextDate.setOnClickListener(v -> showDatePicker());
        editTextTime.setOnClickListener(v -> showTimePicker());

        btnSaveDelivery.setOnClickListener(v -> {
            String order = editTextOrder.getText().toString().trim();
            String date = editTextDate.getText().toString().trim();
            String time = editTextTime.getText().toString().trim();

            if (order.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                Delivery delivery = new Delivery(order, date, time);
                boolean success = myDB.addDelivery(delivery);
                if (success) {
                    Toast.makeText(this, "Delivery saved successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(this, "Failed to save delivery", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, day) -> editTextDate.setText(year + "-" + (month + 1) + "-" + day),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hour, minute) -> editTextTime.setText(String.format("%02d:%02d", hour, minute)),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void clearFields() {
        editTextOrder.setText("");
        editTextDate.setText("");
        editTextTime.setText("");
    }
}
