package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
    private PopularAdapter tourAdapter;
    private LocalCategoryAdapter categoryAdapter;
    private int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExploreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initExploreCategories();
        initTourRecyclerView();
        loadDataFromFirebase();

        // ====================================================================
        // ===== ĐÃ SỬA: ÉP NÚT EXPLORER PHÌNH TO VÀ SÁNG XANH THEO ĐÚNG ID =====
        // ====================================================================
        binding.bottomMenu.post(() -> {
            try {
                // Đổi thành R.id.explorer (có chữ r) cho trùng khớp với file menu XML của bạn
                binding.bottomMenu.setItemSelected(R.id.explorer, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Xử lý chuyển trang mượt mà khi bấm các nút khác trên menu
        binding.bottomMenu.setOnItemSelectedListener(id -> {
            if (id == R.id.home) {
                Intent intent = new Intent(ExploreActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Giúp đứng im thanh menu không bị giật màn hình
                finish();
            } else if (id == R.id.bookmark) {
                Intent intent = new Intent(ExploreActivity.this, FavoriteActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            } else if (id == R.id.profile) {
                Intent intent = new Intent(ExploreActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void initExploreCategories() {
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Beach");
        categories.add("Mountain");
        categories.add("Adventure");
        categories.add("Camping");
        categories.add("Cultural");
        categories.add("Luxury");

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
                        filterTours("Beach");
                        // Mặc định hiển thị tất cả lúc mới vào, bấm nút nào lọc nút đó
                        //filteredPlaces.clear();
                        //filteredPlaces.addAll(allPlaces);
                        //f (tourAdapter != null) {
                           // tourAdapter.notifyDataSetChanged();
                        //}
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
            filteredPlaces.addAll(allPlaces);
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
            textView.setPadding(40, 20, 40, 20);
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
                int oldPos = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(oldPos);
                notifyItemChanged(selectedPosition);

                filterTours(catName);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}