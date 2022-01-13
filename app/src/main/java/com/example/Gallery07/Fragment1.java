package com.example.Gallery07;


import static com.example.Gallery07.Utils.defaultFolder;
import static com.example.Gallery07.Utils.mContext;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.preference.PreferenceManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.Serializable;
import java.util.ArrayList;

public class Fragment1 extends Fragment {
    private MaterialToolbar topAppBar1;
    private AppBarLayout appBarLayout1;
    private ProgressBar progressBar;
    private ImagesManager imagesManager;
    private static ActionMode actionMode;
    private String folderName = defaultFolder;
    private String sortType = " DESC";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        View view = inflater.inflate(R.layout.fragment1, container, false);
        topAppBar1 = view.findViewById(R.id.topAppBar1);

        appBarLayout1 = view.findViewById(R.id.appBarLayout1);
        if (arguments != null) {
            folderName = arguments.getString("foldername");
            topAppBar1.setTitle(folderName);
            topAppBar1.getMenu().findItem(R.id.menu1_sortA).setVisible(false);
            topAppBar1.getMenu().findItem(R.id.menu1_sortD).setVisible(false);
            topAppBar1.getMenu().findItem(R.id.menu1_capture_image).setVisible(false);
            topAppBar1.getMenu().findItem(R.id.menu1_change_theme).setVisible(false);
            if (folderName=="Trash")
            {
                topAppBar1.getMenu().findItem(R.id.menu1_import_img).setVisible(false);
                topAppBar1.getMenu().findItem(R.id.menu1_import_img_clipboard).setVisible(false);
                topAppBar1.getMenu().findItem(R.id.menu1_clear_trash).setVisible(true);
            }
        }
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        imagesManager = new ImagesManager(folderName);
        imagesManager.setRecyclerView(view.findViewById(R.id.myRecyclerView));
        imagesManager.loadImages(" DESC");
        topAppBar1.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu1_change_theme) {
                    Boolean isDarkMode = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("darkMode", false);
                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean("darkMode", !isDarkMode).apply();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Intent mStartActivity = new Intent(mContext, MainActivity.class);
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, mPendingIntent);
                            System.exit(0);
                        }
                    }, 200);   //0.2 seconds
                    /*Intent intent =new Intent(mContext, Settings.class);
                    mContext.startActivity(intent);*/

                } else if (itemId == R.id.menu1_import_img) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    someActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
                } else if (itemId==R.id.menu1_capture_image)
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //getting uri of the file
                    someActivityResultLauncher2.launch(intent);
                }
                else if (itemId==R.id.menu1_clear_trash)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    // Set a title for alert dialog
                    builder.setTitle("Deleted images cant be recovered!");

                    // Ask the final question
                    builder.setMessage("Are you sure you want to delete all?");

                    // Set the alert dialog yes button click listener
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when user clicked the Yes button
                            imagesManager.deleteAllImages();
                        }
                    });

                    // Set the alert dialog no button click listener
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when No button clicked
                            Toast.makeText(mContext,
                                    "No Clicked",Toast.LENGTH_SHORT).show();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    // Display the alert dialog on interface
                    dialog.setOnShowListener(arg0 -> {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mContext.getResources().getColor(R.color.blue));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mContext.getResources().getColor(R.color.blue));
                    });
                    dialog.show();

                }
                else if (itemId == R.id.menu1_import_img_clipboard) {
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
                else if (itemId == R.id.menu1_sortA) {
                    sortType = " ASC";
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("sortType",sortType);
                    editor.apply();
                    imagesManager.loadImages(sortType);
                }
                else if (itemId == R.id.menu1_sortD) {
                    sortType = " DESC";
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("sortType",sortType);
                    editor.apply();
                    imagesManager.loadImages(sortType);
                }
                return true;
            }
        });
        return (ViewGroup) view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String value = preferences.getString("sortType", "");
        if(!value.equalsIgnoreCase(""))
        {
            sortType = value;
        }
        imagesManager.loadImages(sortType);
    }
    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sortType",sortType);
        editor.apply();
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
                    shareMedia();
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

    public void shareMedia() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            ArrayList<Uri> files = imagesManager.getListURI();
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, null));
        } catch (Exception e) {
            Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show();
        }
        imagesManager.unSelectAllImages();
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
                                imagesManager.loadImages(sortType);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

    ActivityResultLauncher<Intent> someActivityResultLauncher2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        imagesManager.saveImage(imageBitmap, Utils.createFileName());
                        Utils.scanGalleryFile(Utils.galleryPath);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                imagesManager.loadImages(sortType);
                            }
                        }, 1000);   //1 seconds
                    }
                }
            });
}