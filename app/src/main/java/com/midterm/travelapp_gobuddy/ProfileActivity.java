package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class ProfileActivity extends AppCompatActivity {

    TextView txtAvatarLetter;
    TextView txtProfileName, txtProfileEmail;
    TextView txtLastName, txtFirstName, txtBirthday, txtAddress, txtStatus;
    Button btnLogout;

    FirebaseAuth mAuth;
    DatabaseReference database;

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
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("users");

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
                Toast.makeText(ProfileActivity.this, "Explore", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.bookmark) {
                Toast.makeText(ProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ProfileActivity.this, FavoriteActivity.class);
                startActivity(intent);
                finish();

            } else if (id == R.id.profile) {
                // Đang ở trang Profile nên không làm gì
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
}