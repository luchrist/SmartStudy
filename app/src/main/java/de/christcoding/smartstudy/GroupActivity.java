package de.christcoding.smartstudy;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import de.christcoding.smartstudy.dialogs.SaveDescription;
import de.christcoding.smartstudy.models.Event;
import de.christcoding.smartstudy.models.Group;
import de.christcoding.smartstudy.models.Member;
import de.christcoding.smartstudy.utilities.Constants;
import de.christcoding.smartstudy.utilities.PreferenceManager;
import de.christcoding.smartstudy.utilities.Util;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class GroupActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener { //implements SaveFileGroupName.FileGroupNameDialogInterface {

    private String encodedImage, currentUserEmail, currentGroupId;
    private int shownUploadedFiles = 0;
    HashMap<Integer, Integer> fileLayouts = new HashMap<>();
    private boolean isCurrentUserAdmin = false;
    TableLayout tableLayout;
    EditText type, subject, mostImportantInfo;
    TextView date;
    LinearLayout filesLayout;
    ConstraintLayout uploadFiles;
    AppCompatImageView back, chat, copyId, description, addEvent;
    TextView groupName, groupId;
    TableLayout uploadedFilesTable;
    RoundedImageView groupImage;
    ProgressBar uploadProgress;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ref = storage.getReference();
    StorageReference groupFilesRef;
    private Group group;
    //private HashMap<String, Integer> categoryLayouts = new HashMap<String, Integer>();
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri file = data.getData();
                        uploadFile(file);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        preferenceManager = new PreferenceManager(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        group = new Group();
        currentGroupId = preferenceManager.getString(Constants.KEY_GROUP_ID);
        currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);
        groupFilesRef = ref.child("groupFiles").child(currentGroupId);

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
        mostImportantInfo = findViewById(R.id.mIIMultiLine);
        uploadFiles = findViewById(R.id.uploadFile);
        uploadProgress = findViewById(R.id.fileUploadProgressBar);
        filesLayout = findViewById(R.id.eventsAndFilesLayout);
        uploadedFilesTable = findViewById(R.id.uploadedFilesTable);

        uploadProgress.setVisibility(View.VISIBLE);
        groupFilesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference file : listResult.getItems()) {
                        String fileName = file.getName();
                        file.getMetadata().addOnSuccessListener(fileMetadata -> {
                            String fileSizeInCorrectUnit = getFileSizeInCorrectUnit(fileMetadata.getSizeBytes());
                            addFileToTable(fileName, fileSizeInCorrectUnit);
                        });
                    }
                    uploadProgress.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    uploadProgress.setVisibility(View.GONE);
                });

        db.collection(Constants.KEY_COLLECTION_GROUPS).document(currentGroupId).get().addOnSuccessListener(
                documentSnapshot -> {
                    group = documentSnapshot.toObject(Group.class);
                    group.id = documentSnapshot.getId();
//                    group.name = documentSnapshot.getString(Constants.KEY_GROUP_NAME);
//                    group.joinWithId = Boolean.TRUE.equals(documentSnapshot.getBoolean(Constants.KEY_JOIN_WITH_GROUP_ID));
                    if (group.image != null) {
                        groupImage.setImageBitmap(getGroupImage(group.image));
                    }
                    groupName.setText(group.name);
                    preferenceManager.putString(Constants.KEY_GROUP_NAME, group.name);
                    groupId.setText(String.format("Group-ID: %s", group.id));

                    for (Member member : group.members) {
                        if (member.email.equals(currentUserEmail)) {
                            if (!member.isAdmin) {
                                addEvent.setVisibility(View.GONE);
                                description.setVisibility(View.GONE);
                                type.setVisibility(View.GONE);
                                subject.setVisibility(View.GONE);
                                date.setVisibility(View.GONE);
                                mostImportantInfo.setEnabled(false);
                            } else {
                                isCurrentUserAdmin = true;
                            }
                            break;
                        }
                    }
                    mostImportantInfo.setText(group.mostImportantInformation);
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

        typeEntry.setText(event.getType());
        typeEntry.setTextColor(ContextCompat.getColor(this, R.color.primaryVariant));
        typeEntry.setTextSize(getResources().getDimension(R.dimen.tableTextSize));
        typeEntry.setPadding((int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding));
        typeEntry.setTypeface(Typeface.DEFAULT_BOLD);

        subjectEntry.setText(event.getSubject());
        subjectEntry.setTextColor(ContextCompat.getColor(this, R.color.primaryVariant));
        subjectEntry.setTextSize(getResources().getDimension(R.dimen.tableTextSize));
        subjectEntry.setPadding((int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding));
        subjectEntry.setTypeface(Typeface.DEFAULT_BOLD);

        dateEntry.setText(event.getEndDate());
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

        if (!isCurrentUserAdmin) {
            descriptionEntry.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.description_focal_points);
                builder.setMessage(event.getDescription());
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder.show();
            });
        } else {
            descriptionEntry.setOnClickListener(v -> {
                SaveDescription alert = new SaveDescription(group, event);
                alert.show(getSupportFragmentManager(), "test");
            });
            delete.setImageResource(R.drawable.baseline_delete_24);
            delete.setPadding((int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding), (int) getResources().getDimension(R.dimen.tablePadding));
            delete.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.background_icon_light));
            delete.setOnClickListener(v -> {
                tableLayout.removeView(tableRow);
                List<Event> events = group.events;
                events.remove(event);
                db.collection(Constants.KEY_COLLECTION_GROUPS).document(currentGroupId)
                        .update(Constants.KEY_EVENTS, events).addOnFailureListener(e -> {
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
        date.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerActivityFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });

        uploadFiles.setOnClickListener(v -> {
            //Create Intent
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            //intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            //Launch activity to get result
            someActivityResultLauncher.launch(intent);
        });
        mostImportantInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String mII = s.toString().trim();
                db.collection(Constants.KEY_COLLECTION_GROUPS).document(currentGroupId)
                        .update(Constants.KEY_MOST_IMPORTANT_INFO, mII).addOnFailureListener(e -> {
                            showToast("Failed to update most important info");
                            e.printStackTrace();
                        });
            }
        });
        addEvent.setOnClickListener(v -> {
            Event event = collectEventData();
            addEventToTable(event);
            addEventToDb(event);
            type.setText(null);
            subject.setText(null);
            date.setText(null);
        });
        description.setOnClickListener(v -> {
            SaveDescription alert = new SaveDescription(group, null);
            alert.show(getSupportFragmentManager(), "test");
        });
        groupName.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupInfoActivity.class);
            startActivity(intent);
        });
        chat.setOnClickListener(v -> {
            for (Member member : group.members) {
                if (member.email.equals(currentUserEmail)) {
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

    private void uploadFile(Uri file) {
        StorageReference fileRef = groupFilesRef.child(file.getLastPathSegment());
        UploadTask uploadTask = fileRef.putFile(file);
        uploadFiles.setVisibility(View.GONE);
        uploadProgress.setVisibility(View.VISIBLE);

        uploadTask.addOnFailureListener(e -> {
            showToast("Failed to upload file");
            e.printStackTrace();
            Log.d("UploadFile", e.getMessage());
            uploadFiles.setVisibility(View.VISIBLE);
            uploadProgress.setVisibility(View.GONE);
        }).addOnSuccessListener(taskSnapshot -> {
            uploadFiles.setVisibility(View.VISIBLE);
            uploadProgress.setVisibility(View.GONE);
            showToast("File uploaded" + file.getLastPathSegment());
            addFileToTable(file.getLastPathSegment(), getFileSizeInCorrectUnit(taskSnapshot.getMetadata().getSizeBytes()));
        });
    }

    private void addFileToTable(String fileName, String sizeBytes) {
        TableRow downloadFileRow = (TableRow) getLayoutInflater().inflate(R.layout.download_file, null);
        uploadedFilesTable.addView(downloadFileRow);
        TextView fileNameText = downloadFileRow.findViewById(R.id.file_name);
        fileNameText.setText(fileName);
        TextView fileSizeText = downloadFileRow.findViewById(R.id.file_size);
        fileSizeText.setText(sizeBytes);
        shownUploadedFiles++;

        AppCompatImageView deleteFile = downloadFileRow.findViewById(R.id.delete_file);
        AppCompatImageView downloadFile = downloadFileRow.findViewById(R.id.downloadBtn);

        if (isCurrentUserAdmin) {
            deleteFile.setOnClickListener(v -> {
                uploadedFilesTable.removeView(downloadFileRow);
                shownUploadedFiles--;
                groupFilesRef.child(fileName).delete().addOnFailureListener(e -> {
                    showToast("Failed to delete file");
                    e.printStackTrace();
                });
            });
        } else {
            deleteFile.setVisibility(View.GONE);
        }

        downloadFile.setOnClickListener(v -> {
            groupFilesRef.child(fileName).getDownloadUrl().addOnSuccessListener(uri -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }).addOnFailureListener(e -> {
                showToast("Failed to download file");
                e.printStackTrace();
            });
        });
    }

    private String getFileSizeInCorrectUnit(long sizeBytes) {
        if (sizeBytes > 1024) {
            if (sizeBytes > 1024 * 1024) {
                if (sizeBytes > 1024 * 1024 * 1024) {
                    return (sizeBytes / (1024 * 1024 * 1024)) + " GB";
                } else {
                    return (sizeBytes / (1024 * 1024)) + " MB";
                }
            } else {
                return (sizeBytes / 1024) + " KB";
            }
        } else {
            return sizeBytes + " B";
        }
    }

    private void addEventToDb(Event event) {
        event.setEndDate(Util.getFormattedDateForDB(event.getEndDate()));
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        LocalDate chosenDate = LocalDate.of(year, month+1, dayOfMonth);
        date.setText(Util.getFormattedDate(chosenDate));
    }

   /* @Override
    public void showUploadedFileInCategory(String fileName, String sizeBytes, Category category, boolean newCategory) {
        if (newCategory) {
            TextView textView = new TextView(this);
            textView.setText(category.category);
            filesLayout.addView(textView);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayout.setId(View.generateViewId());
            category.layoutId = linearLayout.getId();*
            showUploadedFileInProvidedLayout(fileName, sizeBytes, linearLayout);
        } else {
            LinearLayout linearLayout = findViewById(category.layoutId);
            showUploadedFileInProvidedLayout(fileName, sizeBytes, linearLayout);
        }
    }
*/
}