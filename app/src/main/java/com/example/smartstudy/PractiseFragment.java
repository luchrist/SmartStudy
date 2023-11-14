package com.example.smartstudy;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.example.smartstudy.models.Card;
import com.example.smartstudy.models.CardType;
import com.example.smartstudy.models.Deck;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


public class PractiseFragment extends Fragment {

    private TextView frontCard;
    private TextView correctnessRatio;
    private EditText backCard;
    private AppCompatButton mediumButton;
    private AppCompatImageButton correct, wrong, submitAnswer, flipCard, continueButton;
    private final Deck deck;
    private Deck deckOfCard;
    private Card currentCard;
    private List<Card> allCards, reallyBadCards, badCards, mediumCards, goodCards, reallyGoodCards;
    private int cardCount;
    private double correctAnswers;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    private String currentUserMail;
    private DocumentReference deckDoc;
    private String[] pathParts;

    public PractiseFragment(Deck deck) {
        this.deck = deck;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practise, container, false);
        frontCard = view.findViewById(R.id.front);
        backCard = view.findViewById(R.id.back);
        TextView deckName = view.findViewById(R.id.colName);
        correctnessRatio = view.findViewById(R.id.ratio);
        mediumButton = view.findViewById(R.id.almostCorrectBtn);
        correct = view.findViewById(R.id.correctBtn);
        wrong = view.findViewById(R.id.wrongBtn);
        submitAnswer = view.findViewById(R.id.submitAns);
        flipCard = view.findViewById(R.id.flip);
        continueButton = view.findViewById(R.id.continueBtn);
        deckName.setText(deck.getName());
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getContext());
        currentUserMail = preferenceManager.getString(Constants.KEY_EMAIL);

        pathParts = deck.getPath().split(":");
        deckDoc = db.collection(Constants.KEY_COLLECTION_USERS).document(currentUserMail)
                .collection(Constants.KEY_COLLECTION_DECKS).document(pathParts[0]);

        setListeners();

        allCards = deck.returnAllCards();
        if (allCards.size() == 0) {
            frontCard.setText("No cards in this deck, please add some cards before.");
            return view;
        }
        sortCards();
        showCard(getCardDependingOnDifficultyInPast());

        return view;
    }

    private void sortCards() {
        reallyGoodCards = new ArrayList<>();
        goodCards = new ArrayList<>();
        mediumCards = new ArrayList<>();
        badCards = new ArrayList<>();
        reallyBadCards = new ArrayList<>();
        for (Card card : allCards) {
            pushCardToCorrectList(card);
        }
    }

    private void pullCardFromList(Card card) {
        int certainty = card.getCertainty();
        if (certainty < -3) {
            reallyBadCards.remove(card);
        } else if (certainty < -1) {
            badCards.remove(card);
        } else if (certainty > 3) {
            reallyGoodCards.remove(card);
        } else if (certainty > 1) {
            goodCards.remove(card);
        } else {
            mediumCards.remove(card);
        }
    }

    private void pushCardToCorrectList(Card card) {
        int certainty = card.getCertainty();
        if (certainty < -3) {
            reallyBadCards.add(card);
        } else if (certainty < -1) {
            badCards.add(card);
        } else if (certainty > 3) {
            reallyGoodCards.add(card);
        } else if (certainty > 1) {
            goodCards.add(card);
        } else {
            mediumCards.add(card);
        }
    }

    private void setListeners() {
        correct.setOnClickListener(v -> {
            if (currentCard.getType().equals(CardType.BASIC)) {
                cardCount++;
                correctAnswers++;
                correctnessRatio.setText(correctAnswers + "/" + cardCount);
                upRankCard();
                showCard(getCardDependingOnDifficultyInPast());
                addPoints(10);
            }
        });
        wrong.setOnClickListener(v -> {
            if (currentCard.getType().equals(CardType.BASIC)) {
                cardCount++;
                correctnessRatio.setText(correctAnswers + "/" + cardCount);
                downRankCard();
                showCard(getCardDependingOnDifficultyInPast());
            }
        });
        mediumButton.setOnClickListener(v -> {
            if (currentCard.getType().equals(CardType.BASIC)) {
                cardCount++;
                correctAnswers += 0.5;
                correctnessRatio.setText(correctAnswers + "/" + cardCount);
                pullCardFromList(currentCard);
                if (currentCard.getCertainty() < 0) {
                    currentCard.setCertainty(currentCard.getCertainty() + 1);
                } else {
                    currentCard.setCertainty(currentCard.getCertainty() - 1);
                }
                pushCardToCorrectList(currentCard);
                currentCard.incrementMediumAnswers();
                currentCard.incrementTotalRequests();
                findCardsDeckAndUpdateDeck(currentCard);
                showCard(getCardDependingOnDifficultyInPast());
                addPoints(5);
            }
        });
        flipCard.setOnClickListener(v -> {
            if (currentCard.getType().equals(CardType.BASIC)) {
                backCard.setVisibility(View.VISIBLE);
                backCard.setText(currentCard.getBack());
                backCard.setClickable(false);
            }
        });
        submitAnswer.setOnClickListener(v -> {
            if (currentCard.getType().equals(CardType.MATCHING)) {
                cardCount++;
                if (backCard.getText().toString().trim().equalsIgnoreCase(currentCard.getBack())) {
                    correctAnswers++;
                    upRankCard();
                    showCard(getCardDependingOnDifficultyInPast());
                    addPoints(10);
                } else {
                    backCard.setError("Correct answer: " + currentCard.getBack());
                    continueButton.setVisibility(View.VISIBLE);
                    downRankCard();
                }
                correctnessRatio.setText(correctAnswers + "/" + cardCount);
            }
        });

        continueButton.setOnClickListener(v -> {
            if (currentCard.getType().equals(CardType.MATCHING)) {
                showCard(getCardDependingOnDifficultyInPast());
                continueButton.setVisibility(View.GONE);
                backCard.setError(null);
            }
        });
    }

    private void upRankCard() {
        pullCardFromList(currentCard);
        currentCard.setCertainty(currentCard.getCertainty() + 2);
        if (currentCard.getCertainty() > 5) {
            currentCard.setCertainty(5);
        }
        pushCardToCorrectList(currentCard);
        currentCard.incrementTotalRequests();
        currentCard.incrementRightAnswers();
        findCardsDeckAndUpdateDeck(currentCard);
    }

    private void findCardsDeckAndUpdateDeck(Card currentC) {
        deckDoc.get().addOnSuccessListener(documentSnapshot -> {
            Deck deck = documentSnapshot.toObject(Deck.class);
            Optional<Card> foundCard = Optional.empty();
            List<Card> deckCards = null;
            if (deck != null) {
                deckCards = deck.getCards();
                if (deckCards != null) {
                    foundCard = deckCards.stream()
                            .filter(card -> checkEquality(card, currentC))
                            .findFirst();
                }
                if (foundCard.isPresent()) {
                    List<Card> cards = deck.getCards();
                    cards.remove(foundCard.get());
                    cards.add(currentC);
                    deck.setCards(cards);
                    pathParts = deck.getPath().split(":");
                    updateDeckInSubDeck(deck);
                } else {
                    updateCardInSubDeck(deck, deck.getSubDecks(), currentC);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error while updating card certainty", Toast.LENGTH_SHORT).show();
        });
    }

    private void incrementRightStack(Card card, int i) {
        if (i == 1 || i == -1) {
            card.incrementMediumAnswers();
        } else if (i == 2) {
            card.incrementRightAnswers();
        } else if (i == -2) {
            card.incrementWrongAnswers();
        }
    }

    private boolean checkEquality(Card card, Card currentC) {
        return card.getFront().equals(currentC.getFront()) && card.getBack().equals(currentC.getBack())
                && card.getType().equals(currentC.getType()) && card.isReversed() == currentC.isReversed();
    }

    private void updateCardInSubDeck(Deck deck, List<Deck> subDecks, Card currentC) {
        if(subDecks == null){
            return;
        }
        for (Deck subDeck : subDecks) {
            Optional<Card> foundCard = subDeck.getCards().stream()
                    .filter(card -> checkEquality(card, currentC))
                    .findFirst();
            if (foundCard.isPresent()) {
                List<Card> cards = subDeck.getCards();
                cards.remove(foundCard.get());
                cards.add(currentC);
                subDeck.setCards(cards);
                pathParts = subDeck.getPath().split(":");
                updateDeckInSubDeck(subDeck);
                return;
            } else {
                updateCardInSubDeck(deck, subDeck.getSubDecks(), currentC);
            }
        }
    }

    private void downRankCard() {
        pullCardFromList(currentCard);
        currentCard.setCertainty(currentCard.getCertainty() - 2);
        if (currentCard.getCertainty() < -5) {
            currentCard.setCertainty(-5);
        }
        pushCardToCorrectList(currentCard);
        currentCard.incrementWrongAnswers();
        currentCard.incrementTotalRequests();
        findCardsDeckAndUpdateDeck(currentCard);
    }

    private void showCard(Card randomCard) {
        currentCard = randomCard;
        frontCard.setText(randomCard.getFront());
        if (randomCard.getType().equals(CardType.MATCHING)) {
            correct.setVisibility(View.GONE);
            wrong.setVisibility(View.GONE);
            mediumButton.setVisibility(View.GONE);
            flipCard.setVisibility(View.GONE);
            backCard.setVisibility(View.VISIBLE);
            submitAnswer.setVisibility(View.VISIBLE);
            backCard.setText("");
            backCard.setClickable(true);
        } else {
            correct.setVisibility(View.VISIBLE);
            wrong.setVisibility(View.VISIBLE);
            mediumButton.setVisibility(View.VISIBLE);
            flipCard.setVisibility(View.VISIBLE);
            backCard.setVisibility(View.GONE);
            submitAnswer.setVisibility(View.GONE);
        }
    }

    private Card  getCardDependingOnDifficultyInPast() {
        if (allCards.size() == 0) {
            return null;
        }
        Random rand = new Random();
        int zufall = rand.nextInt(15);
        if (zufall < 5) {
            if (reallyBadCards.size() > 0) {
                int index = rand.nextInt(reallyBadCards.size());
                return reallyBadCards.get(index);
            } else {
                return getCardDependingOnDifficultyInPast();
            }
        } else if (zufall < 9) {
            if (badCards.size() > 0) {
                int index = rand.nextInt(badCards.size());
                return badCards.get(index);
            } else {
                return getCardDependingOnDifficultyInPast();
            }
        } else if (zufall < 12) {
            if (mediumCards.size() > 0) {
                int index = rand.nextInt(mediumCards.size());
                return mediumCards.get(index);
            } else {
                return getCardDependingOnDifficultyInPast();
            }
        } else if (zufall < 14) {
            if (goodCards.size() > 0) {
                int index = rand.nextInt(goodCards.size());
                return goodCards.get(index);
            } else {
                return getCardDependingOnDifficultyInPast();
            }
        } else {
            if (reallyGoodCards.size() > 0) {
                int index = rand.nextInt(reallyGoodCards.size());
                return reallyGoodCards.get(index);
            } else {
                return getCardDependingOnDifficultyInPast();
            }
        }
    }

    private void addPoints(int points) {
        TextView pointsText = getActivity().findViewById(R.id.points);
        int currentPoints = Integer.parseInt(pointsText.getText().toString().trim());
        currentPoints += points;
        pointsText.setText(String.valueOf(currentPoints));

        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        String currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUserEmail).update(Constants.KEY_POINTS, FieldValue.increment(points));
    }

    private void updateDeckInSubDeck() {
        updateDeckInSubDeck(deck);
    }

    private void updateDeckInSubDeck(Deck lowestDeck) {
        deckDoc.get()
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
                    decks.add(lowestDeck);
                    List<Deck> masterSubs = new ArrayList<>();
                    for (int j = decks.size() -1; j > 0; j--) {
                        int finalJ = j;
                        masterSubs = decks.get(j-1).getSubDecks().stream()
                                .filter(subDeck -> !subDeck.getName().equals(decks.get(finalJ).getName()))
                                .collect(Collectors.toList());
                        masterSubs.add(decks.get(j));
                        decks.get(j-1).setSubDecks(masterSubs);
                    }
                    masterDeck.setSubDecks(masterSubs);
                    deckDoc.set(masterDeck);
                }).addOnFailureListener(e -> {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Error")
                            .setMessage("Failed to update deck. Please try again.")
                            .setPositiveButton("Ok", null)
                            .show();
                });
    }
}