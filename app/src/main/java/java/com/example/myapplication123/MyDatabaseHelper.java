package java.com.example.myapplication123;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication123.ClothesItem;

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
        db.execSQL("CREATE TABLE IF NOT EXISTS clothes (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "style TEXT, type TEXT, detailed_type TEXT, preference INTEGER, image_url TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS clothes");
        onCreate(db);
    }
    public List<com.example.myapplication123.ClothesItem> getAllClothesSortedByPreference() {
        List<com.example.myapplication123.ClothesItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT type, preference, image_url FROM clothes ORDER BY preference DESC", null);

        while (cursor.moveToNext()) {
            String type = cursor.getString(0);
            int preference = cursor.getInt(1);
            String imageUrl = cursor.getString(2);
            list.add(new ClothesItem(type, preference, imageUrl));
        }

        cursor.close();
        db.close();
        return list;
    }
}
