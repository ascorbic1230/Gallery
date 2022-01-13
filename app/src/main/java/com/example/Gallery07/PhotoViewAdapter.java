package com.example.Gallery07;

import static com.example.Gallery07.Utils.utils_fragment1;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import static com.example.Gallery07.Utils.mContext;

public class PhotoViewAdapter extends RecyclerView.Adapter {
    private List<CImage> listImgPaths;
    private RecyclerView parentLayout;
    private boolean isItemClickable = false;

    public void setItemClickable(boolean isItemClickable) {
        this.isItemClickable = isItemClickable;
        notifyDataSetChanged();
    }



    public void setData(List listImgPaths, RecyclerView parent) {
        this.listImgPaths = listImgPaths;
        this.parentLayout = parent;
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == 0) {
            view = layoutInflater.inflate(R.layout.recyclerview_textview, parent, false);
            return new TextViewHolder(view);
        }
        view = layoutInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        CImage item = (CImage) listImgPaths.get(position);
        if (item.getType() == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int tmp = position;
        CImage item = (CImage) listImgPaths.get(position);
        if (item.getType() == 0) {
            TextViewHolder textViewHolder = (TextViewHolder) holder;
            textViewHolder.textView.setText(" Tháng " + item.getMonth() + " Năm " + item.getYear());
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            holder.itemView.setLayoutParams(layoutParams);
        } else {
            PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
            String curPath = ((CImage) listImgPaths.get(tmp)).getImageUri();

            ObjectKey obj = new ObjectKey(System.currentTimeMillis());

            Glide.with(mContext)
                    .load(curPath)
                    .apply(new RequestOptions().centerCrop())
                    .signature(obj)
                    .into(photoViewHolder.imgPhoto);

            if (isItemClickable) {
                ((PhotoViewHolder) holder).imgCheckBox.setVisibility(View.VISIBLE);
                ((PhotoViewHolder) holder).imgCheckBox.setChecked(item.isChecked());
                ((PhotoViewHolder) holder).imgCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.setChecked(((PhotoViewHolder) holder).imgCheckBox.isChecked());
                        utils_fragment1.changeTitleContextualActionBar();
                    }
                });
            } else {
                ((PhotoViewHolder) holder).imgCheckBox.setChecked(false);
                ((PhotoViewHolder) holder).imgCheckBox.setVisibility(View.GONE);
                ((PhotoViewHolder) holder).imgPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, FullscreenImgActivity.class);
                        int newCurPos = 0;
                        List newList = new ArrayList();
                        for (int i = 0 ; i < listImgPaths.size();i++) {
                            if (((CImage)listImgPaths.get(i)).getType() != 0) {
                                newList.add(listImgPaths.get(i));
                            }
                            if (position == i) {
                                newCurPos = newList.size() - 1;
                            }
                        }
                        intent.putExtra("curPos", newCurPos);
                        intent.putExtra( "images", (Serializable) newList);
                        ((MainActivity) mContext).startActivity(intent);
                    }
                });
            }
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        if (listImgPaths != null)
            return listImgPaths.size();
        else
            return 0;

  }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recyclerViewTextView);
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPhoto;
        private CheckBox imgCheckBox;
        private boolean clickable = false;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            imgCheckBox = itemView.findViewById(R.id.imgCheckBox);
        }

        public void setClickable(boolean val) {
            clickable = val;
            if (val) {
                imgCheckBox.setChecked(false);
                imgCheckBox.setVisibility(View.VISIBLE);
            } else
                imgCheckBox.setVisibility(View.INVISIBLE);
        }

        public boolean isClickable() {
            return clickable;
        }

        public void toggleChecked(boolean isChecked) {
            imgCheckBox.setChecked(isChecked);
        }
    }
}