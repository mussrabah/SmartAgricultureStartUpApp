package com.muss_coding.crop_recommendation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Report extends AppCompatActivity {

    TextView nValue, pValue, kValue, tempValue, humidityValue, phValue, rainValue, suggestedCrop, predictedMSP;
    Button askQuestionButton;

    String nitrogenReading, phosphorousReading, potassiumReading, temperature, humidity, phValueText, rainfallAmount, predictedCrop, predictedMSPTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        nValue = findViewById(R.id.nValueDisplay);
        pValue = findViewById(R.id.pValueDisplay);
        kValue = findViewById(R.id.kValueDisplay);
        tempValue = findViewById(R.id.tempValueDisplay);
        humidityValue = findViewById(R.id.humidityValueDisplay);
        phValue = findViewById(R.id.phValueDisplay);
        rainValue = findViewById(R.id.rainfallValueDisplay);
        suggestedCrop = findViewById(R.id.suggestedCropValueDisplay);
        askQuestionButton = findViewById(R.id.askQuestionButton);
        predictedMSP = findViewById(R.id.predictedMSP);

        // Retrieve values from Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Retrieve each value using the keys provided in the previous activity
            nitrogenReading = extras.getString("nitrogen_reading");
            phosphorousReading = extras.getString("phosphorous_reading");
            potassiumReading = extras.getString("potassium_reading");
            temperature = extras.getString("temperature");
            humidity = extras.getString("humidity");
            phValueText = extras.getString("ph_value");
            rainfallAmount = extras.getString("rainfall_amount");
            predictedCrop = extras.getString("predicted_crop");

            makePostRequest(predictedCrop);
        }

        // Set onClickListener for askQuestionButton
        askQuestionButton.setOnClickListener(v -> {
            Intent intent = new Intent(Report.this, AskQuestion.class);

            // Add the values as extras to the intent
            intent.putExtra("nitrogen_reading", String.valueOf(nitrogenReading));
            intent.putExtra("phosphorous_reading", String.valueOf(phosphorousReading));
            intent.putExtra("potassium_reading", String.valueOf(potassiumReading));
            intent.putExtra("temperature", String.valueOf(temperature));
            intent.putExtra("humidity", String.valueOf(humidity));
            intent.putExtra("ph_value", String.valueOf(phValueText));
            intent.putExtra("rainfall_amount", String.valueOf(rainfallAmount));
            intent.putExtra("predicted_crop", String.valueOf(predictedCrop));
            intent.putExtra("predicted_msp", String.valueOf(predictedMSPTxt));



            // Start the other activity
            startActivity(intent);
        });
    }

    private void makePostRequest(String predictedCrop) {
        RequestQueue requestQueue = Volley.newRequestQueue(Report.this);

        String url = "http://varad040104.pythonanywhere.com/predict_msp";

        // Create the JSON object to be sent in the POST request
        JSONObject jsonParams = new JSONObject();
        try {
            HashMap<String, String> manualMapping = new HashMap<>();
            manualMapping.put("rice", "Paddy - Common");
            manualMapping.put("maize", "Maize");
            manualMapping.put("chana", "Gram");
            manualMapping.put("rajma", "Masur (Lentil)");
            manualMapping.put("toordal", "Arhar (Tur)");
            manualMapping.put("matkidal", "Urad");
            manualMapping.put("moongdal", "Moong");
            manualMapping.put("uraddal", "Urad");
            manualMapping.put("masoordal", "Masur (Lentil)");
            manualMapping.put("pomegranate", "45300.0");
            manualMapping.put("banana", "16349.23");
            manualMapping.put("mango", "32104.56");
            manualMapping.put("grapes", "24987.60");
            manualMapping.put("watermelon", "8675.833");
            manualMapping.put("muskmelon", "18800.78");
            manualMapping.put("apple", "86017.82");
            manualMapping.put("orange", "49897.71");
            manualMapping.put("papaya", "22700.08");
            manualMapping.put("coconut", "Copra - Milling");
            manualMapping.put("cotton", "Cotton");
            manualMapping.put("jute", "Jute");
            manualMapping.put("coffee", "NA");

            if(isFloat(manualMapping.get(predictedCrop))) {
                nValue.setText(nitrogenReading);
                pValue.setText(phosphorousReading);
                kValue.setText(potassiumReading);
                tempValue.setText(temperature);
                humidityValue.setText(humidity);
                phValue.setText(phValueText);
                rainValue.setText(rainfallAmount);
                suggestedCrop.setText(predictedCrop);
                predictedMSP.setText(manualMapping.get(predictedCrop));

                predictedMSPTxt = manualMapping.get(predictedCrop);
            }
            else {
                jsonParams.put("crop_name", manualMapping.get(predictedCrop));
                jsonParams.put("current_year", 2024);
            }

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
                        Toast.makeText(Report.this, "Response: " + response.toString(), Toast.LENGTH_SHORT).show();

                        try {
                            String predicted_msp = response.getString("predicted_msp");

                            // Set values to TextViews
                            nValue.setText(nitrogenReading);
                            pValue.setText(phosphorousReading);
                            kValue.setText(potassiumReading);
                            tempValue.setText(temperature);
                            humidityValue.setText(humidity);
                            phValue.setText(phValueText);
                            rainValue.setText(rainfallAmount);
                            suggestedCrop.setText(predictedCrop);
                            predictedMSP.setText(predicted_msp);

                            predictedMSPTxt = predicted_msp;

                        } catch (JSONException e) {
                            Toast.makeText(Report.this, "Something wents wrong", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e("Error", error.toString());
                        Toast.makeText(Report.this, "Response: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);

    }

    public static boolean isFloat(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        try {
            Float.parseFloat(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}