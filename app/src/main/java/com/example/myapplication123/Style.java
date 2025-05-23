package com.example.myapplication123;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Style extends AppCompatActivity {
    private ActivityResultLauncher<Intent> cameraLauncher;  // 카메라 결과를 받을 launcher
    private ImageView imageView;        // 사진을 보여줄 ImageView
    private Uri photoUri;               // 저장된 사진의 Uri
    private File photoFile;             // 저장된 사진의 파일 객체
    //드롭다운 메뉴 관련
    private Spinner spinnerStyle, spinnerType;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style);

        //카메라 관련 ********************************************************************************
        imageView = findViewById(R.id.imageView); // 레이아웃에 이미지뷰 가져오기

        // 카메라 결과 처리 콜백 등록
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            // ImageView에 저장된 사진 Uri를 설정해서 이미지 표시
                            imageView.setImageURI(photoUri);
                        }
                    }
                }
        );
        //드롭다운 메뉴 관련 **************************************************************************

        // 스피너 참조erer
        spinnerStyle = findViewById(R.id.spinner_style);
        spinnerType = findViewById(R.id.spinner_type);
        buttonSave = findViewById(R.id.button_save);

        // 항목 리스트 생성
        String[] styleOptions = {"캐주얼", "스포티", "포멀"};
        String[] typeOptions = {"상의", "하의", "아우터"};

        // 어댑터 설정
        setUpSpinner(spinnerStyle, styleOptions);
        setUpSpinner(spinnerType, typeOptions);

        //저장 버튼 (데베 저장) ***********************************************************************
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //데베 접근은 이렇게 하면 됨. 다른 액티비티에서 똑같이 여러 번 써도 됨.
                try (MyDatabaseHelper dbHelper = new MyDatabaseHelper(Style.this);
                     SQLiteDatabase db = dbHelper.getReadableDatabase()) {
                    ContentValues values = new ContentValues();
                    values.put("style", spinnerStyle.getSelectedItem().toString());
                    values.put("type", spinnerType.getSelectedItem().toString());
                    values.put("image_url", photoFile.getAbsolutePath()); //파일 path
                    long rowId = db.insert("clothes", null, values);
                }
                Toast.makeText(Style.this, "저장 완료", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        //카메라실행
        openCamera();
    }
    // 카메라 인텐트 실행 함수
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            // 저장할 임시 이미지 파일 생성
            photoFile = createImageFile();
            // FileProvider를 통해 보안 Uri 생성
            photoUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".fileprovider", // Manifest와 동일하게
                    photoFile
            );

            // 인텐트에 Uri를 넘겨서 고화질 이미지 저장 위치 지정
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            // 카메라 실행
            cameraLauncher.launch(takePictureIntent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 이미지 파일 생성 함수
    private File createImageFile() throws IOException {
        // 현재 시각 기반 파일 이름 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // 외부 저장소의 Pictures 디렉토리에 저장
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // .jpg 확장자 임시 파일 생성
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
    //드롭다운 관련 메소드
    private void setUpSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, items );
        spinner.setAdapter(adapter);
    }
}