package com.muss_coding.crop_recommendation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AskQuestion extends AppCompatActivity {

    EditText questionInput;
    Button submitQuestionButton;

    // Define your Firestore database reference
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        questionInput = findViewById(R.id.questionInput);
        submitQuestionButton = findViewById(R.id.submitQuestionButton);

        // Retrieve values from Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String nitrogenReading = extras.getString("nitrogen_reading");
            String phosphorousReading = extras.getString("phosphorous_reading");
            String potassiumReading = extras.getString("potassium_reading");
            String temperature = extras.getString("temperature");
            String humidity = extras.getString("humidity");
            String phValueText = extras.getString("ph_value");
            String rainfallAmount = extras.getString("rainfall_amount");
            String predictedCrop = extras.getString("predicted_crop");
            String predictedMsp = extras.getString("predicted_msp");

            // Prepare the template question
            String templateQuestion = String.format("Given the following readings:\n" +
                            "Nitrogen: %s\n" +
                            "Phosphorous: %s\n" +
                            "Potassium: %s\n" +
                            "Temperature: %s\n" +
                            "Humidity: %s\n" +
                            "pH: %s\n" +
                            "Rainfall: %s\n" +
                            "Predicted Crop: %s\n\n" +
                            "Predicted Msp: %s\n\n" +
                            "Do you think its correct ?",
                    nitrogenReading, phosphorousReading, potassiumReading, temperature, humidity, phValueText, rainfallAmount, predictedCrop, predictedMsp);

            // Set the template question to the EditText
            questionInput.setText(templateQuestion);

            db = FirebaseFirestore.getInstance();
        }

        submitQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = questionInput.getText().toString().trim();
                if (!question.isEmpty()) {
                    // Here you can add the logic to send the question to nearby farmers
                    // For demonstration purposes, we'll just show a toast
                    Toast.makeText(AskQuestion.this, "Question sent: " + question, Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getSharedPreferences("user_shared_pref", Context.MODE_PRIVATE);
                    String email = sharedPreferences.getString("email", "");

                    Map<String, Object> questionMp = new HashMap<>();
                    questionMp.put("user_id", email);
                    questionMp.put("question", question);

                    DatabaseHandler db = new DatabaseHandler(AskQuestion.this);

                    // Add data to Firestore
                    db.addQuestionToFirestore("questions", questionMp, new DatabaseHandler.FirestoreCallback() {
                        @Override
                        public void onCallback(boolean isSuccess) {
                            if (isSuccess) {
                                Toast.makeText(AskQuestion.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                // Handle success (e.g., navigate to another activity)
                                Intent intent = new Intent(AskQuestion.this, Dashboard.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(AskQuestion.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                // Handle failure (e.g., display an error message)
                            }
                        }
                    });

                } else {
                    Toast.makeText(AskQuestion.this, "Please enter a question", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
