package com.example.smartstudy;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartstudy.adapters.MembersAdapter;
import com.example.smartstudy.models.Group;
import com.example.smartstudy.models.Member;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.example.smartstudy.utilities.SelectListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupFragment extends Fragment implements SelectListener {

    private String encodedImage;
    RoundedImageView groupImage;
    EditText groupNameInput, memberEmailInput;
    AppCompatImageView addMemberImage;
    CheckBox allowJoiningCheck;
    RecyclerView membersRecyclerView;
    Button createGroupButton;
    PreferenceManager preferenceManager = new PreferenceManager(getContext());

    String currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);
    List<Member> members = new ArrayList<>();
    MembersAdapter membersAdapter = new MembersAdapter(members, this, currentUserEmail);

    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);

        groupImage = view.findViewById(R.id.groupImg);
        groupNameInput = view.findViewById(R.id.groupNameInput);
        memberEmailInput = view.findViewById(R.id.memberMailInput);
        addMemberImage = view.findViewById(R.id.addMemberBtn);
        allowJoiningCheck = view.findViewById(R.id.joiningAllowedCheckbox);
        membersRecyclerView = view.findViewById(R.id.memberRecyclerView);
        membersRecyclerView.setAdapter(membersAdapter);
        createGroupButton = view.findViewById(R.id.createGroupBtn);

        setListeners();
        findMemberByEmail(currentUserEmail, true);

        return view;
    }

    private void setListeners() {
        groupImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

        addMemberImage.setOnClickListener(v -> {
            if (memberEmailInput.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), "Enter email", Toast.LENGTH_SHORT).show();
            } else {
                addMember();
            }
        });
        createGroupButton.setOnClickListener(v -> {
            if (groupNameInput.getText().toString().trim().isEmpty()) {
                showToast("Enter group name");
            } else {
                createGroup();
            }
        });
    }

    private void createGroup() {
        Group group = new Group();
        group.name = groupNameInput.getText().toString().trim();
        group.image = encodedImage;
        group.members = members;
        group.joinWithId = allowJoiningCheck.isChecked();
        db.collection(Constants.KEY_COLLECTION_GROUPS).
                add(group).addOnSuccessListener(documentReference -> startActivity(new Intent(getContext(), GroupActivity.class))).addOnFailureListener(e -> showToast("Failed to create group"));
    }

    private void addMember() {
        String email = memberEmailInput.getText().toString().trim();
        if (email.equals(currentUserEmail)) {
            showToast("You can't add yourself");
        } else if (checkIfMemberAlreadyExists(email)) {
           showToast("Member is already added");
        } else if (checkIfInvalidEmail(email)) {
            showToast("Enter valid email");
        } else {
            findMemberByEmail(email, false);
        }

    }

    private boolean checkIfMemberAlreadyExists(String email) {
        for (Member member : members) {
            if (member.email.equals(email)) {
                return true;
            }
        }
        return false;
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


    private boolean checkIfInvalidEmail(String email) {
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void findMemberByEmail(String email, boolean isAdmin) {
        CollectionReference userCol = db.collection(Constants.KEY_COLLECTION_USERS);
        userCol.document(email).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot result = task.getResult();
                        if (result.getString(Constants.KEY_USER_NAME) == null) {
                            showToast("No user found with this email");
                            return;
                        }
                        Member member = new Member();
                        member.email = email;
                        member.name = result.getString(Constants.KEY_USER_NAME);
                        member.image = result.getString(Constants.KEY_IMAGE);
                        member.token = result.getString(Constants.KEY_FCM_TOKEN);
                        member.isAdmin = isAdmin;
                        members.add(member);
                        membersAdapter.notifyItemInserted(members.size() - 1);
                    }
                });
        memberEmailInput.setText("");
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
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
    public void onItemClicked(Member member) {
        if (member.email.equals(currentUserEmail)) {
            return;
        }
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);
        if(member.isAdmin){
            showAdminDialog(member, dialog);
        } else {
            showStandartDialog(member, dialog);
        }
    }

    private void showAdminDialog(Member member, Dialog dialog) {
        TextView dismissAdmin = dialog.findViewById(R.id.makeGroupAdmin);
        dismissAdmin.setText(R.string.dismiss_as_admin);
        int removeColor =  ContextCompat.getColor(getActivity(), R.color.remove);
        dismissAdmin.setTextColor(removeColor);
        prepareDialog(member, dialog);

        dismissAdmin.setOnClickListener(v -> {
            dialog.dismiss();
            int i = members.indexOf(member);
            member.isAdmin = false;
            members.set(i, member);
            membersAdapter.notifyItemChanged(i);
        });
        showDialog(dialog);
    }

    private void showStandartDialog(Member member, Dialog dialog) {
        TextView makeAdmin = dialog.findViewById(R.id.makeGroupAdmin);
        prepareDialog(member, dialog);

        makeAdmin.setOnClickListener(v -> {
            dialog.dismiss();
            int i = members.indexOf(member);
            member.isAdmin = true;
            members.set(i, member);
            membersAdapter.notifyItemChanged(i);
        });
        showDialog(dialog);
    }

    private void showDialog(Dialog dialog) {
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void prepareDialog(Member member, Dialog dialog) {
        TextView name = dialog.findViewById(R.id.username);
        name.setText(member.name);
        TextView removeMember = dialog.findViewById(R.id.remove);
        TextView cancel = dialog.findViewById(R.id.cancel);
        removeMember.setOnClickListener(v -> {
            dialog.dismiss();
            members.remove(member);
            membersAdapter.notifyItemRemoved(members.indexOf(member));
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
    }
}