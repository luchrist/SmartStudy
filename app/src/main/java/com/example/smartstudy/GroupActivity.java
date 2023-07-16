package com.example.smartstudy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.smartstudy.models.Group;
import com.example.smartstudy.models.Member;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private String encodedImage;
    AppCompatImageView back, chat, copyId;
    TextView groupName, groupId;
    RoundedImageView groupImage;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        preferenceManager = new PreferenceManager(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        group = new Group();

        back = findViewById(R.id.backNavBtn);
        chat = findViewById(R.id.chatBtn);
        copyId = findViewById(R.id.copyId);
        groupId = findViewById(R.id.groupId);
        groupName = findViewById(R.id.groupName);
        groupImage = findViewById(R.id.groupImage);

        db.collection(Constants.KEY_COLLECTION_GROUPS).document(preferenceManager.getString(Constants.KEY_GROUP_ID)).get().addOnSuccessListener(
                documentSnapshot -> {
                    group =  documentSnapshot.toObject(Group.class);
                    //group.image = documentSnapshot.getString(Constants.KEY_IMAGE);
                    group.name = documentSnapshot.getString(Constants.KEY_GROUP_NAME);
                    group.joinWithId = Boolean.TRUE.equals(documentSnapshot.getBoolean(Constants.KEY_JOIN_WITH_GROUP_ID));
                    group.id = documentSnapshot.getId();
                    if (group.image != null) {
                        groupImage.setImageBitmap(getGroupImage(group.image));
                    }
                    groupName.setText(group.name);
                    groupId.setText(String.format("ID: %s", group.id));

                    setListeners();
                }
        ).addOnFailureListener(e -> showToast("Group info not available"));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

        copyId.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, group.id);
            startActivity(Intent.createChooser(intent, "Share Group ID"));
        });

        groupImage.setOnClickListener(v -> {
            for (Member member : group.members) {
                if (member.email.equals(preferenceManager.getString(Constants.KEY_EMAIL))) {
                    if (member.isAdmin) {
                        chooseImage();
                    } else {
                        showToast("Only admin can change group image");
                    }
                }
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
    }

    private Bitmap getGroupImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        groupImage.setImageBitmap(bitmap);
                        encodedImage = encodeImage(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}