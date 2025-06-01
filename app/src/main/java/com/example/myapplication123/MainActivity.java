package com.example.myapplication123;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication123.adapters.HourlyWeatherAdapter;
import com.example.myapplication123.databinding.ActivityMainBinding;
import com.example.myapplication123.models.ForecastResponse;
import com.example.myapplication123.models.HourlyWeather;
import com.example.myapplication123.models.WeatherData;
import com.example.myapplication123.network.WeatherApiClient;
import com.example.myapplication123.utils.ConvertHourlyList;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RecyclerView hourlyWeatherRecyclerView;
    private HourlyWeatherAdapter hourlyWeatherAdapter;
    private List<HourlyWeather> hourlyWeatherList;
    private RecyclerView dailyForecastRecyclerView; // 일별 예보 RecyclerView
    private WeatherApiService weatherApiService;
    private String apiKey;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ActivityResultLauncher<String> locationPermissionRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiKey = getString(R.string.weather_api_key);

        hourlyWeatherRecyclerView = binding.hourlyWeatherRecyclerView;
        hourlyWeatherRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hourlyWeatherList = new ArrayList<>();
        hourlyWeatherAdapter = new HourlyWeatherAdapter(this, hourlyWeatherList);
        hourlyWeatherRecyclerView.setAdapter(hourlyWeatherAdapter);

        weatherApiService = WeatherApiClient.getApiService();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // 권한 획득 성공 시 위치 정보 가져오기
                getCurrentLocation();
            } else {
                // 권한 거부 시 기본 위치로 날씨 정보 가져오기 (현재 위치: Guri-si, Gyeonggi-do, South Korea)
                getWeatherData(37.63, 127.13, "kr");
                getForecastData(37.63, 127.13, "kr");
                binding.locationTextView.setText("구리");
            }
        });

        updateCurrentDateTime();
        checkLocationPermission();
    }

    private void updateCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.KOREAN);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.KOREAN);
        binding.dateTextView.setText(dateFormatter.format(now));
        binding.timeTextView.setText(timeFormatter.format(now));
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 권한 요청
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            // 권한이 이미 있는 경우 위치 정보 가져오기
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
                    getWeatherData(latitude, longitude, "kr");
                    getForecastData(latitude, longitude, "kr");
                    locationManager.removeUpdates(this); // 위치 업데이트 중단 (정확도 우선)
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    Log.d("Location", "Provider disabled: " + provider);
                    // GPS가 꺼져 있을 경우 기본 위치 또는 사용자에게 알림 (현재 위치: Guri-si, Gyeonggi-do, South Korea)
                    getWeatherData(37.63, 127.13, "kr");
                    getForecastData(37.63, 127.13, "kr");
                    binding.locationTextView.setText("구리");
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                    Log.d("Location", "Provider enabled: " + provider);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.d("Location", "Status changed: " + provider + " - " + status);
                }
            };
            try {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
            } catch (SecurityException e) {
                Log.e("Location", "위치 권한 없음 (getCurrentLocation): " + e.getMessage());
                // 권한이 없는 경우 기본 위치로 날씨 정보 가져오기 (현재 위치: Guri-si, Gyeonggi-do, South Korea)
                getWeatherData(37.63, 127.13, "kr");
                getForecastData(37.63, 127.13, "kr");
                binding.locationTextView.setText("구리");
            }
        } else {
            // 권한이 없는 경우 (다시 한번 확인)
            getWeatherData(37.63, 127.13, "kr");
            getForecastData(37.63, 127.13, "kr");
            binding.locationTextView.setText("구리");
        }
    }

    private void getWeatherData(String location, String lang) {
        Call<WeatherData> call = weatherApiService.getWeather(location, apiKey, "metric", lang);
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherData currentWeather = response.body();
                    Log.d("API Response", "Weather Data: " + currentWeather.toString());
                    Log.d("WeatherData", "현재 날씨: " + currentWeather);
                    binding.currentTempTextView.setText(String.format("%.1f°C", currentWeather.getMain().getTemp()));
                    binding.feelsLikeTextView.setText(String.format("체감 %.1f°C", currentWeather.getMain().getFeelsLike()));
                    binding.weatherDescriptionTextView.setText(currentWeather.getWeather().get(0).getDescription());
                    String iconCode = currentWeather.getWeather().get(0).getIcon();
                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                    Glide.with(MainActivity.this)
                            .load(iconUrl)
                            .into(binding.weatherIconImageView);
                    if (currentWeather.getName() != null) {
                        binding.locationTextView.setText(currentWeather.getName());
                    }
                } else {
                    Log.e("API Error", "Current Weather API 응답 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.e("API Error", "Current Weather API 요청 실패: " + t.getMessage());
            }
        });
    }

    private void getWeatherData(double latitude, double longitude, String lang) {
        Call<WeatherData> call = weatherApiService.getWeather(latitude, longitude, apiKey, "metric", lang);
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherData currentWeather = response.body();
                    Log.d("API Response", "Weather Data (Lat/Lon): " + currentWeather.toString());
                    Log.d("WeatherData", "현재 날씨 (Lat/Lon): " + currentWeather);
                    binding.currentTempTextView.setText(String.format("%.1f°C", currentWeather.getMain().getTemp()));
                    binding.feelsLikeTextView.setText(String.format("체감 %.1f°C", currentWeather.getMain().getFeelsLike()));
                    binding.weatherDescriptionTextView.setText(currentWeather.getWeather().get(0).getDescription());
                    String iconCode = currentWeather.getWeather().get(0).getIcon();
                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                    Glide.with(MainActivity.this)
                            .load(iconUrl)
                            .into(binding.weatherIconImageView);
                    if (currentWeather.getName() != null) {
                        binding.locationTextView.setText(currentWeather.getName());
                    }
                } else {
                    Log.e("API Error", "Current Weather API (Lat/Lon) 응답 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.e("API Error", "Current Weather API (Lat/Lon) 요청 실패: " + t.getMessage());
            }
        });
    }

    private void getForecastData(String location, String lang) {
        Call<ForecastResponse> call = weatherApiService.getForecast(location, apiKey, "metric", lang);
        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ForecastResponse", "onResponse (Location) 시작");
                    ForecastResponse forecastResponse = response.body();
                    Log.d("API Response", "Forecast Data (Location): " + forecastResponse);
                    List<HourlyWeather> hourlyWeatherListFromApi = ConvertHourlyList.convertForecastListToHourlyList(forecastResponse.getList());
                    Log.d("ConvertHourlyList", "convertForecastListToHourlyList 종료, 리스트 크기: " + hourlyWeatherListFromApi.size());
                    Log.d("ForecastResponse", "onResponse (Location) 종료");

                    hourlyWeatherList.clear();
                    hourlyWeatherList.addAll(hourlyWeatherListFromApi);
                    hourlyWeatherAdapter.notifyDataSetChanged();
                    Log.d("MainActivity", "시간별 날씨 데이터 업데이트 완료 (Location)");
                } else {
                    Log.e("API Error", "Forecast API (Location) 응답 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Log.e("API Error", "Forecast API (Location) 요청 실패: " + t.getMessage());
            }
        });
    }

    private void getForecastData(double latitude, double longitude, String lang) {
        Call<ForecastResponse> call = weatherApiService.getForecast(latitude, longitude, apiKey, "metric", lang);
        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ForecastResponse", "onResponse (Lat/Lon) 시작");
                    ForecastResponse forecastResponse = response.body();
                    Log.d("API Response", "Forecast Data (Lat/Lon): " + forecastResponse);
                    List<HourlyWeather> hourlyWeatherListFromApi = ConvertHourlyList.convertForecastListToHourlyList(forecastResponse.getList());
                    Log.d("ConvertHourlyList", "convertForecastListToHourlyList 종료, 리스트 크기: " + hourlyWeatherListFromApi.size());
                    Log.d("ForecastResponse", "onResponse (Lat/Lon) 종료");

                    hourlyWeatherList.clear();
                    hourlyWeatherList.addAll(hourlyWeatherListFromApi);
                    hourlyWeatherAdapter.notifyDataSetChanged();
                    Log.d("MainActivity", "시간별 날씨 데이터 업데이트 완료 (Lat/Lon)");
                } else {
                    Log.e("API Error", "Forecast API (Lat/Lon) 응답 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Log.e("API Error", "Forecast API (Lat/Lon) 요청 실패: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}