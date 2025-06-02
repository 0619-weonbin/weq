package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

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
        setContentView(R.layout.activity_main);

        styleSpinner = findViewById(R.id.styleSpinner);

        clo1 = findViewById(R.id.clo1);
        clo2 = findViewById(R.id.clo2);
        clo3 = findViewById(R.id.clo3);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.style_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        styleSpinner.setAdapter(adapter);

        loadFromDB();

        String apiKey = "8c16883d64dbbb8dcce77fe26d0da8dc";
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=Seoul&units=metric&appid=" + apiKey;


        styleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (presTemp != null) {
                    String selectedStyle = parent.getItemAtPosition(position).toString();
                    if (selectedStyle.equals("스타일 선택")) {
                        showClo(presTemp, null);
                    } else {
                        showClo(presTemp, selectedStyle);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (presTemp != null){
                    showClo(presTemp,null);
                }

            }
        });
        new Weather(temp -> {
            presTemp = geTemp(temp);
            showClo(presTemp,null);
        }).execute(apiUrl);
    }
    private void loadFromDB(){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM clothes",null);
        allClo.clear();

        if(cursor.moveToFirst()){
            do {
                String style = cursor.getString(cursor.getColumnIndexOrThrow("style"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String detailedType = cursor.getString(cursor.getColumnIndexOrThrow("detailed_type"));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                String tmpTag = cloTemp(detailedType);

                CloTag clo = new CloTag(imageUrl,tmpTag,style,type,detailedType);
                allClo.add(clo);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    private  String cloTemp(String detailedType){
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
            if (!eqtemp) continue;

            if (StyTag != null && ! a.StyTag.equals(StyTag)) continue;

            filter.add(a);
        }
        for (CloTag a : filter){
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(16,16,16,16);
            imageView.setLayoutParams(params);

            Glide.with(this).load(a.CloUrl).into(imageView);

            switch (a.TypeTag){
                case "아우터":
                    clo1.addView(imageView);
                    break;
                case "상의":
                    clo2.addView(imageView);
                    break;
                case "하의":
                    clo3.addView(imageView);
                    break;
                default:
                    break;
            }
        }
    }
}

