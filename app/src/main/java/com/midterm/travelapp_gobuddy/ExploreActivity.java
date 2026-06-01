package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;
import com.midterm.travelapp_gobuddy.databinding.ActivityExploreBinding;

import java.util.ArrayList;

public class ExploreActivity extends AppCompatActivity {
    private ActivityExploreBinding binding;
    private ArrayList<ItemModel> allPlaces = new ArrayList<>();
    private ArrayList<ItemModel> filteredPlaces = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>(); // Đưa danh sách danh mục làm biến toàn cục
    private PopularAdapter tourAdapter;
    private LocalCategoryAdapter categoryAdapter;
    private int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExploreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.btnBack.setOnClickListener(v -> finish());

        // Khởi tạo danh sách danh mục trước
        categories.add("Beach");
        categories.add("Mountain");
        categories.add("Adventure");
        categories.add("Camping");
        categories.add("Cultural");

        initExploreCategories();
        initTourRecyclerView();
        loadDataFromFirebase();
        setupBottomMenu();
    }

    private void setupBottomMenu() {
        binding.bottomMenu.post(() -> {
            try {
                binding.bottomMenu.setItemSelected(R.id.explorer, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.bottomMenu.setOnItemSelectedListener(id -> {
            Intent intent = null;
            if (id == R.id.home) {
                intent = new Intent(ExploreActivity.this, MainActivity.class);
            } else if (id == R.id.bookmark) {
                intent = new Intent(ExploreActivity.this, FavoriteActivity.class);
            } else if (id == R.id.profile) {
                intent = new Intent(ExploreActivity.this, ProfileActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void initExploreCategories() {
        binding.rvExploreCategories.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new LocalCategoryAdapter(categories);
        binding.rvExploreCategories.setAdapter(categoryAdapter);
    }

    private void initTourRecyclerView() {
        tourAdapter = new PopularAdapter(filteredPlaces);
        binding.rvExploreTours.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvExploreTours.setAdapter(tourAdapter);
    }

    private void loadDataFromFirebase() {
        FirebaseDatabase.getInstance().getReference("Popular")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allPlaces.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                ItemModel item = data.getValue(ItemModel.class);
                                if (item != null) {
                                    allPlaces.add(item);
                                }
                            }
                        }
                        if (!categories.isEmpty()) {
                            filterTours(categories.get(selectedPosition));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    public void filterTours(String categoryName) {
        filteredPlaces.clear();
        String targetCategory = categoryName.trim().toLowerCase();

        for (ItemModel item : allPlaces) {
            if (item.getCategory() != null) {
                String firebaseCategory = item.getCategory().trim().toLowerCase();
                if (firebaseCategory.equals(targetCategory) || firebaseCategory.contains(targetCategory)) {
                    filteredPlaces.add(item);
                }
            }
        }

        if (filteredPlaces.isEmpty()) {
            // Bạn có thể thêm một cái txtNoData.setVisibility(View.VISIBLE) ở đây nếu có view thông báo
            binding.rvExploreTours.setVisibility(View.GONE);
        } else {
            binding.rvExploreTours.setVisibility(View.VISIBLE);
        }

        if (tourAdapter != null) {
            tourAdapter.notifyDataSetChanged();
        }
    }

    private class LocalCategoryAdapter extends RecyclerView.Adapter<LocalCategoryAdapter.ViewHolder> {
        private ArrayList<String> list;

        public LocalCategoryAdapter(ArrayList<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(45, 25, 45, 25);
            textView.setTextSize(16);
            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String catName = list.get(position);
            TextView txtName = (TextView) holder.itemView;
            txtName.setText(catName);

            if (position == selectedPosition) {
                txtName.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                txtName.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                txtName.setTextColor(getResources().getColor(android.R.color.darker_gray));
                txtName.setTypeface(null, android.graphics.Typeface.NORMAL);
            }

            holder.itemView.setOnClickListener(v -> {
                int currentPos = holder.getAdapterPosition();
                if (currentPos == RecyclerView.NO_POSITION || currentPos == selectedPosition) return;

                int oldPos = selectedPosition;
                selectedPosition = currentPos;

                notifyItemChanged(oldPos);
                notifyItemChanged(selectedPosition);

                filterTours(catName);
            });
        }

        @Override
        public int getItemCount() {
            return list != null ? list.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}