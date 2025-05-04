package com.example.mamaabbys;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DeliveryDetailsActivity extends AppCompatActivity {
    private MyDataBaseHelper myDB;
    private TextView orderNumberTextView;
    private TextView deliveryDateTextView;
    private TextView deliveryTimeTextView;
    private TextView orderDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        myDB = new MyDataBaseHelper(this);

        // Initialize views
        orderNumberTextView = findViewById(R.id.orderNumber);
        deliveryDateTextView = findViewById(R.id.deliveryDate);
        deliveryTimeTextView = findViewById(R.id.deliveryTime);
        orderDescriptionTextView = findViewById(R.id.orderDescription);

        // Get delivery ID from intent
        String deliveryId = getIntent().getStringExtra("delivery_id");
        if (deliveryId != null) {
            loadDeliveryDetails(deliveryId);
        } else {
            Toast.makeText(this, "Delivery details not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadDeliveryDetails(String deliveryId) {
        Delivery delivery = myDB.getDeliveryById(deliveryId);
        if (delivery != null) {
            orderNumberTextView.setText("Order #" + delivery.getId());
            deliveryDateTextView.setText("Delivery Date: " + delivery.getDeliveryDate());
            deliveryTimeTextView.setText("Delivery Time: " + delivery.getDeliveryTime());
            orderDescriptionTextView.setText("Order Description: " + delivery.getOrderDescription());
        } else {
            Toast.makeText(this, "Failed to load delivery details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDB != null) {
            myDB.close();
        }
    }
} 