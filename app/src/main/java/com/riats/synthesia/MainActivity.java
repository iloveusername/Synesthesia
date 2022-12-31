package com.riats.synthesia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.vosk.Model;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    int state = 0;
    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textView);
        Button start = findViewById(R.id.start);
        Button reflect = findViewById(R.id.reflect);

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

        reflect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state == 0){
                    textView.setRotation(90);
                    textView.setRotationY(180);
                    state = 1;
                }
                else{
                    textView.setRotation(0);
                    textView.setRotationY(0);
                    state = 0;
                }
            }
        });
    }
}