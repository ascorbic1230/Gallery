package com.example.Gallery07;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.util.List;
import java.util.Objects;

public class ScreenSlidePagerAdapter extends PagerAdapter {
    Context context;
    List images;
    LayoutInflater mLayoutInflater;
    public ScreenSlidePagerAdapter(Context context, List images) {
        this.context = context;
        this.images = images;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
            View itemView = mLayoutInflater.inflate(R.layout.fragment_screen_slide_picture, container, false);

            // referencing the image view from the item.xml file
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewMain);

            // setting the image in the imageView
            ObjectKey obj = new ObjectKey(System.currentTimeMillis());
            Glide.with(context)
                    .load(((CImage)images.get(position)).getImageUri())
                    .apply(RequestOptions.placeholderOf(R.drawable.background))
                    .signature(obj)
                    .into(imageView);
            // Adding the View
            Objects.requireNonNull(container).addView(itemView);

            return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
