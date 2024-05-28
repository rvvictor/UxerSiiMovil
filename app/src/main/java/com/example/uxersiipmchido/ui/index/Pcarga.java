package com.example.uxersiipmchido.ui.index;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.uxersiipmchido.R;

import java.util.Timer;
import java.util.TimerTask;

public class Pcarga extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcarga);

        TimerTask tcarga = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Pcarga.this, index.class);
                startActivity(intent);
                finish();
            }
        };
        Timer sec = new Timer();
        sec.schedule(tcarga, 1500);
    }
}