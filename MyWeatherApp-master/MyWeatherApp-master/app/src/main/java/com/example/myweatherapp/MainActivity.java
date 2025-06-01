package com.example.myweatherapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editLocation;
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editLocation = findViewById(R.id.editLocation);
        txtResult = findViewById(R.id.txtResult);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {
            String location = editLocation.getText().toString();
            int stnId = getStnIdByLocation(location);

            if (stnId == -1) {
                txtResult.setText("잘못된 지역명입니다.");
                return;
            }

            WeatherApiHelper.getWeatherData(stnId, (weatherInfo, avgTemp) -> runOnUiThread(() -> {
                txtResult.setText(weatherInfo);
            }));
        });
    }

    private int getStnIdByLocation(String location) {
        switch (location.trim()) {
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
