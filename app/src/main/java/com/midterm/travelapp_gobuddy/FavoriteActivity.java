package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView favoriteRecyclerView;
    private LinearLayout emptyLayout;
    private ImageView backBtn;
    private ArrayList<ItemModel> favoriteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        favoriteRecyclerView = findViewById(R.id.favoriteRecyclerView);
        emptyLayout = findViewById(R.id.emptyLayout);
        backBtn = findViewById(R.id.backBtn);

        // THÊM BOTTOM MENU
        ChipNavigationBar bottomMenu = findViewById(R.id.bottomMenu);
        bottomMenu.setItemSelected(R.id.bookmark, true);

        bottomMenu.setOnItemSelectedListener(id -> {
            if (id == R.id.home) {
                Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else if (id == R.id.explorer) {
                Intent intent = new Intent(FavoriteActivity.this, ExploreActivity.class);
                startActivity(intent);
                finish();

            } else if (id == R.id.bookmark) {
                // Đang ở trang Saved nên không làm gì

            } else if (id == R.id.profile) {
                Intent intent = new Intent(FavoriteActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backBtn.setOnClickListener(v -> finish());

        favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadFavorites();
    }

    private void loadFavorites() {
        FirebaseDatabase.getInstance()
                .getReference("Favorites")
                .child("info")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        favoriteList.clear();

                        for (DataSnapshot infoSnapshot : snapshot.getChildren()) {

                            ItemModel item = new ItemModel();

                            item.setTitle(infoSnapshot.child("title").getValue(String.class));
                            item.setAddress(infoSnapshot.child("address").getValue(String.class));
                            item.setDescription(infoSnapshot.child("description").getValue(String.class));
                            item.setCategory(infoSnapshot.child("category").getValue(String.class));
                            item.setDuration(infoSnapshot.child("duration").getValue(String.class));
                            String timeTour = infoSnapshot.child("timeTour").getValue(String.class);
                            if (timeTour != null) item.setTimeTour(timeTour);

                            String guideName = infoSnapshot.child("guideName").getValue(String.class);
                            if (guideName != null) item.setGuideName(guideName);
                            Double score = infoSnapshot.child("score").getValue(Double.class);
                            if (score != null) item.setScore(score);

                            Integer price = infoSnapshot.child("price").getValue(Integer.class);
                            if (price != null) item.setPrice(price);

                            Integer id = infoSnapshot.child("Id").getValue(Integer.class);
                            if (id != null) item.setId(id);

                            String imagePath = infoSnapshot.child("ImagePath").getValue(String.class);
                            item.setImagePath(imagePath);

                            ArrayList<String> pics = new ArrayList<>();
                            for (DataSnapshot picSnap : infoSnapshot.child("pics").getChildren()) {
                                String pic = picSnap.getValue(String.class);
                                if (pic != null) pics.add(pic);
                            }
                            if (!pics.isEmpty()) {
                                item.setPics(pics);
                            } else if (imagePath != null) {
                                pics.add(imagePath);
                                item.setPics(pics);
                            }

                            favoriteList.add(item);
                        }

                        if (favoriteList.isEmpty()) {
                            emptyLayout.setVisibility(View.VISIBLE);
                            favoriteRecyclerView.setVisibility(View.GONE);
                        } else {
                            emptyLayout.setVisibility(View.GONE);
                            favoriteRecyclerView.setVisibility(View.VISIBLE);
                            PopularAdapter adapter = new PopularAdapter(favoriteList);
                            favoriteRecyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("FavoriteActivity", error.getMessage());
                    }
                });
    }
}