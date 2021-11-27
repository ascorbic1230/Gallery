package com.example.Gallery07;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class FullscreenImgActivity extends Activity {
    private ImageView bigImageView;
    private Button backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_img);
        bigImageView=findViewById(R.id.bigImageView);
        backButton=findViewById(R.id.backButton);
        Intent intent = getIntent();
        String curPath=intent.getStringExtra("curPath");
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
