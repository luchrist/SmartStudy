package de.christcoding.smartstudy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import de.christcoding.smartstudy.adapters.DeckAdapter;
import de.christcoding.smartstudy.models.Deck;
import de.christcoding.smartstudy.utilities.Constants;
import de.christcoding.smartstudy.utilities.DeckSelectListener;
import de.christcoding.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StudyFragment extends Fragment implements DeckSelectListener {
    Button createDeck, aiTest;
    ProgressBar decksProgressBar;
    TextView errorMsg;
    RecyclerView decksRecyclerView;

    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    private List<Deck> decks = new ArrayList<>();
    private DeckAdapter deckAdapter;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.study_fragment, container, false);
        createDeck = view.findViewById(R.id.createDeck);
        aiTest = view.findViewById(R.id.aiTest);
        decksProgressBar = view.findViewById(R.id.decksProgressBar);
        errorMsg = view.findViewById(R.id.errorText);
        decksRecyclerView = view.findViewById(R.id.decksRecyclerView);

        preferenceManager = new PreferenceManager(requireContext());
        db = FirebaseFirestore.getInstance();

        showDecks();
        setListeners();
        return view;
    }

    private void showDecks() {
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_EMAIL))
                .collection(Constants.KEY_COLLECTION_DECKS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    decksProgressBar.setVisibility(View.GONE);
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : documentSnapshots) {
                            Deck deck = snapshot.toObject(Deck.class);
                            decks.add(deck);
                        }
                        deckAdapter = new DeckAdapter(decks, this);
                        decksRecyclerView.setAdapter(deckAdapter);
                    } else {
                        errorMsg.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    decksProgressBar.setVisibility(View.GONE);
                    errorMsg.setVisibility(View.VISIBLE);
                });
    }

    private void setListeners() {
        createDeck.setOnClickListener(v -> {
            CreateDeckDialog dialog = new CreateDeckDialog(null);
            dialog.show(getParentFragmentManager(), "Create Deck Dialog");
        });
        aiTest.setOnClickListener(v -> startActivity(new Intent(this.getActivity(), AiGenerateExam.class)));
    }

    @Override
    public void onStudyDeckSelected(Deck deck) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new PractiseFragment(deck)).commit();
    }

    @Override
    public void onEditDeckSelected(Deck deck) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new EditDeckFragment(deck)).commit();
    }

    @Override
    public void onDeleteDeckSelected(Deck deck) {
       new AlertDialog.Builder(requireContext())
                .setTitle("Delete Deck")
                .setMessage("Are you sure you want to delete this deck?")
                .setPositiveButton("Delete", (dialog1, which) -> {
                    db.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_EMAIL))
                            .collection(Constants.KEY_COLLECTION_DECKS)
                            .document(deck.getName())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                int index = decks.indexOf(deck);
                                decks.remove(deck);
                                deckAdapter.notifyItemRemoved(index);
                            })
                            .addOnFailureListener(e -> {
                                new AlertDialog.Builder(requireContext())
                                        .setTitle("Error")
                                        .setMessage("Failed to delete deck")
                                        .setPositiveButton("Ok", null)
                                        .show();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onTestDeckSelected(Deck deck) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new TestFragment(deck)).commit();
    }
}
