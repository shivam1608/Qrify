package com.shivzee.qrifycs;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.shivzee.qrifycs.api.ApiClient;
import com.shivzee.qrifycs.api.ApiService;
import com.shivzee.qrifycs.models.CreateEventRequest;
import com.shivzee.qrifycs.models.CreateEventResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity {

    private TextInputEditText etEventName, etDescription, etExpiresAt;
    private MaterialButton btnSelectFile, btnCreateEvent;
    private TextView tvSelectedFile;
    private Uri selectedFileUri;
    private Date selectedDateTime;
    private ApiService apiService;

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedFileUri = result.getData().getData();
                if (selectedFileUri != null) {
                    String fileName = getFileName(selectedFileUri);
                    tvSelectedFile.setText("Selected: " + fileName);
                }
            }
        }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openFilePicker();
            } else {
                Toast.makeText(this, "Storage permission is required to select files", Toast.LENGTH_SHORT).show();
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize API service
        apiService = ApiClient.getApiService(this);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Setup views
        etEventName = findViewById(R.id.etEventName);
        etDescription = findViewById(R.id.etDescription);
        etExpiresAt = findViewById(R.id.etExpiresAt);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        tvSelectedFile = findViewById(R.id.tvSelectedFile);

        // Setup date/time picker
        etExpiresAt.setOnClickListener(v -> showDateTimePicker());

        // Setup file picker
        btnSelectFile.setOnClickListener(v -> checkPermissionAndOpenPicker());

        // Setup create event button
        btnCreateEvent.setOnClickListener(v -> createEvent());
    }

    private void checkPermissionAndOpenPicker() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) 
                == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/csv"
        });
        filePickerLauncher.launch(intent);
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDateTime != null) {
            calendar.setTime(selectedDateTime);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view1, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        selectedDateTime = calendar.getTime();

                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                        etExpiresAt.setText(sdf.format(selectedDateTime));
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                );
                timePickerDialog.show();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void createEvent() {
        String name = etEventName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || selectedDateTime == null || selectedFileUri == null) {
            Toast.makeText(this, "Please fill all fields and select a file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create multipart request
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody expiresAtPart = RequestBody.create(MediaType.parse("text/plain"), 
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(selectedDateTime));

        // Get file from URI using InputStream
        try {
            String fileName = getFileName(selectedFileUri);
            InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
            if (inputStream == null) {
                throw new IOException("Failed to open file");
            }

            // Create a temporary file
            File tempFile = File.createTempFile("upload", null, getCacheDir());
            OutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();

            // Create multipart body from temporary file
            RequestBody filePart = RequestBody.create(MediaType.parse("application/octet-stream"), tempFile);
            MultipartBody.Part fileBody = MultipartBody.Part.createFormData("file", fileName, filePart);

            // Create event request
            Call<CreateEventResponse> call = apiService.createEvent(namePart, descriptionPart, expiresAtPart, fileBody);
            call.enqueue(new Callback<CreateEventResponse>() {
                @Override
                public void onResponse(Call<CreateEventResponse> call, Response<CreateEventResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(CreateEventActivity.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String errorMessage = "Failed to create event";
                        try {
                            if (response.errorBody() != null) {
                                errorMessage = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e("CreateEventActivity", "Error creating event: " + errorMessage);
                        Toast.makeText(CreateEventActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CreateEventResponse> call, Throwable t) {
                    Log.e("CreateEventActivity", "Network Error: " + t.getMessage());
                    Toast.makeText(CreateEventActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("CreateEventActivity", "Error processing file: " + e.getMessage());
            Toast.makeText(this, "Error processing file", Toast.LENGTH_SHORT).show();
        }
    }
} 