package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ArrayList<Category> items;

    public CategoryAdapter(ArrayList<Category> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_category, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category item = items.get(position);

        holder.titleTxt.setText(item.getName());

        int drawableResourceId = holder.itemView.getContext().getResources()
                .getIdentifier(
                        item.getImagePath(),
                        "drawable",
                        holder.itemView.getContext().getPackageName()
                );

        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .into(holder.picImg);

        holder.itemView.setOnClickListener(v -> openCategoryDetail(holder, item));
        holder.picImg.setOnClickListener(v -> openCategoryDetail(holder, item));
        holder.titleTxt.setOnClickListener(v -> openCategoryDetail(holder, item));
    }

    private void openCategoryDetail(ViewHolder holder, Category item) {
        Intent intent = new Intent(holder.itemView.getContext(), CategoryDetailActivity.class);
        intent.putExtra("category_name", item.getName());
        holder.itemView.getContext().startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView picImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleTxt);
            picImg = itemView.findViewById(R.id.picImg);
        }
    }
}