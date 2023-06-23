package com.example.smartstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartstudy.databinding.ActivityLoginBinding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        mAuth = FirebaseAuth.getInstance();
    }

    private void setListeners() {
        binding.signInBtn.setOnClickListener(this);
        binding.noAccount.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Registration.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onClick(View view) {
        binding.progressBar.setVisibility(View.VISIBLE);

        String email, pw;
        email = binding.emailInput.getText().toString().trim();
        pw = binding.pwInput.getText().toString().trim();

        if (email.isEmpty()) {
            binding.emailInput.setError("Email is required!");
            binding.emailInput.requestFocus();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        if (pw.isEmpty()) {
            binding.pwInput.setError("Password is required!");
            binding.pwInput.requestFocus();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        if (pw.length() < 6) {
            binding.pwInput.setError("Password must be at least 6 characters!");
            binding.pwInput.requestFocus();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(task -> {
            binding.progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Login.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
            }
        });
    }
}