package com.example.Gallery07;

import static com.example.Gallery07.Utils.createFileName;
import static com.example.Gallery07.Utils.defaultFolder;
import static com.example.Gallery07.Utils.deleteImage;
import static com.example.Gallery07.Utils.galleryPath;
import static com.example.Gallery07.Utils.mContext;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImagesManager {
    private RecyclerView recyclerView;
    private PhotoViewAdapter myRecyclerViewAdapter;
    private String folderPath;  //Thu muc rieng cua app
    private List listAllImages = new ArrayList<CImage>();    //Luu list path dan toi file

    public ImagesManager(String folderName) {
        super();
        if (!folderName.equals(defaultFolder))
            folderPath = mContext.getFilesDir().getAbsolutePath() + File.separator + folderName;
        else
            folderPath = galleryPath;
    }


    public List getAllImagePaths() {
        List tmpList = new ArrayList<String>();
        for (int i = 0; i < listAllImages.size(); i++) {
            CImage tmp = (CImage) listAllImages.get(i);
            if (tmp.getType() == 1)
                tmpList.add(tmp.getImageUri());
        }
        return tmpList;
    }

    public void setRecyclerView(RecyclerView _recyclerView) {
        this.recyclerView = _recyclerView;
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        myRecyclerViewAdapter = new PhotoViewAdapter();
        recyclerView.setAdapter(myRecyclerViewAdapter);
        myRecyclerViewAdapter.setData(listAllImages, this.recyclerView);
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
        myRecyclerViewAdapter.setData(listAllImages, this.recyclerView);
    }

    public void deleteImagesSelected() {

        for (int i = listAllImages.size() - 1; i >= 0; i--) {
            if (((CImage) listAllImages.get(i)).isChecked()) {
                try {
                    deleteImage(((CImage) listAllImages.get(i)).getImageUri());
                    listAllImages.remove(i);
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                }
            }
        }
        //Update RecyclerView
        myRecyclerViewAdapter.setData(listAllImages, this.recyclerView);
    }


    public void toggleCheckBox(boolean val) {
        if (!val) {
            unSelectAllImages();
        }
        myRecyclerViewAdapter.setItemClickable(val);
    }

    public void unSelectAllImages() {
        for (int i = 0; i < listAllImages.size(); i++) {
            ((CImage) listAllImages.get(i)).setChecked(false);
        }
    }

    public int getNumberOfSelectedImages() {
        int count = 0;
        for (int i = 0; i < listAllImages.size(); i++) {
            if (((CImage) listAllImages.get(i)).isChecked()) {
                count++;
            }
        }
        return count;
    }


    private void saveImage(Bitmap bitmap, String name) {
        File myDir = new File(folderPath);
        if (!myDir.exists() && !myDir.isDirectory())
            myDir.mkdirs();
        File file = new File(myDir, name);
        if (file.exists()) file.delete();
        try {
            String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            if (listAllImages.size() == 0)
                listAllImages.add(new CImage("", dateTime, 0));
            else if (!dateTime.substring(0, 8).equals(((CImage) listAllImages.get(1)).getDateByMonthAndYear())) {
                listAllImages.add(0, new CImage("", dateTime, 0));
            }
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            if (folderPath == galleryPath) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(file.getAbsolutePath());
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                mContext.sendBroadcast(mediaScanIntent);
            }
            listAllImages.add(1, new CImage(file.getAbsolutePath(), dateTime, 1));
            myRecyclerViewAdapter.setData(listAllImages, recyclerView);


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

        if (folderPath.equals(galleryPath))
            loadImagesFromPhoneGallery();
        else {
            for (int i = 0; i < files.length; i++) {
                String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(new File(folderPath + File.separator + files[i].getName()).lastModified()));
                listAllImages.add(new CImage(folderPath + File.separator + files[i].getName(), dateTime, 1));
            }
        }

        myRecyclerViewAdapter.setData(listAllImages, recyclerView);
    }

    private void loadImagesFromPhoneGallery() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        int col_idx_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int col_idx_date = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        String format = "yyyyMMddHHmmss";
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
        while (cursor.moveToNext()) {
            String path = cursor.getString(col_idx_data);
            Log.i("AbsolutePath: ", path);
            String date = cursor.getString(col_idx_date);
            //https://stackoverflow.com/questions/30106784/change-androids-media-date-taken-format
            // https://stackoverflow.com/questions/535004/unix-epoch-time-to-java-date-object
            String dateTime = formatter.format(new Date(Long.parseLong(date) * 1000));
            if (listAllImages.size() == 0) {
                listAllImages.add(new CImage("", dateTime, 0));
            } else if (!dateTime.substring(0, 8).equals(((CImage) listAllImages.get(listAllImages.size() - 1)).getDateByMonthAndYear())) {
                listAllImages.add(new CImage("", dateTime, 0));
            }
            listAllImages.add(new CImage(path, dateTime, 1));
        }
    }


    public void importImgByUrl(String url, ProgressBar progressBar) throws URISyntaxException {
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(mContext)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background))
                .asBitmap()
                .load("http://" + url.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", ""))
                .placeholder(R.drawable.placeholder)
                .timeout(5000)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(mContext, "Import Image Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        String name = createFileName();
                        saveImage(resource, name);
                        progressBar.setVisibility(View.INVISIBLE);
                        myRecyclerViewAdapter.setData(listAllImages, recyclerView);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }
}