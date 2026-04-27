package com.midterm.travelapp_gobuddy;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import com.midterm.travelapp_gobuddy.databinding.ActivityMainBinding;
public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterCategory;
    private RecyclerView recyclerViewCategory;
    private FirebaseDatabase database;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ FIX 1: init binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ FIX 2: init database trước
        database = FirebaseDatabase.getInstance();

        initCategory();
        initPopular();
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
                        if (item != null) {
                            list.add(item);
                        }
                    }
                }

                if (!list.isEmpty()) {
                    binding.rvPopular.setLayoutManager(
                            new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false)
                    );

                    RecyclerView.Adapter adapter = new PopularAdapter(list);
                    binding.rvPopular.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                        if (category != null) {
                            list.add(category);
                        }
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