package com.shivzee.qrifycs.api;

import com.shivzee.qrifycs.models.HighlightRequest;
import com.shivzee.qrifycs.models.LoginRequest;
import com.shivzee.qrifycs.models.LoginResponse;
import com.shivzee.qrifycs.models.QREntity;
import com.shivzee.qrifycs.models.SearchUserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/events/{id}")
    Call<SearchUserResponse> searchUser(@Path("id") String eventId, @Body QREntity request);

    @PATCH("api/events/{id}")
    Call<SearchUserResponse> highlightRow(@Path("id") String eventId, @Body HighlightRequest request);
}
