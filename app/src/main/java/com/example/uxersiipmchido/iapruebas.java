package com.example.uxersiipmchido;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class iapruebas extends AppCompatActivity {

    private static final String TAG = "iapruebas";
    private PreviewView previewView;
    private TextView predictionTextView;
    private Interpreter tflite;
    private ExecutorService cameraExecutor;
    String[] labels = {"Manzana Fresca", "Banana Fresca", "Pepino Fresco",
            "Naranja Fresca", "Tomate Fresco",
            "Manzana Podrida", "Banana Podrida", "Pepino Podrido",
            "Naranja Podrida", "Tomate Podrido"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iapruebas);

        previewView = findViewById(R.id.previewView);
        predictionTextView = findViewById(R.id.predictionTextView);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            Log.e(TAG, "Error loading model", e);
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
        startCamera();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalyzer());

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("modelo_v6.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        fileDescriptor.close();
        return mappedByteBuffer;
    }

    private class ImageAnalyzer implements ImageAnalysis.Analyzer {

        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            Bitmap bitmap = toBitmap(imageProxy);
            if (bitmap != null) {
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
                float[] input = bitmapToInputArray(resizedBitmap);

                float[][] output = new float[1][labels.length];
                tflite.run(input, output);

                runOnUiThread(() -> predictionTextView.setText(getLabel(output)));
            }

            imageProxy.close();
        }

        private Bitmap toBitmap(ImageProxy imageProxy) {
            ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
            if (bitmap == null) {
                return null;
            }

            return Bitmap.createBitmap(bitmap, imageProxy.getCropRect().left, imageProxy.getCropRect().top,
                    imageProxy.getCropRect().width(), imageProxy.getCropRect().height());
        }

        private float[] bitmapToInputArray(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float[] input = new float[width * height * 3];
            int[] intValues = new int[width * height];
            bitmap.getPixels(intValues, 0, width, 0, 0, width, height);

            for (int i = 0; i < intValues.length; ++i) {
                final int val = intValues[i];
                input[i * 3 + 0] = ((val >> 16) & 0xFF) / 255.0f;
                input[i * 3 + 1] = ((val >> 8) & 0xFF) / 255.0f;
                input[i * 3 + 2] = (val & 0xFF) / 255.0f;
            }
            return input;
        }

        private String getLabel(float[][] output) {
            int maxIndex = -1;
            float maxValue = Float.MIN_VALUE;
            for (int i = 0; i < output[0].length; i++) {
                if (output[0][i] > maxValue) {
                    maxValue = output[0][i];
                    maxIndex = i;
                }
            }
            return labels[maxIndex];
        }
    }
}

