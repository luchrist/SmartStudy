package com.example.smartstudy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.adapters.GroupsAdapter;
import com.example.smartstudy.models.Group;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.GroupSelectListener;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupFragment extends Fragment implements GroupSelectListener {

    private SearchView searchView;
    private AppCompatImageView addBtn;
    ProgressBar progressBar;
    TextView errorMsg;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    private List<Group> groups = new ArrayList<>();
    GroupsAdapter groupsAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.groupsRecyclerView);
        errorMsg = view.findViewById(R.id.errorMsg);
        addBtn = view.findViewById(R.id.addGroupBtn);

        groupsAdapter = new GroupsAdapter(groups, this);
        recyclerView.setAdapter(groupsAdapter);
        showGroups();
        setListeners();
        return view;
    }

    private void setListeners() {
        addBtn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CreateGroupActivity.class));
        });
    }

    private void showGroups() {
        loading(true);
        db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_EMAIL))
                .collection(Constants.KEY_COLLECTION_GROUPS).get()
                .addOnCompleteListener(task -> {
                    List<String> groupIds;
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot documents = task.getResult();
                        groupIds = (List<String>) documents.getDocuments().stream().map(DocumentSnapshot::getId).collect(Collectors.toList());
                        if (groupIds.size() > 0) {
                            getGroupInfos(groupIds);
                        } else {
                            showErrorMsg();
                        }
                    } else {
                        showErrorMsg();
                    }
                });
    }

    private void getGroupInfos(List<String> groupIds) {
        CollectionReference groupCollection = db.collection(Constants.KEY_COLLECTION_GROUPS);
        for (int i = 0; i < groupIds.size(); i++) {
            groupCollection.document(groupIds.get(i))
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                                Group group = new Group();
                                group.name = document.getString(Constants.KEY_GROUP_NAME);
                                group.image = document.getString(Constants.KEY_IMAGE);
                                group.id = document.getId();
                                groups.add(group);
                                recyclerView.setVisibility(View.VISIBLE);
                                groupsAdapter.notifyItemInserted(groups.size() - 1);
                        } else {
                            showErrorMsg();
                        }
                                loading(false);
                    });
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(requireContext());
        db = FirebaseFirestore.getInstance();
    }

    private void showErrorMsg() {
        errorMsg.setText(R.string.no_groups_available);
        errorMsg.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /*@Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setIconified(true);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // SearchView.OnQueryTextListener
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something when user press the search button
                mySearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Do something when user type something in the search box
                mySearch(newText);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void mySearch(String newText) {
    }*/

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGroupSelected(Group group) {
        preferenceManager.putString(Constants.KEY_GROUP_ID, group.id);
        preferenceManager.putString(Constants.KEY_GROUP_NAME, group.name);
        startActivity(new Intent(getContext(), GroupActivity.class));
    }
}