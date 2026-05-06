package com.midterm.travelapp_gobuddy;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryDetailActivity extends AppCompatActivity {

    TextView txtCategoryTitle;
    RecyclerView rvCategoryImages;

    ArrayList<CategoryPlace> placeList;
    CategoryPlaceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        txtCategoryTitle = findViewById(R.id.txtCategoryTitle);
        rvCategoryImages = findViewById(R.id.rvCategoryImages);

        String categoryName = getIntent().getStringExtra("category_name");

        if (categoryName == null) {
            categoryName = "Hotel";
        }

        txtCategoryTitle.setText(categoryName);

        placeList = new ArrayList<>();

        if (categoryName.equalsIgnoreCase("Hotel")) {
            placeList.add(new CategoryPlace(R.drawable.hotel1, "Luxury Beach Hotel", 5.0f));
            placeList.add(new CategoryPlace(R.drawable.hotel2, "Mountain View Resort", 4.5f));
            placeList.add(new CategoryPlace(R.drawable.hotel3, "City Center Hotel", 4.0f));
            placeList.add(new CategoryPlace(R.drawable.hotel4, "Sunset Paradise Hotel", 4.5f));
            placeList.add(new CategoryPlace(R.drawable.hotel5, "Ocean Pearl Hotel", 4.8f));
            placeList.add(new CategoryPlace(R.drawable.hotel6, "Grand Palace Hotel", 4.7f));
            placeList.add(new CategoryPlace(R.drawable.hotel7, "Blue Sky Resort", 4.6f));
            placeList.add(new CategoryPlace(R.drawable.hotel8, "Golden Lotus Hotel", 4.4f));

        } else if (categoryName.equalsIgnoreCase("Flight")) {
            placeList.add(new CategoryPlace(R.drawable.flight1, "Vietnam Airlines", 5.0f));
            placeList.add(new CategoryPlace(R.drawable.flight2, "Bamboo Airways", 4.5f));
            placeList.add(new CategoryPlace(R.drawable.flight3, "Vietjet Air", 4.0f));
            placeList.add(new CategoryPlace(R.drawable.flight4, "Pacific Airlines", 4.0f));


        } else if (categoryName.equalsIgnoreCase("Place")) {
            placeList.add(new CategoryPlace(R.drawable.place1, "Da Nang Dragon Bridge", 5.0f));
            placeList.add(new CategoryPlace(R.drawable.place2, "Hoi An Ancient Town", 4.5f));
            placeList.add(new CategoryPlace(R.drawable.place3, "Ha Long Bay", 5.0f));
            placeList.add(new CategoryPlace(R.drawable.place4, "Ben Thanh Market", 4.5f));
            placeList.add(new CategoryPlace(R.drawable.place5, "Ba Na Hills", 4.8f));
            placeList.add(new CategoryPlace(R.drawable.place6, "Nha Trang Beach", 4.7f));
            placeList.add(new CategoryPlace(R.drawable.place7, "Phu Quoc Island", 4.9f));
            placeList.add(new CategoryPlace(R.drawable.place8, "Sapa Rice Terraces", 4.6f));

        } else if (categoryName.equalsIgnoreCase("Food")) {
            placeList.add(new CategoryPlace(R.drawable.food1, "Vietnamese Pho", 5.0f));
            placeList.add(new CategoryPlace(R.drawable.food2, "Banh Mi", 4.5f));
            placeList.add(new CategoryPlace(R.drawable.food3, "Seafood Hotpot", 4.0f));
            placeList.add(new CategoryPlace(R.drawable.food4, "Spring Rolls", 4.5f));
            placeList.add(new CategoryPlace(R.drawable.food5, "Mi Quang", 4.6f));
            placeList.add(new CategoryPlace(R.drawable.food6, "Com Tam", 4.4f));
            placeList.add(new CategoryPlace(R.drawable.food7, "Beef Noodle Soup", 4.7f));
            placeList.add(new CategoryPlace(R.drawable.food8, "Grilled Seafood", 4.8f));
        }

        adapter = new CategoryPlaceAdapter(placeList);
        rvCategoryImages.setLayoutManager(new GridLayoutManager(this, 2));
        rvCategoryImages.setAdapter(adapter);
    }
}