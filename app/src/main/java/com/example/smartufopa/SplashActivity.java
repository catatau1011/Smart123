package com.example.smartufopa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.smartufopa.Activitys.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(2000);
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }catch (Exception e){

                }
            }
        };thread.start();


    }
}