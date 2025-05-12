package com.example.mamaabbys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {
    EditText fullnameEditTxt, EmailEditTxt, PasswordEditTxt, confirmPasswordEditTxt;
    Button btnregisterButton;
    ImageButton backButton;
    MyDataBaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        fullnameEditTxt = findViewById(R.id.fullNameEditText);
        EmailEditTxt = findViewById(R.id.emailEditText);
        PasswordEditTxt = findViewById(R.id.passwordEditText);
        confirmPasswordEditTxt = findViewById(R.id.confirmPasswordEditText);
        btnregisterButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);

        myDB = new MyDataBaseHelper(this);

        // Set up back button click listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to login page
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        btnregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strfullname = fullnameEditTxt.getText().toString().trim();
                String strEmail = EmailEditTxt.getText().toString().trim();
                String strpassword = PasswordEditTxt.getText().toString().trim();
                String strconfirm = confirmPasswordEditTxt.getText().toString().trim();

                if (strfullname.isEmpty() || strEmail.isEmpty() || strpassword.isEmpty() || strconfirm.isEmpty()) {
                    Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!strpassword.equals(strconfirm)) {
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User(strfullname, strEmail, strpassword);
                    myDB.addUser(user);

                    Toast.makeText(Register.this, "Account Created!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}