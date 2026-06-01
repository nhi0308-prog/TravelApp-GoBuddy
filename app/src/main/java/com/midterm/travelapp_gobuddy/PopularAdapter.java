package com.midterm.travelapp_gobuddy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.midterm.travelapp_gobuddy.databinding.ViewholderPopularBinding;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private final ArrayList<ItemModel> items;

    public PopularAdapter(ArrayList<ItemModel> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    @NonNull
    @Override
    public PopularAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.ViewHolder holder, int position) {
        ItemModel item = items.get(position);

        // Ưu tiên title, nếu không có thì lấy name
        String title = item.getTitle();
        if (title == null || title.isEmpty()) {
            title = item.getName();
        }

        if (title != null && !title.isEmpty()) {
            holder.binding.titleTxt.setText(title);
        } else {
            holder.binding.titleTxt.setText("No title");
        }

        // Address
        if (item.getAddress() != null && !item.getAddress().isEmpty()) {
            holder.binding.addressTxt.setText(item.getAddress());
        } else {
            holder.binding.addressTxt.setText("No address");
        }

        // Price
        holder.binding.priceTxt.setText("$" + item.getPrice() + "/Tour");

        // Score
        holder.binding.scoreTxt.setText(String.valueOf(item.getScore()));

        // Image
        String imageUrl = "";

        if (item.getPic() != null && !item.getPic().isEmpty()) {
            imageUrl = item.getPic().get(0);
        } else if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            imageUrl = item.getImagePath();
        }

        if (!imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.thumb_1)
                    .error(R.drawable.thumb_1)
                    .into(holder.binding.pic);
        } else {
            holder.binding.pic.setImageResource(R.drawable.thumb_1);
        }

        holder.itemView.setOnClickListener(v -> openDetail(holder, item));
    }

    private void openDetail(ViewHolder holder, ItemModel item) {
        Context context = holder.itemView.getContext();
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("object", item);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(ArrayList<ItemModel> newItems) {
        items.clear();

        if (newItems != null) {
            items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderPopularBinding binding;

        public ViewHolder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}