package com.midterm.travelapp_gobuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CategoryPlaceAdapter extends RecyclerView.Adapter<CategoryPlaceAdapter.ViewHolder> {

    private ArrayList<CategoryPlace> items;

    public CategoryPlaceAdapter(ArrayList<CategoryPlace> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryPlaceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_category_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryPlaceAdapter.ViewHolder holder, int position) {
        CategoryPlace item = items.get(position);

        holder.txtPlaceName.setText(item.getName());
        holder.ratingBar.setRating(item.getRating());

        Glide.with(holder.itemView.getContext())
                .load(item.getImage())
                .into(holder.imgPlace);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPlace;
        TextView txtPlaceName;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPlace = itemView.findViewById(R.id.imgPlace);
            txtPlaceName = itemView.findViewById(R.id.txtPlaceName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}