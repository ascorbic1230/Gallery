package com.example.Gallery07;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class FullscreenImgActivity extends Activity {
    private ImageView bigImageView;
    private Button backButton;
    private Button addToFolderButton;
    //Avoid Multiple Click On The Same Target
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_img);
        bigImageView = findViewById(R.id.bigImageView);
        backButton = findViewById(R.id.backButton);
        addToFolderButton = findViewById(R.id.addToFolderButton);
        Intent intent = getIntent();
        String curPath = intent.getStringExtra("curPath");
        Glide.with(getApplicationContext())
                .load(curPath)
                .apply(new RequestOptions().centerCrop())
                .into(bigImageView);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
