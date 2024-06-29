package com.muss_coding.crop_recommendation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class UserPolls extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PollAdapter adapter;
    private List<QuestionModel> questionList;

    private ImageView sorryImageView;

    private List<QuestionStatistics> questionStatisticsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_polls);

        recyclerView = findViewById(R.id.recyclerView);
        sorryImageView = findViewById(R.id.sorryImageView);
        questionStatisticsList = new ArrayList<>();

        DatabaseHandler dbHandler = new DatabaseHandler(this);
        dbHandler.getQuestionStatistics(new DatabaseHandler.OnQuestionStatisticsRetrievedListener() {
            @Override
            public void onQuestionStatisticsRetrieved(List<QuestionStatistics> statistics) {
                questionStatisticsList.addAll(statistics);
                adapter.notifyDataSetChanged();
                toggleEmptyView();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("QuizSection", "Error retrieving question statistics", e);
                toggleEmptyView();
            }
        });

        adapter = new PollAdapter(this, questionStatisticsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toggleEmptyView();  // Initial check

    }

    private void toggleEmptyView() {
        if (questionStatisticsList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            sorryImageView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            sorryImageView.setVisibility(View.GONE);
        }
    }
}