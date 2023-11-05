package com.example.smartstudy.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.databinding.ItemContainerDeckBinding;
import com.example.smartstudy.models.Deck;
import com.example.smartstudy.utilities.DeckSelectListener;

import java.util.List;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {

    private final List<Deck> decks;

    private final DeckSelectListener selectListener;

    public DeckAdapter(List<Deck> decks, DeckSelectListener selectListener) {
        this.decks = decks;
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerDeckBinding itemContainerDeckBinding = ItemContainerDeckBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new DeckViewHolder(itemContainerDeckBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckViewHolder holder, int position) {
        holder.setDeckData(decks.get(position));
        holder.setListener();
    }

    @Override
    public int getItemCount() {
        return decks.size();
    }

    class DeckViewHolder extends RecyclerView.ViewHolder {

        ItemContainerDeckBinding binding;

        public DeckViewHolder(ItemContainerDeckBinding itemContainerDeckBinding) {
            super(itemContainerDeckBinding.getRoot());
            binding = itemContainerDeckBinding;
        }

        public void setListener() {
            binding.btnStudy.setOnClickListener(v -> selectListener.onStudyDeckSelected(decks.get(getAdapterPosition())));
            binding.deckEdit.setOnClickListener(v -> selectListener.onEditDeckSelected(decks.get(getAdapterPosition())));
            binding.btnTest.setOnClickListener(v -> selectListener.onTestDeckSelected(decks.get(getAdapterPosition())));
            binding.deckRemove.setOnClickListener(v -> selectListener.onDeleteDeckSelected(decks.get(getAdapterPosition())));
        }

        public void setDeckData(Deck deck) {
           binding.deckTitle.setText(deck.getName());
        }
    }
}
