package com.example.demo01;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImagesManager {
    private Context context;
    private ArrayList<Image> listAllImages;
    private RecyclerView imagesRecView;
    private ImagesRecViewAdapter adapter;
    private int id = 1;

    public ImagesManager(Context context) {
        this.context = context;
        listAllImages = new ArrayList<>();
    }

    public void setImagesRecView(RecyclerView recView) {
        this.imagesRecView = recView;
        this.listAllImages = new ArrayList<>();
        imagesRecView.setLayoutManager(new GridLayoutManager(context, 3));
        adapter = new ImagesRecViewAdapter(context);
        adapter.setImages(listAllImages);
        imagesRecView.setAdapter(adapter);
        loadImagesFromPhoneGallery();
    }

    public void setSelectMultipleImages(boolean isTrue) {
        adapter.setMultiSelectable(isTrue);
    }

    public void selectAllImages() {
        for (int i = 0; i < listAllImages.size(); i++) {
            listAllImages.get(i).setChecked(true);
            adapter.setSelectAll(true);
        }
    }

    public void unSelectAllImages() {
        for (int i = 0; i < listAllImages.size(); i++) {
            listAllImages.get(i).setChecked(false);
        }
    }

    public int getNumberOfSelectedImages() {
        int count = 0;
        for (int i = 0; i < listAllImages.size(); i++) {
            if (listAllImages.get(i).isChecked()) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<Image> getAllSelectedImages() {
        ArrayList<Image> selectedImages = new ArrayList<>();
        for (int i = 0; i < listAllImages.size(); i++) {
            if (listAllImages.get(i).isChecked()) {
                selectedImages.add(listAllImages.get(i));
            }
        }
        return selectedImages;
    }

    private void loadImagesFromPhoneGallery() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        int col_idx_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            listAllImages.add(new Image(id, cursor.getString(col_idx_data)));
            id++;
        }
    }

}

