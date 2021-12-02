package com.example.Gallery07;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class FolderViewAdapter extends ArrayAdapter {
    private List<Folder> folderList;

    /*
        public FolderViewAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }
    */
    public FolderViewAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        folderList = objects;
    }
    public void setData(List<Folder> folderList) {
        this.folderList = folderList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        v = inflater.inflate(R.layout.gridview_item, null);
        TextView folderTextView = (TextView) v.findViewById(R.id.folderTextView);
        ImageView folderImageView = (ImageView) v.findViewById(R.id.folderImageView);
        Log.i("Image Text Is ", folderList.get(position).getFolderName());
        folderTextView.setText(folderList.get(position).getFolderName());
        Log.i("Image Is ", folderList.get(position).getFolderImage() + "");
        folderImageView.setImageResource(folderList.get(position).getFolderImage());
        return v;
    }


}