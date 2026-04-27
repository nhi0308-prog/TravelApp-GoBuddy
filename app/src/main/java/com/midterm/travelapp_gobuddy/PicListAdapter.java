package com.midterm.travelapp_gobuddy;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.midterm.travelapp_gobuddy.databinding.ViewholderPicListBinding;

import java.util.List;

public class PicListAdapter extends RecyclerView.Adapter<PicListAdapter.ViewHolder>{
    private List<String> item;
    private ImageView picMain;
    private Context context;

    public PicListAdapter(List<String> item, ImageView picMain) {
        this.item = item;
        this.picMain = picMain;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderPicListBinding binding=ViewholderPicListBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false

        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load(item.get(position))
                .into(picMain);

        holder.binding.getRoot().setOnClickListener(view -> Glide.with(context)
                .load(item.get(position))
                .into(picMain));

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderPicListBinding binding;
        public ViewHolder(ViewholderPicListBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
