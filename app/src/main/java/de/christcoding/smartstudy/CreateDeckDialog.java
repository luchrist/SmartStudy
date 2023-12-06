package de.christcoding.smartstudy;

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

import de.christcoding.smartstudy.models.Card;
import de.christcoding.smartstudy.models.CardType;
import de.christcoding.smartstudy.models.Deck;
import de.christcoding.smartstudy.utilities.Constants;
import de.christcoding.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateDeckDialog extends DialogFragment {
    private EditText deckName, front, back;
    private AutoCompleteTextView subDeckName;
    private CheckBox reversed;
    private RadioButton basic, match;
    private ConstraintLayout nextLayout;
    private boolean nextClicked = false;
    private String deckNameString;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    private List<Card> cards;
    private List<Deck> subDecks, masterDecks;
    private List<String> subDeckNames;
    private ArrayAdapter<String> adapter;
    private CollectionReference deckCollection;
    private final Deck parentDeck;

    public CreateDeckDialog(Deck parentDeck) {
        this.parentDeck = parentDeck;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_deck, null);

        deckName = view.findViewById(R.id.deckName);
        subDeckName = view.findViewById(R.id.subDeckName);
        front = view.findViewById(R.id.frontOfCard);
        back = view.findViewById(R.id.backOfCard);
        reversed = view.findViewById(R.id.reversed);
        basic = view.findViewById(R.id.basicType);
        match = view.findViewById(R.id.matchType);
        nextLayout = view.findViewById(R.id.nextLayout);

        cards = new ArrayList<>();
        subDecks = new ArrayList<>();
        subDeckNames = new ArrayList<>();
        setUpSupDeckNameAutoComplete();

        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getContext());
        deckCollection = db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_EMAIL))
                .collection(Constants.KEY_COLLECTION_DECKS);

        deckCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    masterDecks = queryDocumentSnapshots.getDocuments().stream()
                            .map(documentSnapshot -> documentSnapshot.toObject(Deck.class))
                            .collect(Collectors.toList());
                });

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Create Deck")
                // Pass null as the parent view because its going in the dialog layout
                .setPositiveButton("Create", null)
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Add First Cards", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button create = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            create.setTextColor(getResources().getColor(R.color.primaryVariant));
            Button next = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            next.setTextColor(getResources().getColor(R.color.primaryVariant));
            Button cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            cancel.setTextColor(getResources().getColor(R.color.remove));

            next.setOnClickListener(v -> {
                if(!nextClicked) {
                    String name = deckName.getText().toString().trim();
                    if(name.isEmpty()) {
                        deckName.setError("Deck name cannot be empty");
                    }else {
                        deckNameString = name;
                        deckName.setVisibility(View.GONE);
                        nextLayout.setVisibility(View.VISIBLE);
                        nextClicked = true;
                        next.setText("Add Card");
                    }
                } else {
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
                        if(reversedBool) {
                            Card reversedCard = new Card(backString, frontString, cardType, true);
                            cards.add(reversedCard);
                        }
                    } else {
                        boolean foundExisting = false;
                        for (Deck deck : subDecks) {
                            if (deck.getName().equals(subDeck)) {
                                deck.addCard(card);
                                if(reversedBool) {
                                    Card reversedCard = new Card(backString, frontString, cardType, true);
                                    deck.addCard(reversedCard);
                                }
                                foundExisting = true;
                            }
                        }
                        if (!foundExisting) {
                            List<Card> newCards = new ArrayList<>();
                            newCards.add(card);
                            if(reversedBool) {
                                Card reversedCard = new Card(backString, frontString, cardType, true);
                                newCards.add(reversedCard);
                            }
                            Deck newDeck;
                            if(parentDeck == null) {
                                newDeck = new Deck(subDeck, newCards, new ArrayList<>(), String.format("%s:%s", deckNameString, subDeck));
                            } else {
                                newDeck = new Deck(subDeck, newCards, new ArrayList<>(), String.format("%s:%s:%s", parentDeck.getPath(), deckNameString, subDeck));
                            }
                            subDecks.add(newDeck);
                            subDeckNames.add(subDeck);
                            setUpSupDeckNameAutoComplete();
                        }
                    }
                    subDeckName.setText("");
                    front.setText("");
                    back.setText("");
                    reversed.setChecked(false);
                    basic.setChecked(true);
                    match.setChecked(false);
                }
            });
            create.setOnClickListener(v -> {
                if (deckNameString == null) {
                    deckNameString = deckName.getText().toString().trim();
                }
                if(deckNameString.isEmpty()) {
                    deckName.setError("Deck name cannot be empty");
                } else {
                    if (parentDeck == null) {
                        Deck deck = new Deck(deckNameString, cards, subDecks, deckNameString);
                        deckCollection.document(deckNameString).set(deck);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                new StudyFragment()).commit();
                    } else {
                        Deck deck = new Deck(deckNameString, cards, subDecks, String.format("%s:%s", parentDeck.getPath(), deckNameString));
                        List<Deck> decks = parentDeck.getSubDecks();
                        decks.add(deck);
                        parentDeck.setSubDecks(decks);
                        String[] pathParts = parentDeck.getPath().split(":");
                        DocumentReference deckDoc = deckCollection.document(pathParts[0]);
                        if(pathParts.length > 1) {
                            updateDeckInSubDeck(parentDeck, pathParts, deckDoc);
                        } else {
                            deckDoc.set(parentDeck);
                        }
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                new EditDeckFragment(parentDeck)).commit();
                    }
                    dialog.dismiss();

                }
            });

            cancel.setOnClickListener(v -> {
                dialog.dismiss();
                if (parentDeck == null) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                            new StudyFragment()).commit();
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                            new EditDeckFragment(parentDeck)).commit();
                }
            });
        });
        dialog.show();
        return dialog;
    }

    private void updateDeckInSubDeck(Deck lowestDeck, String[] pathParts, DocumentReference deckDoc) {
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

    //need it when I accept more then a depth of 2
/*

    private Deck setParentDeckInMasterDeck(Deck masterDeck) {
        if (masterDeck == parentDeck) {
            return parentDeck;
        } else {
            return setParentDeckInMasterSubDeck(masterDeck, masterDeck.getSubDecks());
        }
    }

    private Deck setParentDeckInMasterSubDeck(Deck masterDeck, List<Deck> subDecks) {
        for (Deck)
    }

    private Deck findMasterDeck() {
        for (Deck deck : masterDecks) {
            if (deck == parentDeck) {
                return deck;
            }
            Deck masterDeck = findParentDeckInSubMasterDecks(deck, deck.getSubDecks());
            if (masterDeck != null) {
                return masterDeck;
            }
        }
        return null;
    }

    private Deck findParentDeckInSubMasterDecks(Deck masterDeck, List<Deck> subDecks) {
        for (Deck deck : subDecks) {
            if(deck == parentDeck) {
                return masterDeck;
            }
            Deck mD = findParentDeckInSubMasterDecks(masterDeck, deck.getSubDecks());
            if (mD != null) {
                return masterDeck;
            }
        }
        return null;
    }
*/

    private void setUpSupDeckNameAutoComplete() {
        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, subDeckNames);
        subDeckName.setAdapter(adapter);
        subDeckName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                subDeckName.showDropDown();
        });
    }
}
