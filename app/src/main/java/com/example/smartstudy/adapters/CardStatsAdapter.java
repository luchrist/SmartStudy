package com.example.smartstudy.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.R;
import com.example.smartstudy.databinding.ItemContainerCardStatisticBinding;
import com.example.smartstudy.models.Card;
import com.example.smartstudy.models.CorrectnessClasses;

import java.util.List;

public class CardStatsAdapter extends RecyclerView.Adapter<CardStatsAdapter.CardViewHolder> {

    private final List<Card> cards;

    public CardStatsAdapter(List<Card> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerCardStatisticBinding itemContainerCardStatisticBinding = ItemContainerCardStatisticBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CardViewHolder(itemContainerCardStatisticBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.setCardData(cards.get(position));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        ItemContainerCardStatisticBinding binding;

        public CardViewHolder(ItemContainerCardStatisticBinding itemContainerCardStatisticBinding) {
            super(itemContainerCardStatisticBinding.getRoot());
            binding = itemContainerCardStatisticBinding;
        }

        public void setCardData(Card card) {
            if (card.getFront().length() > 13) {
                binding.cardFront.setText(card.getFront().substring(0, 10) + "...");
            } else {
                binding.cardFront.setText(card.getFront());
            }
            if (card.getBack().length() > 13) {
                binding.cardBack.setText(card.getBack().substring(0, 10) + "...");
            } else {
                binding.cardBack.setText(card.getBack());
            }
            CorrectnessClasses c = card.returnCorrectnessClass();
            switch (c) {
                case REALLY_GOOD:
                    binding.category.setBackgroundResource(R.drawable.reallygood);
                    break;
                case GOOD:
                    binding.category.setBackgroundResource(R.drawable.good);
                    break;
                case MEDIUM:
                    binding.category.setBackgroundResource(R.drawable.medium);
                    break;
                case BAD:
                    binding.category.setBackgroundResource(R.drawable.bad);
                    break;
                case REALLY_BAD:
                    binding.category.setBackgroundResource(R.drawable.reallybad);
                    break;
            }
            binding.totalReqCount.setText(String.valueOf(card.getTotalRequests()));
            binding.correctAnswersCount.setText(String.valueOf(card.getRightAnswers()));
            binding.wrongAnswersCount.setText(String.valueOf(card.getWrongAnswers()));
            binding.mediumAnswersCount.setText(String.valueOf(card.getMediumAnswers()));
        }
    }
}
