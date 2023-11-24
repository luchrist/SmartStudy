package com.example.smartstudy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.Builder.EventBuilder;
import com.example.smartstudy.adapters.MembersAdapter;
import com.example.smartstudy.models.Event;
import com.example.smartstudy.models.Group;
import com.example.smartstudy.models.Member;
import com.example.smartstudy.models.Report;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.example.smartstudy.utilities.SelectListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GroupInfoActivity extends BaseActivity implements SelectListener {
    ProgressBar progressBar;
    AppCompatImageView back, chat, addMember;
    TextView errorMsg, createdInfo, groupName;
    EditText memberInput;
    Button exitGroup;
    CheckBox checkBox;
    RecyclerView recyclerView;
    PreferenceManager preferenceManager;
    String currentUserEmail, groupId;
    boolean currentUserIsAdmin = false;
    List<Member> members;
    MembersAdapter membersAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean changedOnCreate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.usersRecyclerView);
        errorMsg = findViewById(R.id.errorMsg);
        memberInput = findViewById(R.id.memberMailInput);
        addMember = findViewById(R.id.addMemberBtn);
        back = findViewById(R.id.backNavBtn);
        chat = findViewById(R.id.chatBtn);
        exitGroup = findViewById(R.id.exitGroup);
        groupName = findViewById(R.id.groupName);
        checkBox = findViewById(R.id.addExamsToPlan);
        createdInfo = findViewById(R.id.createdInfo);
        preferenceManager = new PreferenceManager(getApplicationContext());
        currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);
        groupId = preferenceManager.getString(Constants.KEY_GROUP_ID);
        groupName.setText(preferenceManager.getString(Constants.KEY_GROUP_NAME));

        setCheckbox();
        showUsers();
        setListeners();
    }

    private void setCheckbox() {
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUserEmail)
                .collection(Constants.KEY_COLLECTION_GROUPS)
                .document(groupId).get().addOnSuccessListener(documentSnapshot -> {
                    Boolean addExamsToPlan = (Boolean) documentSnapshot.get(Constants.KEY_ADD_EXAMS_TO_PLAN);
                    if (addExamsToPlan != checkBox.isChecked()) {
                        changedOnCreate = true;
                        checkBox.setChecked(addExamsToPlan);
                    }
                });
    }

    private void setListeners() {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (changedOnCreate) {
                changedOnCreate = false;
                return;
            }
            if (isChecked) {
                addExamsToPlan();
            } else {
                removeExamsFromPlan();
            }
        });

        chat.setOnClickListener(v -> {
            Member currentMember = new Member();
            for (Member member : members) {
                if (member.email.equals(currentUserEmail)) {
                    currentMember = member;
                    break;
                }
            }
            Intent intent = new Intent(this, GroupChatActivity.class);
            intent.putExtra(Constants.KEY_SENDER, currentMember);
            startActivity(intent);
        });
        back.setOnClickListener(v -> {
            onBackPressed();
        });
        exitGroup.setOnClickListener(v -> {
            Member currentMember = new Member();
            int pos = 0;
            for (Member member : members) {
                if (member.email.equals(currentUserEmail)) {
                    removeMemberFromGroup(member);
                    currentMember = member;
                    break;
                }
                pos++;
            }

            members.remove(currentMember);
            membersAdapter.notifyItemRemoved(pos);
            for (Member member : members) {
                if (member.isAdmin) {
                    return;
                }
            }
            if (members.size() > 0) {
                members.get(0).isAdmin = true;
                membersAdapter.notifyItemChanged(0);
                updateMembersInDb(false);
            } else {
                deleteGroup();
            }
        });
        addMember.setOnClickListener(v -> {
            if (memberInput.getText().toString().trim().isEmpty()) {
                showToast("Enter email");
            } else {
                addMember();
            }
        });
    }

    private void removeExamsFromPlan() {
        db.collection(Constants.KEY_COLLECTION_USERS).document(currentUserEmail)
                .collection(Constants.KEY_COLLECTION_GROUPS).document(groupId).update(Constants.KEY_ADD_EXAMS_TO_PLAN, false)
                .addOnSuccessListener(unused -> {
                    showToast("Exams will not be added to your plan");
                    try (DBEventHelper dbEventHelper = new DBEventHelper(getApplicationContext())) {
                        db.collection(Constants.KEY_COLLECTION_GROUPS).document(groupId)
                                .get().addOnSuccessListener(documentSnapshot -> {
                                    Group group = documentSnapshot.toObject(Group.class);
                                    for (Event event : group.events) {
                                        dbEventHelper.deleteEventObject(new EventBuilder().setId(String.valueOf(event.getDbId())).build());
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    showToast("Failed to remove exams from plan");
                                });
                    }
                }).addOnFailureListener(e -> {
                    showToast("Failed to remove exams from plan");
                });
    }

    private void addExamsToPlan() {
        db.collection(Constants.KEY_COLLECTION_USERS).document(currentUserEmail)
                .collection(Constants.KEY_COLLECTION_GROUPS).document(groupId).update(Constants.KEY_ADD_EXAMS_TO_PLAN, true)
                .addOnSuccessListener(unused -> {
                    showToast("Exams will be added to your plan");
                    try (DBEventHelper dbEventHelper = new DBEventHelper(getApplicationContext())) {
                        db.collection(Constants.KEY_COLLECTION_GROUPS).document(groupId)
                                .get().addOnSuccessListener(documentSnapshot -> {
                                    Group group = documentSnapshot.toObject(Group.class);
                                    if (group.events != null) {
                                        for (Event event : group.events) {
                                            List<Event> events = group.events;
                                            events.remove(event);
                                            event.setNecessaryMissingAttributes(groupId);
                                            long eventID = dbEventHelper.addEventObject(event);
                                            event.setDbId(eventID);
                                            events.add(event);
                                            db.collection(Constants.KEY_COLLECTION_GROUPS).document(groupId)
                                                    .update(Constants.KEY_EVENTS, events);
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    showToast("Failed to add exams to plan");
                                });
                    }
                }).addOnFailureListener(e -> {
                    showToast("Failed to add exams to plan");
                });

    }

    private void deleteGroup() {
        db.collection(Constants.KEY_COLLECTION_GROUPS).document(groupId).delete();
    }

    private void addMember() {
        String email = memberInput.getText().toString().trim();
        if (email.equals(currentUserEmail)) {
            showToast("You can't add yourself");
        } else if (checkIfMemberAlreadyExists(email)) {
            showToast("Member is already added");
        } else if (checkIfInvalidEmail(email)) {
            showToast("Enter valid email");
        } else {
            findMemberByEmail(email);
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

    private boolean checkIfInvalidEmail(String email) {
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void findMemberByEmail(String email) {
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
                member.isAdmin = false;
                members.add(member);
                membersAdapter.notifyItemInserted(members.size() - 1);
                updateMembersInDb(true);
                updateGroupIdsInUserDb(email);
            }
        });
        memberInput.setText("");
    }

    private void updateGroupIdsInUserDb(String email) {
        HashMap<String, Boolean> addExamsToPlan = new HashMap<>();
        addExamsToPlan.put(Constants.KEY_ADD_EXAMS_TO_PLAN, false);
        db.collection(Constants.KEY_COLLECTION_USERS).document(email).collection(Constants.KEY_COLLECTION_GROUPS)
                .document(groupId).set(addExamsToPlan)
                .addOnSuccessListener(d -> System.out.println("Group id added to user"))
                .addOnFailureListener(e -> showToast("Failed to add group id to user"));
    }

    private void updateMembersInDb(boolean added) {
        db.collection(Constants.KEY_COLLECTION_GROUPS).document(groupId)
                .update(Constants.KEY_MEMBERS, members)
                .addOnSuccessListener(d -> {
                    System.out.println("Member changed");
                })
                .addOnFailureListener(e -> {
                    if (added) {
                        memberInput.setError("Failed to add member in database");
                    } else {
                        showToast("Failed to update member in database");
                    }
                });

    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void removeMemberFromGroup(Member member) {
        db.collection(Constants.KEY_COLLECTION_USERS).document(member.email).collection(Constants.KEY_COLLECTION_GROUPS)
                .document(groupId).delete()
                .addOnSuccessListener(d -> System.out.println("Group id removed from user"))
                .addOnFailureListener(e -> System.out.println("Failed to remove group id from user"));

        db.collection(Constants.KEY_COLLECTION_GROUPS).document(groupId)
                .update(Constants.KEY_MEMBERS, FieldValue.arrayRemove(member))
                .addOnSuccessListener(d -> {
                    if (member.email.equals(currentUserEmail)) {
                        showToast("You left the group");
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        showToast("Member removed");
                    }
                });
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
                            db.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(currentUserEmail).get().addOnSuccessListener(documentSnapshot -> {
                                        List<String> blockedBy = (List<String>) documentSnapshot.get(Constants.KEY_BLOCKED_BY);
                                        if (blockedBy == null) {
                                            blockedBy = Collections.emptyList();
                                        }
                                        membersAdapter = new MembersAdapter(members, this, currentUserEmail, blockedBy);
                                        recyclerView.setAdapter(membersAdapter);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        createdInfo.setText(String.format("Created %s by %s", group.createdTime, group.createdBy));
                                        for (Member member : members) {
                                            if (member.email.equals(currentUserEmail)) {
                                                currentUserIsAdmin = member.isAdmin;
                                            }
                                        }
                                        if (!currentUserIsAdmin) {
                                            memberInput.setVisibility(View.GONE);
                                            addMember.setVisibility(View.GONE);
                                        }
                                            });
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
        TextView block = dialog.findViewById(R.id.block);
        DocumentReference currentUserDoc = db.collection(Constants.KEY_COLLECTION_USERS).document(currentUserEmail);
        currentUserDoc.get().addOnSuccessListener(documentSnapshot -> {
            List<String> blockedUsers = (List<String>) documentSnapshot.get(Constants.KEY_BLOCKED_USERS);
            if (blockedUsers != null) {
                if (blockedUsers.contains(member.email)) {
                    block.setText(R.string.unblock);
                    int positivColor = ContextCompat.getColor(this, R.color.accentBlue);
                    block.setTextColor(positivColor);
                    block.getCompoundDrawables()[0].setTint(positivColor);
                }
            }
        });

        TextView report = dialog.findViewById(R.id.report);
        report.setOnClickListener(v -> {
            dialog.dismiss();
            reportUser(member);
        });
        block.setOnClickListener(v -> {
            dialog.dismiss();
            blockUser(member);
        });
        if (currentUserIsAdmin) {
            if (member.isAdmin) {
                showAdminDialog(member, dialog);
            } else {
                showStandardDialog(member, dialog);
            }
        } else {
            showBaseDialog(member, dialog);
        }
    }

    private void blockUser(Member member) {
        DocumentReference currentUserDoc = db.collection(Constants.KEY_COLLECTION_USERS).document(currentUserEmail);
        DocumentReference memberDoc = db.collection(Constants.KEY_COLLECTION_USERS).document(member.email);
        currentUserDoc.get().addOnSuccessListener(documentSnapshot -> {
            List<String> blockedUsers = (List<String>) documentSnapshot.get(Constants.KEY_BLOCKED_USERS);
            if (blockedUsers != null) {
                if (blockedUsers.contains(member.email)) {
                    currentUserDoc.update(Constants.KEY_BLOCKED_USERS, FieldValue.arrayRemove(member.email));
                    memberDoc.update(Constants.KEY_BLOCKED_BY, FieldValue.arrayRemove(currentUserEmail));
                    showToast("Unblocked");
                    showUsers();
                    return;
                }
            }
            currentUserDoc.update(Constants.KEY_BLOCKED_USERS, FieldValue.arrayUnion(member.email));
            memberDoc.update(Constants.KEY_BLOCKED_BY, FieldValue.arrayUnion(currentUserEmail));
            showToast("Blocked");
            showUsers();
        });
    }

    private void reportUser(Member member) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.report_dialog)
                .setTitle(String.format("%s %s",getString(R.string.report),member.name))
                // Pass null as the parent view because its going in the dialog layout
                .setPositiveButton("Report", null)
                .setNegativeButton("Cancel", null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button report = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            report.setTextColor(getResources().getColor(R.color.remove));
            Button cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            cancel.setTextColor(getResources().getColor(R.color.primaryVariant));

            report.setOnClickListener(v -> {
                CheckBox messages = dialog.findViewById(R.id.messagesBox);
                CheckBox files = dialog.findViewById(R.id.filesBox);
                CheckBox events = dialog.findViewById(R.id.eventsBox);
                CheckBox content = dialog.findViewById(R.id.contentBox);
                CheckBox spam = dialog.findViewById(R.id.spamBox);
                EditText description = dialog.findViewById(R.id.descriptionInput);
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                Report currentReport = new Report(currentUserEmail, description.getText().toString(), member, timeStamp, false,
                        messages.isChecked(), content.isChecked(), files.isChecked(), events.isChecked(), spam.isChecked());
                db.collection(Constants.KEY_COLLECTION_REPORTS).add(currentReport)
                        .addOnSuccessListener(documentReference -> {
                            showToast("Reported");
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            showToast("Failed to report");
                            dialog.dismiss();
                        });
            });
        });
        dialog.show();
    }

    private void showBaseDialog(Member member, Dialog dialog) {
        dialog.findViewById(R.id.adminPart).setVisibility(View.GONE);
        prepareDialog(member, dialog);
        showDialog(dialog);
    }

    private void showStandardDialog(Member member, Dialog dialog) {
        TextView makeAdmin = dialog.findViewById(R.id.makeGroupAdmin);
        prepareDialog(member, dialog);

        makeAdmin.setOnClickListener(v -> {
            dialog.dismiss();
            int i = members.indexOf(member);
            member.isAdmin = true;
            members.set(i, member);
            membersAdapter.notifyItemChanged(i);
            updateMembersInDb(false);
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
            updateMembersInDb(false);
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
        TextView name = dialog.findViewById(R.id.user);
        name.setText(member.name);
        TextView removeMember = dialog.findViewById(R.id.remove);
        TextView cancel = dialog.findViewById(R.id.cancel);
        removeMember.setOnClickListener(v -> {
            dialog.dismiss();
            int pos = members.indexOf(member);
            members.remove(member);
            membersAdapter.notifyItemRemoved(pos);
            removeMemberFromGroup(member);
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
    }
}