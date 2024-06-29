package com.muss_coding.crop_recommendation;

public class QuestionModel {
    String id;
    String question;
    String user_id;

    QuestionModel(String id, String question, String user_id) {
        this.id = id;
        this.question = question;
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
