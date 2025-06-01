package com.example.myapplication123;

import com.example.myapplication123.models.ForecastResponse;
import com.example.myapplication123.models.WeatherData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("weather")
    Call<WeatherData> getWeather(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang // 한국어 설정 파라미터 추가
    );

    @GET("weather")
    Call<WeatherData> getWeather(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang // 한국어 설정 파라미터 추가
    );

    @GET("forecast")
    Call<ForecastResponse> getForecast(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang // 한국어 설정 파라미터 추가
    );

    @GET("forecast")
    Call<ForecastResponse> getForecast(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang // 한국어 설정 파라미터 추가
    );
}