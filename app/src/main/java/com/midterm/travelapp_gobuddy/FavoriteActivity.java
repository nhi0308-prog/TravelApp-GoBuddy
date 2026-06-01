package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView favoriteRecyclerView;
    private LinearLayout emptyLayout;
    private ImageView backBtn;

    private ArrayList<ItemModel> favoriteList = new ArrayList<>();
    private PopularAdapter adapter; // Khai báo Adapter làm biến toàn cục

    private DatabaseReference favoritesRef;
    private ValueEventListener favoritesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // Ánh xạ View
        favoriteRecyclerView = findViewById(R.id.favoriteRecyclerView);
        emptyLayout = findViewById(R.id.emptyLayout);
        backBtn = findViewById(R.id.backBtn);

        // Thiết lập RecyclerView và Adapter trước
        favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PopularAdapter(favoriteList);
        favoriteRecyclerView.setAdapter(adapter);

        // Thiết lập Bottom Menu
        setupBottomMenu();

        // Nút quay lại
        backBtn.setOnClickListener(v -> finish());

        // Bắt đầu tải dữ liệu từ Firebase
        loadFavorites();
    }

    private void setupBottomMenu() {
        ChipNavigationBar bottomMenu = findViewById(R.id.bottomMenu);
        bottomMenu.setItemSelected(R.id.bookmark, true);

        bottomMenu.setOnItemSelectedListener(id -> {
            Intent intent = null;
            if (id == R.id.home) {
                intent = new Intent(FavoriteActivity.this, MainActivity.class);
            } else if (id == R.id.explorer) {
                intent = new Intent(FavoriteActivity.this, ExploreActivity.class);
            } else if (id == R.id.profile) {
                intent = new Intent(FavoriteActivity.this, ProfileActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                finish();
                // Thêm hiệu ứng chuyển trang mượt mà nếu muốn (Tùy chọn)
                overridePendingTransition(0, 0);
            }
        });
    }

    private void loadFavorites() {
        favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child("info");

        favoritesListener = new ValueEventListener() {
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

                    // Xử lý danh sách ảnh phụ (pics)
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

                // Cập nhật giao diện dựa trên danh sách dữ liệu
                if (favoriteList.isEmpty()) {
                    emptyLayout.setVisibility(View.VISIBLE);
                    favoriteRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyLayout.setVisibility(View.GONE);
                    favoriteRecyclerView.setVisibility(View.VISIBLE);

                    // Chỉ thông báo cập nhật dữ liệu thay vì tạo mới Adapter
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FavoriteActivity", "Firebase Error: " + error.getMessage());
            }
        };

        // Gắn listener vào database
        favoritesRef.addValueEventListener(favoritesListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy lắng nghe dữ liệu khi Activity bị hủy để tránh rò rỉ bộ nhớ (Memory Leak)
        if (favoritesRef != null && favoritesListener != null) {
            favoritesRef.removeEventListener(favoritesListener);
        }
    }
}