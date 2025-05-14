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
import com.example.myapplication123.models.DailyWeather;
import com.example.myapplication123.R;
import java.util.List;

public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.DailyForecastViewHolder> {

    private final Context context;
    private List<DailyWeather> dailyWeatherList;

    public DailyForecastAdapter(Context context, List<DailyWeather> dailyWeatherList) {
        this.context = context;
        this.dailyWeatherList = dailyWeatherList;
    }

    @NonNull
    @Override
    public DailyForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_daily_forecast, parent, false);
        return new DailyForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyForecastViewHolder holder, int position) {
        if (dailyWeatherList != null && position < dailyWeatherList.size()) {
            DailyWeather dailyWeather = dailyWeatherList.get(position);
            holder.bind(dailyWeather);
        }
    }

    @Override
    public int getItemCount() {
        return dailyWeatherList == null ? 0 : dailyWeatherList.size();
    }

    public static class DailyForecastViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTextView;
        private final TextView minTempTextView;
        private final TextView maxTempTextView;
        private final TextView descriptionTextView;
        private final ImageView weatherIconImageView;

        public DailyForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            minTempTextView = itemView.findViewById(R.id.lowTempTextView);
            maxTempTextView = itemView.findViewById(R.id.highTempTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            weatherIconImageView = itemView.findViewById(R.id.weatherIconImageView);
        }

        public void bind(DailyWeather dailyWeather) {
            dateTextView.setText(dailyWeather.getDate());
            minTempTextView.setText(String.format("최저: %.1f°C", dailyWeather.getMinTemp()));
            maxTempTextView.setText(String.format("최고: %.1f°C", dailyWeather.getMaxTemp()));
            descriptionTextView.setText(dailyWeather.getDescription());
            // 아이콘 설정은 아직 DailyWeather에 관련 정보가 없어 보류합니다.
            // 필요하다면 DailyWeather 클래스에 아이콘 관련 필드와 로직을 추가해야 합니다.
            // 예: Picasso.get().load(dailyWeather.getIconUrl()).into(weatherIconImageView);
        }
    }
}