package com.example.myapplication123;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class Closet extends AppCompatActivity {
    private Spinner styleSpinner;
    private LinearLayout clo1,clo2,clo3;
    private List<CloTag> allClo = new ArrayList<>();
    private String presTemp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet);
        // ★★★ 이 로그를 추가했습니다 ★★★
        Log.d("ClosetLifecycle", "onCreate 호출됨");

        styleSpinner = findViewById(R.id.styleSpinner);

        clo1 = findViewById(R.id.clo1);
        clo2 = findViewById(R.id.clo2);
        clo3 = findViewById(R.id.clo3);

        // 스타일 설정 버튼 클릭 리스너 설정
        Button goToStyleButton = findViewById(R.id.goToStyleButton);
        goToStyleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Closet.this, Style.class);
                startActivity(intent);
                Log.d("ClosetLifecycle", "스타일 버튼 클릭됨. Style 액티비티 시작.");
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.style_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        styleSpinner.setAdapter(adapter);

        // loadFromDB()는 onResume()에서 호출될 것이므로 여기서 직접 호출하지 않습니다.

        String apiKey = "8c16883d64dbbb8dcce77fe26d0da8dc";
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=Seoul&units=metric&appid=" + apiKey;


        styleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStyle = parent.getItemAtPosition(position).toString();
                Log.d("ClosetSpinner", "스피너 아이템 선택됨: " + selectedStyle);

                if (presTemp != null) {
                    if (selectedStyle.equals("스타일 선택")) {
                        showClo(presTemp, null);
                    } else {
                        showClo(presTemp, selectedStyle);
                    }
                } else {
                    Log.w("ClosetSpinner", "presTemp가 아직 초기화되지 않았습니다. 날씨 API 응답 대기 중.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("ClosetSpinner", "스피너 아무것도 선택 안됨.");
                if (presTemp != null){
                    showClo(presTemp,null);
                }

            }
        });
        new Weather(temp -> {
            presTemp = geTemp(temp);
            Log.d("ClosetWeather", "현재 온도 태그 로드 완료: " + presTemp);
            // 날씨 정보 로드 후, DB에서 옷을 불러와 현재 날씨/스피너 선택에 맞춰 표시
            loadAndDisplayClothing();
        }).execute(apiUrl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ClosetLifecycle", "onResume 호출됨. 옷 데이터 로드 시작.");
        // 액티비티가 다시 활성화될 때마다(예: 옷 추가 화면에서 돌아왔을 때) 데이터를 새로 로드하여 UI 갱신
        if (presTemp != null) {
            loadAndDisplayClothing();
        } else {
            Log.w("ClosetLifecycle", "onResume: presTemp가 아직 null입니다. 날씨 API 대기 중.");
        }
    }

    // DB에서 옷 데이터를 로드하고, 스피너 선택 및 현재 온도에 맞춰 표시하는 통합 메서드
    private void loadAndDisplayClothing() {
        loadFromDB(); // DB에서 allClo 리스트에 모든 옷 로드
        String currentSelectedStyle = styleSpinner.getSelectedItem().toString();

        if (presTemp != null) {
            if (currentSelectedStyle.equals("스타일 선택")) {
                showClo(presTemp, null);
            } else {
                showClo(presTemp, currentSelectedStyle);
            }
        } else {
            Log.w("ClosetDisplay", "presTemp가 null이어서 옷을 표시할 수 없습니다. 날씨 데이터 필요.");
        }
    }

    // MyDatabaseHelper의 getAllClothesForCloset()을 사용하도록 수정합니다.
    private void loadFromDB(){
        MyDatabaseHelper dbHelper = null;
        allClo.clear();

        Log.d("ClosetDB", "DB에서 옷 데이터 로딩 시작.");
        try {
            dbHelper = new MyDatabaseHelper(this);
            List<CloTag> loadedItems = dbHelper.getAllClothesForCloset();

            if (loadedItems != null && !loadedItems.isEmpty()) {
                allClo.addAll(loadedItems);
                Log.d("ClosetDB", "DB에서 총 " + allClo.size() + "개의 CloTag 로드 완료.");
            } else {
                Log.d("ClosetDB", "DB에 'clothes' 테이블이 비어있거나 데이터가 없습니다.");
            }
        } catch (Exception e) {
            Log.e("ClosetDB", "DB 로딩 중 오류 발생: " + e.getMessage());
            Toast.makeText(this, "옷 데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }

    private  String cloTemp(String detailedType){
        if(detailedType == null) return "";
        if(detailedType.equals("패딩")){
            return "vcold";
        } else if (detailedType.equals("기모상의") || detailedType.equals("기모바지")
                || detailedType.equals("두꺼운 긴팔")) {
            return "vcold,cold";
        } else if (detailedType.equals("두꺼운 아우터")) {
            return "cold";
        } else if (detailedType.equals("긴바지")) {
            return "vcold,cold,warm";
        } else if (detailedType.equals("가벼운 아우터") || detailedType.equals("가벼운 긴팔")) {
            return "warm";
        } else if (detailedType.equals("원피스")) {
            return "warm,hot";
        } else if (detailedType.equals("나시")) {
            return "vhot";
        }else {return "hot,vhot";}
    }

    private String geTemp(double presTemp){
        if(presTemp <= 4) {return"vcold";}
        else if(presTemp <= 13){return "cold";}
        else if(presTemp <= 22){return "warm";}
        else if(presTemp <= 28){return "hot";}
        else {return "vhot";}

    }
    private void showClo(String TmpTag,String StyTag) {
        Log.d("ClosetDisplay", "showClo 호출됨. 필터링 기준: 현재 온도 태그=" + TmpTag + ", 선택 스타일 태그=" + (StyTag == null ? "없음" : StyTag));

        clo1.removeAllViews();
        clo2.removeAllViews();
        clo3.removeAllViews();

        List<CloTag> filter = new ArrayList<>();

        for(CloTag a : allClo){
            String[] temps = a.TmpTag.split(",");
            boolean eqtemp = false;
            for (String temp : temps) {
                if (temp.trim().equals(TmpTag)){
                    eqtemp = true;
                    break;
                }
            }
            if (!eqtemp) {
                Log.d("ClosetFilter", "제외됨 (온도 불일치): " + a.NameTag + " (옷 온도: " + a.TmpTag + ", 현재 온도: " + TmpTag + ")");
                continue;
            }

            if (StyTag != null && !StyTag.equals("스타일 선택") && !a.StyTag.equals(StyTag)) {
                Log.d("ClosetFilter", "제외됨 (스타일 불일치): " + a.NameTag + " (옷 스타일: " + a.StyTag + ", 선택 스타일: " + StyTag + ")");
                continue;
            }

            filter.add(a);
            Log.d("ClosetFilter", "필터링 통과 및 추가됨: " + a.NameTag + " (온도: " + a.TmpTag + ", 스타일: " + a.StyTag + ")");
        }

        Log.d("ClosetDisplay", "최종 필터링된 옷 개수: " + filter.size());

        if (filter.isEmpty()) {
            Toast.makeText(this, "현재 조건에 맞는 옷이 없습니다.", Toast.LENGTH_SHORT).show();
        }

        for (CloTag a : filter){
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    400,400
            );
            params.setMargins(16,16,16,16);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Log.d("ClosetGlide", "Glide로 이미지 로드 시도: " + a.CloUrl);
            Glide.with(this)
                    .load(a.CloUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    // ★★★ 이 부분을 수정했습니다. ★★★
                    .error(android.R.drawable.ic_dialog_alert) // 대체 아이콘 사용
                    .into(imageView);

            switch (a.TypeTag){
                case "아우터":
                    clo1.addView(imageView);
                    Log.d("ClosetDisplay", "아우터에 이미지 추가됨: " + a.CloUrl);
                    break;
                case "상의":
                    clo2.addView(imageView);
                    Log.d("ClosetDisplay", "상의에 이미지 추가됨: " + a.CloUrl);
                    break;
                case "하의":
                    clo3.addView(imageView);
                    Log.d("ClosetDisplay", "하의에 이미지 추가됨: " + a.CloUrl);
                    break;
                default:
                    Log.w("ClosetDisplay", "알 수 없는 TypeTag로 분류되지 못한 옷: " + a.NameTag + ", URL: " + a.CloUrl);
                    break;
            }
        }
    }
}