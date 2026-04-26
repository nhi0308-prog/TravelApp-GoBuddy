package com.midterm.travelapp_gobuddy;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.midterm.travelapp_gobuddy.ItemModel;
import com.midterm.travelapp_gobuddy.databinding.ActivityTicketBinding;;

public class TicketActivity extends AppCompatActivity {
    private ActivityTicketBinding binding;
    private ItemModel object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        // Load ảnh chính
        Glide.with(this)
                .load(object.getPic())
                .into(binding.imageView6);

        // Nút Back
        binding.imageView9.setOnClickListener(v -> finish());

        // Thiết lập thông tin text
        binding.textView9.setText(object.getTitle());
        binding.txtGuideNameTop.setText(object.getGuideName()); // Giả định có phương thức này
        binding.txtTime.setText(object.getTime());
        binding.txtDuration.setText(object.getDuration());
        binding.txtGuest.setText(String.valueOf(object.getTotalGuest()));
        binding.txtGuideNameFinal.setText(object.getGuideName());

        // Xử lý sự kiện nút Chat và Call (nếu cần)
        binding.btnChat.setOnClickListener(v -> {
            // Thêm code mở chat tại đây
        });

        binding.btnCall.setOnClickListener(v -> {
            // Thêm code gọi điện tại đây
        });

        binding.btnDownload.setOnClickListener(v -> {
            // Thêm code tải vé tại đây
        });
    }

    private void getIntentExtra() {
        object = (ItemModel) getIntent().getSerializableExtra("object");
    }
}