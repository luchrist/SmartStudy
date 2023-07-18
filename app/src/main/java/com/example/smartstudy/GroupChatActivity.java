package com.example.smartstudy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.smartstudy.adapters.ChatAdapter;
import com.example.smartstudy.models.ChatMessage;
import com.example.smartstudy.models.Group;
import com.example.smartstudy.models.Member;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GroupChatActivity extends AppCompatActivity {

    AppCompatImageView backNav;
    RecyclerView chatRecyclerView;
    EditText inputMsg;
    FrameLayout send;

    List<Member> receivers = new ArrayList<>();
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

        setListeners();
        init();
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        currentUserMail = preferenceManager.getString(Constants.KEY_EMAIL);
        db = FirebaseFirestore.getInstance();

        db.collection(Constants.KEY_COLLECTION_GROUPS).document(preferenceManager.getString(Constants.KEY_GROUP_ID))
                .get().addOnSuccessListener(documentSnapshot -> {
                    Group group = documentSnapshot.toObject(Group.class);
                    for (Member member : group.members){
                        if(!currentUserMail.equals(member.email)) {
                            receivers.add(member);
                        }
                    }
                        }
                ).addOnFailureListener(e -> e.printStackTrace());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                decodeString(receivers.get(0).image),
                currentUserMail
        );
        chatRecyclerView.setAdapter(chatAdapter);

    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, currentUserMail);
        message.put(Constants.KEY_RECEIVER_IDS, receivers.get(0).email);
        message.put(Constants.KEY_MSG, inputMsg.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());

        db.collection(Constants.KEY_COLLECTION_CHATS).add(message);
        inputMsg.setText(null);
    }
    private Bitmap decodeString(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void setListeners() {
        backNav.setOnClickListener(v -> onBackPressed());
        send.setOnClickListener(v -> sendMessage());
    }
}