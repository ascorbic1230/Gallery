package com.example.Gallery07;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class FullscreenImgActivity extends Activity {
    private ImageView bigImageView;
    private Button backButton;
    private Button addToFolderButton;
    private Button confirmUrlButton;
    private View urlView;
    private EditText urlEditText;

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
        addToFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FullscreenImgActivity.this);
                alertDialogBuilder.setCancelable(false);
                popupInputUrlLayout();
                alertDialogBuilder.setView(urlView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                confirmUrlButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = "";
                        if (TextUtils.isEmpty(urlEditText.getText())) {
                            ClipboardManager clipboard = (ClipboardManager) FullscreenImgActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                            if (clipboard == null) return;
                            ClipData clip = clipboard.getPrimaryClip();
                            if (clip == null) return;
                            ClipData.Item item = clip.getItemAt(0);
                            if (item == null) return;
                            url = item.getText().toString();
                        } else
                            url = urlEditText.getText().toString();
                        String folderPath = FullscreenImgActivity.this.getFilesDir().getAbsolutePath() + File.separator + url;
                        File myDir = new File(folderPath);
                        if (!myDir.exists() && !myDir.isDirectory())
                            myDir.mkdirs();

                        Toast.makeText(FullscreenImgActivity.this, "Create folder " + folderPath, Toast.LENGTH_SHORT).show();
                        urlEditText.setText("");
                        alertDialog.cancel();
                    }
                });
                //

            }
        });
    }

    private void popupInputUrlLayout() {
        LayoutInflater layoutInflater = LayoutInflater.from(FullscreenImgActivity.this);
        urlView = layoutInflater.inflate(R.layout.import_image_url, null);
        urlEditText = (EditText) urlView.findViewById(R.id.urlEditText);
        urlEditText.setHint("Enter folder name");
        confirmUrlButton = (Button) urlView.findViewById(R.id.confirmUrlButton);
    }
}
