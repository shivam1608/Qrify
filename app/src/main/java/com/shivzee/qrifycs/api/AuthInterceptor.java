package com.shivzee.qrifycs.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;

import com.shivzee.qrifycs.LoginActivity;
import com.shivzee.qrifycs.TokenManager;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private TokenManager tokenManager;
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
        this.tokenManager = new TokenManager(context);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String token = tokenManager.getToken();
        Request request = chain.request();

        if (token != null) {
            request = request.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
        }

        Response response = chain.proceed(request);

        if (response.code() == 401) { // Token expired
            tokenManager.clearToken();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }

        return response;
    }
}
