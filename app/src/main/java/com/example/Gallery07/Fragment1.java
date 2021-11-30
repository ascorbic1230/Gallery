package com.example.Gallery07;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.Serializable;

public class Fragment1 extends Fragment {
    private ImagesManager imagesManager;
    private MaterialToolbar topAppBar1;
    private Button selectButton;
    private boolean hasCheckBox;
    private ImageButton deleteButton;
    private Button confirmUrlButton;
    private View deleteView;
    private View urlView;
    private EditText urlEditText;
    private String folderName = "AllImages";
    //Avoid Multiple Click On The Same Target
    private long mLastClickTime = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            folderName = arguments.getString("foldernum");
        }
        //Get everything from this view
        hasCheckBox = false;
        selectButton = (Button) view.findViewById(R.id.selectButton);
        deleteView = (View) view.findViewById(R.id.deletebottomui_layout);
        deleteButton = (ImageButton) view.findViewById(R.id.deleteButton);
        topAppBar1 = view.findViewById(R.id.topAppBar1);
        imagesManager = new ImagesManager(getActivity(), folderName);
        imagesManager.setRecyclerView(view.findViewById(R.id.myRecyclerView));
        imagesManager.loadImages();
        //import On Click -> Launch activity (and get result later)
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagesManager.deleteImagesSelected();
                onClickSelectedButton();
            }
        });
        topAppBar1.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu1_setting) {
                    Intent intent = new Intent(getActivity(), Settings.class);
                    startActivity(intent);
                } else if (itemId == R.id.menu1_import_img && SystemClock.elapsedRealtime() - mLastClickTime >= 1000) {
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    someActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
                } else if (itemId == R.id.menu1_import_img_clipboard) {
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    try {
                        ClipData clip = clipboard.getPrimaryClip();
                        String url = clip.getItemAt(0).getText().toString();
                        imagesManager.importImgByUrl(url);
                        urlEditText.setText("");
                    } catch (Exception e) {
                        //Do nothing
                    }
                    ;
                } else if (itemId == R.id.menu1_slideshow) {
                    Intent myIntent = new Intent(getActivity(), SlideShow.class);
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST", (Serializable) imagesManager.getAllImages());
                    myIntent.putExtra("listImgPath", args);
                    getActivity().startActivity(myIntent);
                }
                return true;
            }
        });
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSelectedButton();
            }
        });
        return (ViewGroup) view;
    }

    @Override
    public void onResume() {
        super.onResume();
        imagesManager.loadImages();
    }

    private void onClickSelectedButton() {
        hasCheckBox = !hasCheckBox;
        if (hasCheckBox) {
            selectButton.setText("CANCEL");
            ((MainActivity) getActivity()).setNavVisible(false);
            deleteView.setVisibility(View.VISIBLE);
        } else {
            selectButton.setText("SELECT");
            deleteView.setVisibility(View.GONE);
            ((MainActivity) getActivity()).setNavVisible(true);
        }
        imagesManager.toggleCheckBox(hasCheckBox);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        try {
                            if (data.getClipData() != null) {
                                ClipData clipData = data.getClipData();
                                imagesManager.saveImages(clipData);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

}