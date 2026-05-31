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
        FirebaseDatabase.getInstance().getReference("Favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        favoriteList.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            ItemModel item = data.getValue(ItemModel.class);
                            if (item != null) {
                                favoriteList.add(item);
                            }
                        }

                        if (favoriteList.isEmpty()) {
                            emptyLayout.setVisibility(View.VISIBLE);
                            favoriteRecyclerView.setVisibility(View.GONE);
                        } else {
                            emptyLayout.setVisibility(View.GONE);
                            favoriteRecyclerView.setVisibility(View.VISIBLE);

                            // Dùng PopularAdapter có sẵn trong project
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