package com.example.smartstudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.smartstudy.adapters.UsersAdapter;
import com.example.smartstudy.models.User;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupInfoActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView errorMsg;
    RecyclerView recyclerView;
    PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.usersRecyclerView);
        errorMsg = findViewById(R.id.errorMsg);
        preferenceManager = new PreferenceManager(getApplicationContext());
        showUsers();
        setListeners();
    }

    private void setListeners() {

    }

    private void showUsers() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> test = db.collection(Constants.KEY_COLLECTION_USERS)
                .whereArrayContains(Constants.KEY_GROUP_ID, "test")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Stream.Builder<User> groupMember = Stream.builder();
                        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (currentUserId.equals(document.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.name = document.getString(Constants.KEY_USER_NAME);
                            user.image = document.getString(Constants.KEY_IMAGE);
                            user.token = document.getString(Constants.KEY_FCM_TOKEN);
                            user.email = document.getString(Constants.KEY_EMAIL);
                            groupMember.accept(user);
                        }
                        List<User> member = groupMember.build().collect(Collectors.toList());
                        if (member.size() > 0) {
                            UsersAdapter usersAdapter = new UsersAdapter(member);
                            loading(false);
                            recyclerView.setAdapter(usersAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMsg();
                        }
                    } else {
                        showErrorMsg();
                    }
                });
    }

    private void showErrorMsg() {
        errorMsg.setText(R.string.no_user_available);
        errorMsg.setVisibility(View.VISIBLE);
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}