package com.example.Gallery07;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.io.File;

import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;

public class FullscreenImgActivity extends AppCompatActivity {
    private final int PHOTO_EDITOR_REQUEST_CODE = 231;

    private ImageView bigImageView;
    private Button editButton;
    private Button backButton;
    private Button addToFolderButton;
    private String curPath;

    ActivityResultLauncher<Intent> editResultLauncher;
    //Avoid Multiple Click On The Same Target
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_img);
        bigImageView = findViewById(R.id.bigImageView);
        editButton = findViewById(R.id.editButton);
        backButton = findViewById(R.id.backButton);
        addToFolderButton = findViewById(R.id.addToFolderButton);
        Intent intent = getIntent();
        curPath = intent.getStringExtra("curPath");

        setupActivityResultLaunchers();

        ObjectKey obj = new ObjectKey(System.currentTimeMillis());

        Glide.with(getApplicationContext())
                .load(curPath)
                .signature(obj)
                .into(bigImageView);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditImage();
            }
        });
    }

    private void setupActivityResultLaunchers() {
        editResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                    }
                });
    }

    private void EditImage() {
        try {
            Intent intent = new ImageEditorIntentBuilder(this, curPath, curPath)
                    .withAddText()
                    .withPaintFeature()
                    .withFilterFeature()
                    .withRotateFeature()
                    .withCropFeature()
                    .withBrightnessFeature()
                    .withSaturationFeature()
                    .withBeautyFeature()
                    .withStickerFeature()
                    .forcePortrait(true)
                    .build();
            EditImageActivity.start(editResultLauncher, intent, this);
        } catch (Exception e) {
//            Log.e("editor eror", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String newFilePath = data.getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH);
            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IS_IMAGE_EDITED, false);

            Log.d("curPath: ", curPath);
            Log.d("newFilePath: ", newFilePath);

            if (isImageEdit) {
                File imageFile = new File(newFilePath);
                if (imageFile.exists()) {
                    Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    bigImageView.setImageBitmap(image);
                }
            }
        }
    }
}
