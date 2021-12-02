package com.example.Gallery07;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.util.ArrayList;


public class Fragment2 extends Fragment {
    private MaterialToolbar topAppBar2;
    private Button formConfirmButton;
    private View formView;
    private EditText formEditText;
    private ArrayList folderList;
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
                arguments.putString("foldername", ((Folder) folderList.get(position)).getFolderName());
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
        topAppBar2.getMenu().findItem(R.id.menu2_folder_delete_cancel).setVisible(false);
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
                    alertDialogBuilder.setView(formView);
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    formConfirmButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String form = "";
                            if (TextUtils.isEmpty(formEditText.getText())) {
                                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                if (clipboard == null) return;
                                ClipData clip = clipboard.getPrimaryClip();
                                if (clip == null) return;
                                ClipData.Item item = clip.getItemAt(0);
                                if (item == null) return;
                                form = item.getText().toString();
                            } else
                                form = formEditText.getText().toString();
                            String folderPath = getActivity().getFilesDir().getAbsolutePath() + File.separator + form;
                            File myDir = new File(folderPath);
                            if (!myDir.exists() && !myDir.isDirectory())
                                myDir.mkdirs();
                            folderList.add(new Folder(form, R.drawable.ic_baseline_folder_24));
                            folderViewAdapter.setFolderList(folderList);
                            Toast.makeText(getActivity(), "Create folder " + folderPath, Toast.LENGTH_SHORT).show();
                            formEditText.setText("");
                            alertDialog.cancel();
                        }
                    });
                } else if (itemId == R.id.menu2_folder_pin) {
                    //Do nothing
                } else if (itemId == R.id.menu2_folder_delete) {
                    folderViewAdapter.setItemClickable(true);
                    topAppBar2.getMenu().findItem(R.id.menu2_folder_delete).setVisible(false);
                    topAppBar2.getMenu().findItem(R.id.menu2_folder_delete_cancel).setVisible(true);
                } else if (itemId == R.id.menu2_folder_delete_cancel) {
                    folderViewAdapter.setItemClickable(false);
                    topAppBar2.getMenu().findItem(R.id.menu2_folder_delete).setVisible(true);
                    topAppBar2.getMenu().findItem(R.id.menu2_folder_delete_cancel).setVisible(false);
                }
                ;
                return true;
            }
        });

        return (ViewGroup) view;
    }

    private void popupInputUrlLayout() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        formView = layoutInflater.inflate(R.layout.form_new_folder, null);
        formEditText = (EditText) formView.findViewById(R.id.formEditText);
        formEditText.setHint("Enter folder name");
        formConfirmButton = (Button) formView.findViewById(R.id.formCancelButton);
    }
}
