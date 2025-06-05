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
import com.bumptech.glide.Glide;
import com.example.myapplication123.R;
import com.example.myapplication123.models.HourlyWeather; // 단순화된 모델을 임포트합니다.
// java.time.LocalDateTime; // 더 이상 bind 메서드에서 직접 파싱에 사용되지 않습니다.
// java.time.format.DateTimeFormatter; // 더 이상 bind 메서드에서 직접 파싱에 사용되지 않습니다.
import java.util.List;
import java.util.Locale;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder> {

    private Context context;
    private List<HourlyWeather> hourlyWeatherList;

    public HourlyWeatherAdapter(Context context, List<HourlyWeather> hourlyWeatherList) {
        this.context = context;
        this.hourlyWeatherList = hourlyWeatherList;
    }

    public void setHourlyWeatherList(List<HourlyWeather> hourlyWeatherList) {
        this.hourlyWeatherList = hourlyWeatherList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hourly_weather, parent, false);
        Log.d("HourlyAdapter", "onCreateViewHolder 호출");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("HourlyAdapter", "onBindViewHolder 호출 - position: " + position);
        if (hourlyWeatherList != null && position < hourlyWeatherList.size()) {
            HourlyWeather hourlyWeather = hourlyWeatherList.get(position);
            holder.bind(hourlyWeather, context); // bind 메서드로 Context를 전달합니다.
        } else {
            Log.e("HourlyAdapter", "onBindViewHolder - 리스트가 null이거나 position이 유효하지 않습니다.");
        }
    }

    @Override
    public int getItemCount() {
        int count = hourlyWeatherList == null ? 0 : hourlyWeatherList.size();
        Log.d("HourlyAdapter", "getItemCount 호출 - count: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        ImageView iconImageView;
        TextView tempTextView;
        TextView descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            iconImageView = itemView.findViewById(R.id.weatherIconImageView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }

        // Context를 인자로 받는 bind 메서드로 변경하고, 단순화된 HourlyWeather 모델 사용
        public void bind(HourlyWeather hourlyWeather, Context context) {
            if (hourlyWeather != null) {
                // 시간 표시: formattedTime 필드를 직접 사용
                timeTextView.setText(hourlyWeather.getFormattedTime());

                // 날씨 아이콘 표시: iconCode 필드를 직접 사용
                String iconCode = hourlyWeather.getIconCode();
                if (iconCode != null && !iconCode.isEmpty()) {
                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                    Glide.with(context).load(iconUrl).into(iconImageView); // 전달받은 Context 사용
                } else {
                    iconImageView.setImageResource(android.R.drawable.ic_menu_help); // 기본 아이콘 설정
                }

                // 온도 표시: temperature 필드를 직접 사용
                tempTextView.setText(String.format(Locale.getDefault(), "%.1f°C", hourlyWeather.getTemperature()));

                // 날씨 설명 표시: description 필드를 직접 사용
                descriptionTextView.setText(hourlyWeather.getDescription());

            } else {
                // 데이터가 null일 경우 기본값 설정
                timeTextView.setText("--:--");
                iconImageView.setImageResource(android.R.drawable.ic_menu_help);
                tempTextView.setText("--°C");
                descriptionTextView.setText("정보 없음");
            }
        }
    }
}