package com.shivzee.qrifycs;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.shivzee.qrifycs.adapters.EventAdapter;
import com.shivzee.qrifycs.api.ApiClient;
import com.shivzee.qrifycs.api.ApiService;
import com.shivzee.qrifycs.models.Event;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsListActivity extends AppCompatActivity {

    private EventAdapter eventAdapter;
    private ApiService apiService;
    private List<Event> allEvents = new ArrayList<>();
    private ChipGroup chipGroup;
    private Chip chipAll, chipActive, chipInactive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        // Initialize API service
        apiService = ApiClient.getApiService(this);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Setup views
        RecyclerView rvEvents = findViewById(R.id.rvEvents);
        chipGroup = findViewById(R.id.chipGroup);
        chipAll = findViewById(R.id.chipAll);
        chipActive = findViewById(R.id.chipActive);
        chipInactive = findViewById(R.id.chipInactive);

        // Setup RecyclerView
        eventAdapter = new EventAdapter();
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(eventAdapter);

        // Setup chip group listener
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == chipAll.getId()) {
                eventAdapter.setEvents(allEvents);
            } else if (checkedId == chipActive.getId()) {
                filterEvents("active");
            } else if (checkedId == chipInactive.getId()) {
                filterEvents("inactive");
            }
        });

        // Load all events
        loadEvents("all");
    }

    private void loadEvents(String status) {
        Call<List<Event>> call = apiService.getEvents(status);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allEvents = response.body();
                    eventAdapter.setEvents(allEvents);
                    chipAll.setChecked(true);
                } else {
                    String errorMessage = "Failed to load events";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("EventsListActivity", "Error loading events: " + errorMessage);
                    Toast.makeText(EventsListActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.e("EventsListActivity", "Network Error: " + t.getMessage());
                Toast.makeText(EventsListActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterEvents(String status) {
        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : allEvents) {
            if (event.getStatus().equals(status)) {
                filteredEvents.add(event);
            }
        }
        eventAdapter.setEvents(filteredEvents);
    }
} 