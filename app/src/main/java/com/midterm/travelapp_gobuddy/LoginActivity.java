package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    ImageView btnEye;
    Button btnLogin;
    TextView txtGuest, txtSignup, txtForgot;

    boolean isShow = false;

    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnEye = findViewById(R.id.btnEye);
        btnLogin = findViewById(R.id.btnLogin);
        txtGuest = findViewById(R.id.txtGuest);
        txtSignup = findViewById(R.id.txtSignup);
        txtForgot = findViewById(R.id.txtForgot);

        database = FirebaseDatabase.getInstance().getReference("users");

        // 👁 show password
        btnEye.setOnClickListener(v -> {
            if (isShow) {
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            edtPassword.setSelection(edtPassword.length());
            isShow = !isShow;
        });

        // 🔐 LOGIN
        btnLogin.setOnClickListener(v -> {

            String email = edtEmail.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            database.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                boolean ok = false;

                                for (DataSnapshot data : snapshot.getChildren()) {
                                    String dbPass = data.child("password").getValue(String.class);

                                    if (pass.equals(dbPass)) {
                                        ok = true;
                                        break;
                                    }
                                }

                                if (ok) {
                                    Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(LoginActivity.this, "Account not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(LoginActivity.this, "Firebase error", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // 👤 Guest
        txtGuest.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // 📝 Signup
        txtSignup.setOnClickListener(v -> {
            Toast.makeText(this, "Chưa làm Register", Toast.LENGTH_SHORT).show();
        });

        // 🔄 Forgot
        txtForgot.setOnClickListener(v -> {
            Toast.makeText(this, "Chưa làm Forgot Password", Toast.LENGTH_SHORT).show();
        });
    }
}