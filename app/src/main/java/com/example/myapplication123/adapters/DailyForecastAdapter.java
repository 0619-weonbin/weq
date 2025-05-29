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

    private Context context;
    private List<DailyWeather> dailyWeatherList;

    public DailyForecastAdapter(Context context, List<DailyWeather> dailyWeatherList) {
        this.context = context;
        this.dailyWeatherList = dailyWeatherList;
    }

    @NonNull
    @Override
    public DailyForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_daily_forecast, parent, false); // R.layout.your_layout_file_name 을 실제 레이아웃 파일 이름으로 변경
        return new DailyForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyForecastViewHolder holder, int position) {
        DailyWeather dailyWeather = dailyWeatherList.get(position);

        holder.dateTextView.setText(dailyWeather.getDate());
        holder.minTempTextView.setText(String.format("최저: %.1f°C", dailyWeather.getMinTemp()));
        holder.maxTempTextView.setText(String.format("최고: %.1f°C", dailyWeather.getMaxTemp()));
        holder.descriptionTextView.setText(dailyWeather.getDescription());
        // holder.weatherIconImageView.setImageResource(dailyWeather.getIconResource()); // 아이콘 설정 (DailyWeather 클래스에 getIconResource() 메서드가 필요합니다.)
    }

    @Override
    public int getItemCount() {
        return dailyWeatherList.size();
    }

    public static class DailyForecastViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView minTempTextView;
        TextView maxTempTextView;
        TextView descriptionTextView;
        ImageView weatherIconImageView;

        public DailyForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            minTempTextView = itemView.findViewById(R.id.lowTempTextView); // id 변경
            maxTempTextView = itemView.findViewById(R.id.highTempTextView); // id 변경
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            weatherIconImageView = itemView.findViewById(R.id.weatherIconImageView);
        }
    }
}
