package com.sit708.learningassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sit708.learningassistant.R;
import com.sit708.learningassistant.models.AppDatabase;
import com.sit708.learningassistant.models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etConfirmEmail,
            etPassword, etConfirmPassword, etPhone;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = AppDatabase.getInstance(this);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etConfirmEmail = findViewById(R.id.etConfirmEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        MaterialButton btnCreate = findViewById(R.id.btnCreateAccount);

        btnCreate.setOnClickListener(v -> attemptRegister());
    }

    private String text(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void attemptRegister() {
        String username = text(etUsername);
        String email = text(etEmail);
        String confirmEmail = text(etConfirmEmail);
        String password = text(etPassword);
        String confirmPassword = text(etConfirmPassword);
        String phone = text(etPhone);

        if (TextUtils.isEmpty(username)) { etUsername.setError("Required"); return; }
        if (TextUtils.isEmpty(email)) { etEmail.setError("Required"); return; }
        if (!email.equals(confirmEmail)) { etConfirmEmail.setError("Emails do not match"); return; }
        if (password.length() < 6) { etPassword.setError("Min 6 characters"); return; }
        if (!password.equals(confirmPassword)) { etConfirmPassword.setError("Passwords do not match"); return; }
        if (TextUtils.isEmpty(phone)) { etPhone.setError("Required"); return; }

        executor.execute(() -> {
            User existing = db.userDao().findByUsername(username);
            handler.post(() -> {
                if (existing != null) {
                    etUsername.setError("Username already taken");
                } else {
                    // Save user with empty interests for now — set on InterestsActivity
                    User newUser = new User(username, email, password, phone, "[]");
                    executor.execute(() -> {
                        db.userDao().insert(newUser);
                        handler.post(() -> {
                            Intent intent = new Intent(this, InterestsActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.slide_in_left,
                                    android.R.anim.slide_out_right);
                        });
                    });
                }
            });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
