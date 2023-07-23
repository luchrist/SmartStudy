package com.example.smartstudy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.ArraySet;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.example.smartstudy.dialogs.SaveDescription;
import com.example.smartstudy.models.Event;
import com.example.smartstudy.models.Group;
import com.example.smartstudy.models.Member;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupActivity extends AppCompatActivity {

    private String encodedImage, currentUserEmail, currentGroupId;
    private boolean isCurrentUserAdmin = false;
    TableLayout tableLayout;
    EditText type, subject, date;
    AppCompatImageView back, chat, copyId, description, addEvent;
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
        currentGroupId = preferenceManager.getString(Constants.KEY_GROUP_ID);
        currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);

        type = findViewById(R.id.typeInput);
        subject = findViewById(R.id.subjectInput);
        date = findViewById(R.id.dateInput);
        description = findViewById(R.id.description);
        addEvent = findViewById(R.id.addEvent);
        tableLayout = findViewById(R.id.tableLayout);
        back = findViewById(R.id.backNavBtn);
        chat = findViewById(R.id.chatBtn);
        copyId = findViewById(R.id.copyId);
        groupId = findViewById(R.id.groupId);
        groupName = findViewById(R.id.groupName);
        groupImage = findViewById(R.id.groupImage);

        db.collection(Constants.KEY_COLLECTION_GROUPS).document(currentGroupId).get().addOnSuccessListener(
                documentSnapshot -> {
                    group =  documentSnapshot.toObject(Group.class);
//                    group.name = documentSnapshot.getString(Constants.KEY_GROUP_NAME);
//                    group.joinWithId = Boolean.TRUE.equals(documentSnapshot.getBoolean(Constants.KEY_JOIN_WITH_GROUP_ID));
                    group.id = documentSnapshot.getId();
                    if (group.image != null) {
                        groupImage.setImageBitmap(getGroupImage(group.image));
                    }
                    groupName.setText(group.name);
                    groupId.setText(String.format("ID: %s", group.id));
                    for (Member member : group.members) {
                        if (member.email.equals(currentUserEmail)) {
                            if (!member.isAdmin) {
                                addEvent.setVisibility(View.GONE);
                                description.setVisibility(View.GONE);
                                type.setVisibility(View.GONE);
                                subject.setVisibility(View.GONE);
                                date.setVisibility(View.GONE);
                            }else {
                                isCurrentUserAdmin = true;
                            }
                            break;
                        }
                    }
                    fillEventTable(group);

                    setListeners();
                }
        ).addOnFailureListener(e -> showToast("Group info not available"));
    }

    private void fillEventTable(Group group) {
        List<Event> events = group.events;
        if (events != null) {
            for (Event event : events) {
                addEventToTable(event);
            }
        }
    }

    private void addEventToTable(Event event) {
        TableRow tableRow = new TableRow(this);
        TextView typeEntry = new TextView(this);
        TextView subjectEntry = new TextView(this);
        TextView dateEntry = new TextView(this);
        AppCompatImageView descriptionEntry = new AppCompatImageView(this);
        AppCompatImageView delete = new AppCompatImageView(this);

        typeEntry.setText(event.type);
        typeEntry.setTextColor(ContextCompat.getColor(this, R.color.primaryVariant));
        typeEntry.setTextSize(getResources().getDimension(R.dimen.tableTextSize));
        typeEntry.setPadding((int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding));
        typeEntry.setTypeface(Typeface.DEFAULT_BOLD);

        subjectEntry.setText(event.subject);
        subjectEntry.setTextColor(ContextCompat.getColor(this, R.color.primaryVariant));
        subjectEntry.setTextSize(getResources().getDimension(R.dimen.tableTextSize));
        subjectEntry.setPadding((int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding));
        subjectEntry.setTypeface(Typeface.DEFAULT_BOLD);

        dateEntry.setText(event.date);
        dateEntry.setTextColor(ContextCompat.getColor(this, R.color.primaryVariant));
        dateEntry.setTextSize(getResources().getDimension(R.dimen.tableTextSize));
        dateEntry.setPadding((int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding));
        dateEntry.setTypeface(Typeface.DEFAULT_BOLD);

        descriptionEntry.setImageResource(R.drawable.baseline_description_24);
        descriptionEntry.setPadding((int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding));
        //add all views together
        tableRow.addView(typeEntry);
        tableRow.addView(subjectEntry);
        tableRow.addView(dateEntry);
        tableRow.addView(descriptionEntry);

        if(!isCurrentUserAdmin) {
            descriptionEntry.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.description_focal_points);
                builder.setMessage(event.description);
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder.show();
            });
        } else {
            descriptionEntry.setOnClickListener(v -> {
                SaveDescription alert = new SaveDescription(event.description);
                alert.show(getSupportFragmentManager(), "test");
                event.description = preferenceManager.getString(Constants.KEY_DESCRIPTION);
                db.collection(Constants.KEY_COLLECTION_GROUPS).document(currentGroupId)
                        .update(Constants.KEY_EVENTS, FieldValue.arrayRemove(event)).addOnSuccessListener(unused -> {
                            db.collection(Constants.KEY_COLLECTION_GROUPS).document(currentGroupId)
                                    .update(Constants.KEY_EVENTS, FieldValue.arrayUnion(event)).addOnSuccessListener(unused1 -> {
                                        showToast("Event updated");
                                    }).addOnFailureListener(e -> {
                                        showToast("Failed to update event to db");
                                        e.printStackTrace();
                                    });
                        }).addOnFailureListener(e -> {
                            showToast("Failed to update event to db");
                            e.printStackTrace();
                        });
            });
            delete.setImageResource(R.drawable.baseline_delete_24);
            delete.setPadding((int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding));
            delete.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.background_icon_light));
            delete.setOnClickListener(v -> {
                tableLayout.removeView(tableRow);
                db.collection(Constants.KEY_COLLECTION_GROUPS).document(currentGroupId)
                        .update(Constants.KEY_EVENTS, FieldValue.arrayRemove(event)).addOnFailureListener(e -> {
                            showToast("Failed to delete event from db");
                            e.printStackTrace();
                        });
            });
            tableRow.addView(delete);
        }
        tableLayout.addView(tableRow);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setListeners() {
        addEvent.setOnClickListener(v -> {
            Event event = collectEventData();
            addEventToTable(event);
            addEventToDb(event);
            type.setText(null);
            subject.setText(null);
            date.setText(null);
        });
        description.setOnClickListener(v -> {
            SaveDescription alert = new SaveDescription(null);
            alert.show(getSupportFragmentManager(), "test");
        });
        groupName.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupInfoActivity.class);
            startActivity(intent);
        });
        chat.setOnClickListener(v -> {
            for (Member member : group.members) {
                if(member.email.equals(currentUserEmail)) {
                    Member currentMember = member;
                    Intent intent = new Intent(this, GroupChatActivity.class);
                    intent.putExtra(Constants.KEY_SENDER, currentMember);
                    startActivity(intent);
                }
            }

        });
        back.setOnClickListener(v -> {
            startActivity(new Intent(GroupActivity.this, MainActivity.class));
            this.finish();
        });

        copyId.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, group.id);
            startActivity(Intent.createChooser(intent, "Share Group ID"));
        });

        groupImage.setOnClickListener(v -> {
            for (Member member : group.members) {
                if (member.email.equals(currentUserEmail)) {
                    if (member.isAdmin) {
                        chooseImage();
                    } else {
                        showToast("Only admin can change group image");
                    }
                }
            }
        });
    }

    private void addEventToDb(Event event) {
        db.collection(Constants.KEY_COLLECTION_GROUPS).document(currentGroupId)
                .update(Constants.KEY_EVENTS, FieldValue.arrayUnion(event)).addOnFailureListener(e -> {
                    showToast("Failed to add event to db");
                    e.printStackTrace();
                });
    }

    private Event collectEventData() {
        String typeText = type.getText().toString().trim();
        String subjectText = subject.getText().toString().trim();
        String dateText = date.getText().toString().trim();
        String descriptionText = preferenceManager.getString(Constants.KEY_DESCRIPTION);
        preferenceManager.putString(Constants.KEY_DESCRIPTION, "");

        return new Event(typeText, subjectText, dateText, descriptionText);
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