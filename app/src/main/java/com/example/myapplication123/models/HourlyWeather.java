package com.example.myapplication123.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HourlyWeather {

    @SerializedName("dt_txt")
    private String dateTime;
    @SerializedName("main")
    private MainInfo main;
    @SerializedName("weather")
    private List<WeatherInfo> weather;

    // Getter and Setter for dateTime
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    // Getter and Setter for main
    public MainInfo getMain() {
        return main;
    }

    public void setMain(MainInfo main) {
        this.main = main;
    }

    // Getter and Setter for weather
    public List<WeatherInfo> getWeather() {
        return weather;
    }

    public void setWeather(List<WeatherInfo> weather) {
        this.weather = weather;
    }

    public static class MainInfo {
        @SerializedName("temp")
        private Double temp;
        @SerializedName("feels_like")
        private Double feelsLike;

        // Getter and Setter for temp
        public Double getTemp() {
            return temp;
        }

        public void setTemp(Double temp) {
            this.temp = temp;
        }

        // Getter and Setter for feelsLike
        public Double getFeelsLike() {
            return feelsLike;
        }

        public void setFeelsLike(Double feelsLike) {
            this.feelsLike = feelsLike;
        }
    }

    public static class WeatherInfo {
        @SerializedName("icon")
        private String icon;
        @SerializedName("description")
        private String description;
        @SerializedName("main")
        private String main;

        // Getter and Setter for icon
        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        // Getter and Setter for description
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }
    }
}