package com.muss_coding.crop_recommendation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    TextView signUpBtn;
    EditText email, password;
    Button loginBtn;

    private static final String SHARED_PREF_NAME = "user_shared_pref";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpBtn = findViewById(R.id.sign_up_btn);
        email = findViewById(R.id.email_id);
        password = findViewById(R.id.user_pass);
        loginBtn = findViewById(R.id.loginBtn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, UserRegistration.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = email.getText().toString();
                String passwordStr = password.getText().toString();

                if (emailStr.isEmpty() || passwordStr.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(emailStr, passwordStr);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        DatabaseHandler dbHandler = new DatabaseHandler(LoginActivity.this);
        dbHandler.getUserByEmailAndPassword(email, password, new DatabaseHandler.OnUserRetrievedListener() {
            @Override
            public void onUserRetrieved(User user) {

                saveUserData(user.getUserName(), email, password);

                Toast.makeText(LoginActivity.this, "User: "+user.getUserName(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                // User not found or error occurred
                Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", "Error: " + e.getMessage(), e);
            }
        });
    }

    private void saveUserData(String username, String email, String password) {
        // Get shared preferences editor
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store user data in shared preferences
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);

        // Apply changes
        editor.apply();
    }
}
