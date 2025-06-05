package com.example.myapplication123;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication123.models.WeatherData;
import com.example.myapplication123.network.WeatherApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity {

    private TextView txtLocation;
    private TextView txtCurrentTemp;
    private TextView txtFeelsLike;
    private TextView txtWeatherDescription;
    private ImageView imgWeatherIcon;

    private ImageView mapView;

    private WeatherApiService weatherApiService;
    private String apiKey;
    private String originalLocation; // <--- 이 변수를 추가했습니다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // UI 요소 초기화
        txtLocation = findViewById(R.id.locationTextView);
        txtCurrentTemp = findViewById(R.id.currentTempTextView);
        txtFeelsLike = findViewById(R.id.feelsLikeTextView);
        txtWeatherDescription = findViewById(R.id.weatherDescriptionTextView);
        imgWeatherIcon = findViewById(R.id.weatherIconImageView);

        mapView = findViewById(R.id.mapView);

        // WeatherMain 액티비티에서 전달받은 지역 이름
        String location = getIntent().getStringExtra("location");
        originalLocation = location; // <--- 전달받은 원래 지역명을 저장합니다.

        // API 서비스 및 키 초기화
        weatherApiService = WeatherApiClient.getApiService();
        apiKey = getString(R.string.weather_api_key);

        if (location != null && !location.isEmpty()) {
            txtLocation.setText(originalLocation); // 처음부터 originalLocation을 표시합니다.

            String apiLocation = location;
            // 한글 도시 이름을 OpenWeatherMap이 인식할 수 있는 형태로 매핑
            switch (location) {
                case "서울":
                    apiLocation = "Seoul,KR";
                    break;
                case "부산":
                    apiLocation = "Busan,KR";
                    break;
                case "대구":
                    apiLocation = "Daegu,KR";
                    break;
                case "인천":
                    apiLocation = "Incheon,KR";
                    break;
                case "대전":
                    apiLocation = "Daejeon,KR";
                    break;
                case "광주":
                    apiLocation = "Gwangju,KR";
                    break;
                case "울산":
                    apiLocation = "Ulsan,KR";
                    break;
                case "세종":
                    apiLocation = "Sejong,KR";
                    break;
                case "수원":
                    apiLocation = "Suwon,KR";
                    break;
                case "고양":
                    apiLocation = "Goyang,KR";
                    break;
                case "성남":
                    apiLocation = "Seongnam,KR";
                    break;
                case "청주":
                    apiLocation = "Cheongju,KR";
                    break;
                case "전주":
                    apiLocation = "Jeonju,KR";
                    break;
                case "춘천":
                    apiLocation = "Chuncheon,KR";
                    break;
                case "강릉":
                    apiLocation = "Gangneung,KR";
                    break;
                case "제주":
                    apiLocation = "Jeju City,KR";
                    break;
                // 기타 필요한 매핑 추가
            }

            getWeatherDataByLocation(apiLocation, "kr"); // 매핑된 apiLocation으로 API 호출
            showMapWithMarker(originalLocation); // 지도 마커는 originalLocation 사용
        } else {
            txtLocation.setText("지역 정보를 찾을 수 없습니다.");
            txtCurrentTemp.setText("");
            txtFeelsLike.setText("");
            txtWeatherDescription.setText("");
            imgWeatherIcon.setImageDrawable(null);
            Toast.makeText(this, "검색할 지역을 입력해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("WeatherActivity", "인텐트로부터 지역 정보가 전달되지 않았습니다.");
        }
    }

    private void getWeatherDataByLocation(String location, String lang) {
        Log.d("WeatherActivity", "날씨 데이터 요청: " + location + ", API Key: " + apiKey + ", Lang: " + lang);

        Call<WeatherData> call = weatherApiService.getWeather(location, apiKey, "metric", lang);
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherData currentWeather = response.body();
                    Log.d("WeatherActivity", "API 응답 성공: " + currentWeather.toString());

                    // UI 업데이트: originalLocation을 사용하여 한글로 표시
                    txtLocation.setText(originalLocation); // <--- 이 부분을 수정했습니다.
                    txtCurrentTemp.setText(String.format("%.1f°C", currentWeather.getMain().getTemp()));
                    txtFeelsLike.setText(String.format("체감 %.1f°C", currentWeather.getMain().getFeelsLike()));

                    // 날씨 설명 처리 (온맑음 -> 맑음 등)
                    String description = currentWeather.getWeather().get(0).getDescription();
                    txtWeatherDescription.setText(convertWeatherDescription(description)); // <--- 새 메서드 사용

                    // 날씨 아이콘 로드 (Glide 사용)
                    String iconCode = currentWeather.getWeather().get(0).getIcon();
                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                    Glide.with(WeatherActivity.this)
                            .load(iconUrl)
                            .into(imgWeatherIcon);

                } else {
                    Log.e("WeatherActivity", "API 응답 실패: " + response.code() + ", Message: " + response.message());
                    txtLocation.setText("검색 실패");
                    txtCurrentTemp.setText("");
                    txtFeelsLike.setText("");
                    txtWeatherDescription.setText("날씨 정보를 찾을 수 없습니다.");
                    imgWeatherIcon.setImageDrawable(null);
                    Toast.makeText(WeatherActivity.this, "해당 지역의 날씨 정보를 찾을 수 없습니다. (오류 코드: " + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.e("WeatherActivity", "API 요청 실패: " + t.getMessage(), t);
                txtLocation.setText("네트워크 오류");
                txtCurrentTemp.setText("");
                txtFeelsLike.setText("");
                txtWeatherDescription.setText("날씨 정보를 가져오는 데 실패했습니다.");
                imgWeatherIcon.setImageDrawable(null);
                Toast.makeText(WeatherActivity.this, "네트워크 오류: 날씨 정보를 가져올 수 없습니다.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- 날씨 설명 변환 메서드 추가 ---
    // --- 날씨 설명 변환 메서드 (WeatherActivity.java 내) ---
    private String convertWeatherDescription(String apiDescription) {
        // OpenWeatherMap의 한글 번역이 이상할 때 수동으로 수정하는 맵
        switch (apiDescription) {
            case "온흐림": // <--- 이 부분을 추가했습니다.
                return "흐림"; // 또는 "구름 많음", "대체로 흐림" 등 원하는 표현으로 변경
            default:
                return apiDescription; // 매핑되지 않은 설명은 원본 그대로 사용
        }
    }

    // --- 기존 지도 관련 메서드 (변경 없음) ---
    private void showMapWithMarker(String location) {
        Bitmap originalMap = BitmapFactory.decodeResource(getResources(), R.drawable.korea_map);
        if (originalMap == null) {
            Log.e("WeatherActivity", "Error: korea_map.png not found in drawable folder or cannot be decoded.");
            Toast.makeText(this, "지도 이미지를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap mutableMap = originalMap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableMap);
        Paint paint = new Paint();
        paint.setColor(android.graphics.Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        float[] coords = getMarkerCoords(location);
        canvas.drawCircle(coords[0], coords[1], 15, paint);
        mapView.setImageBitmap(mutableMap);
    }

    private float[] getMarkerCoords(String location) {
        switch (location) {
            case "서울": return new float[]{620, 380};
            case "부산": return new float[]{880, 780};
            case "대전": return new float[]{640, 560};
            case "대구": return new float[]{740, 680};
            case "광주": return new float[]{540, 720};
            case "인천": return new float[]{600, 370};
            case "춘천": return new float[]{700, 300};
            // 기타 다른 한국 도시들의 좌표를 추가할 수 있습니다.
            default:
                Log.w("WeatherActivity", "Unknown location for map marker: " + location + ". Using default coords.");
                return new float[]{650, 500};
        }
    }
}