package java.com.example.myapplication123.adapters;

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
import com.example.myapplication123.models.HourlyWeather;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            holder.bind(hourlyWeather);
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

        public void bind(HourlyWeather hourlyWeather) {
            if (hourlyWeather != null) {
                // 시간 표시
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(hourlyWeather.getDateTime().replace(" ", "T"));
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
                    timeTextView.setText(dateTime.format(timeFormatter));
                } catch (Exception e) {
                    Log.e("HourlyAdapter", "날짜/시간 파싱 오류: " + e.getMessage());
                    timeTextView.setText("--:--");
                }

                // 날씨 아이콘 표시
                if (hourlyWeather.getWeather() != null && !hourlyWeather.getWeather().isEmpty()) {
                    String iconUrl = "https://openweathermap.org/img/wn/" + hourlyWeather.getWeather().get(0).getIcon() + "@2x.png";
                    Glide.with(itemView.getContext()).load(iconUrl).into(iconImageView); // Glide 사용
                } else {
                    iconImageView.setImageResource(android.R.drawable.ic_menu_help); // 기본 아이콘
                }

                // 온도 표시
                if (hourlyWeather.getMain() != null) {
                    tempTextView.setText(String.format(Locale.getDefault(), "%.1f°C", hourlyWeather.getMain().getTemp()));
                } else {
                    tempTextView.setText("--°C");
                }

                // 날씨 설명 표시
                if (hourlyWeather.getWeather() != null && !hourlyWeather.getWeather().isEmpty()) {
                    descriptionTextView.setText(hourlyWeather.getWeather().get(0).getDescription());
                } else {
                    descriptionTextView.setText("날씨 정보 없음");
                }
            }
        }
    }
}