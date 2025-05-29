package com.example.myweatherapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WeatherActivity extends AppCompatActivity {

    TextView txtWeather, txtClothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        txtWeather = findViewById(R.id.txtWeather);
        txtClothing = findViewById(R.id.txtClothing);

        String location = getIntent().getStringExtra("location");
        int stnId = getStationId(location);

        WeatherApiHelper.getWeatherData(stnId, new WeatherApiHelper.WeatherCallback() {
            @Override
            public void onSuccess(String weatherInfo, double avgTemp) {
                runOnUiThread(() -> {
                    txtWeather.setText(weatherInfo);
//                    txtClothing.setText(ClothingRecommender.recommend(avgTemp));
                });
            }
        });
    }

    private int getStationId(String location) {
        switch (location) {
            case "서울": return 108;
            case "부산": return 159;
            case "대전": return 133;
            case "광주": return 156;
            case "강릉": return 105;
            default: return 108; // 기본은 서울
        }
    }
}
