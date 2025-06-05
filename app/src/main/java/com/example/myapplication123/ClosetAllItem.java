package com.example.myapplication123;

import android.os.Bundle;
import android.util.Log; // Log 임포트 추가

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ClosetAllItem extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClothesAdapter adapter;
    private MyDatabaseHelper dbHelper;
    // ★★★ ClothesItem 대신 CloTag 리스트로 타입 변경 ★★★
    private List<CloTag> allItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet_all_item);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3열 그리드

        dbHelper = new MyDatabaseHelper(this);
        // ★★★ 메서드 이름 변경: getAllClothesSortedByPreference() -> getAllClothesForCloset() ★★★
        allItems = dbHelper.getAllClothesForCloset();
        Log.d("ClosetAllItem", "로드된 모든 옷 개수: " + allItems.size()); // 로드된 아이템 개수 로그
        if (!allItems.isEmpty()) {
            Log.d("ClosetAllItem", "첫 번째 옷 이미지 URL: " + allItems.get(0).CloUrl); // CloTag의 CloUrl 사용
        }

        // ★★★ ClothesAdapter가 CloTag 리스트를 받도록 수정해야 함 (이전에 수정되었다고 가정) ★★★
        adapter = new ClothesAdapter(allItems);
        recyclerView.setAdapter(adapter);

        // 버튼 클릭 리스너 설정
        findViewById(R.id.button1).setOnClickListener(v -> filterByType("상의"));
        findViewById(R.id.button2).setOnClickListener(v -> filterByType("하의"));
        findViewById(R.id.button3).setOnClickListener(v -> filterByType("아우터"));
    }

    private void filterByType(String type) {
        List<CloTag> filtered = new ArrayList<>(); // ★★★ ClothesItem 대신 CloTag 리스트 사용 ★★★
        for (CloTag item : allItems) { // ★★★ ClothesItem 대신 CloTag 사용 ★★★
            // CloTag에 getType() 메서드가 있어야 합니다. (이전에 추가했다고 가정)
            if (item.getType().equals(type)) {
                filtered.add(item);
            }
        }
        adapter.updateList(filtered); // ClothesAdapter의 updateList도 CloTag 리스트를 받도록 수정되었다고 가정
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 액티비티가 다시 활성화될 때마다 데이터를 새로 로드하여 갱신
        Log.d("ClosetAllItem", "onResume 호출됨. 데이터 새로고침.");
        allItems = dbHelper.getAllClothesForCloset(); // 데이터 새로 로드
        adapter.updateList(allItems); // 어댑터 업데이트
    }
}