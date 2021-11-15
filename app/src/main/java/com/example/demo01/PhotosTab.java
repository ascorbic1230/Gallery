package com.example.demo01;

import android.content.Context;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import java.util.ArrayList;

public class PhotosTab extends Fragment {

    private String mParam1;
    private Context context;
    private RecyclerView imageRecView;
    private ImagesManager imagesManager;

    public PhotosTab() {
        super(R.layout.fragment_photos_tab);
        // Required empty public constructor
    }

    public static PhotosTab newInstance(String param1) {
        PhotosTab fragment = new PhotosTab();
        Bundle args = new Bundle();
        args.putString(param1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("param1");
        }
        context = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout fragment_layout = (FrameLayout) inflater.inflate(R.layout.fragment_photos_tab, container, false);
        imageRecView = (RecyclerView) fragment_layout.findViewById(R.id.imageRecyclerView);

        imagesManager = new ImagesManager(context);
        imagesManager.setImagesRecView(imageRecView);


        return fragment_layout;
    }

    public void onMsgFromMainToPhotosTabFragment(String value) {
        if (value.equals("SELECT ITEMS")) {
            imagesManager.setSelectMultipleImages(true);
        }
        else if (value.equals("TURN OFF SELECT ITEMS")) {
            imagesManager.setSelectMultipleImages(false);
            imagesManager.unSelectAllImages();
        }
        else if (value.equals("CHANGE CONTEXTUAL ACTION BAR")) {
            int numberSelectedImages = imagesManager.getNumberOfSelectedImages();
            String title = "";
            if (numberSelectedImages > 0) {
                title += numberSelectedImages;
            }
            else if (numberSelectedImages == 0) {
                title += "Select items";
            }
            ((MainActivity) context).onMsgFromPhotosTabFragmentToMain("CHANGE TITLE", title);
        }
        else if (value.equals("SELECT ALL")) {
            imagesManager.selectAllImages();
        }

    }


}