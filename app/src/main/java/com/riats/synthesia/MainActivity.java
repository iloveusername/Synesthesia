package com.riats.synthesia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements RecognitionListener {
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private Handler taskHandler = new android.os.Handler();
    boolean ready = false;
    int staState = 0;
    int refState = 0;
    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        Button start = findViewById(R.id.start);
        Button reflect = findViewById(R.id.reflect);
        Button info = findViewById(R.id.info);

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            startHandler();
        }
        else {
            initModel();
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (staState == 0 && ready == true){
                    recognizeMicrophone();
                    start.setText("Stop");
                    staState = 1;
                }
                else{
                    if (speechService != null) {
                        speechService.stop();
                        speechService = null;
                    }
                    start.setText("Start");
                    staState = 0;
                }

            }
        });

        reflect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (refState == 0){
                    reflect.setText("HUD Mode");
                    reflect.setAlpha(0.2F);
                    start.setAlpha(0.2F);
                    info.setAlpha(0.2F);
                    textView.setRotation(90);
                    textView.setRotationY(180);
                    refState = 1;
                }
                else{
                    reflect.setText("Basic Mode");
                    reflect.setAlpha(1F);
                    start.setAlpha(1F);
                    info.setAlpha(1F);
                    textView.setRotation(0);
                    textView.setRotationY(0);
                    refState = 0;
                }
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MainActivity.super.finish();
                goToSettings();
            }
        });
    }

    private Runnable checkPerms = new Runnable() {
        @Override
        public void run() {
            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                initModel();
                stopHandler();
            }
            else{
                startHandler();
            }
        }
    };

    void startHandler(){
        taskHandler.postDelayed(checkPerms, 250);
    }

    void stopHandler(){
        taskHandler.removeCallbacks(checkPerms);
    }

    public void initModel() {
        StorageService.unpack(this, "model-en-us", "model",
                (model) -> {
                    this.model = model;
                    textView = findViewById(R.id.textView);
                    textView.setText("Press Start To Begin.");
                    ready = true;
                },
                (exception) -> setErrorState("Failed to unpack the model" + exception.getMessage()));
    }

    private void setErrorState(String message) {
        System.out.print(message);
        textView.setText(message);
    }

    private void recognizeMicrophone() {
        if (speechService != null) {
            speechService.stop();
            speechService = null;
        }
        else {
            try {
                Recognizer rec = new Recognizer(model, 16000.0f);
                speechService = new SpeechService(rec, 16000.0f);
                speechService.startListening(this);
            } catch (IOException e) {
                setErrorState(e.getMessage());
            }
        }
    }

    public void goToSettings(){
        Intent intent = new Intent(this, Information.class);
        startActivity(intent);
    }

    @Override
    public void onPartialResult(String hypothesis) {
        hypothesis = hypothesis.replace("{","");
        hypothesis = hypothesis.replace("}","");
        hypothesis = hypothesis.replace(" : ","");
        hypothesis = hypothesis.replace("\"partial\"","");
        hypothesis = hypothesis.replace("\"","");
        hypothesis = hypothesis.replace("\n","");

        if (hypothesis.length() > 50){
            hypothesis = hypothesis.substring(hypothesis.length() - 50);
        }

        textView.setText(hypothesis);
    }

    @Override
    public void onResult(String hypothesis) {

    }

    @Override
    public void onFinalResult(String hypothesis) {
        if (speechStreamService != null) {
            speechStreamService = null;
        }
    }

    @Override
    public void onError(Exception e) {
        setErrorState(e.getMessage());
    }

    @Override
    public void onTimeout() {

    }
}