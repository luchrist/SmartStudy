package de.christcoding.smartstudy.utilities;

import de.christcoding.smartstudy.models.Deck;

public interface DeckSelectListener {

    void onStudyDeckSelected(Deck deck);
    void onEditDeckSelected(Deck deck);
    void onDeleteDeckSelected(Deck deck);
    void onTestDeckSelected(Deck deck);
}
