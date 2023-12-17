package de.christcoding.smartstudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import de.christcoding.smartstudy.utilities.PreferenceManager;

public class GetNotifiedActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_notified);
        preferenceManager = new PreferenceManager(getApplicationContext());
        LinearLayout understand = findViewById(R.id.understand);
        understand.setOnClickListener(v -> {
            requestNotificationPermission();
        });
    }

    private void requestNotificationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Check if permission is granted or not
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission is denied

                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(GetNotifiedActivity.this, CreateGroupActivity.class));
            finish();
        }
    }
}