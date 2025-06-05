package com.example.myapplication123.utils;

import com.example.myapplication123.models.ForecastResponse;
import com.example.myapplication123.models.HourlyWeather;
import java.util.ArrayList;
import java.util.Date; // Date 클래스 임포트
import java.util.List;
import java.text.SimpleDateFormat; // SimpleDateFormat 임포트
import java.util.Locale; // Locale 임포트

public class ConvertHourlyList {
    public static List<HourlyWeather> convertForecastListToHourlyList(List<ForecastResponse.ForecastItem> forecastList) {
        List<HourlyWeather> hourlyList = new ArrayList<>();
        if (forecastList != null) {
            // 시간 포맷을 위한 SimpleDateFormat (예: "03:00")
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

            for (ForecastResponse.ForecastItem forecastItem : forecastList) {
                // 1. Unix 타임스탬프 (초 단위) 가져오기
                long dateTime = forecastItem.getDt();

                // 2. 온도 가져오기
                double temperature = 0.0;
                if (forecastItem.getMain() != null) {
                    temperature = forecastItem.getMain().getTemp();
                }

                // 3. 아이콘 코드와 설명 가져오기
                String iconCode = "";
                String description = "";
                if (forecastItem.getWeather() != null && !forecastItem.getWeather().isEmpty()) {
                    iconCode = forecastItem.getWeather().get(0).getIcon();
                    description = forecastItem.getWeather().get(0).getDescription();
                }

                // 4. 표시용 시간 문자열 포맷팅 (Unix 타임스탬프를 밀리초로 변환)
                String formattedTime = timeFormatter.format(new Date(dateTime * 1000L));

                // 5. 새로운 HourlyWeather 객체 생성 및 리스트에 추가
                // HourlyWeather의 생성자가 (long dateTime, double temperature, String iconCode, String description, String formattedTime) 형식임을 기억하세요.
                hourlyList.add(new HourlyWeather(dateTime, temperature, iconCode, description, formattedTime));
            }
        }
        return hourlyList;
    }
}