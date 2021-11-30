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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;


public class Fragment2 extends Fragment {
    private Button allImagesButton;
    private Button folder1Button;
    private Button folder2Button;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        allImagesButton = (Button) view.findViewById(R.id.allImagesButton);
        folder1Button = (Button) view.findViewById(R.id.folder1Button);
        folder2Button = (Button) view.findViewById(R.id.folder2Button);
        allImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment1 fragment1 = new Fragment1();
                Bundle arguments = new Bundle();
                arguments.putString("foldernum", "AllImages");
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
        folder1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment1 fragment1 = new Fragment1();
                Bundle arguments = new Bundle();
                arguments.putString("foldernum", "Folder1");
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
        folder2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment1 fragment1 = new Fragment1();
                Bundle arguments = new Bundle();
                arguments.putString("foldernum", "Folder2");
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
        return (ViewGroup) view;
    }
}
