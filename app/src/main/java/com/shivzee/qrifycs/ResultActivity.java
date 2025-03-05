package com.shivzee.qrifycs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.shivzee.qrifycs.api.ApiClient;
import com.shivzee.qrifycs.api.ApiService;
import com.shivzee.qrifycs.models.HighlightConfig;
import com.shivzee.qrifycs.models.HighlightRequest;
import com.shivzee.qrifycs.models.QREntity;
import com.shivzee.qrifycs.models.Row;
import com.shivzee.qrifycs.models.SearchUserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends AppCompatActivity {

    private final Gson gson = new GsonBuilder().create();
    private Button btnPresent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        btnPresent = findViewById(R.id.btnPresent);

        TextView resultText = findViewById(R.id.resultText);
        ApiService apiService = ApiClient.getApiService(this);


        Intent intent = getIntent();
        String scannedData = intent.getStringExtra("scanned_data");


        try{
            QREntity qr = gson.fromJson(scannedData , QREntity.class);
            if (qr.getEventId() != null){
                Call<SearchUserResponse> call = apiService.searchUser(qr.getEventId(), qr);
                call.enqueue(new Callback<SearchUserResponse>() {
                    @Override
                    public void onResponse(Call<SearchUserResponse> call, Response<SearchUserResponse> response) {

                        if(response.body()==null){
                            return;
                        }

                        if (response.isSuccessful()) {
                            Row row = response.body().getMatchingRows().get(0);

                            StringBuilder builder = new StringBuilder();
                            for(String data : row.getRowData()){
                                builder.append(data).append("\n");
                            }

                            builder.append("Row Number :").append(row.getRowNumber()).append("\n");
                            btnPresent.setOnClickListener((v)->{
                                try{

                                    Call<SearchUserResponse> call2 = apiService.highlightRow(qr.getEventId(), new HighlightRequest(row.getRowNumber(), new HighlightConfig("#22EB5D")));
                                    call2.enqueue(new Callback<SearchUserResponse>() {
                                        @Override
                                        public void onResponse(Call<SearchUserResponse> call, Response<SearchUserResponse> response) {
                                            if(response.isSuccessful()) {
                                                assert response.body() != null;
                                                if (response.body().getMessage()!=null) {
                                                    Toast.makeText(ResultActivity.this , "User Marked Present" , Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<SearchUserResponse> call, Throwable t) {
                                            Log.e("Mark as Present Failed", t.getMessage());
                                            Toast.makeText(ResultActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }catch (Exception e){
                                    Log.e("Mark as Present Failed" , e.getMessage());
                                }
                            });
                            resultText.setText(builder.toString());
                        } else {
                            String message = response.body().getMessage();
                            resultText.setText(message);
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchUserResponse> call, Throwable t) {
                        Log.e("QR Scanner Error", t.getMessage());
                        Toast.makeText(ResultActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
                    }
                });

            }else{
                Log.e("Invalid QR Code" ,"QR code scanned is not as per convention");
                Toast.makeText(ResultActivity.this , "Invalid QR Code" , Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (JsonSyntaxException error){
            Log.e("JSON Syntax Error" ,error.getMessage());
            Toast.makeText(ResultActivity.this , "Invalid QR Code" , Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
