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
        @SerializedName("feels_like") // 체감 온도 필드 추가
        private Double feels_like;

        public Double getTemp() {
            return temp;
        }

        public Double getFeels_like() {
            return feels_like;
        }
    }

    public static class WeatherInfo {
        @SerializedName("description")
        private String description;

        public String getDescription() {
            return description;
        }
    }
}