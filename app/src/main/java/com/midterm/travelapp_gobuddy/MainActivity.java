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

public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterCategory;
    private RecyclerView recyclerViewCategory;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCategory();
        initPopular();

    }

    private void initPopular() {
        DatabaseReference myref=database.getReference("Popular");
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        ArrayList<ItemModel> list=new ArrayList<>();
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot issue : snapshot.getChildren()){
                        list.add(issue.getValue(ItemModel.class));
                    }
                }
                if(!list.isEmpty()){
                    binding.recyclerViewPopular.setLayoutManager(
                            new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL,false)
                    );
                    RecyclerView.Adapter adapter = new PopularAdapter(list);
                    binding.recycleViewPopular.setAdapter(adapter);
                    binding.progressBarPopular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
});
    }

    private void initCategory() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Categories");
        ArrayList<Category> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Category.class));
                    }
                    if (!list.isEmpty()) {
                        recyclerViewCategory = findViewById(R.id.rvCategories);
                        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));


                        adapterCategory = new CategoryAdapter(list);
                        recyclerViewCategory.setAdapter(adapterCategory);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}