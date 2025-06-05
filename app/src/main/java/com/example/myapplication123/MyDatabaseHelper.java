package com.example.myapplication123;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log; // Log 임포트 추가

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "MyDatabase.db";
    private static final int DB_VERSION = 1;

    public MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // clothes 테이블 생성 쿼리 (이미지 URL, 스타일, 타입, 상세 타입, 선호도 모두 포함)
        db.execSQL("CREATE TABLE IF NOT EXISTS clothes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "style TEXT, " +
                "type TEXT, " +
                "detailed_type TEXT, " + // CloTag의 NameTag에 해당
                "preference INTEGER, " +
                "image_url TEXT)");
        Log.d("MyDatabaseHelper", "Database onCreate: 'clothes' table created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("MyDatabaseHelper", "Database onUpgrade: Dropping 'clothes' table and recreating.");
        db.execSQL("DROP TABLE IF EXISTS clothes");
        onCreate(db);
    }

    // ★★★ 이 메서드를 수정하여 CloTag 리스트를 반환하도록 합니다. ★★★
    public List<CloTag> getAllClothesForCloset() { // 메서드 이름 변경 (명확성을 위해)
        List<CloTag> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase(); // 읽기 가능한 DB 인스턴스 가져오기
            // clothes 테이블에서 모든 필요한 컬럼을 SELECT 합니다.
            // CloTag에 필요한 CloUrl, TmpTag, StyTag, TypeTag, NameTag (detailed_type)를 모두 가져와야 합니다.
            cursor = db.rawQuery("SELECT image_url, style, type, detailed_type, preference FROM clothes ORDER BY preference DESC", null);

            if (cursor != null && cursor.moveToFirst()) {
                int imageUrlIndex = cursor.getColumnIndex("image_url");
                int styleIndex = cursor.getColumnIndex("style");
                int typeIndex = cursor.getColumnIndex("type");
                int detailedTypeIndex = cursor.getColumnIndex("detailed_type");
                int preferenceIndex = cursor.getColumnIndex("preference");

                do {
                    String imageUrl = cursor.getString(imageUrlIndex);
                    String style = cursor.getString(styleIndex);
                    String type = cursor.getString(typeIndex);
                    String detailedType = cursor.getString(detailedTypeIndex);
                    int preference = cursor.getInt(preferenceIndex);

                    // CloTag 생성에 필요한 TmpTag는 여기서 detailedType을 기반으로 계산합니다.
                    String tmpTag = cloTemp(detailedType);

                    // ★★★ CloTag 객체 생성 (preference 속성 포함) ★★★
                    CloTag clo = new CloTag(imageUrl, tmpTag, style, type, detailedType, preference);
                    list.add(clo);
                    Log.d("MyDatabaseHelper", "로드된 CloTag: " + detailedType + ", URL: " + imageUrl);
                } while (cursor.moveToNext());
                Log.d("MyDatabaseHelper", "getAllClothesForCloset: 총 " + list.size() + "개의 CloTag 로드 완료.");
            } else {
                Log.d("MyDatabaseHelper", "getAllClothesForCloset: 'clothes' 테이블에 데이터가 없습니다.");
            }
        } catch (Exception e) {
            Log.e("MyDatabaseHelper", "getAllClothesForCloset 오류: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return list;
    }

    // 이 메서드는 CloTag의 TmpTag를 생성하기 위해 필요합니다.
    // Closet.java에 있는 cloTemp 메서드와 동일합니다.
    private String cloTemp(String detailedType){
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
}