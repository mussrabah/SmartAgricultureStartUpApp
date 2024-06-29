package com.muss_coding.crop_recommendation;

import java.util.ArrayList;
import java.util.List;

public class QuestionStatistics {
    private String questionId;
    private String question;
    private int yesCount;
    private int noCount;
    private List<String> reasons;

    // Constructor, getters, and setters
    public QuestionStatistics(String questionId, String question) {
        this.questionId = questionId;
        this.question = question;
        this.yesCount = 0;
        this.noCount = 0;
        this.reasons = new ArrayList<>();
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getYesCount() {
        return yesCount;
    }

    public void incrementYesCount() {
        this.yesCount++;
    }

    public int getNoCount() {
        return noCount;
    }

    public void incrementNoCount() {
        this.noCount++;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void addReason(String reason) {
        this.reasons.add(reason);
    }
}
