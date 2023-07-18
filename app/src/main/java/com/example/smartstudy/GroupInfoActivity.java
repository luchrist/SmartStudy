package com.example.smartstudy;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.adapters.MembersAdapter;
import com.example.smartstudy.adapters.UsersAdapter;
import com.example.smartstudy.models.Group;
import com.example.smartstudy.models.Member;
import com.example.smartstudy.models.User;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.example.smartstudy.utilities.SelectListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupInfoActivity extends AppCompatActivity implements SelectListener {
    ProgressBar progressBar;
    AppCompatImageView back, chat;
    TextView errorMsg, createdInfo;
    Button exitGroup;
    CheckBox checkBox;
    RecyclerView recyclerView;
    PreferenceManager preferenceManager;
    String currentUserEmail, groupId;
    List<Member> members;
    MembersAdapter membersAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.usersRecyclerView);
        errorMsg = findViewById(R.id.errorMsg);
        back = findViewById(R.id.backNavBtn);
        chat = findViewById(R.id.chatBtn);
        exitGroup = findViewById(R.id.exitGroup);
        checkBox = findViewById(R.id.addExamsToPlan);
        createdInfo = findViewById(R.id.createdInfo);
        preferenceManager = new PreferenceManager(getApplicationContext());
        currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);
        groupId = preferenceManager.getString(Constants.KEY_GROUP_ID);
        showUsers();
        setListeners();
    }

    private void setListeners() {
        chat.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupChatActivity.class);
            startActivity(intent);
            this.finish();
        });
        back.setOnClickListener(v -> {
            startActivity(new Intent(this, GroupActivity.class));
            finish();
        });
        exitGroup.setOnClickListener(v -> {
            removeCurrentMemberFromGroup();
        });
    }

    private void removeCurrentMemberFromGroup() {
        db.collection(Constants.KEY_COLLECTION_USERS).document(currentUserEmail)
                .update(Constants.KEY_GROUP_ID, FieldValue.arrayRemove(groupId))
                .addOnSuccessListener(d -> System.out.println("Group id removed from user"))
                .addOnFailureListener(e -> System.out.println("Failed to remove group id from user"));
        for (Member member : members) {
            if (member.email.equals(currentUserEmail)) {
                db.collection(Constants.KEY_COLLECTION_GROUPS).document(groupId)
                        .update(Constants.KEY_MEMBERS, FieldValue.arrayRemove(member))
                        .addOnSuccessListener(d -> {
                            startActivity(new Intent(this, MainActivity.class));
                        });
            }
        }
    }

    private void showUsers() {
        loading(true);
        db.collection(Constants.KEY_COLLECTION_GROUPS)
                .document(groupId)
                .get()
                .addOnSuccessListener(doc -> {
                    Group group = doc.toObject(Group.class);
                    if (group != null) {
                        members = group.members;
                        if (members.size() > 0) {
                            membersAdapter = new MembersAdapter(members, this, currentUserEmail);
                            recyclerView.setAdapter(membersAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                            createdInfo.setText(String.format("Created %s by %s", group.createdTime, group.createdBy));
                        } else {
                            showErrorMsg();
                        }
                    }

                    loading(false);
                }).addOnFailureListener(doc -> {
                    showErrorMsg();
                    loading(false);
                });
    }

    private void showErrorMsg() {
        errorMsg.setText(R.string.no_user_available);
        errorMsg.setVisibility(View.VISIBLE);
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemClicked(Member member) {
        if (member.email.equals(currentUserEmail)) {
            return;
        }
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);
        if (member.isAdmin) {
            showAdminDialog(member, dialog);
        } else {
            showStandartDialog(member, dialog);
        }
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

    private void showAdminDialog(Member member, Dialog dialog) {
        TextView dismissAdmin = dialog.findViewById(R.id.makeGroupAdmin);
        dismissAdmin.setText(R.string.dismiss_as_admin);
        int removeColor = ContextCompat.getColor(this, R.color.remove);
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
            int pos = members.indexOf(member);
            members.remove(member);
            membersAdapter.notifyItemRemoved(pos);
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
    }
}