package com.example.smartstudy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.example.smartstudy.models.Card;
import com.example.smartstudy.models.CardType;
import com.example.smartstudy.models.Deck;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateCardDialog extends DialogFragment {
    private EditText front, back;
    private AutoCompleteTextView subDeckName;
    private CheckBox reversed;
    private RadioButton basic, match;
    private boolean nextClicked = false;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    private List<Card> cards;
    private List<Deck> subDecks;
    private ArrayAdapter arrayAdapter;
    private final Deck parentDeck;

    public CreateCardDialog(Deck parentDeck) {
        this.parentDeck = parentDeck;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_card, null);

        subDeckName = view.findViewById(R.id.subDeckName);
        front = view.findViewById(R.id.frontOfCard);
        back = view.findViewById(R.id.backOfCard);
        reversed = view.findViewById(R.id.reversed);
        basic = view.findViewById(R.id.basicType);
        match = view.findViewById(R.id.matchType);

        cards = parentDeck.getCards();
        subDecks = parentDeck.getSubDecks();
        setUpSupDeckNameAutoComplete();

        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getContext());
        CollectionReference deckCollection = db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_EMAIL)).collection(Constants.KEY_COLLECTION_DECKS);

        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).setTitle("Create New Cards")
                // Pass null as the parent view because its going in the dialog layout
                .setPositiveButton("Create", null).setNegativeButton("Cancel", null).setNeutralButton("Add Card", null).create();

        dialog.setOnShowListener(dialogInterface -> {
            Button create = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            create.setTextColor(getResources().getColor(R.color.primaryVariant));
            Button next = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            next.setTextColor(getResources().getColor(R.color.primaryVariant));
            Button cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            cancel.setTextColor(getResources().getColor(R.color.remove));

            next.setOnClickListener(v -> {
                nextClicked = true;
                String subDeck = subDeckName.getText().toString().trim();
                String frontString = front.getText().toString().trim();
                String backString = back.getText().toString().trim();
                boolean reversedBool = reversed.isChecked();
                boolean matchBool = match.isChecked();
                CardType cardType = CardType.BASIC;
                if (matchBool) {
                    cardType = CardType.MATCHING;
                }
                Card card = new Card(frontString, backString, cardType, false);
                if (subDeck.isEmpty()) {
                    cards.add(card);
                    if (reversedBool) {
                        Card reversedCard = new Card(backString, frontString, cardType, true);
                        cards.add(reversedCard);
                    }
                } else {
                    boolean foundExisting = false;
                    for (Deck deck : subDecks) {
                        if (deck.getName().equals(subDeck)) {
                            deck.addCard(card);
                            if (reversedBool) {
                                Card reversedCard = new Card(backString, frontString, cardType, true);
                                deck.addCard(reversedCard);
                            }
                            foundExisting = true;
                        }
                    }
                    if (!foundExisting) {
                        List<Card> newCards = new ArrayList<>();
                        newCards.add(card);
                        if (reversedBool) {
                            Card reversedCard = new Card(backString, frontString, cardType, true);
                            newCards.add(reversedCard);
                        }
                        Deck newDeck = new Deck(subDeck, newCards, new ArrayList<>(), parentDeck.getName());
                        subDecks.add(newDeck);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
                subDeckName.setText("");
                front.setText("");
                back.setText("");
                reversed.setChecked(false);
                basic.setChecked(true);
                match.setChecked(false);
            });
            create.setOnClickListener(v -> {
                if (nextClicked) {
                    parentDeck.setCards(cards);
                    parentDeck.setSubDecks(subDecks);
                    if(parentDeck.getParentDeck() == null) {
                        deckCollection.document(parentDeck.getName()).set(parentDeck);
                    } else {
                        deckCollection.document(parentDeck.getParentDeck()).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    Deck masterDeck = documentSnapshot.toObject(Deck.class);
                                    List<Deck> subDecks = masterDeck.getSubDecks();
                                    subDecks = subDecks.stream()
                                            .filter(subDeck -> !subDeck.getName().equals(parentDeck.getName()))
                                            .collect(Collectors.toList());
                                    subDecks.add(parentDeck);
                                    masterDeck.setSubDecks(subDecks);
                                    deckCollection.document(masterDeck.getName()).set(masterDeck);
                                });
                    }

                }
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new EditDeckFragment(parentDeck)).commit();
                dialog.dismiss();
            });

            cancel.setOnClickListener(v -> {
                dialog.dismiss();
                if (parentDeck == null) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new StudyFragment()).commit();
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new EditDeckFragment(parentDeck)).commit();
                }
            });
        });
        dialog.show();
        return dialog;
    }

    private void setUpSupDeckNameAutoComplete() {
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, subDecks);
        subDeckName.setAdapter(arrayAdapter);
        subDeckName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) subDeckName.showDropDown();
        });
    }
}

