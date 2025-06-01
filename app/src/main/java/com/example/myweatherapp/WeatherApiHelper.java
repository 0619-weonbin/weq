package com.example.myweatherapp;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApiHelper {

    public interface WeatherCallback {
        void onSuccess(String weatherInfo, double avgTemp);
    }

    public static void getWeatherData(int stnId, WeatherCallback callback) {
        new Thread(() -> {
            try {
                String api = "https://apihub.kma.go.kr/api/typ01/url/fct_medm_reg.php"
                        + "?tmfc=0"
                        + "&authKey=여기에_당신의_API키"
                        + "&dataType=JSON"
                        + "&dataCd=ASOS"
                        + "&dateCd=DAY"
                        + "&startDt=20250520"
                        + "&endDt=20250520"
                        + "&stnIds=" + stnId;

                HttpURLConnection conn = (HttpURLConnection) new URL(api).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);
                reader.close();

                JSONObject body = new JSONObject(result.toString())
                        .getJSONObject("response").getJSONObject("body");
                JSONArray items = body.getJSONObject("items").getJSONArray("item");
                JSONObject today = items.getJSONObject(0);

                String info = today.getString("tm") + "\n평균: " + today.getString("avgTa") +
                        "℃\n최저: " + today.getString("minTa") + "℃\n최고: " + today.getString("maxTa") + "℃";
                double temp = today.getDouble("avgTa");

                callback.onSuccess(info, temp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
