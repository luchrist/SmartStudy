package com.example.smartstudy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.example.smartstudy.utilities.PreferenceManager;

public class CreateGroupActivity extends BaseActivity {

    Button createGroupButton, joinGroupButton;
    AppCompatImageView cancelButton;
    private int selectedColor;
    PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        createGroupButton = findViewById(R.id.createGroupBtn);
        cancelButton = findViewById(R.id.cancelNavBtn);
        joinGroupButton = findViewById(R.id.joinGroupBtn);
        selectedColor = ContextCompat.getColor(getApplicationContext(), R.color.accentBlue);
        preferenceManager = new PreferenceManager(getApplicationContext());

        createGroupButton.setTextColor(selectedColor);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new CreateGroupFragment(preferenceManager)).commit();
        setListeners();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setListeners() {
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateGroupActivity.this, MainActivity.class);
            intent.putExtra("fragment", "group");
            startActivity(intent);
            finish();
        });
        createGroupButton.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new CreateGroupFragment(preferenceManager)).commit();
            createGroupButton.setBackground(getDrawable(R.drawable.button_background_selected_left));
            joinGroupButton.setBackground(getDrawable(R.drawable.button_background_not_selected_right));
            createGroupButton.setTextColor(selectedColor);
            joinGroupButton.setTextColor(Color.WHITE);
        });
        joinGroupButton.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new JoinGroupFragment()).commit();
            createGroupButton.setBackground(getDrawable(R.drawable.button_background_not_selected_left));
            joinGroupButton.setBackground(getDrawable(R.drawable.button_background_selected_right));
            joinGroupButton.setTextColor(selectedColor);
            createGroupButton.setTextColor(Color.WHITE);

        });
    }
}