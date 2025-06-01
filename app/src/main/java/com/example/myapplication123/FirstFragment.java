package com.example.myapplication123;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication123.adapters.HourlyWeatherAdapter;
import com.example.myapplication123.databinding.FragmentFirstBinding;
import com.example.myapplication123.models.HourlyWeather;
import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private RecyclerView hourlyWeatherRecyclerView;
    private HourlyWeatherAdapter hourlyWeatherAdapter;
    private List<HourlyWeather> hourlyWeatherList = new ArrayList<>();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hourlyWeatherRecyclerView = view.findViewById(R.id.hourlyWeatherRecyclerView);
        Log.d("FirstFragment", "hourlyWeatherRecyclerView: " + hourlyWeatherRecyclerView);

        hourlyWeatherRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        hourlyWeatherAdapter = new HourlyWeatherAdapter(getContext(), hourlyWeatherList);
        hourlyWeatherRecyclerView.setAdapter(hourlyWeatherAdapter);
        Log.d("FirstFragment", "hourlyWeatherRecyclerView adapter: " + hourlyWeatherRecyclerView.getAdapter());

        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );
    }

    public void setHourlyWeatherData(List<HourlyWeather> data) {
        Log.d("FirstFragment", "setHourlyWeatherData 호출, 데이터 크기: " + data.size());
        hourlyWeatherList.clear();
        hourlyWeatherList.addAll(data);
        if (hourlyWeatherAdapter != null) {
            hourlyWeatherAdapter.notifyDataSetChanged();
            Log.d("FirstFragment", "hourlyWeatherAdapter.notifyDataSetChanged() 호출됨");
        } else {
            Log.e("FirstFragment", "hourlyWeatherAdapter가 null입니다.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}