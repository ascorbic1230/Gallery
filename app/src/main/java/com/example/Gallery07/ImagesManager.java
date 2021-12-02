package com.example.Gallery07;

import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImagesManager {
    private RecyclerView recyclerView;
    private PhotoViewAdapter myRecyclerViewAdapter;
    private ArrayList<String> listAllImages;    //Luu list path dan toi file
    private Context mContext;
    private String folderPath;  //Thu muc rieng cua app
    private static int photoNumber = 0; //Danh so thu tu anh, tranh tinh trang trung ten khi import nhieu anh cung luc

    public ImagesManager(Context mContext, String folderName) {
        super();
        this.mContext = mContext;
        folderPath = mContext.getFilesDir().getAbsolutePath() + File.separator + folderName;
        listAllImages = new ArrayList<>();
    }

    public ArrayList<String> getAllImages() {
        return listAllImages;
    }

    public void setRecyclerView(RecyclerView _recyclerView) {
        this.recyclerView = _recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        myRecyclerViewAdapter = new PhotoViewAdapter();
        recyclerView.setAdapter(myRecyclerViewAdapter);
        myRecyclerViewAdapter.setData(listAllImages);
        myRecyclerViewAdapter.setmContext(mContext);
    }

    public void saveImages(ClipData clipData) {
        int n = clipData.getItemCount();
        for (int i = 0; i < n; i++) {
            String name = createFileName();
            try {
                saveImage(MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), clipData.getItemAt(i).getUri()), name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        myRecyclerViewAdapter.setData(listAllImages);
    }

    public void deleteImagesSelected() {
        int childCount = recyclerView.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            PhotoViewAdapter.PhotoViewHolder viewHolder = (PhotoViewAdapter.PhotoViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
            if (viewHolder.isChecked() == true) {
                deleteImage(listAllImages.get(i));
                listAllImages.remove(i);
            }
        }
        //Update RecyclerView
        myRecyclerViewAdapter.setData(listAllImages);

    }


    public void toggleCheckBox(boolean val) {
        myRecyclerViewAdapter.setItemClickable(val);
    }

    public void deleteImage(String path) {
        File fi = new File(path);
        if (fi.exists())
            fi.delete();
    }

    private void saveImage(Bitmap bitmap, String name) {
        File myDir = new File(folderPath);
        if (!myDir.exists() && !myDir.isDirectory())
            myDir.mkdirs();
        File file = new File(myDir, name);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            listAllImages.add(0, folderPath + File.separator + name);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadImages() {
        listAllImages.clear();
        File myDir = new File(folderPath);
        if (!myDir.exists() && !myDir.isDirectory())
            myDir.mkdirs();
        File[] filess = myDir.listFiles();
        if (filess == null)
            return;
        //Load anh trong folder cua minh
        for (int i = 0; i < filess.length; i++)
            listAllImages.add(folderPath + File.separator + filess[i].getName());
        //Load anh tu phone gallery & Update RecyclerView
        if (folderPath.equals(mContext.getFilesDir().getAbsolutePath() + File.separator + "All Images"))
            loadImagesFromPhoneGallery();
        myRecyclerViewAdapter.setData(listAllImages);
    }


    private void loadImagesFromPhoneGallery() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        int col_idx_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext())
            listAllImages.add(cursor.getString(col_idx_data));
    }

    public void importImgByUrl(String url) {
        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        String name = createFileName();
                        saveImage(resource, name);
                        myRecyclerViewAdapter.setData(listAllImages);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private String createFileName() {
        photoNumber++;
        if (photoNumber > 99)
            photoNumber = 0;
        return (new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + photoNumber + ".png");
    }
}