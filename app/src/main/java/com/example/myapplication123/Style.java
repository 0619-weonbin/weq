// Style.java
package com.example.myapplication123;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.database.sqlite.SQLiteDatabase; // SQLiteDatabase 임포트
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Style extends AppCompatActivity {
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ImageView imageView;
    private Uri photoUri;
    private File photoFile;
    private Spinner spinnerStyle, spinnerType, spinnerDetailedType, spinnerPreference;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style);

        imageView = findViewById(R.id.imageView);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            imageView.setImageURI(photoUri);
                            // 사진이 성공적으로 찍히고 표시되면 로그 출력
                            Log.d("StyleActivity", "카메라 사진 로드 성공. Photo URI: " + photoUri.toString());
                        } else {
                            Log.d("StyleActivity", "카메라 취소 또는 오류.");
                            // 카메라를 취소했을 경우, 앱을 종료하거나 다른 조치를 취할 수 있습니다.
                            // finish(); // 예: 카메라 취소 시 액티비티 종료
                        }
                    }
                }
        );

        spinnerStyle = findViewById(R.id.spinner_style);
        spinnerType = findViewById(R.id.spinner_type);
        spinnerDetailedType = findViewById(R.id.spinner_detailed_type);
        spinnerPreference = findViewById(R.id.spinner_preference);
        buttonSave = findViewById(R.id.button_save);

        String[] styleOptions = {"아메카지", "스트릿", "미니멀", "캐주얼", "스포티/애슬레저", "걸리시",
                "프레피룩", "빈티지", "레트로", "오피스룩 / 포멀룩", "노멀코어", "테크웨어", "댄디룩", "모던룩",
                "내추럴룩", "보헤미안", "유니섹스", "힙스터 스타일", "멀티레이어드", "코리안 스트릿", "Y2K",
                "케이팝 스타일", "룩북 스타일 / 인스타 감성룩"};
        String[] typeOptions = {"상의", "하의", "아우터"};
        String[] typeOptions_outer = {"두꺼운 아우터", "가벼운 아우터","패딩"};
        String[] typeOptions_top = {"기모상의","두꺼운 긴팔" ,"가벼운 긴팔", "반팔", "나시", "원피스"};
        String[] typeOptions_bottom = {"기모바지", "긴바지", "반바지"};
        String[] preferenceOptions = {"선호도: 5", "선호도: 4", "선호도: 3", "선호도: 2", "선호도: 1"};

        setUpSpinner(spinnerStyle, styleOptions);
        setUpSpinner(spinnerType, typeOptions);
        setUpSpinner(spinnerPreference, preferenceOptions);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // 상의
                        setUpSpinner(spinnerDetailedType, typeOptions_top);
                        break;
                    case 1: // 하의
                        setUpSpinner(spinnerDetailedType, typeOptions_bottom);
                        break;
                    case 2: // 아우터
                        setUpSpinner(spinnerDetailedType, typeOptions_outer);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 저장 버튼 클릭 리스너
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("StyleActivity", "저장 버튼 클릭됨");

                // 사진이 찍혔는지 확인
                if (photoFile == null || !photoFile.exists()) {
                    Toast.makeText(Style.this, "사진을 먼저 찍어주세요.", Toast.LENGTH_SHORT).show();
                    Log.e("StyleActivity", "저장 실패: photoFile이 없거나 유효하지 않음.");
                    return; // 사진 없으면 저장 진행 안 함
                }

                // 드롭다운 메뉴 선택 확인
                if (spinnerStyle.getSelectedItem() == null || spinnerType.getSelectedItem() == null ||
                        spinnerDetailedType.getSelectedItem() == null || spinnerPreference.getSelectedItem() == null) {
                    Toast.makeText(Style.this, "모든 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    Log.e("StyleActivity", "저장 실패: 드롭다운 항목이 모두 선택되지 않음.");
                    return; // 항목 미선택 시 저장 진행 안 함
                }

                MyDatabaseHelper dbHelper = null; // 초기화
                SQLiteDatabase db = null; // 초기화

                try {
                    dbHelper = new MyDatabaseHelper(Style.this);
                    // ★★★ 여기가 수정된 부분입니다: getWritableDatabase() 사용 ★★★
                    db = dbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put("style", spinnerStyle.getSelectedItem().toString());
                    values.put("type", spinnerType.getSelectedItem().toString());
                    values.put("detailed_type", spinnerDetailedType.getSelectedItem().toString());

                    String preference = spinnerPreference.getSelectedItem().toString();
                    values.put("preference", Integer.parseInt(preference.substring(
                            preference.indexOf(":") + 1).trim()));
                    // 파일의 절대 경로를 DB에 저장합니다.
                    values.put("image_url", photoFile.getAbsolutePath());

                    long rowId = db.insert("clothes", null, values); // 데이터 삽입

                    if (rowId != -1) {
                        Toast.makeText(Style.this, "옷이 성공적으로 저장되었습니다!", Toast.LENGTH_LONG).show();
                        Log.d("StyleActivity", "DB에 옷 추가 성공. Row ID: " + rowId + ", Image URL: " + photoFile.getAbsolutePath());
                    } else {
                        Toast.makeText(Style.this, "옷 저장에 실패했습니다.", Toast.LENGTH_LONG).show();
                        Log.e("StyleActivity", "DB에 옷 추가 실패.");
                    }

                } catch (Exception e) {
                    Log.e("StyleActivity", "DB 저장 중 예외 발생: " + e.getMessage());
                    Toast.makeText(Style.this, "오류 발생: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } finally {
                    // 데이터베이스 리소스는 항상 닫아주어야 합니다.
                    if (db != null) {
                        db.close();
                    }
                    if (dbHelper != null) {
                        dbHelper.close();
                    }
                }

                // 메인 화면으로 돌아가기 (옷장 화면이 아님)
                // 만약 옷장 화면으로 바로 가고 싶다면 Closet.class로 변경
                Intent intent = new Intent(Style.this, MainActivity.class);
                startActivity(intent);
                finish(); // 스타일 설정 액티비티 종료
            }
        });

        // onCreate에서 바로 카메라를 실행합니다.
        openCamera();
    }

    // 카메라 인텐트 실행 함수
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 카메라 앱을 처리할 수 있는 컴포넌트가 있는지 확인
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile(); // 임시 이미지 파일 생성
                if (photoFile != null) {
                    // FileProvider를 통해 보안 Uri 생성
                    photoUri = FileProvider.getUriForFile(
                            this,
                            getApplicationContext().getPackageName() + ".fileprovider",
                            photoFile
                    );

                    // 인텐트에 Uri를 넘겨서 고화질 이미지 저장 위치 지정
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    cameraLauncher.launch(takePictureIntent); // 카메라 실행
                    Log.d("StyleActivity", "카메라 실행 시도. PhotoFile: " + photoFile.getAbsolutePath());
                }
            } catch (IOException e) {
                Log.e("StyleActivity", "이미지 파일 생성 중 오류: " + e.getMessage());
                Toast.makeText(this, "이미지 파일을 생성할 수 없습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "카메라 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            Log.e("StyleActivity", "카메라 앱을 찾을 수 없음.");
        }
    }

    // 이미지 파일 생성 함수
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // 외부 저장소의 앱 전용 디렉토리 (사진 갤러리에 직접 노출되지 않음)
        // Environment.DIRECTORY_PICTURES는 공용 Pictures 폴더가 아닌 앱 내부의 Pictures 폴더입니다.
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // 해당 디렉토리가 없으면 생성 시도
        if (storageDir != null && !storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e("StyleActivity", "저장 디렉토리 생성 실패: " + storageDir.getAbsolutePath());
                throw new IOException("저장 디렉토리 생성 실패");
            }
        }

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d("StyleActivity", "이미지 파일 생성됨: " + image.getAbsolutePath());
        return image;
    }

    // 드롭다운 스피너 설정 메소드
    private void setUpSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
    }
}