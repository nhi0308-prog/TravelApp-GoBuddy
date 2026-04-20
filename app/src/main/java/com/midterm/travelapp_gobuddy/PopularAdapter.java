package com.midterm.travelapp_gobuddy;

import android.content.Context;
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
    ViewholderPopularBinding binding;

    public PopularAdapter(ArrayList<ItemModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PopularAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ViewholderPopularBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        context = parent.getContext();
        return new viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.viewholder holder, int position) {
        binding.titleTxt.setText(items.get(position).getTitle());
        binding.priceTxt.setText("$" + items.get(position).getPrice() + "/Night");
        binding.addressTxt.setText(items.get(position).getAddress());
        binding.scoreTxt.setText("" + items.get(position).getScore());

        Glide.with(context)
                .load(items.get(position).getPic().get(0))
                .into(binding.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        public viewholder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
        }
    }
}