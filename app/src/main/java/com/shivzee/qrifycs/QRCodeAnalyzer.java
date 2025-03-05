package com.shivzee.qrifycs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

public class QRCodeAnalyzer implements ImageAnalysis.Analyzer {

    private final QRScannerActivity activity;
    private boolean isProcessing = false;

    public QRCodeAnalyzer(QRScannerActivity activity) {
        this.activity = activity;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void analyze(ImageProxy image) {
        if (isProcessing) {
            image.close();
            return; // ✅ Ignore further frames once processing starts
        }

        @SuppressLint("UnsafeOptInUsageError")
        InputImage inputImage = InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees());

        BarcodeScanner scanner = BarcodeScanning.getClient();
        scanner.process(inputImage)
                .addOnSuccessListener(barcodes -> {
                    if (!barcodes.isEmpty()) {
                        isProcessing = true;
                        Barcode barcode = barcodes.get(0);
                        String scannedData = barcode.getRawValue();

                        Log.d("QRScanner", "QR Code: " + scannedData);
                        Toast.makeText(activity, "Scanned: " + scannedData, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(activity, ResultActivity.class);
                        intent.putExtra("scanned_data", scannedData);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace)
                .addOnCompleteListener(task -> image.close());
    }
}
