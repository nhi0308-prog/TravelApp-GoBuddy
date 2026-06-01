package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import com.midterm.travelapp_gobuddy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterCategory;
    private RecyclerView recyclerViewCategory;
    private FirebaseDatabase database;
    private ActivityMainBinding binding;

    // biến cho search
    private ArrayList<ItemModel> allPlaceList = new ArrayList<>();
    private ArrayList<ItemModel> searchResultList = new ArrayList<>();
    private PopularAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        TextView txtHello = findViewById(R.id.txtHello);
        String name = getIntent().getStringExtra("USERNAME");

        if (name != null && !name.isEmpty()) {
            txtHello.setText("Hi, " + name);
        }

        setCategoryButtonClick();

        initCategory();
        initPopular();

        // gọi search
        initSearch();
        loadPlacesForSearch();

        // bấm nút Profile ở bottom menu thì chuyển qua ProfileActivity
        binding.bottomMenu.setOnItemSelectedListener(id -> {
            if (id == R.id.profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            } else if (id == R.id.bookmark) {
                if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(MainActivity.this, "Vui lòng đăng nhập để xem Saved", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
            // ====================================================================
            // ===== CHỎ SỬA LỖI: Kiểm tra an toàn cho nút Explore qua Tên ID =====
            // ====================================================================
            else {
                try {
                    // Lấy tên định danh thực tế của ID được nhấn trong file XML
                    String entryName = getResources().getResourceEntryName(id);

                    // Nếu tên ID chứa chữ "explore" hoặc "explorer", ta mở màn hình ExploreActivity
                    if (entryName.contains("explore")) {
                        Intent intent = new Intent(MainActivity.this, ExploreActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.bottomMenu.setItemSelected(R.id.home, true);
    }

    // setup RecyclerView
    private void initSearch() {
        searchAdapter = new PopularAdapter(searchResultList);

        binding.rvSearchResults.setLayoutManager(
                new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false)
        );
        binding.rvSearchResults.setAdapter(searchAdapter);

        binding.txtSearchResultTitle.setVisibility(View.GONE);
        binding.rvSearchResults.setVisibility(View.GONE);

        binding.btnSearch.setOnClickListener(v -> {
            String keyword = binding.edtSearch.getText().toString().trim();

            if (keyword.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter a destination", Toast.LENGTH_SHORT).show();
                binding.txtSearchResultTitle.setVisibility(View.GONE);
                binding.rvSearchResults.setVisibility(View.GONE);
                return;
            }

            filterPlaces(keyword);
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();

                if (keyword.isEmpty()) {
                    searchResultList.clear();
                    searchAdapter.notifyDataSetChanged();
                    binding.txtSearchResultTitle.setVisibility(View.GONE);
                    binding.rvSearchResults.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // load địa điểm từ Firebase
    private void loadPlacesForSearch() {
        DatabaseReference myRef = database.getReference("Popular");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allPlaceList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        ItemModel item = issue.getValue(ItemModel.class);

                        if (item != null) {
                            allPlaceList.add(item);
                        }
                    }
                }

                String currentKeyword = binding.edtSearch.getText().toString().trim();
                if (!currentKeyword.isEmpty()) {
                    filterPlaces(currentKeyword);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // lọc địa điểm
    private void filterPlaces(String keyword) {
        searchResultList.clear();

        if (keyword == null || keyword.trim().isEmpty()) {
            binding.txtSearchResultTitle.setVisibility(View.GONE);
            binding.rvSearchResults.setVisibility(View.GONE);
            searchAdapter.notifyDataSetChanged();
            return;
        }

        String searchText = keyword.toLowerCase().trim();

        for (ItemModel item : allPlaceList) {
            String title = item.getTitle() != null ? item.getTitle().toLowerCase() : "";
            String name = item.getName() != null ? item.getName().toLowerCase() : "";
            String address = item.getAddress() != null ? item.getAddress().toLowerCase() : "";
            String description = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
            String duration = item.getDuration() != null ? item.getDuration().toLowerCase() : "";
            String distance = item.getDistance() != null ? item.getDistance().toLowerCase() : "";

            if (title.contains(searchText)
                    || name.contains(searchText)
                    || address.contains(searchText)
                    || description.contains(searchText)
                    || duration.contains(searchText)
                    || distance.contains(searchText)) {
                searchResultList.add(item);
            }
        }

        binding.txtSearchResultTitle.setVisibility(View.VISIBLE);
        binding.rvSearchResults.setVisibility(View.VISIBLE);

        if (searchResultList.isEmpty()) {
            binding.txtSearchResultTitle.setText("No results found");
        } else {
            binding.txtSearchResultTitle.setText("Search results");
        }

        searchAdapter.notifyDataSetChanged();
    }

    private void setCategoryButtonClick() {
        binding.layoutHotel.setOnClickListener(v -> openCategory("Hotel"));
        binding.layoutFlight.setOnClickListener(v -> openCategory("Flight"));
        binding.layoutPlace.setOnClickListener(v -> openCategory("Place"));
        binding.layoutFood.setOnClickListener(v -> openCategory("Food"));
    }

    private void openCategory(String categoryName) {
        Intent intent = new Intent(MainActivity.this, CategoryDetailActivity.class);
        intent.putExtra("category_name", categoryName);
        startActivity(intent);
    }

    private void initPopular() {
        DatabaseReference myref = database.getReference("Popular");

        binding.progressBarPopular.setVisibility(View.VISIBLE);
        ArrayList<ItemModel> list = new ArrayList<>();

        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        ItemModel item = issue.getValue(ItemModel.class);
                        if (item != null) list.add(item);
                    }
                }

                if (!list.isEmpty()) {
                    binding.rvPopular.setLayoutManager(
                            new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false)
                    );
                    binding.rvPopular.setAdapter(new PopularAdapter(list));
                }

                binding.progressBarPopular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBarPopular.setVisibility(View.GONE);
            }
        });
    }

    private void initCategory() {
        DatabaseReference myRef = database.getReference("Categories");
        ArrayList<Category> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Category category = issue.getValue(Category.class);
                        if (category != null) list.add(category);
                    }

                    if (!list.isEmpty()) {
                        recyclerViewCategory = findViewById(R.id.rvCategories);

                        recyclerViewCategory.setLayoutManager(
                                new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false)
                        );

                        adapterCategory = new CategoryAdapter(list);
                        recyclerViewCategory.setAdapter(adapterCategory);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}