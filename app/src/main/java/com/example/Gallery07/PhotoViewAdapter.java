package com.example.Gallery07;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.util.List;

public class PhotoViewAdapter extends RecyclerView.Adapter<PhotoViewAdapter.PhotoViewHolder> {
    private List<String> listImgPaths;
    private Context mContext;
    private boolean isItemClickable = false;

    public void setItemClickable(boolean isItemClickable) {
        this.isItemClickable = isItemClickable;
        notifyDataSetChanged();
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<String> listImgPaths) {
        this.listImgPaths = listImgPaths;
        notifyDataSetChanged();
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String curPath = listImgPaths.get(position);
        Log.i("curpath", curPath);
        ObjectKey obj = new ObjectKey(System.currentTimeMillis());

        Glide.with(mContext)
                .load(curPath)
                .signature(obj)
                .into(holder.imgPhoto);
        if (isItemClickable)
            holder.setClickable(true);
        else
            holder.setClickable(false);

        holder.imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isClickable()) {
                    holder.toggleChecked();
                } else {
                    Intent intent = new Intent(mContext, FullscreenImgActivity.class);
                    intent.putExtra("curPath", curPath);
                    ((MainActivity) mContext).startActivity(intent);
                }

            }
        });
    }

    // total number of cells
    @Override
    public int getItemCount() {
        if (listImgPaths != null)
            return listImgPaths.size();
        else
            return 0;
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPhoto;
        private CheckBox imgCheckBox;
        private boolean clickable = false;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            imgCheckBox = itemView.findViewById(R.id.imgCheckBox);
            imgCheckBox.setVisibility(View.INVISIBLE);
        }

        ;

        public void setClickable(boolean val) {
            clickable = val;
            if (val == true) {
                imgCheckBox.setChecked(false);
                imgCheckBox.setVisibility(View.VISIBLE);
            } else
                imgCheckBox.setVisibility(View.INVISIBLE);
        }

        public boolean isClickable() {
            return clickable;
        }

        public void toggleChecked() {
            if (imgCheckBox.isChecked())
                imgCheckBox.setChecked(false);
            else
                imgCheckBox.setChecked(true);
        }


        public boolean isChecked() {
            return imgCheckBox.isChecked();
        }
    }
}