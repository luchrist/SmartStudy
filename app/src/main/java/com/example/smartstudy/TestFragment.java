package com.example.smartstudy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.example.smartstudy.models.Card;
import com.example.smartstudy.models.CardType;
import com.example.smartstudy.models.Deck;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Random;


public class TestFragment extends Fragment {
    private TextView frontCard;
    private TextView correctnessRatio;
    private EditText backCard;
    private AppCompatButton mediumButton;
    private AppCompatImageButton correct, wrong, submitAnswer, flipCard, continueButton, repeatBtn, exitBtn;
    private final Deck deck;
    private Card currentCard;
    private List<Card> allCards;
    private int cardCount;
    private double correctAnswers;

    public TestFragment(Deck deck) {
        this.deck = deck;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        frontCard = view.findViewById(R.id.frontCard);
        backCard = view.findViewById(R.id.backCard);
        TextView deckName = view.findViewById(R.id.collectionName);
        correctnessRatio = view.findViewById(R.id.correctAnswerRatio);
        mediumButton = view.findViewById(R.id.almostCorrect);
        correct = view.findViewById(R.id.correct);
        wrong = view.findViewById(R.id.wrong);
        submitAnswer = view.findViewById(R.id.submitAnswer);
        flipCard = view.findViewById(R.id.flipCard);
        continueButton = view.findViewById(R.id.continueIfFalse);
        repeatBtn = view.findViewById(R.id.repeatB);
        exitBtn = view.findViewById(R.id.exitB);
        deckName.setText(deck.getName());

        setListeners();

        allCards = deck.getAllCards();
        if(allCards.size() == 0) {
            frontCard.setText("No cards in this deck, please add some cards before.");
            return view;
        }
        showCard(getRandomCard(allCards));

        return view;
    }

    private void setListeners() {
        correct.setOnClickListener(v -> {
            if(currentCard.getType().equals(CardType.BASIC)) {
                cardCount++;
                correctAnswers++;
                correctnessRatio.setText(correctAnswers + "/" + cardCount);
                continueTest();
            }
        });
        wrong.setOnClickListener(v -> {
            if(currentCard.getType().equals(CardType.BASIC)) {
                cardCount++;
                correctnessRatio.setText(correctAnswers + "/" + cardCount);
                continueTest();
            }
        });
        mediumButton.setOnClickListener(v -> {
            if(currentCard.getType().equals(CardType.BASIC)) {
                cardCount++;
                correctAnswers += 0.5;
                correctnessRatio.setText(correctAnswers + "/" + cardCount);
                continueTest();
            }
        });
        flipCard.setOnClickListener(v -> {
            if(currentCard.getType().equals(CardType.BASIC)) {
                backCard.setVisibility(View.VISIBLE);
                backCard.setText(currentCard.getBack());
                backCard.setClickable(false);
            }
        });
        submitAnswer.setOnClickListener(v -> {
            if(currentCard.getType().equals(CardType.MATCHING)) {
                cardCount++;
                if(backCard.getText().toString().trim().equalsIgnoreCase(currentCard.getBack())) {
                    correctAnswers++;
                    continueTest();
                } else {
                    backCard.setError("Correct answer: " + currentCard.getBack());
                    continueButton.setVisibility(View.VISIBLE);
                }
                correctnessRatio.setText(correctAnswers + "/" + cardCount);
            }
        });

        continueButton.setOnClickListener(v -> {
            if(currentCard.getType().equals(CardType.MATCHING)) {
                continueTest();
                continueButton.setVisibility(View.GONE);
            }
        });
    }

    private void continueTest() {
        if(cardCount == 10 || allCards.size() == 0) {
            testFinished();
        } else {
            showCard(getRandomCard(allCards));
        }
    }

    private void testFinished() {
        removeViews();
        double ratio = correctAnswers / cardCount;
        double grade = getGrade(ratio);
        addPoints(ratio);
        String recommendation = getRecommendation(grade);

        frontCard.setText(String.format("Your Grade: %s \nPoints: %s of %s \n%s", grade, correctAnswers, cardCount, recommendation));
        repeatBtn.setVisibility(View.VISIBLE);
        exitBtn.setVisibility(View.VISIBLE);
    }

    private void showCard(Card randomCard) {
        allCards.remove(randomCard);
        if (randomCard.isReversed()) {
            randomCard = randomCard.returnCardInRandomOrder();
        }
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

    private Card getRandomCard(List<Card> allCards) {
        if(allCards.size() == 0) {
            return null;
        }
        Random rand = new Random();
        int i = rand.nextInt(allCards.size());
        return allCards.get(i);
    }

    private void addPoints(double ratio) {
        int points = (int) (ratio * 100);
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

    private String getRecommendation(double grade) {
        if(grade <= 1.5) {
            return "Großartig! Bereite dich darauf vor als Streber bezeichnet zu werden!";
        } else if (grade <= 2.5) {
            return "Gut! Du bist bereit für die Prüfung!";
        } else if (grade <= 3.5) {
            return "ok! Ruh dich nicht darauf aus";
        } else if (grade <= 4.5) {
            return "Naja! Du solltest nochmal lernen oder beten";
        } else {
            return "Kataststrophe, Renn zum Arzt und schreib dich krank!";
        }
    }

    private double getGrade(double ratio) {
        if (ratio >= 0.95){
            return 1;
        } else if (ratio >= 0.85){
            return 1.5;
        } else if (ratio >= 0.75){
            return 2;
        } else if (ratio >= 0.65){
            return 2.5;
        } else if (ratio >= 0.55){
            return 3;
        } else if (ratio >= 0.45){
            return 3.5;
        } else if (ratio >= 0.35){
            return 4;
        } else if (ratio >= 0.25){
            return 4.5;
        } else if (ratio >= 0.15){
            return 5;
        } else {
            return 6;
        }
    }

    private void removeViews() {
        backCard.setVisibility(View.GONE);
        correct.setVisibility(View.GONE);
        wrong.setVisibility(View.GONE);
        mediumButton.setVisibility(View.GONE);
        flipCard.setVisibility(View.GONE);
        submitAnswer.setVisibility(View.GONE);
        continueButton.setVisibility(View.GONE);
    }
}