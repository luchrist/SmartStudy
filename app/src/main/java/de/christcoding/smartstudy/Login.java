package de.christcoding.smartstudy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import de.christcoding.smartstudy.databinding.ActivityLoginBinding;
import de.christcoding.smartstudy.utilities.Constants;
import de.christcoding.smartstudy.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;
    private PreferenceManager preferenceManager;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

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
        preferenceManager = new PreferenceManager(getApplicationContext());
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
        binding.forgot.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onClick(View view) {
        loading(true);

        String email, pw;
        email = binding.emailInput.getText().toString().trim();
        pw = binding.pwInput.getText().toString().trim();

        if (email.isEmpty()) {
            binding.emailInput.setError("Email is required!");
            binding.emailInput.requestFocus();
            loading(false);
            return;
        }

        if (pw.isEmpty()) {
            binding.pwInput.setError("Password is required!");
            binding.pwInput.requestFocus();
            loading(false);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailInput.getText().toString()).matches()) {
            binding.emailInput.setError("Valid email is required!");
            binding.emailInput.requestFocus();
            loading(false);
            return;
        }

        if (pw.length() < 6) {
            binding.pwInput.setError("Password must be at least 6 characters!");
            binding.pwInput.requestFocus();
            loading(false);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(task -> {
            loading(false);
            if (task.isSuccessful()) {
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                preferenceManager.putString(Constants.KEY_EMAIL, email);
                db = FirebaseFirestore.getInstance();
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(email)
                        .get().addOnSuccessListener(documentSnapshot -> {
                            preferenceManager.putString(Constants.KEY_USER_NAME, documentSnapshot.getString(Constants.KEY_USER_NAME));
                        });
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                showToast("Failed to login! Please check your credentials");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.signInBtn.setVisibility(View.INVISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.signInBtn.setVisibility(View.VISIBLE);
        }
    }
}