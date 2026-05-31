package com.midterm.travelapp_gobuddy;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.midterm.travelapp_gobuddy.databinding.ActivityTicketBinding;
import java.io.OutputStream;
import java.util.Random;

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
        if (object == null) {
            String placeName = getIntent().getStringExtra("placeName");
            String date = getIntent().getStringExtra("date");
            String price = getIntent().getStringExtra("price");
            String status = getIntent().getStringExtra("status");
            String imagePath = getIntent().getStringExtra("imagePath");

            if (placeName == null) placeName = "Chuyến đi";
            if (date == null) date = "Chưa có ngày";
            if (price == null) price = "Chưa có giá";
            if (status == null) status = "Đã đặt";
            if (imagePath == null) imagePath = "";

            binding.imageView9.setOnClickListener(v -> finish());

            Glide.with(TicketActivity.this)
                    .load(imagePath)
                    .into(binding.imageView6);

            binding.textView9.setText(placeName);
            binding.txtDuration.setText(status);
            binding.txtGuideNameTop.setText("GoBuddy Guide");
            binding.txtGuideNameFinal.setText("GoBuddy Guide");
            binding.txtTime.setText(date);
            binding.txtGuest.setText(price);

            Random random = new Random();

            String randomOrderId = String.valueOf(100000 + random.nextInt(900000));
            binding.txtOrderId.setText("Order Id:" + randomOrderId);

            String randomBarcodeNumber = String.valueOf(100000000 + random.nextInt(900000000));
            binding.textViewBarcodeNumber.setText(randomBarcodeNumber);

            try {
                Bitmap barcodeBitmap = generateBarcodeBitmap(randomBarcodeNumber, 400, 100);
                binding.imgBarcode.setImageBitmap(barcodeBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            binding.btnChat.setOnClickListener(v -> {
                Toast.makeText(this, "Không có số điện thoại hướng dẫn viên", Toast.LENGTH_SHORT).show();
            });

            binding.btnCall.setOnClickListener(v -> {
                Toast.makeText(this, "Không có số điện thoại hướng dẫn viên", Toast.LENGTH_SHORT).show();
            });

            binding.btnDownload.setOnClickListener(v -> {
                View targetView = (View) binding.imageView6.getParent();

                try {
                    Bitmap bitmap = Bitmap.createBitmap(targetView.getWidth(), targetView.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    targetView.draw(canvas);

                    String fileName = "GoBuddy_Ticket_" + randomOrderId + ".png";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/GoBuddy");
                    }

                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    if (uri != null) {
                        OutputStream outputStream = getContentResolver().openOutputStream(uri);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        if (outputStream != null) outputStream.close();

                        Toast.makeText(this, "Đã lưu vé vào Thư viện ảnh thành công!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi khi tải vé: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        // --- 1. XỬ LÝ LOAD HÌNH ẢNH BANNER
        String imageUri = "";
        if (object.getPics() != null && !object.getPics().isEmpty()) {
            imageUri = object.getPics().get(0);
        } else if (object.getImagePath() != null) {
            imageUri = object.getImagePath();
        }

        if (imageUri == null || imageUri.isEmpty()) {
            if (object.getPic() != null && !object.getPic().isEmpty()) {
                imageUri = object.getPic().get(0);
            }
        }

        Glide.with(TicketActivity.this)
                .load(imageUri)
                .into(binding.imageView6);


        // --- 2. HIỂN THỊ DỮ LIỆU CHỮ TỪ DETAIL TRANH SANG
        binding.imageView9.setOnClickListener(v -> finish());

        binding.textView9.setText(object.getTitle());
        binding.txtDuration.setText(object.getDuration());
        binding.txtGuideNameTop.setText(object.getGuideName());
        binding.txtGuideNameFinal.setText(object.getGuideName());
        binding.txtTime.setText(object.getTimeTour());
        binding.txtGuest.setText(String.valueOf(object.getTotalGuest()));

        // --- 3. ĐỔI DỮ LIỆU ĐỘNG: ORDER ID, MÃ VẠCH
        Random random = new Random();

        String randomOrderId = String.valueOf(100000 + random.nextInt(900000));
        binding.txtOrderId.setText("Order Id:" + randomOrderId);

        String randomBarcodeNumber = String.valueOf(100000000 + random.nextInt(900000000));
        binding.textViewBarcodeNumber.setText(randomBarcodeNumber);

        try {
            Bitmap barcodeBitmap = generateBarcodeBitmap(randomBarcodeNumber, 400, 100);
            binding.imgBarcode.setImageBitmap(barcodeBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // --- 4. CÁC NÚT LIÊN LẠC ---
        binding.btnChat.setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + object.getTourGuidePhone()));
            sendIntent.putExtra("sms_body", "Type your message");
            startActivity(sendIntent);
        });

        binding.btnCall.setOnClickListener(v -> {
            String phone = object.getTourGuidePhone();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        });


        // --- 5. NÚT DOWNLOAD TICKET (CHỤP VÀ LƯU VÀO THƯ VIỆN ẢNH) ---
        binding.btnDownload.setOnClickListener(v -> {
            View ticketView = binding.imageView6.getRootView();

            View targetView = (View) binding.imageView6.getParent();

            try {
                Bitmap bitmap = Bitmap.createBitmap(targetView.getWidth(), targetView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                targetView.draw(canvas);

                String fileName = "GoBuddy_Ticket_" + randomOrderId + ".png";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/GoBuddy");
                }

                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    if (outputStream != null) outputStream.close();

                    Toast.makeText(this, "Đã lưu vé vào Thư viện ảnh thành công!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi tải vé: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Thuật toán chuyển đổi Chuỗi số thành Ảnh sọc mã vạch hệ mã CODE_128
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

    private void getIntentExtra() {
        object = (ItemModel) getIntent().getSerializableExtra("object");
    }
}