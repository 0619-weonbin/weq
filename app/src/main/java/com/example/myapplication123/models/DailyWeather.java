// DailyWeather.java (새로 생성하거나 com.example.myapplication123.models 패키지에 생성)
package com.example.myapplication123.models;

public class DailyWeather {
    private String date;
    private double minTemp;
    private double maxTemp;
    private String description;

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public double getMinTemp() {
        return minTemp;
    }
    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }
    public double getMaxTemp() {
        return maxTemp;
    }
    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DailyWeather{" +
                "date='" + date + '\'' +
                ", minTemp=" + minTemp +
                ", maxTemp=" + maxTemp +
                ", description='" + description + '\'' +
                '}';
    }
}

