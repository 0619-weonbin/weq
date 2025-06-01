package com.example.myweatherapp;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WeatherActivity extends AppCompatActivity {

    private TextView txtWeather;
    private ImageView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        txtWeather = findViewById(R.id.txtWeather);
        mapView = findViewById(R.id.mapView);

        String location = getIntent().getStringExtra("location");
        int stnId = getStnIdByLocation(location);

        if (stnId == -1) {
            txtWeather.setText("잘못된 지역명입니다.");
            return;
        }

        WeatherApiHelper.getWeatherData(stnId, (weatherInfo, avgTemp) -> runOnUiThread(() -> {
            txtWeather.setText(location + " 날씨 정보:\n" + weatherInfo);
            showMapWithMarker(location);
        }));
    }

    private void showMapWithMarker(String location) {
        Bitmap originalMap = BitmapFactory.decodeResource(getResources(), R.drawable.korea_map);
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
            default: return new float[]{650, 500}; // 기본 위치
        }
    }

    private int getStnIdByLocation(String location) {
        switch (location) {
            case "서울": return 108;
            case "부산": return 159;
            case "대전": return 133;
            case "대구": return 143;
            case "광주": return 156;
            case "울산": return 152;
            case "인천": return 112;
            case "수원": return 119;
            case "춘천": return 101;
            case "강릉": return 105;
            case "청주": return 131;
            case "전주": return 146;
            case "제주": return 184;
            default: return -1;
        }
    }
}
