package com.example.Gallery07;

import static com.example.Gallery07.Utils.mContext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FolderViewAdapter extends ArrayAdapter {
    private List folderList;
    private boolean isItemClickable = false;

    public FolderViewAdapter(int textViewResourceId, ArrayList objects) {
        super(mContext, textViewResourceId, objects);
        folderList = objects;
    }

    public void setFolderList(List<CFolder> folderList) {
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
        folderTextView.setText(((CFolder) folderList.get(position)).getFolderName());
        folderImageView.setImageResource(((CFolder) folderList.get(position)).getFolderImage());
        if (position == folderList.size() - 1)
            return v;
        if (isItemClickable)
            folderImageButton.setVisibility(View.VISIBLE);
        else
            folderImageButton.setVisibility(View.GONE);
        folderImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                // Set a title for alert dialog
                builder.setTitle(((CFolder) folderList.get(position)).getFolderName() +" will be deleted!");

                // Ask the final question
                builder.setMessage("Are you sure you want to delete this folder?");

                // Set the alert dialog yes button click listener
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when user clicked the Yes button
                        File fi = new File(mContext.getFilesDir().getAbsolutePath() + File.separator + ((CFolder) folderList.get(position)).getFolderName());
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
        });
        return v;
    }


}