package com.example.Gallery07;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
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
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImagesManager {
    private RecyclerView recyclerView;
    private PhotoViewAdapter myRecyclerViewAdapter;
    private Context mContext;
    private String folderPath;  //Thu muc rieng cua app
    private static int photoNumber = 0; //Danh so thu tu anh, tranh tinh trang trung ten khi import nhieu anh cung luc
    private List listAllImages;    //Luu list path dan toi file
    private String defaultFolder = "All Images";
    private final String galleryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM" + File.separator + "Camera";
    private final String trashFolder = "Trash";

    public ImagesManager(Context mContext, String folderName) {
        super();
        if (!folderName.equals(defaultFolder))
            folderPath = mContext.getFilesDir().getAbsolutePath() + File.separator + folderName;
        else
            folderPath = galleryPath;
        this.mContext = mContext;
        listAllImages = new ArrayList<CImage>();
    }

    public void setFolder(String folderName) {
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

    public void deleteImage(String path) {
        if (new File(path).getParentFile().getName().equals(trashFolder)) {
            File fi = new File(path);
            if (fi.exists())
                fi.delete();
        } else {
            moveFile(path, trashFolder);
            if (folderPath.equals(galleryPath)) {
                deleteGalleryImage(new File(path));
            }
        }
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
            Log.e("Error Is", e.toString());
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
        String dateString = "";
        String format = "yyyyMMddHHmmss";
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
        while (cursor.moveToNext()) {
            String path = cursor.getString(col_idx_data);
            Log.i("AbsolutePath: ", path);
            String date = cursor.getString(col_idx_date);
            //https://stackoverflow.com/questions/30106784/change-androids-media-date-taken-format
            // https://stackoverflow.com/questions/535004/unix-epoch-time-to-java-date-object
            String dateTime = formatter.format(new Date(Long.parseLong(date) * 1000));
            Log.i("info1", dateTime.substring(0, 8));
            if (listAllImages.size() >= 1)
                Log.i("info2", ((CImage) listAllImages.get(listAllImages.size() - 1)).getDateByMonthAndYear());
            if (listAllImages.size() == 0) {
                listAllImages.add(new CImage("", dateTime, 0));
            } else if (!dateTime.substring(0, 8).equals(((CImage) listAllImages.get(listAllImages.size() - 1)).getDateByMonthAndYear())) {
                listAllImages.add(new CImage("", dateTime, 0));
            }
            Log.i("Root Path: ", path);
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

    private String createFileName() {
        photoNumber++;
        if (photoNumber > 99)
            photoNumber = 0;
        return (new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + photoNumber + ".png");
    }

    public void moveFile(String srcPath, String folderDes) {
        String desPath = mContext.getFilesDir().getAbsolutePath() + File.separator + folderDes;
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


    private void deleteGalleryImage(File file) {
        String[] projection = {MediaStore.Images.Media._ID};
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{file.getAbsolutePath()};
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
        } else {
            // File not found in media store DB
        }
        c.close();
    }

}