package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import com.midterm.travelapp_gobuddy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterCategory;
    private RecyclerView recyclerViewCategory;
    private FirebaseDatabase database;
    private ActivityMainBinding binding;

    private FirebaseAuth mAuth;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        binding.bottomMenu.setItemSelected(R.id.home, true);

        TextView txtHello = findViewById(R.id.txtHello);
        TextView txtHomeAvatarLetter = findViewById(R.id.txtHomeAvatarLetter);

        name = getIntent().getStringExtra("USERNAME");

        if (name != null && !name.isEmpty()) {
            txtHello.setText("Hi, " + name + " 👋");
            setHomeAvatarLetter(txtHomeAvatarLetter, name);
        } else {
            loadUserName(txtHello, txtHomeAvatarLetter);
        }

        binding.bottomMenu.setOnItemSelectedListener(id -> {
            if (id == R.id.profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            } else if (id == R.id.home) {
                binding.bottomMenu.setItemSelected(R.id.home, true);
            }
        });

        setCategoryButtonClick();

        initCategory();
        initPopular();
    }

    private void loadUserName(TextView txtHello, TextView txtHomeAvatarLetter) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            txtHello.setText("Hi, Guest 👋");
            txtHomeAvatarLetter.setText("U");
            return;
        }

        String userId = currentUser.getUid();

        database.getReference("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String firebaseName = snapshot.child("name").getValue(String.class);

                        if (firebaseName == null || firebaseName.trim().isEmpty()) {
                            String lastName = snapshot.child("lastName").getValue(String.class);
                            String firstName = snapshot.child("firstName").getValue(String.class);

                            if (lastName == null) lastName = "";
                            if (firstName == null) firstName = "";

                            firebaseName = (lastName + " " + firstName).trim();
                        }

                        if (firebaseName == null || firebaseName.trim().isEmpty()) {
                            txtHello.setText("Hi, Guest 👋");
                            txtHomeAvatarLetter.setText("U");
                        } else {
                            name = firebaseName;
                            txtHello.setText("Hi, " + firebaseName + " 👋");
                            setHomeAvatarLetter(txtHomeAvatarLetter, firebaseName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        txtHello.setText("Hi, Guest 👋");
                        txtHomeAvatarLetter.setText("U");
                    }
                });
    }

    private void setHomeAvatarLetter(TextView avatarTextView, String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            avatarTextView.setText("U");
            return;
        }

        String[] parts = fullName.trim().split("\\s+");
        String avatarText = "";

        if (parts.length >= 2) {
            avatarText = parts[0].substring(0, 1).toUpperCase()
                    + parts[parts.length - 1].substring(0, 1).toUpperCase();
        } else {
            avatarText = parts[0].substring(0, 1).toUpperCase();
        }

        avatarTextView.setText(avatarText);
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