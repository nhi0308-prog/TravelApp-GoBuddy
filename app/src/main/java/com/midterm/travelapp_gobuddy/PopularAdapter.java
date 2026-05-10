package com.midterm.travelapp_gobuddy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.midterm.travelapp_gobuddy.databinding.ViewholderPopularBinding;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.viewholder> {
    ArrayList<ItemModel> items;
    Context context;

    public PopularAdapter(ArrayList<ItemModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PopularAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );

        context = parent.getContext();

        return new viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.viewholder holder, int position) {
        ItemModel item = items.get(position);

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.priceTxt.setText("$" + item.getPrice() + "/Night");
        holder.binding.addressTxt.setText(item.getAddress());
        holder.binding.scoreTxt.setText("" + item.getScore());

        if (item.getPic() != null && !item.getPic().isEmpty()) {
            Glide.with(context)
                    .load(item.getPic().get(0))
                    .into(holder.binding.pic);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", item);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        ViewholderPopularBinding binding;

        public viewholder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}