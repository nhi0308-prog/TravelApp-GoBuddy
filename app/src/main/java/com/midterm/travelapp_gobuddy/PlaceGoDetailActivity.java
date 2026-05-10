package com.midterm.travelapp_gobuddy;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.midterm.travelapp_gobuddy.databinding.ActivityDetailBinding;

public class PlaceGoDetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nhận dữ liệu dựa trên cái tên "object_place" từ Adapter gửi sang
        PlaceGoModel place = (PlaceGoModel) getIntent().getSerializableExtra("object_place");

        if (place != null) {
            // Hiển thị tên (Đảm bảo ID trong activity_detail.xml là titleTxt)
            binding.titleTxt.setText(place.getName());

            // Hiển thị đúng hình của địa điểm đã click (ID trong xml là pic)
            Glide.with(this)
                    .load(place.getImagePath())
                    .into(binding.pic);
        }
    }
}