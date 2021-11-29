package com.example.Gallery07;

import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ImagesManager {
    private RecyclerView recyclerView;
    private PhotoViewAdapter myRecyclerViewAdapter;
    private ArrayList<Image> listAllImages;    //Luu list path dan toi file
    private ArrayList<String> MonthYearGroup;
    private Context mContext;
    private String folderPath;  //Thu muc rieng cua app
    private static int photoNumber = 0; //Danh so thu tu anh, tranh tinh trang trung ten khi import nhieu anh cung luc

    public ImagesManager(Context mContext, String folderName) {
        super();
        this.mContext = mContext;
        folderPath = mContext.getFilesDir().getAbsolutePath() + File.separator + folderName;
        listAllImages = new ArrayList<>();
        MonthYearGroup = new ArrayList<>();
    }
    public ArrayList<Image> getAllImages(){
        return listAllImages;
    }
    public void setRecyclerView(RecyclerView _recyclerView) {
        this.recyclerView = _recyclerView;
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        myRecyclerViewAdapter = new PhotoViewAdapter();
        recyclerView.setAdapter(myRecyclerViewAdapter);
        myRecyclerViewAdapter.setData(listAllImages,loadMonthYearGroup(),this.recyclerView);
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
        myRecyclerViewAdapter.setData(listAllImages,loadMonthYearGroup(),this.recyclerView);
    }

    public void deleteImagesSelected() {
        int childCount = recyclerView.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            PhotoViewAdapter.PhotoViewHolder viewHolder = (PhotoViewAdapter.PhotoViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
            if (viewHolder.isChecked() == true) {
                deleteImage(listAllImages.get(i).getImageUri());
                listAllImages.remove(i);
            }
        }
        //Update RecyclerView
        myRecyclerViewAdapter.setData(listAllImages,loadMonthYearGroup(),this.recyclerView);

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
            listAllImages.add(0, new Image(folderPath + File.separator + name,"",1));
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
        File[] files = myDir.listFiles();
        if (files == null)
            return;
        //Load anh trong folder cua minh
        for (int i = 0; i < files.length; i++)
            listAllImages.add(new Image(folderPath + File.separator + files[i].getName(),"",1));
        //Load anh tu phone gallery & Update RecyclerView
        loadImagesFromPhoneGallery();
    }


    private void loadImagesFromPhoneGallery() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.Media.DATE_ADDED};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        int col_idx_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int col_idx_date = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        String dateString = "";
        String format = "MM-dd-yyyy HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
        while (cursor.moveToNext()) {
            String path = cursor.getString(col_idx_data);
            String date = cursor.getString(col_idx_date);
            //https://stackoverflow.com/questions/30106784/change-androids-media-date-taken-format
            // https://stackoverflow.com/questions/535004/unix-epoch-time-to-java-date-object
            String dateTime = formatter.format(new Date(Long.parseLong(date) * 1000));
            if (listAllImages.size() == 0) {
                listAllImages.add(new Image("",dateTime,0));
            }
            else if (!getDateByMonthAndYear(dateTime).equals(listAllImages.get(listAllImages.size() - 1).getDateByMonthAndYear())) {
                listAllImages.add(new Image("",dateTime,0));
            }
            listAllImages.add(new Image(path,dateTime,1));
            myRecyclerViewAdapter.setData(listAllImages,loadMonthYearGroup(),this.recyclerView);
        }

    }
    public String getDateByMonthAndYear(String date) {
        String m = date.substring(0,2);
        String y = date.substring(6,10);
        String result = m + y;
        return result;
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
                        myRecyclerViewAdapter.setData(listAllImages,loadMonthYearGroup(), recyclerView);
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
    private ArrayList<String> loadMonthYearGroup() {
        String defaultFormat = "0000";
        ArrayList<String> rs = new ArrayList<>();
        for (int i = 0 ; i < listAllImages.size();i++) {
            String newFormat = listAllImages.get(i).getDateByMonthAndYear();
            if (!newFormat.equals(defaultFormat)) {
                defaultFormat = newFormat;
                rs.add(defaultFormat);
            }
        }
        return rs;
    }
}