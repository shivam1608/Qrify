package com.shivzee.qrifycs.models;

//
//{
//        "message": "Rows found successfully",
//        "matchingRows": [
//
//        ]
//        }

import java.util.List;

public class SearchUserResponse {
    private String message;
    private List<Row> matchingRows;

    public String getMessage() {
        return message;
    }

    public List<Row> getMatchingRows() {
        return matchingRows;
    }
}
