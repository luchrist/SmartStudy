package com.example.smartstudy.utilities;

import com.example.smartstudy.models.Card;

public interface CardSelectListener {

        void onPauseCardSelected(Card card);

        void onEditCardSelected(Card card);

        void onDeleteCardSelected(Card card);
}
