package com.example.myapplication123.models;

public class DailyWeather {
    private String date;
    private double minTemp;
    private double maxTemp;
    private String description;
    private String iconCode; // 새로 추가된 아이콘 코드 필드

    // 전체 필드를 포함하는 생성자 (새로 추가하거나 기존 생성자 수정)
    public DailyWeather(String date, double minTemp, double maxTemp, String description, String iconCode) {
        this.date = date;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.description = description;
        this.iconCode = iconCode; // iconCode 초기화
    }

    // 기본 생성자는 필요에 따라 유지하거나 삭제할 수 있습니다.
    // 만약 `DailyWeather` 객체를 항상 모든 정보로 생성한다면, 이 기본 생성자는 필요 없습니다.
    public DailyWeather() {
    }

    // Getters
    public String getDate() {
        return date;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public String getDescription() {
        return description;
    }

    public String getIconCode() { // iconCode를 위한 Getter 추가
        return iconCode;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIconCode(String iconCode) { // iconCode를 위한 Setter 추가
        this.iconCode = iconCode;
    }

    @Override
    public String toString() {
        return "DailyWeather{" +
                "date='" + date + '\'' +
                ", minTemp=" + minTemp +
                ", maxTemp=" + maxTemp +
                ", description='" + description + '\'' +
                ", iconCode='" + iconCode + '\'' + // toString()에도 추가
                '}';
    }
}