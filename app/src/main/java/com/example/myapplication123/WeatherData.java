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
        @SerializedName("feels_like") // API 응답과 일치하도록 변경
        private Double feels_like;

        public Double getTemp() {
            return temp;
        }

        public Double getFeels_like() { // getter 메서드 이름 변경
            return feels_like;
        }
    }

    public static class WeatherInfo {
        @SerializedName("description")
        private String description;
        @SerializedName("icon") // 아이콘 코드 필드 유지
        private String icon;

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }
}