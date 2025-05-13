package com.example.myapplication123.models; // models 패키지 생성 후 이동

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HourlyWeather {
    @SerializedName("dt_txt")
    private String dateTime;

    @SerializedName("main")
    private MainInfo main;

    @SerializedName("weather")
    private java.util.List<WeatherInfo> weather;

    public String getDateTime() {
        return dateTime;
    }

    public MainInfo getMain() {
        return main;
    }

    public List<WeatherInfo> getWeather() {
        return weather;
    }

    public static class MainInfo {
        @SerializedName("temp")
        private Double temp;

        public Double getTemp() {
            return temp;
        }
    }

    public static class WeatherInfo {
        @SerializedName("icon")
        private String icon;

        @SerializedName("description")
        private String description;

        public String getIcon() {
            return icon;
        }

        public String getDescription() {
            return description;
        }
    }
}