package com.example.myapplication123.models;

// import java.util.List; // 이 모델에서는 중첩된 리스트가 필요 없으므로 주석 처리하거나 삭제합니다.

public class HourlyWeather {
    // Unix 타임스탬프 (초 단위) - ForecastResponse.ForecastItem의 'dt' 필드와 직접 매핑됩니다.
    private long dateTime;

    private double temperature;
    private String iconCode;
    private String description;
    private String formattedTime; // 예: "03:00", "06:00" (시간만 표시)

    // ForecastItem 데이터를 사용하여 HourlyWeather 객체를 쉽게 생성하기 위한 생성자
    public HourlyWeather(long dateTime, double temperature, String iconCode, String description, String formattedTime) {
        this.dateTime = dateTime;
        this.temperature = temperature;
        this.iconCode = iconCode;
        this.description = description;
        this.formattedTime = formattedTime;
    }

    // 모든 필드에 대한 Getter
    public long getDateTime() {
        return dateTime;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getIconCode() {
        return iconCode;
    }

    public String getDescription() {
        return description;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    // Setter (필요에 따라 추가할 수 있지만, 일반적으로 이 모델은 생성 시 데이터를 모두 설정합니다.)
    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setIconCode(String iconCode) {
        this.iconCode = iconCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }
}