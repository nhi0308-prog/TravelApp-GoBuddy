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

public class PlaceGoAdapter extends RecyclerView.Adapter<PlaceGoAdapter.ViewHolder> {
    private ArrayList<PlaceGoModel> items;
    private Context context;

    public PlaceGoAdapter(ArrayList<PlaceGoModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaceGoModel item = items.get(position);

        // Hiển thị tên địa điểm
        holder.binding.titleTxt.setText(item.getName());

        // Dùng Glide kéo hình từ link ImagePath trên Firebase
        Glide.with(context)
                .load(item.getImagePath())
                .into(holder.binding.pic);

        // Sự kiện Click: Gói dữ liệu và mở trang chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlaceGoDetailActivity.class);
            intent.putExtra("object_place", item);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderPopularBinding binding;

        public ViewHolder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}