package com.midterm.travelapp_gobuddy;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.Random;

public class BookingDetailActivity extends AppCompatActivity {

    ImageView btnBackBooking, imgBookingTour, imgBookingBarcode;
    TextView txtBookingOrderId, txtBookingPlaceName, txtBookingGuide;
    TextView txtBookingTime, txtBookingStatus, txtBookingPrice, txtBookingBarcodeNumber;
    Button btnCancelTour;

    String bookingId, placeName, date, price, status, imagePath;
    String guideName, guidePhone, duration, totalGuest, orderId, barcodeNumber;
    String userId;
    String randomBarcodeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        btnBackBooking = findViewById(R.id.btnBackBooking);
        imgBookingTour = findViewById(R.id.imgBookingTour);
        imgBookingBarcode = findViewById(R.id.imgBookingBarcode);
        txtBookingOrderId = findViewById(R.id.txtBookingOrderId);
        txtBookingPlaceName = findViewById(R.id.txtBookingPlaceName);
        txtBookingGuide = findViewById(R.id.txtBookingGuide);
        txtBookingTime = findViewById(R.id.txtBookingTime);
        txtBookingStatus = findViewById(R.id.txtBookingStatus);
        txtBookingPrice = findViewById(R.id.txtBookingPrice);
        txtBookingBarcodeNumber = findViewById(R.id.txtBookingBarcodeNumber);
        btnCancelTour = findViewById(R.id.btnCancelTour);

        bookingId = getIntent().getStringExtra("bookingId");
        placeName = getIntent().getStringExtra("placeName");
        date = getIntent().getStringExtra("date");
        price = getIntent().getStringExtra("price");
        status = getIntent().getStringExtra("status");
        imagePath = getIntent().getStringExtra("imagePath");

        guideName = getIntent().getStringExtra("guideName");
        guidePhone = getIntent().getStringExtra("guidePhone");
        duration = getIntent().getStringExtra("duration");
        totalGuest = getIntent().getStringExtra("totalGuest");
        orderId = getIntent().getStringExtra("orderId");
        barcodeNumber = getIntent().getStringExtra("barcodeNumber");

        if (placeName == null) placeName = "Chuyến đi";
        if (date == null) date = "Chưa có ngày";
        if (price == null) price = "Chưa có giá";
        if (status == null) status = "Đã đặt";
        if (imagePath == null) imagePath = "";

        if (guideName == null || guideName.isEmpty()) guideName = "GoBuddy Guide";
        if (guidePhone == null) guidePhone = "";
        if (duration == null || duration.isEmpty()) duration = status;
        if (totalGuest == null || totalGuest.isEmpty()) totalGuest = price;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        btnBackBooking.setOnClickListener(v -> finish());

        txtBookingPlaceName.setText(placeName);
        txtBookingGuide.setText(guideName);
        txtBookingTime.setText(date);
        txtBookingStatus.setText(duration);
        txtBookingPrice.setText(price);

        Glide.with(BookingDetailActivity.this)
                .load(imagePath)
                .into(imgBookingTour);

        if (orderId == null || orderId.isEmpty()) {
            Random random = new Random();
            orderId = String.valueOf(100000 + random.nextInt(900000));
        }

        txtBookingOrderId.setText("Order Id:" + orderId);

        if (barcodeNumber == null || barcodeNumber.isEmpty()) {
            Random random = new Random();
            barcodeNumber = String.valueOf(100000000 + random.nextInt(900000000));
        }

        randomBarcodeNumber = barcodeNumber;
        txtBookingBarcodeNumber.setText(randomBarcodeNumber);

        try {
            Bitmap barcodeBitmap = generateBarcodeBitmap(randomBarcodeNumber, 400, 100);
            imgBookingBarcode.setImageBitmap(barcodeBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnCancelTour.setOnClickListener(v -> {
            new AlertDialog.Builder(BookingDetailActivity.this)
                    .setTitle("Hủy tour")
                    .setMessage("Bạn có chắc muốn hủy tour này không?")
                    .setNegativeButton("Không", null)
                    .setPositiveButton("Hủy tour", (dialog, which) -> cancelTour())
                    .show();
        });
    }

    private void cancelTour() {
        if (userId == null || bookingId == null || bookingId.isEmpty()) {
            Toast.makeText(this, "Không thể hủy tour này", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance()
                .getReference("Bookings")
                .child(userId)
                .child(bookingId)
                .removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Đã hủy tour", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(BookingDetailActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hủy tour thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private Bitmap generateBarcodeBitmap(String data, int width, int height) throws Exception {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix matrix = writer.encode(data, BarcodeFormat.CODE_128, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bitmap.setPixel(i, j, matrix.get(i, j) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        return bitmap;
    }
}