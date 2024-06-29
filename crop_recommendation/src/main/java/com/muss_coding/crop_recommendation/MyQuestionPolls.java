package com.muss_coding.crop_recommendation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyQuestionPolls extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PollAdapter adapter;
    private List<QuestionStatistics> questionStatisticsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_question_polls);

        recyclerView = findViewById(R.id.recyclerView);
        questionStatisticsList = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("user_shared_pref", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        DatabaseHandler dbHandler = new DatabaseHandler(this);
        dbHandler.getUserSpecificQuestionStatistics(email, new DatabaseHandler.OnQuestionStatisticsRetrievedListener() {
            @Override
            public void onQuestionStatisticsRetrieved(List<QuestionStatistics> statistics) {
                questionStatisticsList.clear();
                questionStatisticsList.addAll(statistics);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("QuizSection", "Error retrieving question statistics", e);
            }
        });

        adapter = new PollAdapter(this, questionStatisticsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}