package com.muss_coding.crop_recommendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizSection extends AppCompatActivity {

    private static final String TAG = "FIRE_RESPONSE";

    private RecyclerView recyclerView;
    private QuizAdapter adapter;
    private List<QuestionModel> questionList;
    private ImageView sorryImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_section);

        recyclerView = findViewById(R.id.recyclerView);
        sorryImageView = findViewById(R.id.sorryImageView);

        // Initialize the list to hold questions
        questionList = new ArrayList<>();

        // Initialize Firestore
//        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DatabaseHandler dbHandler = new DatabaseHandler(QuizSection.this);

        SharedPreferences sharedPreferences = getSharedPreferences("user_shared_pref", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        dbHandler.getAllocatedQuestionsInRealTime(email, new DatabaseHandler.OnQuestionsRetrievedListener() {
            @Override
            public void onQuestionsRetrieved(List<DocumentSnapshot> questions) {
                questionList.clear(); // Clear existing list before adding new data
                for (DocumentSnapshot document : questions) {
                    // Get question from Firestore document
                    String question = document.getString("question");
                    String id = document.getReference().getId();
                    String uid = document.getString("user_id");
                    // Add question to the list
                    questionList.add(new QuestionModel(id, question, uid));
                }

                // Update the RecyclerView with the new data
                adapter.notifyDataSetChanged();
                toggleEmptyView();
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the error
                e.printStackTrace();
                toggleEmptyView();
            }
        });

        // Initialize and set up the adapter with the initial question list
        adapter = new QuizAdapter(QuizSection.this, questionList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toggleEmptyView();
    }

    private void toggleEmptyView() {
        if (questionList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            sorryImageView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            sorryImageView.setVisibility(View.GONE);
        }
    }
}
