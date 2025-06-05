package java.com.example.myapplication123.utils;

import com.example.myapplication123.models.ForecastResponse;
import com.example.myapplication123.models.HourlyWeather;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ConvertHourlyList {
    public static List<HourlyWeather> convertForecastListToHourlyList(List<ForecastResponse.ForecastItem> forecastList) {
        List<HourlyWeather> hourlyList = new ArrayList<>();
        if (forecastList != null) {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");

            for (ForecastResponse.ForecastItem forecastItem : forecastList) {
                HourlyWeather hourlyWeather = new HourlyWeather();
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(forecastItem.getDtTxt().replace(" ", "T"), inputFormatter);
                    hourlyWeather.setDateTime(dateTime.toString()); // ISO 8601 형식으로 저장
                } catch (Exception e) {
                    hourlyWeather.setDateTime(forecastItem.getDtTxt()); // 파싱 오류 시 원본 데이터 유지
                }

                ForecastResponse.Main mainInfoResponse = forecastItem.getMain();
                if (mainInfoResponse != null) {
                    HourlyWeather.MainInfo mainInfo = new HourlyWeather.MainInfo();
                    mainInfo.setTemp(mainInfoResponse.getTemp());
                    mainInfo.setFeelsLike(mainInfoResponse.getFeelsLike());
                    hourlyWeather.setMain(mainInfo);
                }

                List<HourlyWeather.WeatherInfo> weatherInfoList = new ArrayList<>();
                List<ForecastResponse.Weather> weatherListResponse = forecastItem.getWeather();
                if (weatherListResponse != null) {
                    for (ForecastResponse.Weather weatherItem : weatherListResponse) {
                        HourlyWeather.WeatherInfo weatherInfo = new HourlyWeather.WeatherInfo();
                        weatherInfo.setIcon(weatherItem.getIcon());
                        weatherInfo.setDescription(weatherItem.getDescription());
                        weatherInfoList.add(weatherInfo);
                    }
                }
                hourlyWeather.setWeather(weatherInfoList);
                hourlyList.add(hourlyWeather);
            }
        }
        return hourlyList;
    }
}