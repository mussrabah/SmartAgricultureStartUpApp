package com.muss_coding.crop_recommendation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class ReportLobby extends AppCompatActivity {

    LinearLayout quizReport, ownReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_lobby);

        quizReport = findViewById(R.id.quizReport);
        ownReport = findViewById(R.id.ownReport);

        quizReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportLobby.this, UserPolls.class);
                startActivity(intent);
            }
        });

        ownReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportLobby.this, MyQuestionPolls.class);
                startActivity(intent);
            }
        });
    }
}