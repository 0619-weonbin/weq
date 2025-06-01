package com.example.myapplication123.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CurrentWeatherResponse {
    @SerializedName("coord")
    private Coord coord;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("base")
    private String base;

    @SerializedName("main")
    private Main main;

    @SerializedName("visibility")
    private Integer visibility;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("dt")
    private Long dt;

    @SerializedName("sys")
    private Sys sys;

    @SerializedName("timezone")
    private Integer timezone;

    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("cod")
    private Integer cod;

    public Coord getCoord() {
        return coord;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public String getBase() {
        return base;
    }

    public Main getMain() {
        return main;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public Long getDt() {
        return dt;
    }

    public Sys getSys() {
        return sys;
    }

    public Integer getTimezone() {
        return timezone;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCod() {
        return cod;
    }

    public static class Coord {
        @SerializedName("lon")
        private Double lon;

        @SerializedName("lat")
        private Double lat;

        public Double getLon() {
            return lon;
        }

        public Double getLat() {
            return lat;
        }
    }

    public static class Weather {
        @SerializedName("id")
        private Integer id;

        @SerializedName("main")
        private String main;

        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        public Integer getId() {
            return id;
        }

        public String getMain() {
            return main;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    public static class Main {
        @SerializedName("temp")
        private Double temp;

        @SerializedName("feels_like")
        private Double feels_like;

        @SerializedName("temp_min")
        private Double temp_min;

        @SerializedName("temp_max")
        private Double temp_max;

        @SerializedName("pressure")
        private Integer pressure;

        @SerializedName("humidity")
        private Integer humidity;

        public Double getTemp() {
            return temp;
        }

        public Double getFeels_like() {
            return feels_like;
        }

        public Double getTemp_min() {
            return temp_min;
        }

        public Double getTemp_max() {
            return temp_max;
        }

        public Integer getPressure() {
            return pressure;
        }

        public Integer getHumidity() {
            return humidity;
        }
    }

    public static class Wind {
        @SerializedName("speed")
        private Double speed;

        @SerializedName("deg")
        private Integer deg;

        public Double getSpeed() {
            return speed;
        }

        public Integer getDeg() {
            return deg;
        }
    }

    public static class Clouds {
        @SerializedName("all")
        private Integer all;

        public Integer getAll() {
            return all;
        }
    }

    public static class Sys {
        @SerializedName("type")
        private Integer type;

        @SerializedName("id")
        private Integer id;

        @SerializedName("country")
        private String country;

        @SerializedName("sunrise")
        private Integer sunrise;

        @SerializedName("sunset")
        private Integer sunset;

        public Integer getType() {
            return type;
        }

        public Integer getId() {
            return id;
        }

        public String getCountry() {
            return country;
        }

        public Integer getSunrise() {
            return sunrise;
        }

        public Integer getSunset() {
            return sunset;
        }
    }
}