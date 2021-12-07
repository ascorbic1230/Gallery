package com.example.Gallery07;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.denzcoskun.imageslider.constants.ScaleTypes;

import java.util.ArrayList;

public class SlideShow extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);
        ArrayList imageList = new ArrayList<SlideModel>();
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("listImgPath");
        ArrayList<String> listImgPath = (ArrayList<String>) args.getSerializable("ARRAYLIST");
        for (String imgPath : listImgPath) {
            imageList.add(new SlideModel("file://" + imgPath, ScaleTypes.FIT));
            Log.i("ListPath: ", imgPath);

        }
        ImageSlider imageSlider = (ImageSlider) findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList, ScaleTypes.CENTER_INSIDE);
        imageSlider.startSliding(2000);
    }
}