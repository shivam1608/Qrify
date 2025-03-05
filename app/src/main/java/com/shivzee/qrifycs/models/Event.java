package com.shivzee.qrifycs.models;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class Event {
    private String id;
    private String name;
    private String description;
    private String expiresAt;
    private String sheetId;
    private String sheetName;
    private String createdAt;
    private String userId;
    private CreatedBy createdBy;

    public static class CreatedBy {
        private String name;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getSheetId() {
        return sheetId;
    }

    public void setSheetId(String sheetId) {
        this.sheetId = sheetId;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        if (expiresAt == null || expiresAt.isEmpty()) {
            return "Invalid Date";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date expirationTime = sdf.parse(expiresAt);
            Date currentTime = new Date();

            return currentTime.after(expirationTime) ? "inactive" : "active";
        } catch (ParseException e) {
            try {
                // Try alternate format without milliseconds
                SimpleDateFormat altSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                altSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date expirationTime = altSdf.parse(expiresAt);
                Date currentTime = new Date();

                return currentTime.after(expirationTime) ? "inactive" : "active";
            } catch (ParseException ex) {
                Log.e("Event", "Error parsing date: " + expiresAt, ex);
                return "Invalid Date";
            }
        }
    }
} 