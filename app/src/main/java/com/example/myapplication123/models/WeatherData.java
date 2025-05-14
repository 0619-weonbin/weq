package com.example.myapplication123.models;

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
        @SerializedName("feels_like")
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
        @SerializedName("icon")  // 아이콘 코드 필드 추가
        private String icon;

        public String getDescription() {
            return description;
        }

        public String getIcon() { // 아이콘 코드 가져오는 메서드 추가
            return icon;
        }
    }
}
