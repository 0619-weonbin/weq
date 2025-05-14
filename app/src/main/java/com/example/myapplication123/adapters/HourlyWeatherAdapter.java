package com.example.myapplication123.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log; // Log 추가
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
import java.time.format.DateTimeParseException; // DateTimeParseException 추가
import java.util.List;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder> {

    private final List<HourlyWeather> hourlyWeatherList;

    public HourlyWeatherAdapter(Context context, List<HourlyWeather> hourlyWeatherList) {
        this.hourlyWeatherList = hourlyWeatherList;
    }

    @NonNull
    @Override
    public HourlyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hourly_weather, parent, false);
        return new HourlyWeatherViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull HourlyWeatherViewHolder holder, int position) {
        HourlyWeather hourlyWeather = hourlyWeatherList.get(position);

        try {
            // 1.  API 응답 형식에 맞춰 DateTimeFormatter를 수정.  현재 코드는 "yyyy-MM-dd HH:mm:ss" 로 파싱합니다.
            //     만약 실제 API 응답이 다른 형식이면, 이 부분을 수정해야 합니다.
            //     예: "yyyy-MM-dd'T'HH:mm:ss"  또는 "yyyy-MM-dd HH:mm:ss" 등.
            String dateTimeString = hourlyWeather.getDateTime(); // Get the datetime string
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // **THIS LINE IS CRITICAL**
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

            // 시간 표시 형식 지정
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            holder.timeTextView.setText(timeFormatter.format(dateTime));

            holder.tempTextView.setText(String.format("%.1f°C", hourlyWeather.getMain().getTemp()));
            holder.descriptionTextView.setText(hourlyWeather.getWeather().get(0).getDescription());

            // 날씨 아이콘 로드 (Picasso 라이브러리 필요 - build.gradle에 추가)
            String iconCode = hourlyWeather.getWeather().get(0).getIcon();
            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
            Picasso.get().load(iconUrl).into(holder.iconImageView);

        } catch (DateTimeParseException e) {
            // 2. 예외 처리: 날짜 파싱 오류가 발생하면 로그를 남기고, UI에 대체 값을 표시합니다.
            Log.e("DateTimeParseError", "Date time parsing error for: " + hourlyWeather.getDateTime(), e);
            holder.timeTextView.setText("N/A"); // or "" or some default
            holder.tempTextView.setText("N/A");
            holder.descriptionTextView.setText("N/A");
            //holder.iconImageView.setImageResource(R.drawable.ic_error_outline); // You might have an error icon.
        }
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
