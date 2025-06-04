package com.example.myapplication123;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication123.adapters.HourlyWeatherAdapter;
import com.example.myapplication123.models.HourlyWeather;
import com.example.myapplication123.models.ForecastResponse; // ForecastData -> ForecastResponse 로 변경
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView dateTextView;
    private TextView timeTextView;
    private TextView locationTextView;
    private TextView currentTempTextView;
    private TextView feelsLikeTextView;
    private TextView weatherDescriptionTextView;
    private HourlyWeatherAdapter hourlyWeatherAdapter;
    private List<HourlyWeather> hourlyWeatherList;
    private WeatherApiService weatherService;
    private final String apiKey = "5cdcf11828c78b830592b9a252c231c7"; // 여기에 실제 API 키를 넣으세요

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnClosetAllItem = (Button) findViewById(R.id.btnClosetAllItem);
        btnClosetAllItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ClosetAllItem.class);
                startActivity(intent);
            }
        });

        RecyclerView hourlyWeatherRecyclerView = findViewById(R.id.hourlyWeatherRecyclerView);
        hourlyWeatherRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hourlyWeatherList = new ArrayList<>();
        hourlyWeatherAdapter = new HourlyWeatherAdapter(this, hourlyWeatherList);
        hourlyWeatherRecyclerView.setAdapter(hourlyWeatherAdapter);

        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        locationTextView = findViewById(R.id.locationTextView);
        currentTempTextView = findViewById(R.id.currentTempTextView);
        feelsLikeTextView = findViewById(R.id.feelsLikeTextView);
        weatherDescriptionTextView = findViewById(R.id.weatherDescriptionTextView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherService = retrofit.create(WeatherApiService.class);

        getWeatherData();
        getForecastData("Seoul");
    }

    private void getWeatherData() {
        weatherService.getWeather("Seoul", apiKey, "metric").enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherData weatherData = response.body();
                    updateUI(weatherData);
                } else {
                    Toast.makeText(MainActivity.this, "현재 날씨 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("API Error", "Code: " + response.code() + ", Body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Toast.makeText(MainActivity.this, "네트워크 오류가 발생했습니다: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Network Error", t.getMessage());
            }
        });
    }

    private void getForecastData(String city) {
        weatherService.getForecast(city, apiKey, "metric").enqueue(new Callback<ForecastResponse>() { // ForecastData -> ForecastResponse 로 변경
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) { // ForecastData -> ForecastResponse 로 변경
                if (response.isSuccessful() && response.body() != null) {
                    ForecastResponse forecastResponse = response.body(); // ForecastData -> ForecastResponse 로 변경
                    Log.d("API Response", "Forecast Data: " + response.body().toString());  // 이 줄을 추가했습니다.
                    hourlyWeatherList.clear();
                    hourlyWeatherList.addAll(forecastResponse.getList()); // forecastData -> forecastResponse 로 변경
                    hourlyWeatherAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "시간별 예보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("API Error", "Code: " + response.code() + ", Body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Network Error", t.getMessage());
            }
        });
    }

    private void updateUI(WeatherData weatherData) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        dateTextView.setText(now.format(dateFormatter));
        timeTextView.setText(now.format(timeFormatter));
        locationTextView.setText("서울");

        if (weatherData.getMain() != null) {
            currentTempTextView.setText(String.format("%.1f°C", weatherData.getMain().getTemp()));
            feelsLikeTextView.setText(String.format("체감온도 %.1f°C", weatherData.getMain().getFeels_like()));
        }

        if (weatherData.getWeather() != null && !weatherData.getWeather().isEmpty()) {
            weatherDescriptionTextView.setText(weatherData.getWeather().get(0).getDescription());
        }
    }
}

