package com.muss_coding.crop_recommendation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class UserRegistration extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button registerButton;

    private static final String SHARED_PREF_NAME = "user_shared_pref";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        // Initialize EditText fields
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // Initialize register button
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect input from EditText fields
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate input
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(UserRegistration.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed with registration
                    // You can add your registration logic here
                    DatabaseHandler db = new DatabaseHandler(UserRegistration.this);

                    Map<String, Object> userMp = new HashMap<>();
                    userMp.put("points", 0);
                    userMp.put("user_name", username);
                    userMp.put("email", email);
                    userMp.put("password", password);

                    // Add data to Firestore
                    db.addUserToFirestore(email, userMp, new DatabaseHandler.FirestoreCallback() {
                        @Override
                        public void onCallback(boolean isSuccess) {
                            if (isSuccess) {
                                Toast.makeText(UserRegistration.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                // Handle  success (e.g., navigate to another activity)
                                saveUserData(username, email, password);

                                Intent intent = new Intent(UserRegistration.this, Dashboard.class);
                                startActivity(intent);
                                
                            } else {
                                Toast.makeText(UserRegistration.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                // Handle failure (e.g., display an error message)

                            }
                        }
                    });
                }
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
