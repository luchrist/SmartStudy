package com.example.smartstudy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GroupActivity extends AppCompatActivity {

    AppCompatImageView back;
    TextView groupName;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        back = findViewById(R.id.backNavBtn);
        bottomNavigationView = findViewById(R.id.groupBottomNav);
        groupName = findViewById(R.id.groupName);

        setListeners();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                new GroupBoardFragment()).commit();
    }

    private void setListeners() {
        groupName.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                    new GroupInfoFragment()).commit();
                });
        back.setOnClickListener(v -> {
            startActivity(new Intent(GroupActivity.this, MainActivity.class));
            finish();
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.board:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                            new GroupBoardFragment()).commit();
                    break;
                case R.id.chat:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                            new GroupChatFragment()).commit();
                    break;
            }
            return true;
        });
    }
}