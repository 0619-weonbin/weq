package com.example.myapplication123.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherData {
    @SerializedName("coord")
    private Coord coord;
    @SerializedName("weather")
    private List<Weather> weather;
    @SerializedName("base")
    private String base;
    @SerializedName("main")
    private Main main;
    @SerializedName("visibility")
    private int visibility;
    @SerializedName("wind")
    private Wind wind;
    @SerializedName("clouds")
    private Clouds clouds;
    @SerializedName("dt")
    private long dt;
    @SerializedName("sys")
    private Sys sys;
    @SerializedName("timezone")
    private int timezone;
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("cod")
    private int cod;

    public Coord getCoord() {
        return coord;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public String getName() {
        return name;
    }

    // 내부 클래스 정의
    public static class Coord {
        @SerializedName("lon")
        private double lon;
        @SerializedName("lat")
        private double lat;

        public double getLon() {
            return lon;
        }

        public double getLat() {
            return lat;
        }
    }

    public static class Weather {
        @SerializedName("id")
        private int id;
        @SerializedName("main")
        private String main;
        @SerializedName("description")
        private String description;
        @SerializedName("icon")
        private String icon;

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    public static class Main {
        @SerializedName("temp")
        private double temp;
        @SerializedName("feels_like")
        private double feelsLike;
        @SerializedName("temp_min")
        private double tempMin;
        @SerializedName("temp_max")
        private double tempMax;
        @SerializedName("pressure")
        private int pressure;
        @SerializedName("humidity")
        private int humidity;

        public double getTemp() {
            return temp;
        }

        public double getFeelsLike() {
            return feelsLike;
        }
    }

    public static class Wind {
        @SerializedName("speed")
        private double speed;
        @SerializedName("deg")
        private int deg;
    }

    public static class Clouds {
        @SerializedName("all")
        private int all;
    }

    public static class Sys {
        @SerializedName("type")
        private int type;
        @SerializedName("id")
        private int id;
        @SerializedName("country")
        private String country;
        @SerializedName("sunrise")
        private long sunrise;
        @SerializedName("sunset")
        private long sunset;
    }
}