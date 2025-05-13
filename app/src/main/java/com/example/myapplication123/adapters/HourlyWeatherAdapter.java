package com.example.myapplication123.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication123.R;
import com.example.myapplication123.models.HourlyWeather;
import com.squareup.picasso.Picasso; // 이 줄이 있는지 확인

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder> {

    private Context context;
    private List<HourlyWeather> hourlyWeatherList;

    public HourlyWeatherAdapter(Context context, List<HourlyWeather> hourlyWeatherList) {
        this.context = context;
        this.hourlyWeatherList = hourlyWeatherList;
    }

    @NonNull
    @Override
    public HourlyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hourly_weather, parent, false); // item_hourly_weather.xml 레이아웃 필요
        return new HourlyWeatherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyWeatherViewHolder holder, int position) {
        HourlyWeather hourlyWeather = hourlyWeatherList.get(position);

        // 날짜 및 시간 포맷 변경
        LocalDateTime dateTime = LocalDateTime.parse(hourlyWeather.getDateTime());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        holder.timeTextView.setText(dateTime.format(timeFormatter));

        holder.tempTextView.setText(String.format("%.1f°C", hourlyWeather.getMain().getTemp()));
        holder.descriptionTextView.setText(hourlyWeather.getWeather().get(0).getDescription());

        // 날씨 아이콘 로드 (Picasso 라이브러리 필요 - build.gradle에 추가)
        String iconCode = hourlyWeather.getWeather().get(0).getIcon();
        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
        Picasso.get().load(iconUrl).into(holder.iconImageView);
    }

    @Override
    public int getItemCount() {
        return hourlyWeatherList.size();
    }

    public static class HourlyWeatherViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView tempTextView;
        TextView descriptionTextView;
        ImageView iconImageView;

        public HourlyWeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
        }
    }
}