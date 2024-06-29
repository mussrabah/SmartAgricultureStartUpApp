package com.muss_coding.crop_recommendation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<PollAdapter.PollViewHolder> {
    private List<QuestionStatistics> questionStatisticsList;
    private Context context;

    public PollAdapter(Context context, List<QuestionStatistics> questionStatisticsList) {
        this.context = context;
        this.questionStatisticsList = questionStatisticsList;
    }

    @NonNull
    @Override
    public PollViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_poll_card, parent, false);
        return new PollViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PollViewHolder holder, int position) {
        QuestionStatistics stats = questionStatisticsList.get(position);
        holder.bind(stats);
    }

    @Override
    public int getItemCount() {
        return questionStatisticsList.size();
    }

    static class PollViewHolder extends RecyclerView.ViewHolder {
        private TextView questionTextView;
        private ProgressBar yesProgressBar, noProgressBar;
        private TextView yesVotesTextView, noVotesTextView;
        private TextView reasonEditText;

        public PollViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            yesProgressBar = itemView.findViewById(R.id.yesProgressBar);
            noProgressBar = itemView.findViewById(R.id.noProgressBar);
            yesVotesTextView = itemView.findViewById(R.id.yesVotesTextView);
            noVotesTextView = itemView.findViewById(R.id.noVotesTextView);
            reasonEditText = itemView.findViewById(R.id.reasonEditText);
        }

        public void bind(QuestionStatistics stats) {
            questionTextView.setText(stats.getQuestion());

            int totalVotes = stats.getYesCount() + stats.getNoCount();
            if (totalVotes > 0) {
                int yesPercentage = (stats.getYesCount() * 100) / totalVotes;
                int noPercentage = (stats.getNoCount() * 100) / totalVotes;
                yesProgressBar.setProgress(yesPercentage);
                noProgressBar.setProgress(noPercentage);
                yesVotesTextView.setText(stats.getYesCount() + " out of " + totalVotes);
                noVotesTextView.setText(stats.getNoCount() + " out of " + totalVotes);
            } else {
                yesProgressBar.setProgress(0);
                noProgressBar.setProgress(0);
                yesVotesTextView.setText("0 out of 0");
                noVotesTextView.setText("0 out of 0");
            }

        }
    }
}
