package com.midterm.travelapp_gobuddy;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.midterm.travelapp_gobuddy.databinding.ActivityDetailBinding;

import java.util.ArrayList;
import java.util.Collections;

public class DetailActivity extends AppCompatActivity {
private ActivityDetailBinding binding;
private  ItemModel object;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText("$" + object.getPrice());
        binding.backBtn.setOnClickListener(v -> finish());
        binding.descriptionTxt.setText(object.getDescription());
        binding.addressTxt.setText(object.getAddress());
        binding.ratingBar.setRating((float) object.getScore());
        binding.ratingTxt.setText(object.getScore() + " Rating");
        Glide.with(DetailActivity.this)
                .load(object.getPic())
                .into(binding.pic);

        binding.btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, TicketActivity.class);
                intent.putExtra("object", object);
                startActivity(intent);
            }
        });
    }

    private void getIntentExtra() {
        object = (ItemModel) getIntent().getSerializableExtra("object");
    }
    
}