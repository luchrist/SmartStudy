package com.example.smartstudy;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.smartstudy.models.Group;
import com.example.smartstudy.models.Member;
import com.example.smartstudy.models.User;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class JoinGroupFragment extends Fragment {

    Button joinGroupBtn;
    EditText groupId;
    private PreferenceManager preferenceManager;
    String currentUserMail;
    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_join_group, container, false);
        joinGroupBtn = view.findViewById(R.id.joinGroupBtn);
        groupId = view.findViewById(R.id.groupIdInput);

        preferenceManager = new PreferenceManager(requireContext());
        currentUserMail = preferenceManager.getString(Constants.KEY_EMAIL);

        setListeners();
        return view;
    }

    private void setListeners() {
        joinGroupBtn.setOnClickListener(v -> {
            String groupIdText = groupId.getText().toString().trim();
            if (groupIdText.isEmpty()) {
                groupId.setError("Enter Group ID");
            } else {
                db = FirebaseFirestore.getInstance();
                findGroup(groupIdText);
            }
        });
    }


    private void findGroup(String groupIdText) {
        db.collection(Constants.KEY_COLLECTION_GROUPS).document(groupIdText).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Group group = documentSnapshot.toObject(Group.class);
                if (group != null) {
                    if (group.members != null && group.members.size() > 0) {
                        if (isNotAlreadyMember(group)) {
                            getCurrentUserAndAddToGroup(groupIdText, group);
                        }
                    } else {
                        groupId.setError("Group does not exist or is empty");
                    }
                }
            } else {
                groupId.setError("Group does not exist");
            }
        }).addOnFailureListener(e -> groupId.setError("Something went wrong"));
    }

    private void getCurrentUserAndAddToGroup(String groupIdText, Group group) {
        db.collection(Constants.KEY_COLLECTION_USERS).document(currentUserMail).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                User user = document.toObject(User.class);
                if (user != null) {
                    Member member = createMember(user);
                    group.members.add(member);
                    addMemberToGroupAndStartActivity(groupIdText, group.members);
                } else {
                    groupId.setError("User does not exist");
                }
            } else {
                groupId.setError("User does not exist");
            }
        }).addOnFailureListener(e -> groupId.setError("Something went wrong"));
    }

    private void addMemberToGroupAndStartActivity(String groupIdText, List<Member> members) {
        db.collection(Constants.KEY_COLLECTION_GROUPS).document(groupIdText).update(Constants.KEY_MEMBERS, members).addOnSuccessListener(none -> {
            groupId.setText("");
            groupId.clearFocus();
            preferenceManager.putString(Constants.KEY_GROUP_ID, groupIdText);
            startActivity(new Intent(getContext(), GroupActivity.class));
        }).addOnFailureListener(e -> groupId.setError("Something went wrong"));
    }

    private Member createMember(User user) {
        Member member = new Member();
        member.email = currentUserMail;
        member.name = user.userName;
        member.isAdmin = false;
        member.token = user.fcmToken;
        member.image = user.image;
        return member;
    }

    private boolean isNotAlreadyMember(Group group) {
        for (Member member : group.members){
            if(member.email.equals(currentUserMail)) {
                groupId.setError("You are already a member of this group");
                return false;
            }
        }
        return true;
    }
}