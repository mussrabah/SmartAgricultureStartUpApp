package com.muss_coding.crop_recommendation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                // Check if SharedPreferences for user details are present
                SharedPreferences sharedPreferences = getSharedPreferences("user_shared_pref", Context.MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.contains("username");

                // Redirect to appropriate activity based on login status
                if (isLoggedIn) {
                    startActivity(new Intent(SplashScreen.this, Dashboard.class));
                } else {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                }
                finish(); // Finish the SplashScreen activity to prevent going back to it
            }
        }, 1000);
    }
}
