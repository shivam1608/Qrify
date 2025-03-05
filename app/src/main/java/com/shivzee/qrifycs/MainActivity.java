package com.shivzee.qrifycs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shivzee.qrifycs.adapters.EventAdapter;
import com.shivzee.qrifycs.api.ApiClient;
import com.shivzee.qrifycs.api.ApiService;
import com.shivzee.qrifycs.models.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private EventAdapter eventAdapter;
    private TextView tvActiveEventsCount;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tokenManager = new TokenManager(this);

        if(tokenManager.getToken() == null){
            startActivity(new Intent(this , LoginActivity.class));
            finish();
        }

        setContentView(R.layout.activity_main);

        // Initialize API service
        apiService = ApiClient.getApiService(this);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup views
        tvActiveEventsCount = findViewById(R.id.tvActiveEventsCount);
        RecyclerView rvEvents = findViewById(R.id.rvEvents);
        MaterialButton btnCreateEvent = findViewById(R.id.btnCreateEvent);
        MaterialButton btnShowMore = findViewById(R.id.btnShowMore);
        FloatingActionButton fabScanQr = findViewById(R.id.fabScanQr);

        // Setup RecyclerView
        eventAdapter = new EventAdapter();
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(eventAdapter);

        // Setup click listeners
        btnCreateEvent.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CreateEventActivity.class));
        });

        btnShowMore.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, EventsListActivity.class));
        });

        fabScanQr.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, QRScannerActivity.class)));

        // Load events
        loadEvents("active");
    }

    private void loadEvents(String status) {
        Call<List<Event>> call = apiService.getEvents(status);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> events = response.body();
                    eventAdapter.setEvents(events);
                    tvActiveEventsCount.setText(String.valueOf(events.size()));
                } else {
                    String errorMessage = "Failed to load events";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("MainActivity", "Error loading events: " + errorMessage);
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.e("MainActivity", "Network Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}