package com.example.Gallery07;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Fragment2 extends Fragment {
    private GridLayout gridLayout;
    private ImageView allImagesImageView;
    private ImageView folder1ImageView;
    private ImageView folder2ImageView;
    private MaterialToolbar topAppBar2;
    private Button confirmUrlButton;
    private View urlView;
    private EditText urlEditText;
    private ArrayList<Folder> folderList;
    private GridView folderGridView;
    private FolderViewAdapter folderViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        folderList = new ArrayList<Folder>();
        topAppBar2 = (MaterialToolbar) view.findViewById(R.id.topAppBar2);
        folderGridView = (GridView) view.findViewById(R.id.folderGridView);
        String path = getActivity().getFilesDir().getAbsolutePath();
        File directory = new File(path);
        File[] files = directory.listFiles();
        folderList.add(new Folder("All Images", R.drawable.ic_baseline_folder_24));
        for (int i = 0; i < files.length; i++) {
            if (!files[i].getName().equals("All Images"))
                folderList.add(new Folder(files[i].getName(), R.drawable.ic_baseline_folder_24));
        }
        folderViewAdapter = new FolderViewAdapter(getActivity(), R.layout.gridview_item, folderList);
        folderGridView.setAdapter(folderViewAdapter);
        folderGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment1 fragment1 = new Fragment1();
                Bundle arguments = new Bundle();
                arguments.putString("foldername", folderList.get(position).getFolderName());
                fragment1.setArguments(arguments);
                assert getFragmentManager() != null;
                FragmentTransaction trans = getFragmentManager()
                        .beginTransaction();
                trans.replace(R.id.emptyLayoutId, fragment1);
                trans.add(R.id.emptyLayoutId, new FragmentBackButton());
                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                trans.addToBackStack(null);
                trans.commit();
            }
        });
        topAppBar2.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu2_folder_setting) {
                    Intent intent = new Intent(getActivity(), Settings.class);
                    startActivity(intent);
                } else if (itemId == R.id.menu2_folder_new) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
                                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                if (clipboard == null) return;
                                ClipData clip = clipboard.getPrimaryClip();
                                if (clip == null) return;
                                ClipData.Item item = clip.getItemAt(0);
                                if (item == null) return;
                                url = item.getText().toString();
                            } else
                                url = urlEditText.getText().toString();
                            String folderPath = getActivity().getFilesDir().getAbsolutePath() + File.separator + url;
                            File myDir = new File(folderPath);
                            if (!myDir.exists() && !myDir.isDirectory())
                                myDir.mkdirs();
                            folderList.add(new Folder(url, R.drawable.ic_baseline_folder_24));
                            folderViewAdapter.setData(folderList);
                            Toast.makeText(getActivity(), "Create folder " + folderPath, Toast.LENGTH_SHORT).show();
                            urlEditText.setText("");
                            alertDialog.cancel();
                        }
                    });
                } else if (itemId == R.id.menu2_folder_pin) {
                    //Do nothing
                }
                ;
                return true;
            }
        });

        return (ViewGroup) view;
    }

    private void popupInputUrlLayout() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        urlView = layoutInflater.inflate(R.layout.import_image_url, null);
        urlEditText = (EditText) urlView.findViewById(R.id.urlEditText);
        urlEditText.setHint("Enter folder name");
        confirmUrlButton = (Button) urlView.findViewById(R.id.confirmUrlButton);
    }
}
