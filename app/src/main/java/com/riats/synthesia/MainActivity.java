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
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements RecognitionListener {
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
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

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
        else {
            initModel();
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (staState == 0){
                    recognizeMicrophone();
                    start.setText("Stop");
                    textView.setText("Start Talking.");
                    staState = 1;
                }
                else{
                    recognizeMicrophone();
                    start.setText("Start");
                    textView.setText("Press Start To Begin.");
                    staState =0;
                }

            }
        });

        reflect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (refState == 0){
                    textView.setRotation(90);
                    textView.setRotationY(180);
                    refState = 1;
                }
                else{
                    textView.setRotation(0);
                    textView.setRotationY(0);
                    refState = 0;
                }
            }
        });
    }

    private void initModel() {
        StorageService.unpack(this, "model-en-us", "model",
                (model) -> {
                    this.model = model;
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

    @Override
    public void onPartialResult(String hypothesis) {
        hypothesis = hypothesis.replace("{","");
        hypothesis = hypothesis.replace("}","");
        hypothesis = hypothesis.replace(":","");
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