package com.example.myapplication123;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication123.adapters.HourlyWeatherAdapter;
import com.example.myapplication123.adapters.DailyForecastAdapter;
import com.example.myapplication123.models.ForecastResponse;
import com.example.myapplication123.models.HourlyWeather;
import com.example.myapplication123.models.DailyWeather;
import com.example.myapplication123.models.WeatherData;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView dateTextView;
    private TextView timeTextView;
    private TextView locationTextView;
    private TextView currentTempTextView;
    private TextView feelsLikeTextView;
    private TextView weatherDescriptionTextView;
    private ImageView weatherIconImageView;
    private RecyclerView hourlyWeatherRecyclerView;
    private HourlyWeatherAdapter hourlyWeatherAdapter;
    private List<HourlyWeather> hourlyWeatherList;
    private WeatherApiService weatherService;
    private final String apiKey = " "; // 여기에 API 키를 넣으세요
    private RecyclerView dailyForecastRecyclerView;
    private DailyForecastAdapter dailyForecastAdapter;
    private List<DailyWeather> dailyWeatherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        locationTextView = findViewById(R.id.locationTextView);
        currentTempTextView = findViewById(R.id.currentTempTextView);
        feelsLikeTextView = findViewById(R.id.feelsLikeTextView);
        weatherDescriptionTextView = findViewById(R.id.weatherDescriptionTextView);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);
        hourlyWeatherRecyclerView = findViewById(R.id.hourlyWeatherRecyclerView);
        dailyForecastRecyclerView = findViewById(R.id.dailyForecastRecyclerView);

        hourlyWeatherRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hourlyWeatherList = new ArrayList<>();
        hourlyWeatherAdapter = new HourlyWeatherAdapter(this, hourlyWeatherList);
        hourlyWeatherRecyclerView.setAdapter(hourlyWeatherAdapter);

        dailyForecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        dailyWeatherList = new ArrayList<>();
        dailyForecastAdapter = new DailyForecastAdapter(this, dailyWeatherList);
        dailyForecastRecyclerView.setAdapter(dailyForecastAdapter);

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
                    showErrorMessage("Failed to get current weather data.", response);
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                showFailureMessage("Network error: " + t.getMessage(), t);
            }
        });
    }

    private void getForecastData(String city) {
        weatherService.getForecast(city, apiKey, "metric").enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ForecastResponse forecastResponse = response.body();
                    Log.d("API Response", "Forecast Data: " + forecastResponse.toString());

                    List<HourlyWeather> fetchedHourlyWeatherList = convertForecastListToHourlyList(forecastResponse.getList());
                    hourlyWeatherList.clear();
                    hourlyWeatherList.addAll(fetchedHourlyWeatherList);
                    hourlyWeatherAdapter.setHourlyWeatherList(hourlyWeatherList);
                    hourlyWeatherAdapter.notifyDataSetChanged();

                    processDailyData(forecastResponse);
                    dailyForecastAdapter.notifyDataSetChanged();

                } else {
                    showErrorMessage("Failed to get forecast data.", response);
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                showFailureMessage("Network error: " + t.getMessage(), t);
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
            feelsLikeTextView.setText(String.format("체감온도 %.1f°C", weatherData.getMain().getFeelsLike()));
        }

        if (weatherData.getWeather() != null && !weatherData.getWeather().isEmpty()) {
            weatherDescriptionTextView.setText(weatherData.getWeather().get(0).getDescription());
            String iconCode = weatherData.getWeather().get(0).getIcon();
            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
            Picasso.get().load(iconUrl).into(weatherIconImageView);
        }
    }

    private void showErrorMessage(String message, Response<?> response) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        Log.e("API Error", "Code: " + response.code() + ", Body: " + (response.errorBody() != null ? response.errorBody().toString() : "null"));
    }

    private void showFailureMessage(String message, Throwable t) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        Log.e("Network Error", t.getMessage());
    }

    private void processDailyData(ForecastResponse forecastResponse) {
        dailyWeatherList = new ArrayList<>();
        if (forecastResponse.getList() != null && !forecastResponse.getList().isEmpty()) {
            Map<String, DailyWeatherData> dailyDataMap = new HashMap<>();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

            for (ForecastResponse.ForecastItem forecastItem : forecastResponse.getList()) {
                LocalDateTime dateTime = LocalDateTime.parse(forecastItem.getDtTxt().replace(" ", "T"));
                String date = dateTime.format(dateFormatter);

                if (!dailyDataMap.containsKey(date)) {
                    dailyDataMap.put(date, new DailyWeatherData());
                }

                DailyWeatherData dailyData = dailyDataMap.get(date);

                double temp = forecastItem.getMain().getTemp();
                if (temp > dailyData.maxTemp) {
                    dailyData.maxTemp = temp;
                }
                if (temp < dailyData.minTemp) {
                    dailyData.minTemp = temp;
                }

                if (dailyData.description == null && forecastItem.getWeather() != null && !forecastItem.getWeather().isEmpty()) {
                    dailyData.description = forecastItem.getWeather().get(0).getDescription();
                }
            }

            for (Map.Entry<String, DailyWeatherData> entry : dailyDataMap.entrySet()) {
                String date = entry.getKey();
                DailyWeatherData dailyWeatherData = entry.getValue();
                DailyWeather dailyWeather = new DailyWeather();
                dailyWeather.setDate(date);
                dailyWeather.setMaxTemp(dailyWeatherData.maxTemp);
                dailyWeather.setMinTemp(dailyWeatherData.minTemp);
                dailyWeather.setDescription(dailyWeatherData.description);
                dailyWeatherList.add(dailyWeather);
            }
        }
    }

    private List<HourlyWeather> convertForecastListToHourlyList(List<ForecastResponse.ForecastItem> forecastList) {
        List<HourlyWeather> hourlyList = new ArrayList<>();
        if (forecastList != null) {
            for (ForecastResponse.ForecastItem forecastItem : forecastList) {
                HourlyWeather hourlyWeather = new HourlyWeather();
                hourlyWeather.setDateTime(forecastItem.getDtTxt());

                HourlyWeather.Main main =  new HourlyWeather.Main();
                main.setTemp(forecastItem.getMain().getTemp());
                main.setFeelsLike(forecastItem.getMain().getFeelsLike());
                hourlyWeather.setMain(main);

                List<HourlyWeather.Weather> weatherList = new ArrayList<>();
                if (forecastItem.getWeather() != null) {
                    for (ForecastResponse.Weather weatherItem : forecastItem.getWeather()) {
                        HourlyWeather.Weather weather =  new HourlyWeather.Weather();
                        weather.setIcon(weatherItem.getIcon());
                        weather.setDescription(weatherItem.getDescription());
                        weather.setMain(weatherItem.getMain());
                        weatherList.add(weather);
                    }
                }
                hourlyWeather.setWeather(weatherList);

                hourlyList.add(hourlyWeather);
            }
        }
        return hourlyList;
    }

    private static class DailyWeatherData {
        double maxTemp = Double.NEGATIVE_INFINITY;
        double minTemp = Double.POSITIVE_INFINITY;
        String description;
    }
}

