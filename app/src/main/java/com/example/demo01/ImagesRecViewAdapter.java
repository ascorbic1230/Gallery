package com.example.demo01;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImagesRecViewAdapter extends RecyclerView.Adapter<ImagesRecViewAdapter.ViewHolder> {

    private ArrayList<Image> images = new ArrayList<>();
    private Context context;
    private boolean isMultiSelectable = false;
    private boolean isSelectAll = false;


    public ImagesRecViewAdapter(Context context) {
        this.context = context;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setMultiSelectable(boolean multiSelectable) {
        setSelectAll(false);
        isMultiSelectable = multiSelectable;
        notifyDataSetChanged();
    }

    public void setSelectAll(boolean isSelectAll) {
        this.isSelectAll = isSelectAll;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_list_layout, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Image " + position + " selected", Toast.LENGTH_SHORT).show();
            }
        });

        // Bật tắt checkbox để chọn hình ảnh
        if (!isMultiSelectable) {
            holder.checkBox.setVisibility(View.GONE);
        }
        else {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (isSelectAll) {
                holder.checkBox.setChecked(images.get(position).isChecked());
                ((MainActivity) context).changeTitleContextualActionBar();

                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        images.get(position).setChecked(holder.checkBox.isChecked());
                        ((MainActivity) context).changeTitleContextualActionBar();
                    }
                });
            }
            else {
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        images.get(position).setChecked(holder.checkBox.isChecked());
                        ((MainActivity) context).changeTitleContextualActionBar();
                    }
                });
            }
        }



        String curPath = images.get(position).getImageUrl();

        Glide.with(context)
                .load(curPath)
                .apply(new RequestOptions().centerCrop())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private RelativeLayout parent;
        private CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            parent = (RelativeLayout) itemView.findViewById(R.id.parent);
            checkBox = (CheckBox) itemView.findViewById(R.id.imgCheckbox);
        }
    }


}
