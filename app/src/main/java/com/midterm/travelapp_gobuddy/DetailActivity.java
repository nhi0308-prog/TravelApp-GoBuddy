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
        // Hiển thị lại timeTour và guideName đã lưu

        // =====================================================
        // XỬ LÝ GIÁ TOUR THEO SỐ KHÁCH
        // =====================================================

        final int[] guestCount = {1};

        final int basePrice = object.getPrice();

        binding.txtGuestCount.setText(String.valueOf(guestCount[0]));

        Runnable updateTotalPrice = () -> {
            int totalPrice = basePrice * guestCount[0];
            binding.priceTxt.setText("$" + totalPrice);
        };

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

            final int[] currentIndex = {0};

            Runnable updateMainImage = () -> {
                Glide.with(DetailActivity.this)
                        .load(images.get(currentIndex[0]))
                        .into(binding.pic);

                for (int j = 0; j < thumbs.length; j++) {
                    if (j == currentIndex[0]) {
                        thumbs[j].animate().scaleX(1.2f).scaleY(1.2f)
                                .translationZ(16f).setDuration(200).start();
                        thumbs[j].setAlpha(1.0f);
                    } else {
                        thumbs[j].animate().scaleX(1.0f).scaleY(1.0f)
                                .translationZ(0f).setDuration(200).start();
                        thumbs[j].setAlpha(0.4f);
                    }
                }
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
                        currentIndex[0] = finalI;
                        updateMainImage.run();
                    });

                } else {
                    thumbs[i].setVisibility(View.GONE);
                }
            }

            // =====================================================
            // SWIPE TRÁI/PHẢI ĐỔI ẢNH CHÍNH
            // =====================================================

            binding.pic.setOnTouchListener(new View.OnTouchListener() {

                private float startX = 0;
                private float startY = 0;
                private boolean isSwiping = false;
                private static final int SWIPE_THRESHOLD = 40;
                private static final int SWIPE_VELOCITY = 5;

                @Override
                public boolean onTouch(View v, android.view.MotionEvent event) {

                    switch (event.getAction()) {

                        case android.view.MotionEvent.ACTION_DOWN:
                            startX = event.getX();
                            startY = event.getY();
                            isSwiping = false;
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return true;

                        case android.view.MotionEvent.ACTION_MOVE:
                            float moveX = event.getX() - startX;
                            float moveY = event.getY() - startY;

                            if (!isSwiping && Math.abs(moveX) > Math.abs(moveY)
                                    && Math.abs(moveX) > SWIPE_VELOCITY) {
                                isSwiping = true;
                            }

                            if (isSwiping) {
                                binding.pic.setTranslationX(moveX * 0.4f);
                                v.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            return true;

                        case android.view.MotionEvent.ACTION_UP:
                        case android.view.MotionEvent.ACTION_CANCEL:
                            float diffX = event.getX() - startX;
                            v.getParent().requestDisallowInterceptTouchEvent(false);

                            if (isSwiping && Math.abs(diffX) > SWIPE_THRESHOLD) {

                                if (diffX < 0) {
                                    if (currentIndex[0] < images.size() - 1) {
                                        currentIndex[0]++;
                                    } else {
                                        currentIndex[0] = 0;
                                    }
                                } else {
                                    if (currentIndex[0] > 0) {
                                        currentIndex[0]--;
                                    } else {
                                        currentIndex[0] = images.size() - 1;
                                    }
                                }

                                float slideOut = diffX < 0
                                        ? -binding.pic.getWidth()
                                        : binding.pic.getWidth();

                                binding.pic.animate()
                                        .translationX(slideOut)
                                        .alpha(0f)
                                        .setDuration(150)
                                        .withEndAction(() -> {
                                            updateMainImage.run();
                                            binding.pic.setTranslationX(-slideOut * 0.6f);
                                            binding.pic.setAlpha(0f);
                                            binding.pic.animate()
                                                    .translationX(0f)
                                                    .alpha(1f)
                                                    .setDuration(150)
                                                    .start();
                                        }).start();

                            } else {
                                binding.pic.animate()
                                        .translationX(0f)
                                        .alpha(1f)
                                        .setDuration(100)
                                        .start();
                            }
                            return true;
                    }
                    return false;
                }
            });

        } else if (object.getImagePath() != null) {

            Glide.with(this)
                    .load(object.getImagePath())
                    .into(binding.pic);

            binding.thumbScroll.setVisibility(View.GONE);
        }

        if (object.getDuration() != null && !object.getDuration().isEmpty()) {
            binding.txtDurationDetail.setText(object.getDuration());
        } else {
            binding.txtDurationDetail.setText("Chưa cập nhật");
        }

        binding.btnMinusGuest.setOnClickListener(v -> {
            if (guestCount[0] > 1) {
                guestCount[0]--;
                binding.txtGuestCount.setText(String.valueOf(guestCount[0]));
                updateTotalPrice.run();
            }
        });

        binding.btnPlusGuest.setOnClickListener(v -> {
            guestCount[0]++;
            binding.txtGuestCount.setText(String.valueOf(guestCount[0]));
            updateTotalPrice.run();
        });

        final String[] selectedTime = {"08:30 AM"};
        if (object.getTimeTour() != null && !object.getTimeTour().isEmpty()) {
            selectedTime[0] = object.getTimeTour();
            binding.btnSelectTime.setText(object.getTimeTour());
        }

        binding.btnSelectTime.setOnClickListener(v -> {

            android.app.TimePickerDialog timePickerDialog =
                    new android.app.TimePickerDialog(
                            this,
                            (view1, hourOfDay, minute) -> {

                                String amPm = (hourOfDay >= 12) ? " PM" : " AM";
                                int hour = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
                                if (hour == 0) hour = 12;

                                selectedTime[0] = String.format("%02d:%02d%s", hour, minute, amPm);
                                binding.btnSelectTime.setText(selectedTime[0]);
                            },
                            8, 30, false
                    );

            timePickerDialog.show();
        });

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
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        guideNames.clear();
                        guidePhones.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {

                            ItemModel guideObject = data.getValue(ItemModel.class);

                            if (guideObject != null) {
                                guideNames.add(guideObject.getGuideName());
                                guidePhones.add(guideObject.getTourGuidePhone());
                            }
                        }

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
                        if (object.getGuideName() != null) {
                            for (int i = 0; i < guideNames.size(); i++) {
                                if (guideNames.get(i).equals(object.getGuideName())) {
                                    binding.spinnerGuides.setSelection(i);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("FirebaseError", error.getMessage());
                    }
                });

        String tourKey = object.getTitle().replaceAll("[^a-zA-Z0-9]", "_");

        DatabaseReference favInfoRef =
                FirebaseDatabase.getInstance()
                        .getReference("Favorites")
                        .child("info")
                        .child(tourKey);

        DatabaseReference favBookingRef =
                FirebaseDatabase.getInstance()
                        .getReference("Favorites")
                        .child("booking")
                        .child(tourKey);

        final boolean[] isFavorited = {false};

        favInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                isFavorited[0] = snapshot.exists();
                updateHeartIcon(isFavorited[0]);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DetailActivity", error.getMessage());
            }
        });

        // Click icon tim
        binding.imageView5.setOnClickListener(v -> {

            isFavorited[0] = !isFavorited[0];

            // Hiệu ứng bong bóng
            v.animate()
                    .scaleX(1.4f).scaleY(1.4f).setDuration(120)
                    .withEndAction(() ->
                            v.animate()
                                    .scaleX(0.85f).scaleY(0.85f).setDuration(80)
                                    .withEndAction(() ->
                                            v.animate()
                                                    .scaleX(1.1f).scaleY(1.1f).setDuration(60)
                                                    .withEndAction(() ->
                                                            v.animate()
                                                                    .scaleX(1.0f).scaleY(1.0f)
                                                                    .setDuration(50).start()
                                                    ).start()
                                    ).start()
                    ).start();

            if (isFavorited[0]) {

                // Lưu info
                java.util.HashMap<String, Object> infoMap = new java.util.HashMap<>();
                infoMap.put("Id", object.getId());
                infoMap.put("ImagePath", object.getImagePath());
                infoMap.put("address", object.getAddress());
                infoMap.put("category", object.getCategory());
                infoMap.put("description", object.getDescription());
                infoMap.put("duration", object.getDuration());
                infoMap.put("pics", object.getPics());
                infoMap.put("price", object.getPrice());
                infoMap.put("score", object.getScore());
                infoMap.put("title", object.getTitle());
                infoMap.put("timeTour", selectedTime[0]);

                String savedGuideName = (!guideNames.isEmpty())
                        ? guideNames.get(binding.spinnerGuides.getSelectedItemPosition())
                        : "Emily Waston";
                infoMap.put("guideName", savedGuideName);
                favInfoRef.setValue(infoMap)
                        .addOnSuccessListener(unused ->
                                Toast.makeText(this, "Đã lưu", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Lỗi lưu", Toast.LENGTH_SHORT).show()
                        );

                // Lưu booking
                // Lưu booking
                java.util.HashMap<String, Object> bookingMap = new java.util.HashMap<>();
                bookingMap.put("timeTour", selectedTime[0]);
                bookingMap.put("totalGuest", guestCount[0]);

// Lấy guide đang được chọn trong spinner
                String selectedGuideName = "Emily Waston"; // giá trị mặc định

                if (!guideNames.isEmpty()) {
                    int pos = binding.spinnerGuides.getSelectedItemPosition();
                    if (pos >= 0 && pos < guideNames.size()) {
                        selectedGuideName = guideNames.get(pos);
                    }
                }

                bookingMap.put("guideName", selectedGuideName);
                favBookingRef.setValue(bookingMap);

            } else {

                favInfoRef.removeValue();
                favBookingRef.removeValue()
                        .addOnSuccessListener(unused ->
                                Toast.makeText(this, "Đã xóa khỏi Favorites", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Lỗi xóa Favorites", Toast.LENGTH_SHORT).show()
                        );
            }
            updateHeartIcon(isFavorited[0]);
        });

        // =====================================================
        // BOOK TOUR
        // =====================================================

        binding.btnBook.setOnClickListener(view -> {

            // 1. Kiểm tra đăng nhập
            com.google.firebase.auth.FirebaseAuth mAuth =
                    com.google.firebase.auth.FirebaseAuth.getInstance();
            com.google.firebase.auth.FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser == null) {
                Toast.makeText(DetailActivity.this,
                        "Vui lòng đăng nhập để đặt tour!", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = currentUser.getUid();

            // 2. Lấy guide được chọn
            if (!guideNames.isEmpty()) {
                int selectedIndex = binding.spinnerGuides.getSelectedItemPosition();
                object.setGuideName(guideNames.get(selectedIndex));
                object.setTourGuidePhone(guidePhones.get(selectedIndex));
            } else {
                object.setGuideName("Emily Waston");
                object.setTourGuidePhone("0905123456");
            }

            // 3. Gán dữ liệu booking
            object.setTimeTour(selectedTime[0]);
            object.setTotalGuest(guestCount[0]);
            object.setDuration(binding.txtDurationDetail.getText().toString());

            int totalPrice = basePrice * guestCount[0];
            object.setPrice(totalPrice);

            // 4. Lưu booking lên Firebase
            DatabaseReference bookingRef =
                    FirebaseDatabase.getInstance().getReference("Bookings");
            String bookingId = bookingRef.child(userId).push().getKey();

            java.util.HashMap<String, String> newBooking = new java.util.HashMap<>();
            newBooking.put("placeName", object.getTitle());

            String currentDate = android.text.format.DateFormat
                    .format("dd/MM/yyyy", new java.util.Date()).toString();
            newBooking.put("date", currentDate + " (" + selectedTime[0] + ")");
            newBooking.put("price", "$" + totalPrice);
            newBooking.put("status", "Đã đặt thành công");

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

            if (bookingId != null) {
                bookingRef.child(userId).child(bookingId).setValue(newBooking);
            }

            // 5. Chuyển sang TicketActivity
            Intent intent = new Intent(DetailActivity.this, TicketActivity.class);
            intent.putExtra("object", object);
            startActivity(intent);
        });
    }

    private void updateHeartIcon(boolean isFavorited) {

        if (isFavorited) {

            binding.imageView5.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

            binding.imageView5.animate()
                    .scaleX(1.3f).scaleY(1.3f).setDuration(150)
                    .withEndAction(() ->
                            binding.imageView5.animate()
                                    .scaleX(1.0f).scaleY(1.0f)
                                    .setDuration(150).start()
                    ).start();

        } else {

            binding.imageView5.clearColorFilter();

            binding.imageView5.animate()
                    .scaleX(0.85f).scaleY(0.85f).setDuration(100)
                    .withEndAction(() ->
                            binding.imageView5.animate()
                                    .scaleX(1.0f).scaleY(1.0f)
                                    .setDuration(100).start()
                    ).start();
        }
    }
}