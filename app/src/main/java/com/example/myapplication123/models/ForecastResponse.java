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
    private List<ForecastItem> list;
    @SerializedName("city")
    private City city;

    public String getCod() {
        return cod;
    }

    public int getMessage() {
        return message;
    }

    public int getCnt() {
        return cnt;
    }

    public List<ForecastItem> getList() {
        return list;
    }

    public City getCity() {
        return city;
    }

    public static class ForecastItem {
        @SerializedName("dt")
        private long dt;
        @SerializedName("main")
        private Main main;
        @SerializedName("weather")
        private List<Weather> weather;
        @SerializedName("clouds")
        private Clouds clouds; // Clouds 클래스 정의 필요
        @SerializedName("wind")
        private Wind wind;     // Wind 클래스 정의 필요
        @SerializedName("visibility")
        private int visibility;
        @SerializedName("pop")
        private double pop;
        @SerializedName("sys")
        private Sys sys;       // Sys 클래스 정의 필요
        @SerializedName("dt_txt")
        private String dtTxt;

        public long getDt() {
            return dt;
        }

        public Main getMain() {
            return main;
        }

        public List<Weather> getWeather() {
            return weather;
        }

        public Clouds getClouds() {
            return clouds;
        }

        public Wind getWind() {
            return wind;
        }

        public int getVisibility() {
            return visibility;
        }

        public double getPop() {
            return pop;
        }

        public Sys getSys() {
            return sys;
        }

        public String getDtTxt() {
            return dtTxt;
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
        @SerializedName("sea_level")
        private int seaLevel;
        @SerializedName("grnd_level")
        private int grndLevel;
        @SerializedName("humidity")
        private int humidity;
        @SerializedName("temp_kf")
        private double tempKf;

        // Getter methods (필요하다면)
        public double getTemp() {
            return temp;
        }

        public double getFeelsLike() {
            return feelsLike;
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

        // Getter methods (필요하다면)
        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    // 추가된 클래스 정의
    public static class Clouds {
        @SerializedName("all")
        private int all;

        public int getAll() {
            return all;
        }
    }

    public static class Wind {
        @SerializedName("speed")
        private double speed;
        @SerializedName("deg")
        private int deg;
        @SerializedName("gust")
        private double gust;

        public double getSpeed() {
            return speed;
        }

        public int getDeg() {
            return deg;
        }

        public double getGust() {
            return gust;
        }
    }

    public static class Sys {
        @SerializedName("pod")
        private String pod;

        public String getPod() {
            return pod;
        }
    }

    public static class City {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("coord")
        private Coord coord;
        @SerializedName("country")
        private String country;
        @SerializedName("population")
        private int population;
        @SerializedName("timezone")
        private int timezone;
        @SerializedName("sunrise")
        private long sunrise;
        @SerializedName("sunset")
        private long sunset;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Coord getCoord() {
            return coord;
        }

        public String getCountry() {
            return country;
        }

        public int getPopulation() {
            return population;
        }

        public int getTimezone() {
            return timezone;
        }

        public long getSunrise() {
            return sunrise;
        }

        public long getSunset() {
            return sunset;
        }
    }

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
}