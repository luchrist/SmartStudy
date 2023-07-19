package com.example.smartstudy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.adapters.ChatAdapter;
import com.example.smartstudy.models.ChatMessage;
import com.example.smartstudy.models.Group;
import com.example.smartstudy.models.Member;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GroupChatActivity extends AppCompatActivity {

    AppCompatImageView backNav;
    RecyclerView chatRecyclerView;
    EditText inputMsg;
    FrameLayout send;
    ProgressBar progressBar;
    TextView groupName;

    Member receiver;
    private String currentUserMail;
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

        loadReceiverId();
        setListeners();
        init();
        listenMessages();
    }

    private void loadReceiverId() {
        receiver = (Member) getIntent().getSerializableExtra(Constants.KEY_RECEIVER);
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        groupName.setText(preferenceManager.getString(Constants.KEY_GROUP_NAME));
        currentUserMail = preferenceManager.getString(Constants.KEY_EMAIL);
        db = FirebaseFirestore.getInstance();

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                decodeString(receiver.image),
                currentUserMail
        );
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, currentUserMail);
        message.put(Constants.KEY_RECEIVER_IDS, receiver.email);
        message.put(Constants.KEY_MSG, inputMsg.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());

        db.collection(Constants.KEY_COLLECTION_CHATS).add(message);
        inputMsg.setText(null);
    }

    private void listenMessages() {
        db.collection(Constants.KEY_COLLECTION_CHATS)
                .whereEqualTo(Constants.KEY_SENDER_ID, currentUserMail)
                .whereEqualTo(Constants.KEY_RECEIVER_IDS, receiver.email)
                .addSnapshotListener(eventListener);
        db.collection(Constants.KEY_COLLECTION_CHATS)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiver.email)
                .whereEqualTo(Constants.KEY_RECEIVER_IDS, currentUserMail)
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
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MSG);
                    chatMessage.dateTime = getReadableDate(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessage.receiversId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IDS);
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

    private Bitmap decodeString(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void setListeners() {
        backNav.setOnClickListener(v -> onBackPressed());
        send.setOnClickListener(v -> sendMessage());
    }

    private String getReadableDate(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}