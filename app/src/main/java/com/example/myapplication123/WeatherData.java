package com.example.myapplication123;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherData {
    @SerializedName("main")
    private MainInfo main;

    @SerializedName("weather")
    private List<WeatherInfo> weather;

    public MainInfo getMain() {
        return main;
    }

    public List<WeatherInfo> getWeather() {
        return weather;
    }

    public static class MainInfo {
        @SerializedName("temp")
        private Double temp;
        @SerializedName("feels_like") // 체감 온도 필드 (API 응답과 일치)
        private Double feelsLike;

        public Double getTemp() {
            return temp;
        }

        public Double getFeelsLike() {
            return feelsLike;
        }
    }

    public static class WeatherInfo {
        @SerializedName("description")
        private String description;
        @SerializedName("icon") // 아이콘 코드 필드 추가
        private String icon;

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }
}