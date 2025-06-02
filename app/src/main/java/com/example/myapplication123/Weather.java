package com.example.myapplication123;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Weather extends AsyncTask<String, Void, Double> {

    public interface WeatherCallback {
        void onWeatherFetched(double temp);
    }

    private WeatherCallback callback;

    public Weather(WeatherCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Double doInBackground(String... params) {
        String apiUrl = params[0];
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            JSONObject jsonObject = new JSONObject(result.toString());
            JSONObject main = jsonObject.getJSONObject("main");
            return main.getDouble("temp");  // 현재 온도 (섭씨)

        } catch (Exception e) {
            Log.e("WeatherTask", "Error fetching weather", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Double temp) {
        if (callback != null && temp != null) {
            callback.onWeatherFetched(temp);
        }
    }

}

