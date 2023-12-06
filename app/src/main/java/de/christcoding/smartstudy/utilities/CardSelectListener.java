package de.christcoding.smartstudy.utilities;

import de.christcoding.smartstudy.models.Card;

public interface CardSelectListener {

        void onPauseCardSelected(Card card);

        void onEditCardSelected(Card card);

        void onDeleteCardSelected(Card card);
}
