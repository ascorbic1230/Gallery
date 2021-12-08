package com.example.Gallery07;

import static com.example.Gallery07.Utils.copyFile;
import static com.example.Gallery07.Utils.deleteImagePerma;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;

public class FullscreenImgActivity extends AppCompatActivity {
    private PhotoView bigImageView;
    private BottomNavigationView bottomMenu;
    private String curPath;

    ActivityResultLauncher<Intent> editResultLauncher;
    private View formView;
    private ListView formListView;
    private Button formCancelButton;
    private WallpaperManager wallpaperManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_img);
        bigImageView = findViewById(R.id.bigImageView);
        bottomMenu = findViewById(R.id.bottom_menu);

        wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

        Intent intent = getIntent();
        setupActivityResultLaunchers();
        curPath = intent.getStringExtra("curPath");
        ObjectKey obj = new ObjectKey(System.currentTimeMillis());
        Glide.with(FullscreenImgActivity.this)
                .load(curPath)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background))
                .override(500, 500)
                .signature(obj)
                .into(bigImageView);

        bottomMenu.getMenu().setGroupCheckable(0, false, true);

        bottomMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editMenu:
                        EditImage();
                        break;
                    case R.id.wallpaperMenu:
                        setWallpaper();
                        break;
                    case R.id.backMenu:
                        finish();
                        break;
                    case R.id.moveMenu:
                        addToFolder();
                        break;
                }
                return true;
            }
        });
    }

    private void setWallpaper() {
        try {
            File imageFile = new File(curPath);
            if (imageFile.exists()) {
                Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                wallpaperManager.setBitmap(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addToFolder() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FullscreenImgActivity.this);
        alertDialogBuilder.setCancelable(false);
        popupMoveToFolder();
        alertDialogBuilder.setView(formView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        String path = FullscreenImgActivity.this.getFilesDir().getAbsolutePath();
        File directory = new File(path);
        File[] files = directory.listFiles();
        ArrayList<String> listFolderNames = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            listFolderNames.add(files[i].getName());
        }
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(FullscreenImgActivity.this, android.R.layout.simple_list_item_1, listFolderNames);
        formListView.setAdapter(itemsAdapter);
        formListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                copyFile(curPath, FullscreenImgActivity.this.getFilesDir().getAbsolutePath() + File.separator + listFolderNames.get(position));
                deleteImagePerma(curPath);
                alertDialog.cancel();
                onBackPressed();
            }
        });
        formCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    private void popupMoveToFolder() {
        LayoutInflater layoutInflater = LayoutInflater.from(FullscreenImgActivity.this);
        formView = layoutInflater.inflate(R.layout.form_move_to_folder, null);
        formListView = (ListView) formView.findViewById(R.id.formListView);
        formCancelButton = (Button) formView.findViewById(R.id.formCancelButton);
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
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String newFilePath = data.getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH);
            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IS_IMAGE_EDITED, false);
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
