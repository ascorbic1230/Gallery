package com.example.demo01;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class FoldersTab extends Fragment {

    private String mParam1;


    public FoldersTab() {
        // Required empty public constructor
        super(R.layout.fragment_folders_tab);
    }

    public static FoldersTab newInstance(String param1) {
        FoldersTab fragment = new FoldersTab();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("param1");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout layout_fragment = (FrameLayout) inflater.inflate(R.layout.fragment_folders_tab, container, false);


        return layout_fragment;
    }
}