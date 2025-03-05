package com.shivzee.qrifycs.models;

public class HighlightRequest {
    private final long rowNumber;
    private final HighlightConfig config;

    public HighlightRequest(long rowNumber , HighlightConfig config){
        this.rowNumber = rowNumber;
        this.config = config;
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public HighlightConfig getConfig() {
        return config;
    }
}
