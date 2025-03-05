package com.shivzee.qrifycs.api;

import com.shivzee.qrifycs.models.Event;
import com.shivzee.qrifycs.models.HighlightRequest;
import com.shivzee.qrifycs.models.LoginRequest;
import com.shivzee.qrifycs.models.LoginResponse;
import com.shivzee.qrifycs.models.QREntity;
import com.shivzee.qrifycs.models.SearchUserResponse;
import com.shivzee.qrifycs.models.CreateEventResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/events")
    Call<List<Event>> getEvents(@Query("status") String status);

    @POST("api/events/{id}")
    Call<SearchUserResponse> searchUser(@Path("id") String eventId, @Body QREntity request);

    @PATCH("api/events/{id}")
    Call<SearchUserResponse> highlightRow(@Path("id") String eventId, @Body HighlightRequest request);

    @Multipart
    @POST("api/events")
    Call<CreateEventResponse> createEvent(
        @Part("name") RequestBody name,
        @Part("description") RequestBody description,
        @Part("expiresAt") RequestBody expiresAt,
        @Part MultipartBody.Part file
    );
}
