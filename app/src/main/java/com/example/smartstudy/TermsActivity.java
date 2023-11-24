package com.example.smartstudy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class TermsActivity extends AppCompatActivity {

    AppCompatImageButton exitBtn, openInWebBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        exitBtn = findViewById(R.id.exitBtn);
        openInWebBtn = findViewById(R.id.openInWebBtn);
        exitBtn.setOnClickListener(v -> onBackPressed());
        openInWebBtn.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
            startActivity(browserIntent);
        });
    }
}