package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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

        if (object == null) {
            finish();
        }
    }

    private void setVariable() {

        // =====================================================
        // HIỂN THỊ THÔNG TIN CƠ BẢN
        // =====================================================

        binding.titleTxt.setText(object.getTitle());
        binding.addressTxt.setText(object.getAddress());
        binding.descriptionTxt.setText(object.getDescription());

        binding.ratingTxt.setText(object.getScore() + " Rating");
        binding.ratingBar.setRating((float) object.getScore());

        binding.backBtn.setOnClickListener(v -> finish());

        // =====================================================
        // XỬ LÝ GIÁ TOUR THEO SỐ KHÁCH
        // =====================================================

        final int[] guestCount = {1};

        // Giá gốc cho 1 người
        final int basePrice = object.getPrice();

        // Hiển thị số lượng khách mặc định
        binding.txtGuestCount.setText(String.valueOf(guestCount[0]));

        // Hàm cập nhật tổng giá
        Runnable updateTotalPrice = () -> {

            int totalPrice = basePrice * guestCount[0];

            binding.priceTxt.setText("$" + totalPrice);
        };

        // Hiển thị giá ban đầu
        updateTotalPrice.run();

        // =====================================================
        // HIỂN THỊ HÌNH ẢNH
        // =====================================================

        ArrayList<String> images = object.getPics();

        if (images != null && !images.isEmpty()) {

            Glide.with(this)
                    .load(images.get(0))
                    .into(binding.pic);

            ImageView[] thumbs = {
                    binding.thumb1,
                    binding.thumb2,
                    binding.thumb3,
                    binding.thumb4
            };

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

                            if (j == finalI) {
                                thumbs[j].setAlpha(1.0f);
                            } else {
                                thumbs[j].setAlpha(0.4f);
                            }
                        }
                    });

                } else {

                    thumbs[i].setVisibility(View.GONE);
                }
            }

        } else if (object.getImagePath() != null) {

            Glide.with(this)
                    .load(object.getImagePath())
                    .into(binding.pic);

            binding.thumbScroll.setVisibility(View.GONE);
        }

        // =====================================================
        // HIỂN THỊ THỜI LƯỢNG TOUR
        // =====================================================

        if (object.getDuration() != null &&
                !object.getDuration().isEmpty()) {

            binding.txtDurationDetail.setText(object.getDuration());

        } else {

            binding.txtDurationDetail.setText("Chưa cập nhật");
        }

        // =====================================================
        // NÚT GIẢM SỐ KHÁCH
        // =====================================================

        binding.btnMinusGuest.setOnClickListener(v -> {

            if (guestCount[0] > 1) {

                guestCount[0]--;

                binding.txtGuestCount.setText(
                        String.valueOf(guestCount[0])
                );

                // cập nhật giá
                updateTotalPrice.run();
            }
        });

        // =====================================================
        // NÚT TĂNG SỐ KHÁCH
        // =====================================================

        binding.btnPlusGuest.setOnClickListener(v -> {

            guestCount[0]++;

            binding.txtGuestCount.setText(
                    String.valueOf(guestCount[0])
            );

            // cập nhật giá
            updateTotalPrice.run();
        });

        // =====================================================
        // CHỌN GIỜ TOUR
        // =====================================================

        final String[] selectedTime = {"08:30 AM"};

        binding.btnSelectTime.setOnClickListener(v -> {

            android.app.TimePickerDialog timePickerDialog =
                    new android.app.TimePickerDialog(
                            this,

                            (view1, hourOfDay, minute) -> {

                                String amPm =
                                        (hourOfDay >= 12) ? " PM" : " AM";

                                int hour =
                                        (hourOfDay > 12)
                                                ? hourOfDay - 12
                                                : hourOfDay;

                                if (hour == 0) {
                                    hour = 12;
                                }

                                selectedTime[0] =
                                        String.format("%02d:%02d%s", hour, minute, amPm);

                                binding.btnSelectTime.setText(
                                        selectedTime[0]
                                );
                            },

                            8,
                            30,
                            false
                    );

            timePickerDialog.show();
        });

        // =====================================================
        // LOAD TOUR GUIDE TỪ FIREBASE
        // =====================================================

        ArrayList<String> guideNames = new ArrayList<>();
        ArrayList<String> guidePhones = new ArrayList<>();

        android.widget.ArrayAdapter<String> spinnerAdapter =
                new android.widget.ArrayAdapter<>(
                        this,
                        R.layout.spinner_area_item,
                        guideNames
                );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        binding.spinnerGuides.setAdapter(spinnerAdapter);

        FirebaseDatabase.getInstance()
                .getReference("Guides")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                guideNames.clear();
                                guidePhones.clear();

                                for (DataSnapshot data :
                                        snapshot.getChildren()) {

                                    ItemModel guideObject =
                                            data.getValue(ItemModel.class);

                                    if (guideObject != null) {

                                        guideNames.add(
                                                guideObject.getGuideName()
                                        );

                                        guidePhones.add(
                                                guideObject.getTourGuidePhone()
                                        );
                                    }
                                }

                                // dữ liệu dự phòng
                                if (guideNames.isEmpty()) {

                                    guideNames.add("Emily Waston");
                                    guidePhones.add("0905123456");
                                    guideNames.add("Jean Dupont");
                                    guidePhones.add("0905789123");
                                    guideNames.add("Alexandre Nguyen");
                                    guidePhones.add("0914111222");
                                    guideNames.add("Marie Curie");
                                    guidePhones.add("0935333444");
                                }

                                spinnerAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                                Log.e(
                                        "FirebaseError",
                                        error.getMessage()
                                );
                            }
                        }
                );

        // =====================================================
        // FAVORITE TOUR
        // =====================================================

        String tourKey =
                object.getTitle()
                        .replaceAll("[^a-zA-Z0-9]", "_");

        DatabaseReference favRef =
                FirebaseDatabase.getInstance()
                        .getReference("Favorites")
                        .child(tourKey);

        final boolean[] isFavorited = {false};

        // Kiểm tra trạng thái favorite
        favRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        isFavorited[0] = snapshot.exists();

                        updateHeartIcon(isFavorited[0]);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        Log.e(
                                "DetailActivity",
                                error.getMessage()
                        );
                    }
                }
        );

        // Click icon tim
        binding.imageView5.setOnClickListener(v -> {

            isFavorited[0] = !isFavorited[0];

            if (isFavorited[0]) {

                favRef.setValue(object)
                        .addOnSuccessListener(unused ->
                                Toast.makeText(
                                        this,
                                        "Đã lưu",
                                        Toast.LENGTH_SHORT
                                ).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(
                                        this,
                                        "Lỗi lưu",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );

            } else {

                favRef.removeValue()
                        .addOnSuccessListener(unused ->
                                Toast.makeText(
                                        this,
                                        "Đã xóa khỏi Favorites",
                                        Toast.LENGTH_SHORT
                                ).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(
                                        this,
                                        "Lỗi xóa Favorites",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
            }

            updateHeartIcon(isFavorited[0]);
        });

        // =====================================================
        // BOOK TOUR
        // =====================================================

        binding.btnBook.setOnClickListener(view -> {

            // 1. Kiểm tra đăng nhập
            com.google.firebase.auth.FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
            com.google.firebase.auth.FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser == null) {
                Toast.makeText(DetailActivity.this, "Vui lòng đăng nhập để đặt tour!", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = currentUser.getUid();

            // 2. Lấy guide được chọn
            if (!guideNames.isEmpty()) {

                int selectedIndex =
                        binding.spinnerGuides
                                .getSelectedItemPosition();

                object.setGuideName(
                        guideNames.get(selectedIndex)
                );

                object.setTourGuidePhone(
                        guidePhones.get(selectedIndex)
                );

            } else {

                object.setGuideName("Emily Waston");
                object.setTourGuidePhone("0905123456");
            }

            // 3. Gán dữ liệu booking
            object.setTimeTour(selectedTime[0]);
            object.setTotalGuest(guestCount[0]);
            object.setDuration(
                    binding.txtDurationDetail
                            .getText()
                            .toString()
            );

            // Cập nhật tổng giá
            int totalPrice = basePrice * guestCount[0];
            object.setPrice(totalPrice);

            // 4. Lưu trực tiếp lịch sử đặt vé lên Firebase (Chạy nền)
            DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("Bookings");
            String bookingId = bookingRef.child(userId).push().getKey();

            java.util.HashMap<String, String> newBooking = new java.util.HashMap<>();
            newBooking.put("placeName", object.getTitle());
            String currentDate = android.text.format.DateFormat.format("dd/MM/yyyy", new java.util.Date()).toString();
            newBooking.put("date", currentDate + " (" + selectedTime[0] + ")");
            newBooking.put("price", "$" + totalPrice);
            newBooking.put("status", "Đã đặt thành công");

            // ===== THÊM: LƯU ĐẦY ĐỦ THÔNG TIN VÉ ĐỂ LỊCH SỬ HIỂN THỊ ĐÚNG =====
            String imagePath = "";

            if (object.getPics() != null && !object.getPics().isEmpty()) {
                imagePath = object.getPics().get(0);
            } else if (object.getImagePath() != null) {
                imagePath = object.getImagePath();
            } else if (object.getPic() != null && !object.getPic().isEmpty()) {
                imagePath = object.getPic().get(0);
            }

            java.util.Random random = new java.util.Random();
            String orderId = String.valueOf(100000 + random.nextInt(900000));
            String barcodeNumber = String.valueOf(100000000 + random.nextInt(900000000));

            newBooking.put("bookingId", bookingId);
            newBooking.put("imagePath", imagePath);
            newBooking.put("guideName", object.getGuideName());
            newBooking.put("guidePhone", object.getTourGuidePhone());
            newBooking.put("duration", object.getDuration());
            newBooking.put("totalGuest", String.valueOf(object.getTotalGuest()));
            newBooking.put("timeTour", selectedTime[0]);
            newBooking.put("orderId", orderId);
            newBooking.put("barcodeNumber", barcodeNumber);
            // ===== HẾT PHẦN THÊM =====

            if (bookingId != null) {
                bookingRef.child(userId).child(bookingId).setValue(newBooking);
            }

            // 5. Chuyển sang TicketActivity ngay lập tức
            Intent intent =
                    new Intent(
                            DetailActivity.this,
                            TicketActivity.class
                    );

            intent.putExtra("object", object);

            startActivity(intent);
        });
    }

    // =====================================================
    // CẬP NHẬT ICON TIM
    // =====================================================

    private void updateHeartIcon(boolean isFavorited) {

        if (isFavorited) {

            binding.imageView5.setColorFilter(
                    Color.RED,
                    PorterDuff.Mode.SRC_IN
            );

        } else {

            binding.imageView5.clearColorFilter();
        }
    }
}