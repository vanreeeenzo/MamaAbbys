package com.example.mamaabbys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;

public class Login extends AppCompatActivity {

    MyDataBaseHelper myDB;
    EditText usernameEditText, passwordEditText;
    Button loginButton;
    TextView registerTextView;
    SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is already logged in
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        myDB = new MyDataBaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                String usernames = "javaro";
                String passwords = "123";

                if (username.equals(usernames) && password.equals(passwords)) {
                    // For the default user, use ID 1
                    sessionManager.setLogin(true, username, 1);
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    int userId = myDB.getUserId(username, password);
                    if (userId != -1) {
                        sessionManager.setLogin(true, username, userId);
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Incorrect Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }
}
