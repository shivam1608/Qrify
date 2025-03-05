package com.shivzee.qrifycs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ProfileActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private ColorManager colorManager;
    private MaterialButton btnColorPicker;
    private TextView tvSelectedColor;
    private int selectedColor;
    private String selectedColorHex;

    private void updateColorButton(int color) {
        selectedColor = color;
        selectedColorHex = String.format("#%06X", (0xFFFFFF & color));
        tvSelectedColor.setText("Selected: " + selectedColorHex);
        btnColorPicker.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        colorManager.setHighlightColor(selectedColorHex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tokenManager = new TokenManager(this);
        colorManager = ColorManager.getInstance(this);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Setup color picker
        btnColorPicker = findViewById(R.id.btnColorPicker);
        tvSelectedColor = findViewById(R.id.tvSelectedColor);
        selectedColor = colorManager.getHighlightColorInt();
        selectedColorHex = colorManager.getHighlightColor();
        updateColorButton(selectedColor);

        btnColorPicker.setOnClickListener(v -> {
            AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, selectedColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                    // Canceled
                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    updateColorButton(color);
                }
            });
            dialog.show();
        });

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