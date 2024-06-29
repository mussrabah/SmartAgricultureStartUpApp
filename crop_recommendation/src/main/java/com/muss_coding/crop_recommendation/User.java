package com.muss_coding.crop_recommendation;

public class User {
    private String email;
    private String password;
    private int points;
    private String user_name;

    public User() {
        // Public no-arg constructor needed for Firestore deserialization
    }

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public String getUserName() { return user_name; }
    public void setUserName(String user_name) { this.user_name = user_name; }
}
