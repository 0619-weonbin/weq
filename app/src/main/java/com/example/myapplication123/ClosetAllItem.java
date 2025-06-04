package com.example.myapplication123;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ClosetAllItem extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClothesAdapter adapter;
    private MyDatabaseHelper dbHelper;
    private List<ClothesItem> allItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet_all_item);
        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3열 그리드

        dbHelper = new MyDatabaseHelper(this);
        allItems = dbHelper.getAllClothesSortedByPreference();
        //Log.d("ThirdActivity",allItems.get(0).getImageUrl());

        adapter = new ClothesAdapter(allItems);
        recyclerView.setAdapter(adapter);

        // 버튼 클릭 리스너 설정
        findViewById(R.id.button1).setOnClickListener(v -> filterByType("상의"));
        findViewById(R.id.button2).setOnClickListener(v -> filterByType("하의"));
        findViewById(R.id.button3).setOnClickListener(v -> filterByType("아우터"));
    }

    private void filterByType(String type) {
        List<ClothesItem> filtered = new ArrayList<>();
        for (ClothesItem item : allItems) {
            if (item.getType().equals(type)) {
                filtered.add(item);
            }
        }
        adapter.updateList(filtered);
    }
}