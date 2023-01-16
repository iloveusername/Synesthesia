package com.riats.synthesia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {
    private AnimatedVectorDrawable animation;
    private Handler taskHandler = new android.os.Handler();
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
        setContentView(R.layout.activity_splash);
        ImageView image = findViewById(R.id.image2);
        Drawable d = image.getDrawable();
        animation = (AnimatedVectorDrawable) d;
        animation.start();
        startHandler();

//        image.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                goToMain();
//            }
//        }, 750);

    }

    private Runnable checkPerms = new Runnable() {
        @Override
        public void run() {
            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                goToMain();
            }
            else{
                startHandler();
            }
        }
    };

    void startHandler(){
        taskHandler.postDelayed(checkPerms, 750);
    }

    void stopHandler(){
        taskHandler.removeCallbacks(checkPerms);
    }

    public void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}