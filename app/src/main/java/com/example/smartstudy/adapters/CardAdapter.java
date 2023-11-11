package com.example.smartstudy.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.R;
import com.example.smartstudy.databinding.ItemContainerCardEditBinding;
import com.example.smartstudy.models.Card;
import com.example.smartstudy.utilities.CardSelectListener;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final List<Card> cards;

    private final CardSelectListener selectListener;

    public CardAdapter(List<Card> cards, CardSelectListener selectListener) {
        this.cards = cards;
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerCardEditBinding itemContainerCardEditBinding = ItemContainerCardEditBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CardViewHolder(itemContainerCardEditBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.setCardData(cards.get(position));
        holder.setListener();
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        ItemContainerCardEditBinding binding;

        public CardViewHolder(ItemContainerCardEditBinding itemContainerCardEditBinding) {
            super(itemContainerCardEditBinding.getRoot());
            binding = itemContainerCardEditBinding;
        }

        public void setListener() {
            binding.cardPauseResume.setOnClickListener(v -> selectListener.onPauseCardSelected(cards.get(getAdapterPosition())));
            binding.cardEdit.setOnClickListener(v -> selectListener.onEditCardSelected(cards.get(getAdapterPosition())));
            binding.cardRemove.setOnClickListener(v -> selectListener.onDeleteCardSelected(cards.get(getAdapterPosition())));
        }

        public void setCardData(Card card) {
            if(card.getFront().length() > 20) {
                binding.cardFront.setText(card.getFront().substring(0, 18) + "...");
            } else {
                binding.cardFront.setText(card.getFront());
            }
            if (card.isPaused()) {
                binding.cardPauseResume.setImageResource(R.drawable.baseline_play_arrow_24);
            }
        }
    }
}
