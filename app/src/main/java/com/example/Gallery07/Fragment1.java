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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;

public class Fragment1 extends Fragment {
    private ImagesManager imagesManager;
    private Button importButton;
    private Button selectButton;
    private Button importUrlButton;
    private Button confirmUrlButton;
    private Button slideShareButton;
    private boolean hasCheckBox;
    private ImageButton deleteButton;
    private View deleteView;
    private View urlView;
    private EditText urlEditText;
    private String folderName = "Folder1";
    //Avoid Multiple Click On The Same Target
    private long mLastClickTime = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        //Get everything from this view
        importButton = (Button) view.findViewById(R.id.importButton);
        selectButton = (Button) view.findViewById(R.id.selectButton);
        importUrlButton = (Button) view.findViewById(R.id.importUrlButton);
        slideShareButton=(Button)view.findViewById(R.id.slideShareButton);
        hasCheckBox = false;
        deleteView = (View) view.findViewById(R.id.deletebottomui_layout);
        deleteButton = (ImageButton) view.findViewById(R.id.deleteButton);
        imagesManager = new ImagesManager(getActivity(), folderName);
        imagesManager.setRecyclerView(view.findViewById(R.id.myRecyclerView));
        imagesManager.loadImages();
        //import On Click -> Launch activity (and get result later)
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                someActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
            }
        });
        //selectButton On Click -> Set nav invisible, set deleteui visible, setcheckbox visible
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSelectedButton();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagesManager.deleteImagesSelected();
                onClickSelectedButton();
            }
        });
        importUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setCancelable(false);
                popupInputUrlLayout();
                alertDialogBuilder.setView(urlView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                confirmUrlButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url="";
                        if (TextUtils.isEmpty(urlEditText.getText()))
                        {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            if (clipboard == null) return;
                            ClipData clip = clipboard.getPrimaryClip();
                            if (clip == null) return;
                            ClipData.Item item = clip.getItemAt(0);
                            if (item == null) return;
                            url = item.getText().toString();
                        }
                        else
                        url=urlEditText.getText().toString();
                        imagesManager.importImgByUrl(url);
                        urlEditText.setText("");
                        alertDialog.cancel();
                    }
                });
            }

        });
        slideShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), SlideShow.class);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST",(Serializable)imagesManager.getAllImages());
                myIntent.putExtra("listImgPath",args);
                getActivity().startActivity(myIntent);
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


    private void popupInputUrlLayout() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        urlView = layoutInflater.inflate(R.layout.import_image_url, null);
        urlEditText = (EditText) urlView.findViewById(R.id.urlEditText);
        confirmUrlButton = (Button) urlView.findViewById(R.id.confirmUrlButton);
    }
}