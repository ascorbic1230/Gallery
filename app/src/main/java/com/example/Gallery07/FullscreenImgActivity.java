package com.example.Gallery07;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.IOException;

import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;

public class FullscreenImgActivity extends AppCompatActivity {
    private final int PHOTO_EDITOR_REQUEST_CODE = 231;

    private ImageView bigImageView;
    private Button setWallPaperButton;
    private Button editButton;
    private Button backButton;
    private Button addToFolderButton;

    private String curPath;

    ActivityResultLauncher<Intent> editResultLauncher;
    private View formView;
    private ListView formListView;
    private Button formCancelButton;
    //Avoid Multiple Click On The Same Target
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_img);
        setWallPaperButton=findViewById(R.id.setWallpaperButton);
        bigImageView = findViewById(R.id.bigImageView);
        editButton = findViewById(R.id.editButton);
        backButton = findViewById(R.id.backButton);
        addToFolderButton = findViewById(R.id.addToFolderButton);
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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        setWallPaperButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                Log.i(curPath," :uri");
                try {
                    // set the wallpaper by calling the setResource function and passing the drawable file
                    File imageFile = new File(curPath);
                    if (imageFile.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        wallpaperManager.setBitmap(image);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditImage();
            }
        });
  
        addToFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        moveFile(curPath, listFolderNames.get(position));
                        Toast.makeText(FullscreenImgActivity.this, "Move file successfully", Toast.LENGTH_SHORT).show();
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
        });
    }

    private void popupMoveToFolder() {
        LayoutInflater layoutInflater = LayoutInflater.from(FullscreenImgActivity.this);
        formView = layoutInflater.inflate(R.layout.form_move_to_folder, null);
        formListView = (ListView) formView.findViewById(R.id.formListView);
        formCancelButton = (Button) formView.findViewById(R.id.formCancelButton);
    }


    public void moveFile(String srcPath, String folderDes) {
        String desPath = FullscreenImgActivity.this.getFilesDir().getAbsolutePath() + File.separator + folderDes;
        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(desPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            in = new FileInputStream(srcPath);
            out = new FileOutputStream(desPath + File.separator + createFileName());

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(srcPath).delete();


        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    private String createFileName() {
        return (new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png");
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
