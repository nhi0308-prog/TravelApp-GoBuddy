package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.HashSet;
import java.util.Set;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView favoriteRecyclerView;
    private LinearLayout emptyLayout;
    private ImageView backBtn;
    private TextView btnDeleteMode;
    private ArrayList<ItemModel> favoriteList = new ArrayList<>();

    // Chế độ chọn để xóa
    private boolean isSelectionMode = false;
    private Set<Integer> selectedPositions = new HashSet<>();
    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        favoriteRecyclerView = findViewById(R.id.favoriteRecyclerView);
        emptyLayout = findViewById(R.id.emptyLayout);
        backBtn = findViewById(R.id.backBtn);
        btnDeleteMode = findViewById(R.id.btnDeleteMode);

        // BOTTOM MENU
        ChipNavigationBar bottomMenu = findViewById(R.id.bottomMenu);
        bottomMenu.setItemSelected(R.id.bookmark, true);

        bottomMenu.setOnItemSelectedListener(id -> {
            if (id == R.id.home) {
                startActivity(new Intent(FavoriteActivity.this, MainActivity.class));
                finish();
            } else if (id == R.id.explorer) {
                startActivity(new Intent(FavoriteActivity.this, ExploreActivity.class));
                finish();
            } else if (id == R.id.profile) {
                startActivity(new Intent(FavoriteActivity.this, ProfileActivity.class));
                finish();
            }
        });

        // Nút back
        backBtn.setOnClickListener(v -> {
            if (isSelectionMode) {
                exitSelectionMode();
            } else {
                finish();
            }
        });

        // Nút thùng rác
        btnDeleteMode.setOnClickListener(v -> {
            if (!isSelectionMode) {
                // Vào chế độ chọn
                enterSelectionMode();
            } else {
                // Đang chọn → xóa hoặc thoát
                if (!selectedPositions.isEmpty()) {
                    deleteSelectedItems();
                } else {
                    exitSelectionMode();
                }
            }
        });

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

                            adapter = new FavoriteAdapter(
                                    favoriteList,
                                    // Click thường
                                    (item, position) -> {
                                        if (isSelectionMode) {
                                            toggleSelection(position);
                                        } else {
                                            Intent intent = new Intent(FavoriteActivity.this, DetailActivity.class);
                                            intent.putExtra("object", item);
                                            startActivity(intent);
                                        }
                                    },
                                    // Long press
                                    (item, position) -> {
                                        if (!isSelectionMode) enterSelectionMode();
                                        toggleSelection(position);
                                    }
                            );
                            favoriteRecyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("FavoriteActivity", error.getMessage());
                    }
                });
    }

    private void enterSelectionMode() {
        isSelectionMode = true;
        btnDeleteMode.setTextColor(0xFFFF3B30); // đỏ khi đang chọn
        if (adapter != null) adapter.setSelectionMode(true);
    }

    private void exitSelectionMode() {
        isSelectionMode = false;
        selectedPositions.clear();
        btnDeleteMode.setTextColor(0xFF1A1A1A); // về màu bình thường
        if (adapter != null) {
            adapter.setSelectionMode(false);
            adapter.clearSelection();
        }
    }

    private void toggleSelection(int position) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position);
        } else {
            selectedPositions.add(position);
        }
        if (adapter != null) {
            adapter.setSelectedPositions(selectedPositions);
        }
        if (selectedPositions.isEmpty()) {
            exitSelectionMode();
        }
    }

    private void deleteSelectedItems() {
        for (int pos : selectedPositions) {
            if (pos < favoriteList.size()) {
                String tourKey = favoriteList.get(pos).getTitle()
                        .replaceAll("[^a-zA-Z0-9]", "_");

                FirebaseDatabase.getInstance()
                        .getReference("Favorites")
                        .child("info")
                        .child(tourKey)
                        .removeValue();

                FirebaseDatabase.getInstance()
                        .getReference("Favorites")
                        .child("booking")
                        .child(tourKey)
                        .removeValue();
            }
        }
        Toast.makeText(this, "Đã xóa " + selectedPositions.size() + " tour", Toast.LENGTH_SHORT).show();
        exitSelectionMode();
    }
}