package com.example.smartstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private Button reset, back;
    private EditText email;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        reset = findViewById(R.id.reset);
        back = findViewById(R.id.back);
        email = findViewById(R.id.email);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        setListeners();
    }

    private void setListeners() {
        reset.setOnClickListener(v -> {
            String emailAddress = email.getText().toString().trim();
            if (emailAddress.isEmpty()) {
                email.setError("Enter email");
            } else {
                resetPassword(emailAddress);
            }
        });
        back.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, Login.class);
            startActivity(intent);
            finish();
        });
    }

    private void resetPassword(String emailAddress) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        reset.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(emailAddress).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            finish();
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            reset.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Email is not registered", Toast.LENGTH_SHORT).show();
        });
    }
}