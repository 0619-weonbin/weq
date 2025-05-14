package com.example.myapplication123.adapters;

import android.content.Context;
import android.util.Log;
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
import java.time.format.DateTimeParseException;
import java.util.List;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder> {

    private final Context context;
    private List<HourlyWeather> hourlyWeatherList;

    public HourlyWeatherAdapter(Context context, List<HourlyWeather> hourlyWeatherList) {
        this.context = context;
        this.hourlyWeatherList = hourlyWeatherList;
    }

    public void setHourlyWeatherList(List<HourlyWeather> newHourlyWeatherList) {
        if (this.hourlyWeatherList != null) {
            this.hourlyWeatherList.clear(); // 기존 리스트 내용 삭제
            this.hourlyWeatherList.addAll(newHourlyWeatherList); // 새로운 리스트 내용 추가
        } else {
            this.hourlyWeatherList = newHourlyWeatherList;
        }
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
        return hourlyWeatherList == null ? 0 : hourlyWeatherList.size();
    }

    public static class HourlyWeatherViewHolder extends RecyclerView.ViewHolder {
        private final TextView timeTextView;
        private final ImageView weatherIconImageView;
        private final TextView tempTextView;
        private final TextView descriptionTextView;

        public HourlyWeatherViewHolder(View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            weatherIconImageView = itemView.findViewById(R.id.weatherIconImageView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }

        public void bind(HourlyWeather hourlyWeather) {
            if (hourlyWeather.getDateTime() != null) {
                try {
                    String dateTimeString = hourlyWeather.getDateTime();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    timeTextView.setText(timeFormatter.format(dateTime));
                } catch (DateTimeParseException e) {
                    Log.e("DateTimeParseError", "Date time parsing error for: " + hourlyWeather.getDateTime(), e);
                    timeTextView.setText("N/A");
                }
            } else {
                timeTextView.setText("");
            }

            if (hourlyWeather.getWeather() != null && !hourlyWeather.getWeather().isEmpty()) {
                String iconCode = hourlyWeather.getWeather().get(0).getIcon();
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                Picasso.get().load(iconUrl).into(weatherIconImageView);
                if (hourlyWeather.getWeather().get(0).getDescription() != null) {
                    descriptionTextView.setText(hourlyWeather.getWeather().get(0).getDescription());
                } else {
                    descriptionTextView.setText("");
                }
            } else {
                weatherIconImageView.setImageResource(android.R.drawable.ic_menu_help);
                descriptionTextView.setText("");
            }

            if (hourlyWeather.getMain() != null) {
                tempTextView.setText(String.format("%.1f°C", hourlyWeather.getMain().getTemp()));
            } else {
                tempTextView.setText("");
            }
        }
    }
}