package com.riats.synthesia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {
    private AnimatedVectorDrawable animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView image = findViewById(R.id.image2);
        Drawable d = image.getDrawable();
        animation = (AnimatedVectorDrawable) d;
        animation.start();

        image.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToMain();
            }
        }, 750);

    }

    public void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}