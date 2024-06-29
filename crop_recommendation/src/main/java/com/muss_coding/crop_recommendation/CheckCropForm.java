package com.muss_coding.crop_recommendation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckCropForm extends AppCompatActivity {
    EditText nValue, pValue, kValue, tempValue, humidityValue, phValue, rainfallValue;
    Button checkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_crop_form);

        // Initialize EditText fields
        nValue = findViewById(R.id.nValue);
        pValue = findViewById(R.id.pValue);
        kValue = findViewById(R.id.kValue);
        tempValue = findViewById(R.id.tempValue);
        humidityValue = findViewById(R.id.humidityValue);
        phValue = findViewById(R.id.phValue);
        rainfallValue = findViewById(R.id.rainfallValue);

        // Initialize Button
        checkButton = findViewById(R.id.btn_sign_up);

        // Set OnClickListener for the button
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve values from EditText fields
                int nitrogenReading = Integer.parseInt(nValue.getText().toString());
                int phosphorousReading = Integer.parseInt(pValue.getText().toString());
                int potassiumReading = Integer.parseInt(kValue.getText().toString());
                float temperature = Float.parseFloat(tempValue.getText().toString());
                float humidity = Float.parseFloat(humidityValue.getText().toString());
                float phValueText = Float.parseFloat(phValue.getText().toString());
                float rainfallAmount = Float.parseFloat(rainfallValue.getText().toString());

                makePostRequest(nitrogenReading, phosphorousReading, potassiumReading, temperature, humidity, phValueText, rainfallAmount);
            }
        });
    }

    private void makePostRequest(int n, int p, int k, float temp, float humidity, float ph, float rainfall) {
        RequestQueue requestQueue = Volley.newRequestQueue(CheckCropForm.this);

        String url = "http://varad040104.pythonanywhere.com/crop_recommendation";

        // Create the JSON object to be sent in the POST request
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("N", n);
            jsonParams.put("P", p);
            jsonParams.put("K", k);
            jsonParams.put("temperature", temp);
            jsonParams.put("humidity", humidity);
            jsonParams.put("ph", ph);
            jsonParams.put("rainfall", rainfall);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        Log.d("Response", response.toString());
                        Toast.makeText(CheckCropForm.this, "Response: " + response.toString(), Toast.LENGTH_SHORT).show();

                        try {
                            String predictedCrop = response.getString("predicted_crop");
                            Log.d("my-response", "Predicted crop: " + predictedCrop);

                            // Create an Intent to start the other activity
                            Intent intent = new Intent(CheckCropForm.this, Report.class);

                            // Add the values as extras to the intent
                            intent.putExtra("nitrogen_reading", ""+n);
                            intent.putExtra("phosphorous_reading", ""+p);
                            intent.putExtra("potassium_reading", ""+k);
                            intent.putExtra("temperature", ""+temp);
                            intent.putExtra("humidity", ""+humidity);
                            intent.putExtra("ph_value", ""+ph);
                            intent.putExtra("rainfall_amount", ""+rainfall);
                            intent.putExtra("predicted_crop", ""+predictedCrop);

                            // Start the other activity
                            startActivity(intent);


                        } catch (JSONException e) {
                            Toast.makeText(CheckCropForm.this, "Something wents wrong", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e("Error", error.toString());
                        Toast.makeText(CheckCropForm.this, "Response: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);

    }

}