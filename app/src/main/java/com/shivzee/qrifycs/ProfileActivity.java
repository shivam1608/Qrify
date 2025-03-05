package com.shivzee.qrifycs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {

    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tokenManager = new TokenManager(this);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Setup logout button
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            tokenManager.clearToken();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finishAffinity();
        });

        // TODO: Set user information from your user model
        TextView userName = findViewById(R.id.userName);
        TextView userEmail = findViewById(R.id.userEmail);
        // userName.setText(user.getName());
        // userEmail.setText(user.getEmail());
    }
} 