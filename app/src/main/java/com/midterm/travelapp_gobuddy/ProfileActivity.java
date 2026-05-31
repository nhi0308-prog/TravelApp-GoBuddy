package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    TextView txtAvatarLetter;
    TextView txtProfileName, txtProfileEmail;
    TextView txtLastName, txtFirstName, txtBirthday, txtAddress, txtStatus;
    TextView txtNoBooking;
    RecyclerView rvBookingHistory;
    Button btnLogout;

    FirebaseAuth mAuth;
    DatabaseReference database;
    DatabaseReference bookingDatabase;

    ArrayList<BookingItem> bookingList = new ArrayList<>();
    BookingHistoryAdapter bookingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtAvatarLetter = findViewById(R.id.txtAvatarLetter);
        txtProfileName = findViewById(R.id.txtProfileName);
        txtProfileEmail = findViewById(R.id.txtProfileEmail);
        txtLastName = findViewById(R.id.txtLastName);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtBirthday = findViewById(R.id.txtBirthday);
        txtAddress = findViewById(R.id.txtAddress);
        txtStatus = findViewById(R.id.txtStatus);
        txtNoBooking = findViewById(R.id.txtNoBooking);
        rvBookingHistory = findViewById(R.id.rvBookingHistory);
        btnLogout = findViewById(R.id.btnLogout);

        rvBookingHistory.setLayoutManager(new LinearLayoutManager(this));
        bookingAdapter = new BookingHistoryAdapter(bookingList);
        rvBookingHistory.setAdapter(bookingAdapter);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("users");
        bookingDatabase = FirebaseDatabase.getInstance().getReference("Bookings");

        loadUserInfo();

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();

            Toast.makeText(ProfileActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        ChipNavigationBar bottomMenu = findViewById(R.id.bottomMenu);
        bottomMenu.setItemSelected(R.id.profile, true);

        bottomMenu.setOnItemSelectedListener(id -> {
            if (id == R.id.home) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else if (id == R.id.explorer) {
                Intent intent = new Intent(ProfileActivity.this, ExploreActivity.class);
                startActivity(intent);
                finish();

            } else if (id == R.id.bookmark) {
                Intent intent = new Intent(ProfileActivity.this, FavoriteActivity.class);
                startActivity(intent);
                finish();

            } else if (id == R.id.profile) {
            }
        });
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        String userId = currentUser.getUid();

        loadBookingHistory(userId);

        database.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    return;
                }

                String lastName = snapshot.child("lastName").getValue(String.class);
                String firstName = snapshot.child("firstName").getValue(String.class);
                String name = snapshot.child("name").getValue(String.class);
                String birthday = snapshot.child("birthday").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String address = snapshot.child("address").getValue(String.class);

                if (lastName == null) lastName = "";
                if (firstName == null) firstName = "";
                if (birthday == null) birthday = "";
                if (email == null) email = "";
                if (address == null) address = "";

                if (name == null || name.trim().isEmpty()) {
                    name = (lastName + " " + firstName).trim();
                }

                if (name.isEmpty()) {
                    name = "Người dùng";
                }

                txtProfileName.setText(name);
                txtProfileEmail.setText(email);

                txtLastName.setText(lastName);
                txtFirstName.setText(firstName);
                txtBirthday.setText(birthday);
                txtAddress.setText(address);
                txtStatus.setText("Đã đăng nhập");

                String avatarText = "";

                if (!lastName.isEmpty()) {
                    avatarText += lastName.substring(0, 1).toUpperCase();
                }

                if (!firstName.isEmpty()) {
                    avatarText += firstName.substring(0, 1).toUpperCase();
                }

                if (avatarText.isEmpty()) {
                    avatarText = "U";
                }

                txtAvatarLetter.setText(avatarText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBookingHistory(String userId) {
        bookingDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();

                if (!snapshot.exists()) {
                    txtNoBooking.setVisibility(View.VISIBLE);
                    rvBookingHistory.setVisibility(View.GONE);
                    bookingAdapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()) {
                    String bookingId = data.getKey();

                    String placeName = data.child("placeName").getValue(String.class);
                    String date = data.child("date").getValue(String.class);
                    String price = data.child("price").getValue(String.class);
                    String status = data.child("status").getValue(String.class);

                    String imagePath = data.child("imagePath").getValue(String.class);

                    if (imagePath == null || imagePath.isEmpty()) {
                        imagePath = data.child("imageUrl").getValue(String.class);
                    }

                    if (imagePath == null || imagePath.isEmpty()) {
                        imagePath = data.child("pic").getValue(String.class);
                    }

                    String guideName = data.child("guideName").getValue(String.class);
                    String guidePhone = data.child("guidePhone").getValue(String.class);
                    String duration = data.child("duration").getValue(String.class);
                    String totalGuest = data.child("totalGuest").getValue(String.class);
                    String orderId = data.child("orderId").getValue(String.class);
                    String barcodeNumber = data.child("barcodeNumber").getValue(String.class);

                    if (bookingId == null) bookingId = "";
                    if (placeName == null) placeName = "Chuyến đi";
                    if (date == null) date = "Chưa có ngày";
                    if (price == null) price = "Chưa có giá";
                    if (status == null) status = "Đã đặt";
                    if (imagePath == null) imagePath = "";

                    if (guideName == null) guideName = "GoBuddy Guide";
                    if (guidePhone == null) guidePhone = "";
                    if (duration == null) duration = status;
                    if (totalGuest == null) totalGuest = "";
                    if (orderId == null) orderId = "";
                    if (barcodeNumber == null) barcodeNumber = "";

                    BookingItem booking = new BookingItem(
                            bookingId,
                            placeName,
                            date,
                            price,
                            status,
                            imagePath,
                            guideName,
                            guidePhone,
                            duration,
                            totalGuest,
                            orderId,
                            barcodeNumber
                    );

                    bookingList.add(booking);
                }

                if (bookingList.isEmpty()) {
                    txtNoBooking.setVisibility(View.VISIBLE);
                    rvBookingHistory.setVisibility(View.GONE);
                } else {
                    txtNoBooking.setVisibility(View.GONE);
                    rvBookingHistory.setVisibility(View.VISIBLE);
                }

                bookingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtNoBooking.setText("Không thể tải lịch sử đặt vé");
                txtNoBooking.setVisibility(View.VISIBLE);
                rvBookingHistory.setVisibility(View.GONE);
            }
        });
    }

    private class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

        ArrayList<BookingItem> list;

        public BookingHistoryAdapter(ArrayList<BookingItem> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            BookingItem booking = list.get(position);

            holder.txtBookingPlaceName.setText("• " + booking.placeName);
            holder.txtBookingDate.setText("Ngày: " + booking.date);
            holder.txtBookingPrice.setText("Giá: " + booking.price);
            holder.txtBookingStatus.setText("Trạng thái: " + booking.status);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, BookingDetailActivity.class);
                intent.putExtra("bookingId", booking.bookingId);
                intent.putExtra("placeName", booking.placeName);
                intent.putExtra("date", booking.date);
                intent.putExtra("price", booking.price);
                intent.putExtra("status", booking.status);
                intent.putExtra("imagePath", booking.imagePath);

                intent.putExtra("guideName", booking.guideName);
                intent.putExtra("guidePhone", booking.guidePhone);
                intent.putExtra("duration", booking.duration);
                intent.putExtra("totalGuest", booking.totalGuest);
                intent.putExtra("orderId", booking.orderId);
                intent.putExtra("barcodeNumber", booking.barcodeNumber);

                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtBookingPlaceName, txtBookingDate, txtBookingPrice, txtBookingStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                txtBookingPlaceName = itemView.findViewById(R.id.txtBookingPlaceName);
                txtBookingDate = itemView.findViewById(R.id.txtBookingDate);
                txtBookingPrice = itemView.findViewById(R.id.txtBookingPrice);
                txtBookingStatus = itemView.findViewById(R.id.txtBookingStatus);
            }
        }
    }

    private class BookingItem {
        String bookingId;
        String placeName;
        String date;
        String price;
        String status;
        String imagePath;

        String guideName;
        String guidePhone;
        String duration;
        String totalGuest;
        String orderId;
        String barcodeNumber;

        public BookingItem(
                String bookingId,
                String placeName,
                String date,
                String price,
                String status,
                String imagePath,
                String guideName,
                String guidePhone,
                String duration,
                String totalGuest,
                String orderId,
                String barcodeNumber
        ) {
            this.bookingId = bookingId;
            this.placeName = placeName;
            this.date = date;
            this.price = price;
            this.status = status;
            this.imagePath = imagePath;
            this.guideName = guideName;
            this.guidePhone = guidePhone;
            this.duration = duration;
            this.totalGuest = totalGuest;
            this.orderId = orderId;
            this.barcodeNumber = barcodeNumber;
        }
    }
}