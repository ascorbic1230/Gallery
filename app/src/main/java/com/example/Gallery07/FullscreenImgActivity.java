package com.example.Gallery07;

import static com.example.Gallery07.Utils.copyFile;
import static com.example.Gallery07.Utils.deleteImagePerma;
import static com.example.Gallery07.Utils.galleryPath;
import static com.example.Gallery07.Utils.mContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.bumptech.glide.util.Util;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;

public class FullscreenImgActivity extends AppCompatActivity {
    private int curPos;
    private List<CImage> images;
    private BottomNavigationView bottomMenu;
    private String curPath;
    private ViewPager pPager;
    private ScreenSlidePagerAdapter pAdapter;
    ActivityResultLauncher<Intent> editResultLauncher;
    private View formView;
    private ListView formListView;
    private Button formCancelButton;
    private WallpaperManager wallpaperManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_img);
        pPager = (ViewPager) findViewById(R.id.viewPagerMain);
        Intent intent = getIntent();
        setupActivityResultLaunchers();
        curPos = intent.getIntExtra("curPos",0);
        images = (List<CImage>) intent.getSerializableExtra("images");
        pAdapter = new ScreenSlidePagerAdapter(FullscreenImgActivity.this, images);
        pPager.setAdapter(pAdapter);
        pPager.setCurrentItem(curPos);
        bottomMenu = findViewById(R.id.bottom_menu);
        wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        ObjectKey obj = new ObjectKey(System.currentTimeMillis());
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
            curPath = images.get(pPager.getCurrentItem()).getImageUri();
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
        curPath = (images.get(pPager.getCurrentItem())).getImageUri();
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
        listFolderNames.add("All Images");
        for (int i = 0; i < files.length; i++) {
            listFolderNames.add(files[i].getName());
        }
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(FullscreenImgActivity.this, android.R.layout.simple_list_item_1, listFolderNames);
        formListView.setAdapter(itemsAdapter);
        formListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (position == 0)
                    copyFile(curPath, galleryPath);
                else
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
            curPath = ((CImage)images.get(pPager.getCurrentItem())).getImageUri();
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
            Toast.makeText(this, curPath, Toast.LENGTH_SHORT).show();
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
                    curPos = pPager.getCurrentItem();
                    pPager.setAdapter(pAdapter);
                    pPager.setCurrentItem(curPos);
                }
            }
        }
    }
}
