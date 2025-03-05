package com.shivzee.qrifycs.models;

import com.google.gson.annotations.SerializedName;

public class CreateEventResponse {
    @SerializedName("sheetId")
    private String sheetId;

    public String getSheetId() {
        return sheetId;
    }

    public void setSheetId(String sheetId) {
        this.sheetId = sheetId;
    }
} 