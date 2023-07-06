package com.example.smartstudy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GroupActivity extends AppCompatActivity {

    AppCompatImageView back, chat;
    TextView groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        back = findViewById(R.id.backNavBtn);
        chat = findViewById(R.id.chatBtn);
        groupName = findViewById(R.id.groupName);

        setListeners();
    }

    private void setListeners() {
        groupName.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupInfoActivity.class);
            startActivity(intent);
            this.finish();
                });
        chat.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupChatActivity.class);
            startActivity(intent);
            this.finish();
        });
        back.setOnClickListener(v -> {
            startActivity(new Intent(GroupActivity.this, MainActivity.class));
            finish();
        });
    }
}