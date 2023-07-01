package com.example.smartstudy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class Registration extends AppCompatActivity {
    private static final String TAG = "Registration";

    private String encodedImage;
    private PreferenceManager preferenceManager;

    EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    Button register;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressBar progressBar;
    TextView login, imageUpload;
    RoundedImageView profileImage;
    FrameLayout imageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        usernameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.pwInput);
        confirmPasswordInput = findViewById(R.id.pwConfirmInput);
        register = findViewById(R.id.signUp);
        progressBar = findViewById(R.id.progress_bar);
        profileImage = findViewById(R.id.imageProfile);
        imageUpload = findViewById(R.id.imageText);
        imageLayout = findViewById(R.id.layoutImage);

        imageLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        register.setOnClickListener(v -> {
            if (isValidSignUpDetails()) {
                signUp();
            }
        });
        login = findViewById(R.id.signInBtn);
        login.setOnClickListener(view -> {
            Intent intent = new Intent(Registration.this, Login.class);
            startActivity(intent);
            finish();
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void signUp() {
        loading(true);
        String email = emailInput.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, passwordInput.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String username = usernameInput.getText().toString().trim();
                            HashMap<String, Object> user = new HashMap<>();
                            user.put(Constants.KEY_USER_NAME, username);
                            user.put(Constants.KEY_EMAIL, email);
                            user.put(Constants.KEY_IMAGE, encodedImage);
                            db.collection(Constants.KEY_COLLECTION_USERS)
                                    .add(user)
                                    .addOnSuccessListener(documentReference -> {
                                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                        preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                                        preferenceManager.putString(Constants.KEY_USER_NAME, username);
                                        preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                                        showToast("Account created");
                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        showToast(e.getMessage());
                                    });
                        } else {
                            showToast("Authentication failed");
                        }
                        loading(false);
                    }
                });
    }

    private Boolean isValidSignUpDetails() {
        if (encodedImage == null) {
            showToast("Please upload a profile picture");
            return false;
        } else if (usernameInput.getText().toString().trim().isEmpty()) {
            showToast("Please enter a username");
            return false;
        } else if (emailInput.getText().toString().trim().isEmpty()) {
            showToast("Please enter an email");
            return false;
        } else if (passwordInput.getText().toString().trim().isEmpty()) {
            showToast("Please enter a password");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.getText().toString()).matches()) {
            showToast("Please enter a valid email");
            return false;
        } else if (confirmPasswordInput.getText().toString().trim().isEmpty()) {
            showToast("Please confirm your password");
            return false;
        } else if (passwordInput.getText().toString().length() < 6) {
            showToast("Password must be at least 6 characters");
            return false;
        } else if (!passwordInput.getText().toString().equals(confirmPasswordInput.getText().toString())) {
            showToast("Passwords do not match");
            return false;
        } else {
            return true;
        }
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            register.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            register.setVisibility(View.VISIBLE);
        }
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        profileImage.setImageBitmap(bitmap);
                        imageUpload.setVisibility(View.GONE);
                        encodedImage = encodeImage(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );
}