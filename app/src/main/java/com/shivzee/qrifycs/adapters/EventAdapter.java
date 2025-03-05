package com.shivzee.qrifycs.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shivzee.qrifycs.R;
import com.shivzee.qrifycs.models.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events = new ArrayList<>();
    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvEventName.setText(event.getName());
        holder.tvEventDescription.setText(event.getDescription());
        
        try {
            Date date = inputFormat.parse(event.getExpiresAt());
            holder.tvExpiresAt.setText("Expires: " + outputFormat.format(date));
        } catch (ParseException e) {
            holder.tvExpiresAt.setText("Expires: " + event.getExpiresAt());
        }

        holder.tvCreatedBy.setText("Created by: " + event.getCreatedBy().getName());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName;
        TextView tvEventDescription;
        TextView tvExpiresAt;
        TextView tvCreatedBy;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDescription = itemView.findViewById(R.id.tvEventDescription);
            tvExpiresAt = itemView.findViewById(R.id.tvExpiresAt);
            tvCreatedBy = itemView.findViewById(R.id.tvCreatedBy);
        }
    }
} 