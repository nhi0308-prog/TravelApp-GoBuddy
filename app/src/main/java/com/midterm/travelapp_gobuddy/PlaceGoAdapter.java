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
    private final ArrayList<PlaceGoModel> items;
    private Context context;

    public PlaceGoAdapter(ArrayList<PlaceGoModel> items) {
        this.items = items != null ? items : new ArrayList<>();
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

        // Hiển thị ảnh địa điểm từ Firebase bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(item.getImagePath())
                .placeholder(R.drawable.intro_background)
                .error(R.drawable.intro_background)
                .into(holder.binding.pic);

        // Mở màn hình chi tiết khi người dùng chọn một địa điểm
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PlaceGoDetailActivity.class);
            intent.putExtra("object_place", item);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderPopularBinding binding;

        public ViewHolder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}