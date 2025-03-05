package com.shivzee.qrifycs.models;

import java.util.List;

public class QREntity {
    private String event_id;
    private List<String> data;
    private String hash;

    public String getEventId() {
        return event_id;
    }

    public List<String> getData() {
        return data;
    }

    public String getHash() {
        return hash;
    }
}
