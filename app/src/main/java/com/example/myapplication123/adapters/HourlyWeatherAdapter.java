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
import com.squareup.picasso.Picasso;
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

    public void setHourlyWeatherList(List<HourlyWeather> hourlyWeatherList) { // Setter 메서드 추가
        this.hourlyWeatherList = hourlyWeatherList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HourlyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hourly_weather, parent, false);
        return new HourlyWeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyWeatherViewHolder holder, int position) {
        HourlyWeather hourlyWeather = hourlyWeatherList.get(position);
        holder.bind(hourlyWeather);
    }

    @Override
    public int getItemCount() {
        return hourlyWeatherList.size();
    }

    public static class HourlyWeatherViewHolder extends RecyclerView.ViewHolder {
        private TextView timeTextView;
        private ImageView weatherIconImageView;
        private TextView tempTextView;

        public HourlyWeatherViewHolder(View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            weatherIconImageView = itemView.findViewById(R.id.weatherIconImageView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
        }

        public void bind(HourlyWeather hourlyWeather) {
            // 시간 표시
            if (hourlyWeather.getDateTime() != null) {
                LocalDateTime dateTime = LocalDateTime.parse(hourlyWeather.getDateTime().replace(" ", "T"));
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                timeTextView.setText(dateTime.format(timeFormatter));
            } else {
                timeTextView.setText("");
            }


            // 날씨 아이콘 표시
            if (hourlyWeather.getWeather() != null && !hourlyWeather.getWeather().isEmpty() &&
                    hourlyWeather.getWeather().get(0).getIcon() != null) {
                String iconCode = hourlyWeather.getWeather().get(0).getIcon();
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                Picasso.get().load(iconUrl).into(weatherIconImageView);
            } else {
                weatherIconImageView.setImageResource(android.R.drawable.ic_menu_help); // 기본 아이콘 설정
            }

            // 온도 표시
            if (hourlyWeather.getMain() != null) {
                tempTextView.setText(String.format("%.1f°C", hourlyWeather.getMain().getTemp()));
            } else {
                tempTextView.setText("");
            }
        }
    }
}