package com.example.smartstudy;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.adapters.ChatAdapter;
import com.example.smartstudy.models.ChatMessage;
import com.example.smartstudy.models.Group;
import com.example.smartstudy.models.Member;
import com.example.smartstudy.network.ApiService;
import com.example.smartstudy.network.FCMApiClient;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupChatActivity extends BaseActivity {

    Logger logger = Logger.getLogger(GroupChatActivity.class.getName());
    AppCompatImageView backNav;
    RecyclerView chatRecyclerView;
    EditText inputMsg;
    FrameLayout send;
    ProgressBar progressBar;
    TextView groupName;

    Member currentSender;
    private List<Member> members;
    private String currentUserMail, groupId;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        backNav = findViewById(R.id.backNavBtn);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        inputMsg = findViewById(R.id.inputMsg);
        send = findViewById(R.id.layoutSend);
        progressBar = findViewById(R.id.progress_bar);
        groupName = findViewById(R.id.groupName);

        loadReceiver();
        setListeners();
        init();
    }

    private void listenReceiversAvailability() {
       for (Member member: members) {
           updateMembersAvailability(member);
       }
    }

    private void updateMembersAvailability(Member member) {
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(member.email).addSnapshotListener(GroupChatActivity.this, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        if (value.getBoolean(Constants.KEY_AVAILABILITY) != null) {
                            member.isAvailable = value.getBoolean(Constants.KEY_AVAILABILITY);
                            member.token = value.getString(Constants.KEY_FCM_TOKEN);
                        }
                    }
                });
    }

    private void loadReceiver() {
        currentSender = (Member) getIntent().getSerializableExtra(Constants.KEY_SENDER);
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        groupName.setText(preferenceManager.getString(Constants.KEY_GROUP_NAME));
        currentUserMail = currentSender.email;
        groupId = preferenceManager.getString(Constants.KEY_GROUP_ID);
        db = FirebaseFirestore.getInstance();

        chatMessages = new ArrayList<>();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUserMail).get().addOnSuccessListener(documentSnapshot -> {
                            List<String> blockedBy = (List<String>) documentSnapshot.get(Constants.KEY_BLOCKED_BY);
                            List<String> blockedUsers = (List<String>) documentSnapshot.get(Constants.KEY_BLOCKED_USERS);
                            if (blockedBy == null) {
                                blockedBy = Collections.emptyList();
                            }
                            if (blockedUsers == null) {
                                blockedUsers = Collections.emptyList();
                            }
                            chatAdapter = new ChatAdapter(
                                    chatMessages,
                                    currentUserMail,
                                    blockedBy,
                                    blockedUsers
                            );
                            chatRecyclerView.setAdapter(chatAdapter);
                            listenMessages();
                        });

        db.collection(Constants.KEY_COLLECTION_GROUPS).document(groupId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Group group = documentSnapshot.toObject(Group.class);
                    if (group != null) {
                        members = group.members;
                    }
                });
    }

    private void sendMessage() {
        listenReceiversAvailability();
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, currentUserMail);
        message.put(Constants.KEY_SENDER_NAME, currentSender.name);
        message.put(Constants.KEY_MSG, inputMsg.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        message.put(Constants.KEY_SENDER_IMG, currentSender.image);
        message.put(Constants.KEY_GROUP_ID, groupId);

        db.collection(Constants.KEY_COLLECTION_CHATS).add(message);
        sendNotificationToUnavailableMembers(inputMsg.getText().toString());
        inputMsg.setText(null);
    }

    private void sendNotificationToUnavailableMembers(String msg) {
        JSONArray tokens = new JSONArray();
        for (Member member: members){
            if (!member.email.equals(currentUserMail) && !member.isAvailable && member.token != null && !member.token.trim().isEmpty()){
                tokens.put(member.token);
            }
        }
        try {
            JSONObject data = new JSONObject();
            data.put(Constants.KEY_EMAIL, currentUserMail);
            data.put(Constants.KEY_USER_NAME, currentSender.name);
            data.put(Constants.KEY_FCM_TOKEN, currentSender.token);
            data.put(Constants.KEY_GROUP_NAME, groupName.getText().toString().trim());
            data.put(Constants.KEY_GROUP_ID, groupId);
            data.put(Constants.KEY_MSG, msg);
            JSONObject body = new JSONObject();
            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);
            if (tokens.length() > 0){
                sendNotification(body.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void listenMessages() {
        db.collection(Constants.KEY_COLLECTION_CHATS)
                .whereEqualTo(Constants.KEY_GROUP_ID, groupId)
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.senderName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                    chatMessage.senderImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMG);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MSG);
                    chatMessage.dateTime = getReadableDate(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            chatMessages.sort((obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            chatRecyclerView.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    };

    private void setListeners() {
        backNav.setOnClickListener(v -> onBackPressed());
        send.setOnClickListener(v -> sendMessage());
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String message) {
        FCMApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                message
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()){
                    try {
                        if (response.body() != null){
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if (responseJson.getInt("failure") == 1){
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                   showToast("Error " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    private String getReadableDate(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}