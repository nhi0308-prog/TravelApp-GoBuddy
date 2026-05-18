package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
        // --- 3. CẬP NHẬT: XỬ LÝ CHỌN THÔNG TIN ĐẶT VÉ ĐỘNG ---
        // ====================================================================

        // Khởi tạo các mảng lưu giá trị (Dùng mảng 1 phần tử để tránh lỗi khi gọi trong Lambda)
        final int[] guestCount = {2}; // Số lượng khách mặc định khởi tạo bằng 2 giống XML
        final String[] selectedTime = {"08:30 AM"}; // Thời gian mặc định

        // Thiết lập số lượng khách ban đầu lên TextView
        binding.txtGuestCount.setText(String.valueOf(guestCount[0]));

        // Hiển thị thời gian Trip Duration từ Firebase (Nếu Firebase trống thì hiển thị Chưa cập nhật)
        if (object.getDuration() != null && !object.getDuration().isEmpty()) {
            binding.txtDurationDetail.setText(object.getDuration());
        } else {
            binding.txtDurationDetail.setText("Chưa cập nhật");
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

        // --- ĐỌC DANH SÁCH TOUR GUIDE ĐỘNG TỪ NODE "Guides" TRÊN FIREBASE ---
        ArrayList<String> guideNames = new ArrayList<>();
        ArrayList<String> guidePhones = new ArrayList<>();

        // Tạo Adapter cho Spinner (lúc này mảng guideNames đang rỗng)
        android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, guideNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGuides.setAdapter(spinnerAdapter);

        // Lắng nghe dữ liệu từ node "Guides" trên Firebase Realtime Database
        FirebaseDatabase.getInstance().getReference("Guides")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        guideNames.clear();
                        guidePhones.clear();

                        // Duyệt qua từng guide_01, guide_02,... để lấy thông tin
                        for (DataSnapshot data : snapshot.getChildren()) {
                            ItemModel guideObject = data.getValue(ItemModel.class);
                            if (guideObject != null) {
                                guideNames.add(guideObject.getGuideName());
                                guidePhones.add(guideObject.getTourGuidePhone());
                            }
                        }

                        // Nếu lỡ Firebase trống, add dữ liệu mẫu để app không bị crash
                        if (guideNames.isEmpty()) {
                            guideNames.add("Emily Waston");
                            guidePhones.add("0905123456");
                        }

                        // Cập nhật lại Spinner để hiển thị tên vừa load từ Firebase lên màn hình
                        spinnerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("FirebaseError", error.getMessage());
                    }
                });

        // ====================================================================
        // --- 4. CẬP NHẬT: ĐÓNG GÓI VÀ CHUYỂN DỮ LIỆU ĐÃ CHỌN SANG TICKET ---
        // ====================================================================
        binding.btnBook.setOnClickListener(view -> {
            // Đảm bảo danh sách hướng dẫn viên đã tải xong từ Firebase
            if (!guideNames.isEmpty()) {
                // Lấy vị trí index người dùng đang chọn trên Spinner
                int selectedIndex = binding.spinnerGuides.getSelectedItemPosition();

                // Lấy đúng tên và số điện thoại của Hướng dẫn viên được chọn gán vào object
                object.setGuideName(guideNames.get(selectedIndex));
                object.setTourGuidePhone(guidePhones.get(selectedIndex));
            } else {
                // Giá trị phòng hờ nếu mạng quá yếu chưa load xong
                object.setGuideName("Emily Waston");
                object.setTourGuidePhone("0905123456");
            }



            // Gán các thông tin thời gian, số khách, thời lượng tour vào object
            object.setTimeTour(selectedTime[0]);
            object.setTotalGuest(guestCount[0]);
            object.setDuration(binding.txtDurationDetail.getText().toString());

            // Thực hiện Intent chuyển tiếp sang màn hình TicketActivity cùng gói dữ liệu động
            Intent intent = new Intent(DetailActivity.this, TicketActivity.class);
            intent.putExtra("object", object);
            startActivity(intent);
        });
    }
}