package com.example.Gallery07;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class SlideShow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);
        ArrayList imageList = new ArrayList<SlideModel>();
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("listImgPath");
        ArrayList<String> listImgPath = (ArrayList<String>) args.getSerializable("ARRAYLIST");
        for (String imgPath : listImgPath) {
            imageList.add(new SlideModel("file://" +imgPath));
            Log.i("ListPath: ", imgPath);

        }
        ImageSlider imageSlider = (ImageSlider) findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList, true);
        imageSlider.startSliding(2000);
    }
}