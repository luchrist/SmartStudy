package com.example.smartstudy;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.smartstudy.adapters.CardAdapter;
import com.example.smartstudy.adapters.CardStatsAdapter;
import com.example.smartstudy.adapters.DeckAdapter;
import com.example.smartstudy.models.Card;
import com.example.smartstudy.models.Deck;
import com.example.smartstudy.utilities.CardSelectListener;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.DeckSelectListener;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditDeckFragment extends Fragment implements DeckSelectListener, CardSelectListener {
    private EditText deckNameTitle;
    private Button editBtn, statsBtn, createSubDeckBtn, createNewCardsBtn;
    private RecyclerView subDecksRecyclerView, cardsRecyclerView, statsCardsRecyclerView;
    private Spinner filterSpinner;
    private ImageButton filterBtn;
    ConstraintLayout editDeckLayout, statsLayout;
    private final Deck deck;
    private List<Deck> subDecks;
    private List<Card> cards, allCards, reallyGoodCards, goodCards, mediumCards, badCards, reallyBadCards, filteredCards;
    private DeckAdapter deckAdapter;
    private CardStatsAdapter cardStatsAdapter;
    private CardAdapter cardAdapter;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    private int selectedColor;

    public EditDeckFragment(Deck deck) {
        this.deck = deck;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(requireContext());
        selectedColor = ContextCompat.getColor(getContext(), R.color.accentBlue);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_deck, container, false);
        connectViews(view);
        setListeners();
        deckNameTitle.setText(deck.getName());
        subDecks = deck.getSubDecks();
        deckAdapter = new DeckAdapter(subDecks, this);
        subDecksRecyclerView.setAdapter(deckAdapter);

        allCards = deck.returnAllCards();
        sortCards();
        filteredCards = allCards;
        cardStatsAdapter = new CardStatsAdapter(filteredCards);
        statsCardsRecyclerView.setAdapter(cardStatsAdapter);

        cards = deck.getCards();
        cardAdapter = new CardAdapter(cards, this);
        cardsRecyclerView.setAdapter(cardAdapter);
        return view;
    }

    private void sortCards() {
        Stream.Builder<Card> reallyGoodCardBuilder = Stream.builder();
        goodCards = new ArrayList<>();
        mediumCards = new ArrayList<>();
        badCards = new ArrayList<>();
        reallyBadCards = new ArrayList<>();
        for (Card card : allCards) {
            switch (card.returnCorrectnessClass()) {
                case REALLY_GOOD:
                    reallyGoodCardBuilder.add(card);
                    break;
                case GOOD:
                    goodCards.add(card);
                    break;
                case MEDIUM:
                    mediumCards.add(card);
                    break;
                case BAD:
                    badCards.add(card);
                    break;
                case REALLY_BAD:
                    reallyBadCards.add(card);
                    break;
            }
        }
        reallyGoodCards = reallyGoodCardBuilder.build().collect(Collectors.toList());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setListeners() {
        editBtn.setOnClickListener(v -> {
            editDeckLayout.setVisibility(View.VISIBLE);
            statsLayout.setVisibility(View.GONE);
            editBtn.setBackground(getActivity().getDrawable(R.drawable.button_background_selected_left));
            statsBtn.setBackground(getActivity().getDrawable(R.drawable.button_background_not_selected_right));
            editBtn.setTextColor(selectedColor);
            statsBtn.setTextColor(Color.WHITE);
        });
        statsBtn.setOnClickListener(v -> {
            editDeckLayout.setVisibility(View.GONE);
            statsLayout.setVisibility(View.VISIBLE);
            editBtn.setBackground(getActivity().getDrawable(R.drawable.button_background_not_selected_left));
            statsBtn.setBackground(getActivity().getDrawable(R.drawable.button_background_selected_right));
            statsBtn.setTextColor(selectedColor);
            editBtn.setTextColor(Color.WHITE);
        });
        createSubDeckBtn.setOnClickListener(v -> {
            CreateDeckDialog dialog = new CreateDeckDialog(deck);
            dialog.show(getParentFragmentManager(), "Create Deck Dialog");
        });
    }

    private void connectViews(View view) {
        deckNameTitle = view.findViewById(R.id.deckNameTitle);
        editBtn = view.findViewById(R.id.deckEditBtn);
        editBtn.setTextColor(selectedColor);
        statsBtn = view.findViewById(R.id.statisticsBtn);
        createSubDeckBtn = view.findViewById(R.id.createSubDeck);
        createNewCardsBtn = view.findViewById(R.id.addNewCards);
        subDecksRecyclerView = view.findViewById(R.id.subDecksRecyclerView);
        cardsRecyclerView = view.findViewById(R.id.cardsRecyclerView);
        statsCardsRecyclerView = view.findViewById(R.id.cardsStatRecyclerView);
        filterSpinner = view.findViewById(R.id.filterCardsSpinner);
        filterBtn = view.findViewById(R.id.filterCardsBtn);
        editDeckLayout = view.findViewById(R.id.editDeckContent);
        statsLayout = view.findViewById(R.id.statisticsDeckContent);
    }

    @Override
    public void onStudyDeckSelected(Deck subDeck) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new PractiseFragment(subDeck)).commit();
    }

    @Override
    public void onEditDeckSelected(Deck subDeck) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new EditDeckFragment(subDeck)).commit();
    }

    @Override
    public void onDeleteDeckSelected(Deck subDeck) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Deck")
                .setMessage("Are you sure you want to delete this subDeck?")
                .setPositiveButton("Delete", (dialog1, which) -> {
                    db.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_EMAIL))
                            .collection(Constants.KEY_COLLECTION_DECKS)
                            .document(subDeck.getName())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                int index = subDecks.indexOf(subDeck);
                                subDecks.remove(subDeck);
                                deckAdapter.notifyItemRemoved(index);
                            })
                            .addOnFailureListener(e -> {
                                new AlertDialog.Builder(requireContext())
                                        .setTitle("Error")
                                        .setMessage("Failed to delete subDeck")
                                        .setPositiveButton("Ok", null)
                                        .show();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onTestDeckSelected(Deck subDeck) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new TestFragment(subDeck)).commit();
    }

    @Override
    public void onPauseCardSelected(Card card) {

    }

    @Override
    public void onEditCardSelected(Card card) {

    }

    @Override
    public void onDeleteCardSelected(Card card) {

    }
}