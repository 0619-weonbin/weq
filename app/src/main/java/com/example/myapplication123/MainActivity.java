package com.example.myapplication123;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // Toast 임포트 확인
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication123.adapters.HourlyWeatherAdapter;
import com.example.myapplication123.adapters.DailyForecastAdapter;
import com.example.myapplication123.databinding.ActivityMainBinding;
import com.example.myapplication123.models.ForecastResponse;
import com.example.myapplication123.models.HourlyWeather;
import com.example.myapplication123.models.DailyWeather;
import com.example.myapplication123.models.WeatherData;
import com.example.myapplication123.network.WeatherApiClient;
import com.example.myapplication123.WeatherApiService; // WeatherApiService 임포트 추가
import com.example.myapplication123.utils.ConvertHourlyList;
import com.example.myapplication123.utils.ConvertDailyList;
import java.io.IOException;
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

    private RecyclerView dailyForecastRecyclerView;
    private DailyForecastAdapter dailyForecastAdapter;
    private List<DailyWeather> dailyWeatherList;

    private WeatherApiService weatherApiService;
    private String apiKey;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ActivityResultLauncher<String> locationPermissionRequest;

    private TextView hourlyForecastDateLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiKey = getString(R.string.weather_api_key);

        hourlyWeatherRecyclerView = binding.hourlyWeatherRecyclerView;
        hourlyForecastDateLabel = binding.hourlyForecastDateLabel;
        dailyForecastRecyclerView = binding.dailyForecastRecyclerView;

        Button goToClosetBtn = binding.goToClosetButton;
        goToClosetBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Closet.class);
            startActivity(intent);
            Log.d("Closet", "옷장 버튼 클릭됨");
        });

        Button btnRegionalWeather = binding.btnRegionalWeather;
        btnRegionalWeather.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), WeatherMain.class);
            startActivity(intent);
        });

        Button btnClosetAllItem = binding.btnClosetAllItem;
        btnClosetAllItem.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ClosetAllItem.class);
            startActivity(intent);
        });

        // 시간별 예보 RecyclerView 설정
        hourlyWeatherRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hourlyWeatherList = new ArrayList<>();
        hourlyWeatherAdapter = new HourlyWeatherAdapter(this, hourlyWeatherList);
        hourlyWeatherRecyclerView.setAdapter(hourlyWeatherAdapter);

        // 일별 예보 RecyclerView 설정
        dailyForecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        dailyWeatherList = new ArrayList<>();
        dailyForecastAdapter = new DailyForecastAdapter(this, dailyWeatherList);
        dailyForecastRecyclerView.setAdapter(dailyForecastAdapter);
        dailyForecastRecyclerView.setNestedScrollingEnabled(false); // 선택 사항: 스크롤 충돌 방지

        // RecyclerView 스크롤 리스너: 현재 보이는 아이템의 날짜를 Label에 표시
        hourlyWeatherRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition != RecyclerView.NO_POSITION && !hourlyWeatherList.isEmpty()) {
                        HourlyWeather currentHourlyWeather = hourlyWeatherList.get(firstVisibleItemPosition);
                        SimpleDateFormat dayFormatter = new SimpleDateFormat("M월 d일 (E)", Locale.KOREAN);
                        String dateText = dayFormatter.format(new Date(currentHourlyWeather.getDateTime() * 1000L));
                        hourlyForecastDateLabel.setText(dateText + " 시간별 날씨");
                    }
                }
            }
        });

        weatherApiService = WeatherApiClient.getApiService();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                getCurrentLocation();
            } else {
                // 권한 거부 시 기본 위치로 날씨 정보 가져오기 (구리)
                getWeatherData(37.63, 127.13, "kr"); // 구리시 위도 경도
                getForecastData(37.63, 127.13, "kr"); // 구리시 위도 경도
                binding.locationTextView.setText("구리시"); // 직접 구리시로 설정
                hourlyForecastDateLabel.setText(getCurrentKoreanDate() + " 시간별 날씨");
                Toast.makeText(this, "위치 권한이 거부되어 기본 위치(구리시)의 날씨를 표시합니다.", Toast.LENGTH_LONG).show();
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

    private String getCurrentKoreanDate() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        SimpleDateFormat dayFormatter = new SimpleDateFormat("M월 d일 (E)", Locale.KOREAN);
        return dayFormatter.format(now);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
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
                    // 한 번 위치를 받으면 업데이트를 중지하여 배터리 소모를 줄입니다.
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    Log.d("Location", "Provider disabled: " + provider);
                    // 위치 서비스가 비활성화되면 구리시 날씨로 대체
                    getWeatherData(37.63, 127.13, "kr");
                    getForecastData(37.63, 127.13, "kr");
                    binding.locationTextView.setText("구리시"); // 직접 구리시로 설정
                    hourlyForecastDateLabel.setText(getCurrentKoreanDate() + " 시간별 날씨");
                    Toast.makeText(MainActivity.this, "위치 서비스가 비활성화되어 기본 위치(구리시)의 날씨를 표시합니다.", Toast.LENGTH_LONG).show();
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
                // 단일 위치 업데이트 요청
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                // GPS가 바로 잡히지 않을 경우를 대비하여 NETWORK_PROVIDER도 고려할 수 있습니다.
                // locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
            } catch (SecurityException e) {
                Log.e("Location", "위치 권한 없음 (getCurrentLocation): " + e.getMessage());
                // 권한 예외 발생 시 구리시 날씨로 대체
                getWeatherData(37.63, 127.13, "kr");
                getForecastData(37.63, 127.13, "kr");
                binding.locationTextView.setText("구리시"); // 직접 구리시로 설정
                hourlyForecastDateLabel.setText(getCurrentKoreanDate() + " 시간별 날씨");
                Toast.makeText(this, "위치 권한 문제로 기본 위치(구리시)의 날씨를 표시합니다.", Toast.LENGTH_LONG).show();
            }
        } else {
            // 권한이 없는 상태에서 getCurrentLocation 호출 시 구리시 날씨로 대체
            getWeatherData(37.63, 127.13, "kr");
            getForecastData(37.63, 127.13, "kr");
            binding.locationTextView.setText("구리시"); // 직접 구리시로 설정
            hourlyForecastDateLabel.setText(getCurrentKoreanDate() + " 시간별 날씨");
            Toast.makeText(this, "위치 권한이 없어 기본 위치(구리시)의 날씨를 표시합니다.", Toast.LENGTH_LONG).show();
        }
    }

    // 도시 이름을 기반으로 현재 날씨 데이터를 가져오는 메서드
    private void getWeatherData(String location, String lang) {
        Call<WeatherData> call = weatherApiService.getWeather(location, apiKey, "metric", lang);
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherData currentWeather = response.body();
                    Log.d("API Response", "Weather Data: " + currentWeather.toString());
                    binding.currentTempTextView.setText(String.format("%.1f°C", currentWeather.getMain().getTemp()));
                    binding.feelsLikeTextView.setText(String.format("체감 %.1f°C", currentWeather.getMain().getFeelsLike()));
                    // 날씨 설명 변환 적용
                    binding.weatherDescriptionTextView.setText(convertWeatherDescription(currentWeather.getWeather().get(0).getDescription()));
                    String iconCode = currentWeather.getWeather().get(0).getIcon();
                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                    Glide.with(MainActivity.this)
                            .load(iconUrl)
                            .into(binding.weatherIconImageView);
                    // 이 메서드에서는 OpenWeatherMap에서 반환하는 도시 이름을 그대로 사용합니다.
                    // 위도/경도 기반 메서드에서 Geocoder를 사용합니다.
                    if (currentWeather.getName() != null) {
                        binding.locationTextView.setText(currentWeather.getName());
                    } else {
                        binding.locationTextView.setText("알 수 없는 지역"); // API가 이름을 반환하지 않을 경우
                    }
                } else {
                    Log.e("API Error", "Current Weather API 응답 실패: " + response.code());
                    Toast.makeText(MainActivity.this, "날씨 정보 로드 실패: " + response.message(), Toast.LENGTH_SHORT).show(); // LENGTH_SHORT 수정
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.e("API Error", "Current Weather API 요청 실패: " + t.getMessage());
                Toast.makeText(MainActivity.this, "날씨 정보 로드 중 오류 발생", Toast.LENGTH_SHORT).show(); // LENGTH_SHORT 수정
            }
        });
    }

    // 위도/경도를 기반으로 현재 날씨 데이터를 가져오는 메서드 (Geocoder 적용!)
    private void getWeatherData(double latitude, double longitude, String lang) {
        Call<WeatherData> call = weatherApiService.getWeather(latitude, longitude, apiKey, "metric", lang);
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherData currentWeather = response.body();
                    Log.d("API Response", "Weather Data (Lat/Lon): " + currentWeather.toString());

                    binding.currentTempTextView.setText(String.format("%.1f°C", currentWeather.getMain().getTemp()));
                    binding.feelsLikeTextView.setText(String.format("체감 %.1f°C", currentWeather.getMain().getFeelsLike()));
                    binding.weatherDescriptionTextView.setText(convertWeatherDescription(currentWeather.getWeather().get(0).getDescription()));
                    String iconCode = currentWeather.getWeather().get(0).getIcon();
                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                    Glide.with(MainActivity.this)
                            .load(iconUrl)
                            .into(binding.weatherIconImageView);

                    // ====================================================================
                    // **현재 위치를 한글로 변환하여 표기하는 Geocoder 로직**
                    // ====================================================================
                    try {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.KOREAN);
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            String cityName = null;

                            // Geocoder가 반환하는 주소 필드들을 순서대로 시도
                            if (address.getLocality() != null && !address.getLocality().isEmpty()) {
                                cityName = address.getLocality(); // 도시 이름 (예: "구리시")
                            } else if (address.getSubAdminArea() != null && !address.getSubAdminArea().isEmpty()) {
                                cityName = address.getSubAdminArea(); // 시/군/구 (예: "성남시 분당구")
                            } else if (address.getAdminArea() != null && !address.getAdminArea().isEmpty()) {
                                cityName = address.getAdminArea(); // 시/도 (예: "경기도")
                            } else if (address.getFeatureName() != null && !address.getFeatureName().isEmpty()) {
                                cityName = address.getFeatureName(); // 더 일반적인 이름
                            }

                            if (cityName != null) {
                                binding.locationTextView.setText(cityName);
                                Log.d("Location", "Geocoder로 변환된 한글 위치: " + cityName);
                            } else {
                                binding.locationTextView.setText(currentWeather.getName() != null ? currentWeather.getName() : "알 수 없는 지역");
                                Log.w("Location", "Geocoder로 한글 도시 이름을 찾을 수 없어 API 이름 사용 또는 '알 수 없는 지역'");
                            }
                        } else {
                            // Geocoder 결과가 없을 경우 API에서 받은 도시 이름 사용
                            binding.locationTextView.setText(currentWeather.getName() != null ? currentWeather.getName() : "위치 정보 없음");
                            Log.w("Location", "Geocoder 결과가 없어 API에서 받은 도시 이름 사용 또는 '위치 정보 없음'");
                        }
                    } catch (IOException e) {
                        Log.e("Location", "Geocoder 오류: " + e.getMessage());
                        // Geocoder 오류 발생 시 API에서 받은 도시 이름 사용
                        binding.locationTextView.setText(currentWeather.getName() != null ? currentWeather.getName() : "위치 변환 오류");
                        Toast.makeText(MainActivity.this, "위치 이름 변환 중 오류 발생", Toast.LENGTH_SHORT).show(); // LENGTH_SHORT 수정
                    }
                    // ====================================================================

                } else {
                    Log.e("API Error", "Current Weather API (Lat/Lon) 응답 실패: " + response.code());
                    Toast.makeText(MainActivity.this, "날씨 정보 로드 실패: " + response.message(), Toast.LENGTH_SHORT).show(); // LENGTH_SHORT 수정
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.e("API Error", "Current Weather API (Lat/Lon) 요청 실패: " + t.getMessage());
                Toast.makeText(MainActivity.this, "날씨 정보 로드 중 오류 발생", Toast.LENGTH_SHORT).show(); // LENGTH_SHORT 수정
            }
        });
    }

    // 도시 이름을 기반으로 예보 데이터를 가져오는 메서드
    private void getForecastData(String location, String lang) {
        Call<ForecastResponse> call = weatherApiService.getForecast(location, apiKey, "metric", lang);
        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ForecastResponse", "onResponse (Location) 시작");
                    ForecastResponse forecastResponse = response.body();
                    List<ForecastResponse.ForecastItem> fullForecastList = forecastResponse.getList();

                    // 시간별 예보 업데이트 (변환 적용)
                    List<HourlyWeather> hourlyWeatherListFromApi = ConvertHourlyList.convertForecastListToHourlyList(fullForecastList);
                    for (HourlyWeather hw : hourlyWeatherListFromApi) {
                        hw.setDescription(convertWeatherDescription(hw.getDescription()));
                    }
                    hourlyWeatherList.clear();
                    hourlyWeatherList.addAll(hourlyWeatherListFromApi);
                    hourlyWeatherAdapter.notifyDataSetChanged();
                    Log.d("MainActivity", "시간별 날씨 데이터 업데이트 완료 (Location)");

                    if (!hourlyWeatherListFromApi.isEmpty()) {
                        HourlyWeather firstItem = hourlyWeatherListFromApi.get(0);
                        SimpleDateFormat dayFormatter = new SimpleDateFormat("M월 d일 (E)", Locale.KOREAN);
                        String dateText = dayFormatter.format(new Date(firstItem.getDateTime() * 1000L));
                        hourlyForecastDateLabel.setText(dateText + " 시간별 날씨");
                    }

                    // 일별 예보 데이터 업데이트 (변환 적용)
                    List<DailyWeather> dailyWeatherListFromApi = ConvertDailyList.convertForecastListToDailyList(fullForecastList);
                    for (DailyWeather dw : dailyWeatherListFromApi) {
                        dw.setDescription(convertWeatherDescription(dw.getDescription()));
                    }
                    dailyWeatherList.clear();
                    dailyWeatherList.addAll(dailyWeatherListFromApi);
                    dailyForecastAdapter.notifyDataSetChanged();
                    Log.d("MainActivity", "일별 날씨 데이터 업데이트 완료 (Location)");

                } else {
                    Log.e("API Error", "Forecast API (Location) 응답 실패: " + response.code());
                    Toast.makeText(MainActivity.this, "예보 정보 로드 실패: " + response.message(), Toast.LENGTH_SHORT).show(); // LENGTH_SHORT 수정
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Log.e("API Error", "Forecast API (Location) 요청 실패: " + t.getMessage());
                Toast.makeText(MainActivity.this, "예보 정보 로드 중 오류 발생", Toast.LENGTH_SHORT).show(); // LENGTH_SHORT 수정
            }
        });
    }

    // 위도/경도를 기반으로 예보 데이터를 가져오는 메서드
    private void getForecastData(double latitude, double longitude, String lang) {
        Call<ForecastResponse> call = weatherApiService.getForecast(latitude, longitude, apiKey, "metric", lang);
        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ForecastResponse", "onResponse (Lat/Lon) 시작");
                    ForecastResponse forecastResponse = response.body();
                    List<ForecastResponse.ForecastItem> fullForecastList = forecastResponse.getList();

                    // 시간별 예보 업데이트 (변환 적용)
                    List<HourlyWeather> hourlyWeatherListFromApi = ConvertHourlyList.convertForecastListToHourlyList(fullForecastList);
                    for (HourlyWeather hw : hourlyWeatherListFromApi) {
                        hw.setDescription(convertWeatherDescription(hw.getDescription()));
                    }
                    hourlyWeatherList.clear();
                    hourlyWeatherList.addAll(hourlyWeatherListFromApi);
                    hourlyWeatherAdapter.notifyDataSetChanged();
                    Log.d("MainActivity", "시간별 날씨 데이터 업데이트 완료 (Lat/Lon)");

                    if (!hourlyWeatherListFromApi.isEmpty()) {
                        HourlyWeather firstItem = hourlyWeatherListFromApi.get(0);
                        SimpleDateFormat dayFormatter = new SimpleDateFormat("M월 d일 (E)", Locale.KOREAN);
                        String dateText = dayFormatter.format(new Date(firstItem.getDateTime() * 1000L));
                        hourlyForecastDateLabel.setText(dateText + " 시간별 날씨");
                    }

                    // 일별 예보 데이터 업데이트 (변환 적용)
                    List<DailyWeather> dailyWeatherListFromApi = ConvertDailyList.convertForecastListToDailyList(fullForecastList);
                    for (DailyWeather dw : dailyWeatherListFromApi) {
                        dw.setDescription(convertWeatherDescription(dw.getDescription()));
                    }
                    dailyWeatherList.clear();
                    dailyWeatherList.addAll(dailyWeatherListFromApi);
                    dailyForecastAdapter.notifyDataSetChanged();
                    Log.d("MainActivity", "일별 날씨 데이터 업데이트 완료 (Lat/Lon)");

                } else {
                    Log.e("API Error", "Forecast API (Lat/Lon) 응답 실패: " + response.code());
                    Toast.makeText(MainActivity.this, "예보 정보 로드 실패: " + response.message(), Toast.LENGTH_SHORT).show(); // LENGTH_SHORT 수정
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Log.e("API Error", "Forecast API (Lat/Lon) 요청 실패: " + t.getMessage());
                Toast.makeText(MainActivity.this, "예보 정보 로드 중 오류 발생", Toast.LENGTH_SHORT).show(); // LENGTH_SHORT 수정
            }
        });
    }

    // --- 날씨 설명 변환 메서드 (MainActivity 클래스 내부) ---
    private String convertWeatherDescription(String apiDescription) {
        if (apiDescription == null) {
            return "정보 없음";
        }
        switch (apiDescription) {
            case "온맑음":
                return "맑음";
            case "온흐림":
                return "흐림";
            case "튼구름":
                return "구름 많음";
            case "약간의 구름이 낀 하늘":
                return "구름 조금";
            case "흩어진 구름":
                return "구름 많음";
            case "부서진 구름":
                return "구름 많음";
            case "실 비":
                return "약한 비";
            case "보통 비":
                return "비";
            case "지나가는 비":
                return "소나기";
            case "대설":
                return "폭설";
            case "안개":
                return "안개";
            case "박무": // 미스트
                return "옅은 안개";
            case "헤이즈":
                return "안개"; // 또는 "연무"
            case "모래":
                return "황사";
            case "황진": // 더스트
                return "황사";
            // 기타 OpenWeatherMap에서 반환하는 다른 설명들도 필요에 따라 추가할 수 있습니다.
            case "clear sky":
                return "맑음";
            case "few clouds":
                return "구름 조금";
            case "scattered clouds":
                return "구름 많음";
            case "broken clouds":
                return "흐림";
            case "shower rain":
                return "소나기";
            case "rain":
                return "비";
            case "thunderstorm":
                return "천둥번개";
            case "snow":
                return "눈";
            case "mist":
                return "안개";
            case "light rain":
                return "약한 비";
            case "moderate rain":
                return "보통 비";
            case "heavy intensity rain":
                return "강한 비";
            case "light snow":
                return "약한 눈";
            case "heavy snow":
                return "폭설";
            case "sleet":
                return "진눈깨비";
            case "smoke":
                return "연기";
            case "haze":
                return "옅은 안개";
            case "sand/dust whirls":
                return "황사";
            case "fog":
                return "안개";
            case "sand":
                return "황사";
            case "dust":
                return "황사";
            case "volcanic ash":
                return "화산재";
            case "squalls":
                return "돌풍";
            case "tornado":
                return "토네이도";
            default:
                return apiDescription; // 매칭되는 것이 없으면 원래 설명 반환
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}