package com.shivzee.qrifycs.models;

import com.google.gson.annotations.SerializedName;

public class CreateEventRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("expiresAt")
    private String expiresAt;

    public CreateEventRequest(String name, String description, String expiresAt) {
        this.name = name;
        this.description = description;
        this.expiresAt = expiresAt;
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
} 