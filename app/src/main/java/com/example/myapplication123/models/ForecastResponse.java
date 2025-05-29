package com.example.myapplication123.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastResponse {
    @SerializedName("cod")
    private String cod;
    @SerializedName("message")
    private int message;
    @SerializedName("cnt")
    private int cnt;
    @SerializedName("list")
    private List<ForecastItem> list; // 이 부분을 확인
    @SerializedName("city")
    //private City city;

    // Getter 및 Setter 메서드

    public List<ForecastItem> getList() { // getList() 메서드 추가 또는 확인
        return list;
    }

    // 내부 클래스 ForecastItem, Main, Weather (이전 답변 참고)
    public static class ForecastItem {
        @SerializedName("dt_txt")
        private String dtTxt;
        private Main main;
        private List<Weather> weather;

        // Getter 및 Setter 메서드
        public String getDtTxt() { return dtTxt; }
        public Main getMain() { return main; }
        public List<Weather> getWeather() { return weather; }
    }

    public static class Main {
        private double temp;
        @SerializedName("feels_like")
        private double feelsLike;
        // 다른 필드

        // Getter 및 Setter 메서드
        public double getTemp() { return temp; }
        public double getFeelsLike() { return feelsLike; }
    }

    public static class Weather {
        private String main;
        private String description;
        private String icon;

        // Getter 및 Setter 메서드
        public String getMain() { return main; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
    }

    // City 및 Coord 클래스 (필요한 경우)
}