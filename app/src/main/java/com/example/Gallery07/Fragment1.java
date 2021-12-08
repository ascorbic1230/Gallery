package com.example.Gallery07;


import static com.example.Gallery07.Utils.defaultFolder;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.Serializable;

public class Fragment1 extends Fragment {
    private MaterialToolbar topAppBar1;
    private AppBarLayout appBarLayout1;
    private boolean hasCheckBox;
    private ProgressBar progressBar;
    private ImagesManager imagesManager;
    private static ActionMode actionMode;
    private String folderName = defaultFolder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        View view = inflater.inflate(R.layout.fragment1, container, false);
        topAppBar1 = view.findViewById(R.id.topAppBar1);

        appBarLayout1 = view.findViewById(R.id.appBarLayout1);
        if (arguments != null) {
            folderName = arguments.getString("foldername");
            topAppBar1.setTitle(folderName);
        }
        hasCheckBox = false;
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        imagesManager = new ImagesManager(folderName);
        imagesManager.setRecyclerView(view.findViewById(R.id.myRecyclerView));
        imagesManager.loadImages();
        topAppBar1.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu1_setting) {
                    Intent intent = new Intent(getActivity(), Settings.class);
                    startActivity(intent);
                } else if (itemId == R.id.menu1_import_img) {
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
                        imagesManager.importImgByUrl(url, progressBar);
                    } catch (Exception e) {
                        //Do nothing
                    }
                    ;
                } else if (itemId == R.id.menu1_slideshow) {
                    Intent myIntent = new Intent(getActivity(), SlideShow.class);
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST", (Serializable) imagesManager.getAllImagePaths());
                    myIntent.putExtra("listImgPath", args);
                    getActivity().startActivity(myIntent);
                } else if (itemId == R.id.menu1_select_items) {
                    Toast.makeText(getActivity(), folderName, Toast.LENGTH_SHORT).show();

                    appBarLayout1.setVisibility(View.GONE);
                    topAppBar1.setVisibility(View.GONE);
                    actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mCallback);
                    actionMode.setTitle("Select items");
                    imagesManager.toggleCheckBox(true);
                }
                return true;
            }
        });
        return (ViewGroup) view;
    }

    @Override
    public void onResume() {
        super.onResume();
        imagesManager.loadImages();
    }

    private final ActionMode.Callback mCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_action_bar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.share_option:
                    Toast.makeText(getContext(), "Share option clicked", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.delete_option:
                    imagesManager.deleteImagesSelected();
                    imagesManager.toggleCheckBox(false);
                    break;
                default:
                    return false;
            }
            stopActionMode();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            appBarLayout1.setVisibility(View.VISIBLE);
            topAppBar1.setVisibility(View.VISIBLE);
            imagesManager.toggleCheckBox(false);
        }
    };

    public void changeTitleContextualActionBar() {
        int numberSelectedImages = imagesManager.getNumberOfSelectedImages();
        String title = "";
        if (numberSelectedImages > 0) {
            title += numberSelectedImages;
        } else if (numberSelectedImages == 0) {
            title += "Select items";
        }
        actionMode.setTitle(title);
    }

    public static void stopActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
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