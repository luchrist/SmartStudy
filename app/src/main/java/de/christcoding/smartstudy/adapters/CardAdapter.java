package de.christcoding.smartstudy.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.christcoding.smartstudy.R;
import de.christcoding.smartstudy.databinding.ItemContainerCardEditBinding;
import de.christcoding.smartstudy.models.Card;
import de.christcoding.smartstudy.utilities.CardSelectListener;

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
        holder.setListener(cards.get(position));
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

        public void setListener(Card card) {
            binding.cardPauseResume.setOnClickListener(v -> selectListener.onPauseCardSelected(card));
            binding.cardEdit.setOnClickListener(v -> selectListener.onEditCardSelected(card));
            binding.cardRemove.setOnClickListener(v -> selectListener.onDeleteCardSelected(card));
            binding.cardFront.setOnClickListener(v -> {
                LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.card_content_dialog, null);
                TextView front = view.findViewById(R.id.frontOfCard);
                TextView back = view.findViewById(R.id.backOfCard);
                front.setText(card.getFront());
                back.setText(card.getBack());
                new AlertDialog.Builder(v.getContext())
                        .setView(view)
                        .setTitle("Card content")
                        .setPositiveButton("OK", (dialog, which) -> dialog.cancel())
                        .create()
                        .show();
            });
        }

        public void setCardData(Card card) {
            if(card.getFront().length() > 14) {
                binding.cardFront.setText(card.getFront().substring(0, 13) + "...");
            } else {
                binding.cardFront.setText(card.getFront());
            }
            if (card.isPaused()) {
                binding.cardPauseResume.setImageResource(R.drawable.baseline_play_arrow_24);
            } else {
                binding.cardPauseResume.setImageResource(R.drawable.baseline_pause_24);
            }
        }
    }
}
