package com.example.Gallery07;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FullscreenImgActivity extends Activity {
    private ImageView bigImageView;
    private Button backButton;
    private Button addToFolderButton;
    private View formView;
    private ListView formListView;
    private Button formCancelButton;

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
        Glide.with(FullscreenImgActivity.this)
                .load(curPath)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background))
                .override(500, 500)
                .into(bigImageView);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addToFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FullscreenImgActivity.this);
                alertDialogBuilder.setCancelable(false);
                popupMoveToFolder();
                alertDialogBuilder.setView(formView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                String path = FullscreenImgActivity.this.getFilesDir().getAbsolutePath();
                File directory = new File(path);
                File[] files = directory.listFiles();
                ArrayList<String> listFolderNames = new ArrayList<String>();
                for (int i = 0; i < files.length; i++) {
                    listFolderNames.add(files[i].getName());
                }
                ArrayAdapter<String> itemsAdapter =
                        new ArrayAdapter<String>(FullscreenImgActivity.this, android.R.layout.simple_list_item_1, listFolderNames);
                formListView.setAdapter(itemsAdapter);
                formListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        moveFile(curPath, listFolderNames.get(position));
                        Toast.makeText(FullscreenImgActivity.this, "Move file successfully", Toast.LENGTH_SHORT).show();
                        alertDialog.cancel();
                        onBackPressed();
                    }
                });
                formCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });
            }
        });
    }

    private void popupMoveToFolder() {
        LayoutInflater layoutInflater = LayoutInflater.from(FullscreenImgActivity.this);
        formView = layoutInflater.inflate(R.layout.form_move_to_folder, null);
        formListView = (ListView) formView.findViewById(R.id.formListView);
        formCancelButton = (Button) formView.findViewById(R.id.formCancelButton);
    }


    public void moveFile(String srcPath, String folderDes) {
        String desPath = FullscreenImgActivity.this.getFilesDir().getAbsolutePath() + File.separator + folderDes;
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

    private String createFileName() {
        return (new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png");
    }
}
