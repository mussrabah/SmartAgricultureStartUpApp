package com.muss_coding.crop_recommendation;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private List<QuestionModel> questions;
    private Context context;

    public QuizAdapter(Context context, List<QuestionModel> questions) {
        this.context = context;
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_card, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuestionModel questionModel = questions.get(position);
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_shared_pref", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        holder.bind(questionModel);

        holder.yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store 'Yes' response in Firestore
                String reason = holder.reasonTxt.getText().toString(); // Fetch reason text here
                DatabaseHandler db = new DatabaseHandler(v.getContext());
                db.storeAnswer(email, questionModel.getId(), questionModel.getQuestion(), "YES", reason, new DatabaseHandler.OnAnswerStoredListener() {
                    @Override
                    public void onAnswerStored() {
                        Toast.makeText(context, "Answer stored successfully", Toast.LENGTH_SHORT).show();
                        holder.reasonTxt.setText(""); // Clear the reason EditText
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "Failed to store answer", Toast.LENGTH_SHORT).show();
                        Log.e("QuizAdapter", "Error storing answer", e);
                    }
                });
            }
        });

        holder.noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store 'No' response in Firestore
                String reason = holder.reasonTxt.getText().toString(); // Fetch reason text here
                DatabaseHandler db = new DatabaseHandler(v.getContext());
                db.storeAnswer(email, questionModel.getId(), questionModel.getQuestion(), "NO", reason, new DatabaseHandler.OnAnswerStoredListener() {
                    @Override
                    public void onAnswerStored() {
                        Toast.makeText(context, "Answer stored successfully", Toast.LENGTH_SHORT).show();
                        holder.reasonTxt.setText(""); // Clear the reason EditText
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "Failed to store answer", Toast.LENGTH_SHORT).show();
                        Log.e("QuizAdapter", "Error storing answer", e);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        private TextView questionTextView;
        private Button yesBtn, noBtn;
        private EditText reasonTxt;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            yesBtn = itemView.findViewById(R.id.yesButton);
            noBtn = itemView.findViewById(R.id.noButton);
            reasonTxt = itemView.findViewById(R.id.reasonEditText);
        }

        public void bind(QuestionModel questionModel) {
            questionTextView.setText(questionModel.getQuestion());
        }
    }
}
