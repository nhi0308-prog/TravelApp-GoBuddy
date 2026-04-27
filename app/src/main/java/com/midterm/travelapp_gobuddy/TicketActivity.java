package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.midterm.travelapp_gobuddy.ItemModel;
import com.midterm.travelapp_gobuddy.databinding.ActivityTicketBinding;

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
        Glide.with(TicketActivity.this)
                .load(object.getPic())
                .into(binding.imageView6);
        Glide.with(TicketActivity.this)
                .load(object.getPic())
                .into(binding.imageView6);

        binding.imageView9.setOnClickListener(v -> finish());

        binding.textView9.setText(object.getTitle());
        binding.txtDuration.setText(object.getDuration());
        binding.txtGuideNameTop.setText(object.getGuideName());
        binding.txtGuideNameFinal.setText(object.getGuideName());
        binding.txtTime.setText(object.getTime());
        binding.txtGuest.setText(String.valueOf(object.getTotalGuest()));

        binding.btnChat.setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + object.getTourGuidePhone()));
            sendIntent.putExtra("sms_body", "Type your message");
            startActivity(sendIntent);
        });

        binding.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = object.getTourGuidePhone();
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        binding.btnDownload.setOnClickListener(v -> {
            // Thêm code tải vé tại đây
        });
    }

    private void getIntentExtra() {
        object = (ItemModel) getIntent().getSerializableExtra("object");
    }
}