// DailyForecastAdapter.java
package com.example.myapplication123.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Glide 임포트가 반드시 필요합니다!
import com.example.myapplication123.models.DailyWeather;
import com.example.myapplication123.R; // R 클래스 임포트 확인

import java.util.List;

public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.DailyForecastViewHolder> {

    private final Context context; // Glide를 위해 context 필요
    private List<DailyWeather> dailyWeatherList;

    public DailyForecastAdapter(Context context, List<DailyWeather> dailyWeatherList) {
        this.context = context;
        this.dailyWeatherList = dailyWeatherList;
    }

    @NonNull
    @Override
    public DailyForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // XML 레이아웃을 인플레이트하여 뷰홀더 생성
        View view = LayoutInflater.from(context).inflate(R.layout.item_daily_forecast, parent, false);
        return new DailyForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyForecastViewHolder holder, int position) {
        if (dailyWeatherList != null && position < dailyWeatherList.size()) {
            DailyWeather dailyWeather = dailyWeatherList.get(position);
            // DailyForecastViewHolder의 bind 메서드에 DailyWeather 객체와 context를 전달
            holder.bind(dailyWeather, context); // <-- 이 부분을 수정했습니다.
        }
    }

    @Override
    public int getItemCount() {
        return dailyWeatherList == null ? 0 : dailyWeatherList.size();
    }

    public static class DailyForecastViewHolder extends RecyclerView.ViewHolder {
        // 뷰 요소들을 선언
        private final TextView dateTextView;
        private final TextView minTempTextView;
        private final TextView maxTempTextView;
        private final TextView descriptionTextView;
        private final ImageView weatherIconImageView; // 날씨 아이콘 ImageView

        public DailyForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_daily_forecast.xml의 ID와 연결
            dateTextView = itemView.findViewById(R.id.dateTextView);
            minTempTextView = itemView.findViewById(R.id.lowTempTextView); // 또는 minTempTextView
            maxTempTextView = itemView.findViewById(R.id.highTempTextView); // 또는 maxTempTextView
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            weatherIconImageView = itemView.findViewById(R.id.weatherIconImageView); // <-- 이 ID를 item_daily_forecast.xml에서 확인하세요.
        }

        // DailyWeather 데이터를 뷰에 바인딩하는 메서드 (context를 인자로 받음)
        public void bind(DailyWeather dailyWeather, Context context) { // <-- 이 부분을 수정했습니다.
            // 날짜, 최저/최고 온도, 날씨 설명 텍스트 설정
            dateTextView.setText(dailyWeather.getDate());
            minTempTextView.setText(String.format("최저: %.0f°C", dailyWeather.getMinTemp()));
            maxTempTextView.setText(String.format("최고: %.0f°C", dailyWeather.getMaxTemp()));
            descriptionTextView.setText(dailyWeather.getDescription());

            // **날씨 아이콘 로드 로직**
            String iconCode = dailyWeather.getIconCode(); // DailyWeather 모델에서 아이콘 코드 가져오기

            // 아이콘 코드가 유효하면 Glide로 이미지 로드
            if (iconCode != null && !iconCode.isEmpty()) {
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png"; // OpenWeatherMap 아이콘 URL
                Glide.with(context) // context를 사용하여 Glide 초기화
                        .load(iconUrl) // 이미지 로드
                        .error(R.drawable.ic_weather_placeholder) // 로드 실패 시 표시할 이미지
                        .into(weatherIconImageView); // 이미지를 표시할 ImageView
            } else {
                // 아이콘 코드가 없거나 비어있으면 기본 플레이스홀더 이미지 표시
                weatherIconImageView.setImageResource(R.drawable.ic_weather_placeholder);
            }
        }
    }
}