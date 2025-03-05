package com.shivzee.qrifycs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.shivzee.qrifycs.api.ApiClient;
import com.shivzee.qrifycs.api.ApiService;
import com.shivzee.qrifycs.models.HighlightConfig;
import com.shivzee.qrifycs.models.HighlightRequest;
import com.shivzee.qrifycs.models.QREntity;
import com.shivzee.qrifycs.models.Row;
import com.shivzee.qrifycs.models.SearchUserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ResultActivity extends AppCompatActivity {

    private final Gson gson = new GsonBuilder().create();
    private MaterialButton btnPresent;
    private MaterialButton btnColorPicker;
    private TextView tvSelectedColor;
    private int selectedColor;
    private String selectedColorHex;
    private ColorManager colorManager;

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
        setContentView(R.layout.activity_result);
        
        colorManager = ColorManager.getInstance(this);
        selectedColor = colorManager.getHighlightColorInt();
        selectedColorHex = colorManager.getHighlightColor();

        btnPresent = findViewById(R.id.btnPresent);
        btnColorPicker = findViewById(R.id.btnColorPicker);
        tvSelectedColor = findViewById(R.id.tvSelectedColor);

        // Set initial color
        updateColorButton(selectedColor);

        TextView resultText = findViewById(R.id.resultText);
        ApiService apiService = ApiClient.getApiService(this);

        Intent intent = getIntent();
        String scannedData = intent.getStringExtra("scanned_data");

        // Setup color picker
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

        try {
            QREntity qr = gson.fromJson(scannedData, QREntity.class);
            if (qr.getEventId() != null) {
                Call<SearchUserResponse> call = apiService.searchUser(qr.getEventId(), qr);
                call.enqueue(new Callback<SearchUserResponse>() {
                    @Override
                    public void onResponse(Call<SearchUserResponse> call, Response<SearchUserResponse> response) {
                        if (response.body() == null) {
                            return;
                        }

                        if (response.isSuccessful()) {
                            Row row = response.body().getMatchingRows().get(0);

                            StringBuilder builder = new StringBuilder();
                            for (String data : row.getRowData()) {
                                builder.append(data).append("\n");
                            }

                            builder.append("Row Number: ").append(row.getRowNumber()).append("\n");
                            btnPresent.setOnClickListener((v) -> {
                                try {
                                    Call<SearchUserResponse> call2 = apiService.highlightRow(qr.getEventId(), 
                                        new HighlightRequest(row.getRowNumber(), new HighlightConfig(selectedColorHex)));
                                    call2.enqueue(new Callback<SearchUserResponse>() {
                                        @Override
                                        public void onResponse(Call<SearchUserResponse> call, Response<SearchUserResponse> response) {
                                            if (response.isSuccessful()) {
                                                assert response.body() != null;
                                                if (response.body().getMessage() != null) {
                                                    Toast.makeText(ResultActivity.this, "User Marked Present", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<SearchUserResponse> call, Throwable t) {
                                            Log.e("Mark as Present Failed", t.getMessage());
                                            Toast.makeText(ResultActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.e("Mark as Present Failed", e.getMessage());
                                }
                            });
                            resultText.setText(builder.toString());
                        } else {
                            String message = response.body().getMessage();
                            resultText.setText(message);
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchUserResponse> call, Throwable t) {
                        Log.e("QR Scanner Error", t.getMessage());
                        Toast.makeText(ResultActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e("Invalid QR Code", "QR code scanned is not as per convention");
                Toast.makeText(ResultActivity.this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (JsonSyntaxException error) {
            Log.e("JSON Syntax Error", error.getMessage());
            Toast.makeText(ResultActivity.this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
