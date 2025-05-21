package com.example.myapplication123;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

public class Style extends AppCompatActivity {
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ImageView imageView; // 사진 결과 보여줄 뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style);

        imageView = findViewById(R.id.imageView); // 레이아웃에 이미지뷰 있어야 함

        // 1. ActivityResultLauncher 등록 (카메라 결과 받기)
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            // 카메라에서 찍은 사진 비트맵 가져오기
                            Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                            imageView.setImageBitmap(photo);  // ImageView에 사진 보여주기
                        }
                    }
                }
        );
        openCamera();
    }

    // 카메라 실행하는 메서드
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }
}