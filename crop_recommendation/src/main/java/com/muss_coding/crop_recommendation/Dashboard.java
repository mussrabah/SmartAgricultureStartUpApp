package com.muss_coding.crop_recommendation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/** Todo:
 * @Author: Pranav Sangave
 *
 *  1. Add one card for 'Ask Question' to check that the desired crop can be taken in the field or not !
 *  */


public class Dashboard extends AppCompatActivity {

    LinearLayout cropCard, helpCard, reportCard;

    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        cropCard = findViewById(R.id.cropCard);
        helpCard = findViewById(R.id.helpCard);
        userName = findViewById(R.id.user_name_t);
        reportCard = findViewById(R.id.reportCard);

        // Check if user details are available in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_shared_pref", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        userName.setText("Hello "+ username);

        cropCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, CheckCropForm.class);
                startActivity(intent);
            }
        });

        helpCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, QuizSection.class);
                startActivity(intent);
            }
        });

        reportCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, ReportLobby.class);
                startActivity(intent);
            }
        });



    }
}