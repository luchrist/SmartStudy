package com.example.smartstudy;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.smartstudy.adapters.CardAdapter;
import com.example.smartstudy.adapters.CardStatsAdapter;
import com.example.smartstudy.adapters.DeckAdapter;
import com.example.smartstudy.models.Card;
import com.example.smartstudy.models.CardType;
import com.example.smartstudy.models.Deck;
import com.example.smartstudy.utilities.CardSelectListener;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.DeckSelectListener;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditDeckFragment extends Fragment implements DeckSelectListener, CardSelectListener {
    private EditText deckNameTitle;
    private Button editBtn, statsBtn, createSubDeckBtn, createNewCardsBtn;
    private AppCompatImageButton exitBtn;
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
    private DocumentReference masterDeckDoc;
    private String[] pathParts;

    public EditDeckFragment(Deck deck) {
        this.deck = deck;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(requireContext());
        selectedColor = ContextCompat.getColor(getContext(), R.color.accentBlue);
        pathParts = deck.getPath().split(":");
        masterDeckDoc = db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_EMAIL))
                .collection(Constants.KEY_COLLECTION_DECKS)
                .document(pathParts[0]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_deck, container, false);
        connectViews(view);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.cardCategories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);
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


    //need to change also documentname and parentDeckName of underlying subdecks
    @Override
    public void onDestroy() {
        super.onDestroy();
        String deckName = deckNameTitle.getText().toString().trim();
        if(!deckName.equals(deck.getName())){
            if(pathParts.length == 1) {
                masterDeckDoc.delete();
                deck.setName(deckName);
                deck.setPath(deckName);
                for (Deck subDeck : deck.getSubDecks()) {
                    subDeck.setPath(String.format("%s:%s", deckName, subDeck.getName()));
                }
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(preferenceManager.getString(Constants.KEY_EMAIL))
                        .collection(Constants.KEY_COLLECTION_DECKS).document(deckName).set(deck);
            } else {
                updatePathsOfDeckAndSubDecks(deckName);
                updateDeckInSubDeck(deckName);
            }
        }
    }

    private void updatePathsOfDeckAndSubDecks(String deckName) {
        String path = deck.getPath();
        String[] pathParts = path.split(":");
        pathParts[pathParts.length -1] = deckName;
        String newPath = String.join(":", pathParts);
        deck.setPath(newPath);
        updatePathsOfSubDecks(deck.getSubDecks(), deck.getPath());
    }

    private void updatePathsOfSubDecks(List<Deck> subDecks, String prePath) {
        for (Deck subDeck : subDecks) {
            subDeck.setPath(String.format("%s:%s", prePath, subDeck.getName()));
            if(subDeck.getSubDecks() != null) {
                updatePathsOfSubDecks(subDeck.getSubDecks(), subDeck.getPath());
            }
        }
    }

    private void setListeners() {
        exitBtn.setOnClickListener(v -> {
            if (pathParts.length == 1) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new StudyFragment()).commit();
            } else {
                editParentDeck();
            }
        });
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
        createNewCardsBtn.setOnClickListener(v -> {
            CreateCardDialog dialog = new CreateCardDialog(deck);
            dialog.show(getParentFragmentManager(), "Create Card Dialog");
        });
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        filteredCards = allCards.stream().filter(card -> card.getTotalRequests() == 0).collect(Collectors.toList());
                        break;
                    case 4:
                        filteredCards = Stream.concat(reallyGoodCards.stream(), goodCards.stream()).collect(Collectors.toList());
                        break;
                    case 3:
                        filteredCards = mediumCards.stream().filter(card -> card.getTotalRequests() > 0).collect(Collectors.toList());
                        break;
                    case 2:
                        filteredCards = Stream.concat(badCards.stream(), reallyBadCards.stream()).collect(Collectors.toList());
                        break;
                    default:
                        filteredCards = allCards;
                        break;
                }
                cardStatsAdapter = new CardStatsAdapter(filteredCards);
                statsCardsRecyclerView.setAdapter(cardStatsAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        filterBtn.setOnClickListener(v -> {
            filterSpinner.performClick();
        });
    }

    private void editParentDeck() {
        masterDeckDoc.get().addOnSuccessListener(documentSnapshot -> {
            Deck foundDeck = documentSnapshot.toObject(Deck.class);
                for (int i = 1; i < pathParts.length-1; i++) {
                    int finalI = i;
                    foundDeck = foundDeck.getSubDecks().stream()
                            .filter(subDeck -> subDeck.getName().equals(pathParts[finalI]))
                            .findFirst().get();
                }
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new EditDeckFragment(foundDeck)).commit();
        });
    }

    private void connectViews(View view) {
        deckNameTitle = view.findViewById(R.id.deckNameTitle);
        editBtn = view.findViewById(R.id.deckEditBtn);
        editBtn.setTextColor(selectedColor);
        statsBtn = view.findViewById(R.id.statisticsBtn);
        createSubDeckBtn = view.findViewById(R.id.createSubDeck);
        //createSubDeckBtn.setVisibility(View.INVISIBLE);
        exitBtn = view.findViewById(R.id.exitBtn);
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
                    int index = subDecks.indexOf(subDeck);
                    subDecks.remove(subDeck);
                    deckAdapter.notifyItemRemoved(index);
                    deck.setSubDecks(subDecks);
                    if(pathParts.length == 1) {
                        masterDeckDoc.update(Constants.KEY_SUB_DECKS, subDecks);
                    } else {
                        updateDeckInSubDeck(null);
                    }})
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
        int i = cards.indexOf(card);
        card.setPaused(!card.isPaused());
        cards.set(i, card);
        cardAdapter.notifyItemChanged(i);
        deck.setCards(cards);
        if(pathParts.length == 1) {
            masterDeckDoc.update(Constants.KEY_CARDS, cards);
        } else {
            updateDeckInSubDeck(null);
        }
    }

    @Override
    public void onEditCardSelected(Card card) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.card_edit_dialog, null);
        EditText front = view.findViewById(R.id.frontOfCard);
        EditText back = view.findViewById(R.id.backOfCard);
        RadioButton basic = view.findViewById(R.id.basicType);
        RadioButton match = view.findViewById(R.id.matchType);
        front.setText(card.getFront());
        back.setText(card.getBack());
        if(card.getType().equals(CardType.BASIC)) {
            basic.setChecked(true);
        } else {
            match.setChecked(true);
        }
        new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Card content")
                .setPositiveButton("OK", (dialog, which) -> {
                    int i = cards.indexOf(card);
                    String frontString = front.getText().toString().trim();
                    String backString = back.getText().toString().trim();
                    CardType cardType = CardType.BASIC;
                    if(match.isChecked()) {
                        cardType = CardType.MATCHING;
                    }
                    card.setFront(frontString);
                    card.setBack(backString);
                    card.setType(cardType);
                    cards.set(i, card);
                    cardAdapter.notifyItemChanged(i);
                    deck.setCards(cards);
                    if(pathParts.length == 1) {
                        masterDeckDoc.update(Constants.KEY_CARDS, cards);
                    } else {
                        updateDeckInSubDeck(null);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateDeckInSubDeck(String deckName) {
        masterDeckDoc.get()
                .addOnSuccessListener(documentSnapshot -> {
                    Deck masterDeck = documentSnapshot.toObject(Deck.class);
                    ArrayList<Deck> decks = new ArrayList<>();
                    decks.add(masterDeck);
                    if (pathParts.length > 2) {
                        for (int i = 1; i < pathParts.length-1; i++) {
                            int finalI = i;
                            decks.add(decks.get(decks.size()-1).getSubDecks().stream()
                                    .filter(subDeck -> subDeck.getName().equals(pathParts[finalI]))
                                    .findFirst().get());
                        }
                    }
                    decks.add(deck);
                    List<Deck> masterSubs = new ArrayList<>();
                    for (int j = decks.size() -1; j > 0; j--) {
                        int finalJ = j;
                        masterSubs = decks.get(j-1).getSubDecks().stream()
                                .filter(subDeck -> !subDeck.getName().equals(decks.get(finalJ).getName()))
                                .collect(Collectors.toList());
                        if(deckName != null && decks.get(j).getName().equals(deck.getName())) {
                            decks.get(j).setName(deckName);
                            deck.setName(deckName);
                        }
                        masterSubs.add(decks.get(j));
                        decks.get(j-1).setSubDecks(masterSubs);
                    }
                    masterDeck.setSubDecks(masterSubs);
                    masterDeckDoc.set(masterDeck);
                }).addOnFailureListener(e -> {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Error")
                            .setMessage("Failed to update deck. Please try again.")
                            .setPositiveButton("Ok", null)
                            .show();
                    });
    }

    @Override
    public void onDeleteCardSelected(Card card) {
        int i = cards.indexOf(card);
        cards.remove(card);
        cardAdapter.notifyItemRemoved(i);
        deck.setCards(cards);
        if(pathParts.length == 1) {
            masterDeckDoc.update(Constants.KEY_CARDS, FieldValue.arrayRemove(card));
        } else {
            updateDeckInSubDeck(null);
        }
    }
}