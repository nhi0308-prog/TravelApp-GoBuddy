package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.midterm.travelapp_gobuddy.databinding.ActivityDetailBinding;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private ItemModel object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        setVariable();
    }

    private void getIntentExtra() {
        object = (ItemModel) getIntent().getSerializableExtra("object");
        if (object == null) finish();
    }

    private void setVariable() {
        // --- 1. HIỂN THỊ THÔNG TIN CHỮ ---
        binding.titleTxt.setText(object.getTitle());
        binding.addressTxt.setText(object.getAddress());
        binding.priceTxt.setText("$" + object.getPrice());
        binding.descriptionTxt.setText(object.getDescription());
        binding.ratingTxt.setText(object.getScore() + " Rating");
        binding.ratingBar.setRating((float) object.getScore());
        binding.backBtn.setOnClickListener(v -> finish());

        // --- 2. HIỂN THỊ HÌNH ẢNH & XỬ LÝ LƯỚT THUMBNAIL ---
        ArrayList<String> images = object.getPics(); // Khai báo images ở đây để dùng chung

        if (images != null && !images.isEmpty()) {
            // Load ảnh lớn mặc định là tấm đầu tiên
            Glide.with(this).load(images.get(0)).into(binding.pic);

            // Mảng các ImageView trong XML
            ImageView[] thumbs = {binding.thumb1, binding.thumb2, binding.thumb3, binding.thumb4};

            for (int i = 0; i < thumbs.length; i++) {
                if (i < images.size()) {
                    thumbs[i].setVisibility(View.VISIBLE);

                    // Load ảnh vào từng ô nhỏ (Thumb)
                    Glide.with(this)
                            .load(images.get(i))
                            .centerCrop()
                            .into(thumbs[i]);

                    // Xử lý khi nhấn vào ảnh nhỏ (Dùng finalI để tránh lỗi Lambda)
                    final int finalI = i;
                    thumbs[i].setOnClickListener(v -> {
                        // 1. Đổi ảnh chính khi nhấn
                        Glide.with(DetailActivity.this)
                                .load(images.get(finalI))
                                .into(binding.pic);

                        // 2. Hiệu ứng: Làm mờ các ảnh không chọn
                        for (int j = 0; j < thumbs.length; j++) {
                            if (j == finalI) thumbs[j].setAlpha(1.0f);
                            else thumbs[j].setAlpha(0.4f);
                        }
                    });
                } else {
                    // Nếu Firebase trả về ít hơn 4 ảnh, ẩn các ô dư đi
                    thumbs[i].setVisibility(View.GONE);
                }
            }
        } else if (object.getImagePath() != null) {
            // Nếu không có mảng pics, dùng ImagePath duy nhất
            Glide.with(this).load(object.getImagePath()).into(binding.pic);
            binding.thumbScroll.setVisibility(View.GONE);
        }

        // --- 3. NÚT ĐẶT VÉ ---
        binding.btnBook.setOnClickListener(view -> {
            Intent intent = new Intent(DetailActivity.this, TicketActivity.class);
            intent.putExtra("object", object);
            startActivity(intent);
        });
    }
}