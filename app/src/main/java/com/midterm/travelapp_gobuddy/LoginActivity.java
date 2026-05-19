package com.midterm.travelapp_gobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    ImageView btnEye;
    ImageView btnGoogle, btnFacebook;
    Button btnLogin;
    TextView txtGuest, txtSignup, txtForgot;

    boolean isShow = false;

    DatabaseReference database;
    FirebaseAuth mAuth;
    GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        Intent data = result.getData();

                        try {
                            GoogleSignInAccount account =
                                    GoogleSignIn.getSignedInAccountFromIntent(data)
                                            .getResult(ApiException.class);

                            firebaseAuthWithGoogle(account.getIdToken());

                        } catch (ApiException e) {
                            Toast.makeText(LoginActivity.this, "Google login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnEye = findViewById(R.id.btnEye);
        btnLogin = findViewById(R.id.btnLogin);
        txtGuest = findViewById(R.id.txtGuest);
        txtSignup = findViewById(R.id.txtSignupBtn);
        txtForgot = findViewById(R.id.txtForgot);

        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);

        database = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("DAN_WEB_CLIENT_ID_CUA_BAN_VAO_DAY")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // SHOW / HIDE PASSWORD
        btnEye.setOnClickListener(v -> {

            if (isShow) {
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnEye.setImageResource(R.drawable.ic_eye);
                isShow = false;
            } else {
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnEye.setImageResource(R.drawable.ic_eyeon);
                isShow = true;
            }

            edtPassword.setSelection(edtPassword.getText().length());
        });

        // LOGIN
        btnLogin.setOnClickListener(v -> {

            String email = edtEmail.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            database.orderByChild("email").equalTo(email)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {

                                            if (snapshot.exists()) {

                                                for (DataSnapshot data : snapshot.getChildren()) {
                                                    String dbPass = data.child("password").getValue(String.class);

                                                    if (dbPass != null && dbPass.equals(pass)) {

                                                        String name = data.child("name").getValue(String.class);

                                                        Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        intent.putExtra("USERNAME", name);
                                                        intent.putExtra("IS_GUEST", false);
                                                        startActivity(intent);
                                                        finish();
                                                        return;
                                                    }
                                                }

                                                Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();

                                            } else {
                                                Toast.makeText(LoginActivity.this, "Account not found", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            Toast.makeText(LoginActivity.this, "Firebase error", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // GOOGLE LOGIN
        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        // FACEBOOK TEST CLICK
        btnFacebook.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Facebook login chưa cấu hình", Toast.LENGTH_SHORT).show();
        });

        // GUEST
        txtGuest.setOnClickListener(v -> {
            mAuth.signOut();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("IS_GUEST", true);
            startActivity(intent);
            finish();
        });

        // SIGN UP
        txtSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // FORGOT PASSWORD
        txtForgot.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Chưa làm Forgot Password", Toast.LENGTH_SHORT).show();
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        String name = "Google User";

                        if (mAuth.getCurrentUser() != null
                                && mAuth.getCurrentUser().getDisplayName() != null) {
                            name = mAuth.getCurrentUser().getDisplayName();
                        }

                        Toast.makeText(LoginActivity.this, "Google login success", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("USERNAME", name);
                        intent.putExtra("IS_GUEST", false);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, "Firebase Google auth failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}