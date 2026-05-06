package com.midterm.travelapp_gobuddy;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText edtLastName, edtFirstName, edtBirthday, edtSignupEmail, edtAddress, edtSignupPassword;
    ImageView btnSignupEye;
    Button btnCreateAccount;
    TextView txtBackLogin;

    boolean isShowPassword = false;

    FirebaseAuth mAuth;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edtLastName = findViewById(R.id.edtLastName);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtBirthday = findViewById(R.id.edtBirthday);
        edtSignupEmail = findViewById(R.id.edtSignupEmail);
        edtAddress = findViewById(R.id.edtAddress);
        edtSignupPassword = findViewById(R.id.edtSignupPassword);
        btnSignupEye = findViewById(R.id.btnSignupEye);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        txtBackLogin = findViewById(R.id.txtBackLogin);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("users");

        edtBirthday.setOnClickListener(v -> showDatePicker());

        btnSignupEye.setOnClickListener(v -> {
            if (isShowPassword) {
                edtSignupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnSignupEye.setImageResource(R.drawable.ic_eye);
                isShowPassword = false;
            } else {
                edtSignupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnSignupEye.setImageResource(R.drawable.ic_eyeon);
                isShowPassword = true;
            }

            edtSignupPassword.setSelection(edtSignupPassword.getText().length());
        });

        btnCreateAccount.setOnClickListener(v -> registerUser());

        txtBackLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SignUpActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String birthday = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    edtBirthday.setText(birthday);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void registerUser() {
        String lastName = edtLastName.getText().toString().trim();
        String firstName = edtFirstName.getText().toString().trim();
        String birthday = edtBirthday.getText().toString().trim();
        String email = edtSignupEmail.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String password = edtSignupPassword.getText().toString().trim();

        if (lastName.isEmpty()) {
            edtLastName.setError("Nhập họ");
            edtLastName.requestFocus();
            return;
        }

        if (firstName.isEmpty()) {
            edtFirstName.setError("Nhập tên");
            edtFirstName.requestFocus();
            return;
        }

        if (birthday.isEmpty()) {
            edtBirthday.setError("Chọn ngày sinh");
            edtBirthday.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            edtSignupEmail.setError("Nhập email");
            edtSignupEmail.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            edtAddress.setError("Nhập địa chỉ");
            edtAddress.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtSignupPassword.setError("Nhập mật khẩu");
            edtSignupPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edtSignupPassword.setError("Mật khẩu phải từ 6 ký tự");
            edtSignupPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        String userId = mAuth.getCurrentUser().getUid();
                        String fullName = lastName + " " + firstName;

                        HashMap<String, Object> user = new HashMap<>();
                        user.put("lastName", lastName);
                        user.put("firstName", firstName);
                        user.put("name", fullName);
                        user.put("birthday", birthday);
                        user.put("email", email);
                        user.put("address", address);
                        user.put("password", password);

                        database.child(userId).setValue(user)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignUpActivity.this, "Lỗi lưu dữ liệu", Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(SignUpActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}