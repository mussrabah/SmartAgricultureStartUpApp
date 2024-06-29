package com.muss_coding.crop_recommendation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button myButton;

    EditText nTxt, pTxt, kTxt, tempTxt, humidityTxt, phTxt, rainTxt;

    private void makeGetRequest(int n, int p, int k, float temp, float humidity, float ph, float rainfall) {
        // Instantiate the RequestQueue.
//        String url = "https://pranavsangave.pythonanywhere.com/getPrediction/1/2/3/10.5/10.5/10.5/10.5";
        String url = "https://pranavsangave.pythonanywhere.com/getPrediction/"+n+"/"+p+"/"+k+"/"+temp+"/"+humidity+"/"+ph+"/"+rainfall;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response
                        try {
                            String predictedCrop = response.getString("Predicted crop:");
                            Log.d("my-response", "Predicted crop: " + predictedCrop);
                            Toast.makeText(MainActivity.this, "Predicted Crop:" + predictedCrop, Toast.LENGTH_LONG).show();
                            // Here you can update your UI or perform any other actions with the predictedCrop
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.d("my-error", error.toString());
                        Toast.makeText(MainActivity.this, "Error:" + error.toString(), Toast.LENGTH_LONG).show();

                    }
                });

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nTxt = findViewById(R.id.nValue);
        pTxt = findViewById(R.id.pValue);
        kTxt = findViewById(R.id.kValue);
        tempTxt = findViewById(R.id.tempValue);
        humidityTxt = findViewById(R.id.humidityValue);
        phTxt = findViewById(R.id.phValue);
        rainTxt = findViewById(R.id.rainfallValue);

        myButton = findViewById(R.id.fetchData);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int nValue = Integer.parseInt(nTxt.getText().toString().trim());
                int pValue = Integer.parseInt(pTxt.getText().toString().trim());
                int kValue = Integer.parseInt(kTxt.getText().toString().trim());
                float tempValue = Float.parseFloat(tempTxt.getText().toString().trim());
                float humidValue = Float.parseFloat(humidityTxt.getText().toString().trim());
                float phValue = Float.parseFloat(phTxt.getText().toString().trim());
                float rainValue = Float.parseFloat(rainTxt.getText().toString().trim());


                makeGetRequest(nValue,pValue,kValue,tempValue,humidValue,phValue,rainValue);
            }
        });

    }
}