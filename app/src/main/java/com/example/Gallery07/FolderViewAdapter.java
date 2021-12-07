package com.example.Gallery07;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FolderViewAdapter extends ArrayAdapter {
    private List folderList;
    private Context mContext;
    private boolean isItemClickable = false;

    public FolderViewAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        mContext = context;
        folderList = objects;
    }

    public void setFolderList(List<Folder> folderList) {
        this.folderList = folderList;
        notifyDataSetChanged();
    }

    public void setItemClickable(boolean isItemClickable) {
        this.isItemClickable = isItemClickable;
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
        ImageButton folderImageButton = (ImageButton) v.findViewById(R.id.folderImageButton);
        if (isItemClickable) {
            folderImageButton.setVisibility(View.VISIBLE);

        } else {
            folderImageButton.setVisibility(View.GONE);
        }
        folderImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File fi = new File(mContext.getFilesDir().getAbsolutePath() + File.separator + ((Folder) folderList.get(position)).getFolderName());
                if (fi.isDirectory()) {
                    String[] children = fi.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(fi, children[i]).delete();
                    }
                    fi.delete();
                }
                folderList.remove(position);
                notifyDataSetChanged();
            }
        });
        folderTextView.setText(((Folder) folderList.get(position)).getFolderName());
        folderImageView.setImageResource(((Folder) folderList.get(position)).getFolderImage());
        return v;
    }


}