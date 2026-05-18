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
        // --- 1. HIỂN THỊ THÔNG TIN CHỮ GIỮ NGUYÊN ---
        binding.titleTxt.setText(object.getTitle());
        binding.addressTxt.setText(object.getAddress());
        binding.priceTxt.setText("$" + object.getPrice());
        binding.descriptionTxt.setText(object.getDescription());
        binding.ratingTxt.setText(object.getScore() + " Rating");
        binding.ratingBar.setRating((float) object.getScore());
        binding.backBtn.setOnClickListener(v -> finish());

        // --- 2. HIỂN THỊ HÌNH ẢNH & XỬ LÝ LƯỚT THUMBNAIL GIỮ NGUYÊN ---
        ArrayList<String> images = object.getPics();

        if (images != null && !images.isEmpty()) {
            Glide.with(this).load(images.get(0)).into(binding.pic);

            ImageView[] thumbs = {binding.thumb1, binding.thumb2, binding.thumb3, binding.thumb4};

            for (int i = 0; i < thumbs.length; i++) {
                if (i < images.size()) {
                    thumbs[i].setVisibility(View.VISIBLE);

                    Glide.with(this)
                            .load(images.get(i))
                            .centerCrop()
                            .into(thumbs[i]);

                    final int finalI = i;
                    thumbs[i].setOnClickListener(v -> {
                        Glide.with(DetailActivity.this)
                                .load(images.get(finalI))
                                .into(binding.pic);

                        for (int j = 0; j < thumbs.length; j++) {
                            if (j == finalI) thumbs[j].setAlpha(1.0f);
                            else thumbs[j].setAlpha(0.4f);
                        }
                    });
                } else {
                    thumbs[i].setVisibility(View.GONE);
                }
            }
        } else if (object.getImagePath() != null) {
            Glide.with(this).load(object.getImagePath()).into(binding.pic);
            binding.thumbScroll.setVisibility(View.GONE);
        }

        // ====================================================================
        // --- 3. CẬP NHẬT MỚI: XỬ LÝ CHỌN THÔNG TIN ĐẶT VÉ ĐỘNG ---
        // ====================================================================

        // Khởi tạo các mảng lưu giá trị (Dùng mảng 1 phần tử để tránh lỗi khi gọi trong Lambda)
        final int[] guestCount = {2}; // Số lượng khách mặc định khởi tạo bằng 2 giống XML cũ của bạn
        final String[] selectedTime = {"08:30 AM"}; // Thời gian mặc định

        // Thiết lập số lượng khách ban đầu lên TextView
        binding.txtGuestCount.setText(String.valueOf(guestCount[0]));

        // Hiển thị thời gian Trip Duration từ Firebase (Nếu Firebase trống thì để mặc định 3 Days)
        if (object.getDuration() != null && !object.getDuration().isEmpty()) {
            binding.txtDurationDetail.setText(object.getDuration());
        } else {
            binding.txtDurationDetail.setText("3 Days");
        }

        // Sự kiện click nút giảm số lượng khách [-]
        binding.btnMinusGuest.setOnClickListener(v -> {
            if (guestCount[0] > 1) { // Giới hạn tối thiểu là 1 khách đi tour
                guestCount[0]--;
                binding.txtGuestCount.setText(String.valueOf(guestCount[0]));
            }
        });

        // Sự kiện click nút tăng số lượng khách [+]
        binding.btnPlusGuest.setOnClickListener(v -> {
            guestCount[0]++;
            binding.txtGuestCount.setText(String.valueOf(guestCount[0]));
        });

        // Sự kiện mở TimePickerDialog chọn giờ hệ thống
        binding.btnSelectTime.setOnClickListener(v -> {
            android.app.TimePickerDialog timePickerDialog = new android.app.TimePickerDialog(this,
                    (view1, hourOfDay, minute) -> {
                        String amPm = (hourOfDay >= 12) ? " PM" : " AM";
                        int hour = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
                        if (hour == 0) hour = 12;

                        selectedTime[0] = String.format("%02d:%02d%s", hour, minute, amPm);
                        binding.btnSelectTime.setText(selectedTime[0]);
                    }, 8, 30, false); // Mở ra hiển thị mặc định 08:30 sáng
            timePickerDialog.show();
        });

        // Khởi tạo danh sách mảng dữ liệu Hướng dẫn viên
        String[] guideNames = {"Emily Waston", "David Beckham", "Sumire", "Alex Ferguson"};
        String[] guidePhones = {"0905123456", "0914999999", "0935888888", "0888777666"};

        // Đổ danh sách hướng dẫn viên vào Spinner thả xuống
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, guideNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGuides.setAdapter(adapter);

        // ====================================================================
        // --- 4. CẬP NHẬT MỚI: ĐÓNG GÓI VÀ CHUYỂN DỮ LIỆU SANG TICKET ---
        // ====================================================================
        binding.btnBook.setOnClickListener(view -> {
            // Lấy vị trí hướng dẫn viên đang được lựa chọn trên Spinner
            int selectedIndex = binding.spinnerGuides.getSelectedItemPosition();

            // Set ngược các giá trị đã chọn vào trong "object" trước khi gửi đi
            object.setGuideName(guideNames[selectedIndex]);
            object.setTourGuidePhone(guidePhones[selectedIndex]);
            object.setTimeTour(selectedTime[0]);
            object.setTotalGuest(guestCount[0]);
            object.setDuration(binding.txtDurationDetail.getText().toString());

            // Thực hiện Intent chuyển tiếp sang màn hình TicketActivity
            Intent intent = new Intent(DetailActivity.this, TicketActivity.class);
            intent.putExtra("object", object);
            startActivity(intent);
        });
    }
}