// ConvertDailyList.java
package com.example.myapplication123.utils;

import com.example.myapplication123.models.DailyWeather;
import com.example.myapplication123.models.ForecastResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap; // 날짜 순서 유지를 위해 TreeMap 사용

public class ConvertDailyList {

    public static List<DailyWeather> convertForecastListToDailyList(List<ForecastResponse.ForecastItem> fullForecastList) {
        List<DailyWeather> dailyWeatherList = new ArrayList<>();
        if (fullForecastList == null || fullForecastList.isEmpty()) {
            return dailyWeatherList;
        }

        // 날짜별 데이터를 그룹화할 맵 (내부적인 키: yyyy-MM-dd -> 해당 날짜의 ForecastItem 리스트)
        // TreeMap을 사용하여 날짜를 기준으로 정렬되도록 합니다.
        Map<String, List<ForecastResponse.ForecastItem>> dailyGroupedItems = new TreeMap<>();

        // 그룹화할 날짜 키 포맷 (예: "2025-06-07")
        SimpleDateFormat dayKeyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        // 사용자에게 보여줄 날짜 포맷 (예: "6월 7일 (토)")
        SimpleDateFormat displayDayFormat = new SimpleDateFormat("M월 d일 (E)", Locale.KOREAN);

        // API에서 받은 전체 예보 아이템들을 날짜별로 그룹화합니다.
        for (ForecastResponse.ForecastItem item : fullForecastList) {
            Date itemDate = new Date(item.getDt() * 1000L); // UNIX 타임스탬프(초)를 밀리초로 변환
            String dayKey = dayKeyFormat.format(itemDate); // 해당 아이템의 날짜를 "yyyy-MM-dd" 형식으로

            // 해당 날짜의 리스트가 없으면 새로 생성하여 맵에 추가
            if (!dailyGroupedItems.containsKey(dayKey)) {
                dailyGroupedItems.put(dayKey, new ArrayList<>());
            }
            // 해당 날짜의 리스트에 현재 예보 아이템 추가
            dailyGroupedItems.get(dayKey).add(item);
        }

        // 그룹화된 날짜별 데이터를 기반으로 DailyWeather 객체를 생성합니다.
        for (Map.Entry<String, List<ForecastResponse.ForecastItem>> entry : dailyGroupedItems.entrySet()) {
            List<ForecastResponse.ForecastItem> dayItems = entry.getValue(); // 특정 날짜에 해당하는 모든 3시간 예보 아이템
            if (dayItems.isEmpty()) continue; // 비어있는 날짜는 건너뜁니다.

            double minTemp = Double.MAX_VALUE; // 해당 날짜의 최저 온도를 찾기 위한 초기값
            double maxTemp = Double.MIN_VALUE; // 해당 날짜의 최고 온도를 찾기 위한 초기값

            // 그 날의 가장 빈번한 날씨 설명과 아이콘을 찾기 위한 맵
            Map<String, Integer> weatherDescriptionCounts = new HashMap<>();
            Map<String, Integer> weatherIconCounts = new HashMap<>();

            // 해당 날짜의 첫 번째 아이템의 날짜를 가져와 표시용 날짜 문자열을 만듭니다.
            // TreeMap을 사용했기 때문에 dayItems 리스트의 첫 번째 아이템은 해당 날짜의 가장 이른 시간입니다.
            String displayDate = displayDayFormat.format(new Date(dayItems.get(0).getDt() * 1000L));

            // 해당 날짜의 모든 예보 아이템을 순회하며 최저/최고 온도, 날씨 설명/아이콘 통계를 수집합니다.
            for (ForecastResponse.ForecastItem item : dayItems) {
                if (item.getMain() != null) {
                    // 현재 아이템의 'temp' 값을 사용하여 해당 날짜의 최저/최고 온도를 계산
                    double currentTemp = item.getMain().getTemp(); // getTempMin/Max 대신 getTemp 사용
                    minTemp = Math.min(minTemp, currentTemp);
                    maxTemp = Math.max(maxTemp, currentTemp);
                }

                // 날씨 설명과 아이콘 코드의 등장 횟수를 카운트합니다.
                if (item.getWeather() != null && !item.getWeather().isEmpty()) {
                    String description = item.getWeather().get(0).getDescription();
                    String icon = item.getWeather().get(0).getIcon();

                    weatherDescriptionCounts.put(description, weatherDescriptionCounts.getOrDefault(description, 0) + 1);
                    weatherIconCounts.put(icon, weatherIconCounts.getOrDefault(icon, 0) + 1);
                }
            }

            // 가장 빈번하게 등장한 날씨 설명과 아이콘 코드를 찾습니다.
            String dominantDescription = "정보 없음";
            int maxDescriptionCount = 0;
            for (Map.Entry<String, Integer> descEntry : weatherDescriptionCounts.entrySet()) {
                if (descEntry.getValue() > maxDescriptionCount) {
                    maxDescriptionCount = descEntry.getValue();
                    dominantDescription = descEntry.getKey();
                }
            }

            String dominantIconCode = "";
            int maxIconCount = 0;
            for (Map.Entry<String, Integer> iconEntry : weatherIconCounts.entrySet()) {
                if (iconEntry.getValue() > maxIconCount) {
                    maxIconCount = iconEntry.getValue();
                    dominantIconCode = iconEntry.getKey();
                }
            }
            // 참고: OpenWeatherMap 아이콘은 낮('d')과 밤('n')을 구분합니다.
            // 일별 예보에서는 보통 낮 시간대의 아이콘을 대표 아이콘으로 사용하는 경우가 많습니다.
            // 현재 로직은 가장 빈번한 아이콘을 선택합니다. 필요에 따라 특정 시간(예: 정오)의 아이콘을 선택하거나,
            // 'd'가 붙은 아이콘을 선호하도록 로직을 추가할 수도 있습니다.

            // DailyWeather 객체를 생성하고 일별 날씨 리스트에 추가합니다.
            dailyWeatherList.add(new DailyWeather(displayDate, minTemp, maxTemp, dominantDescription, dominantIconCode));
        }

        return dailyWeatherList;
    }
}