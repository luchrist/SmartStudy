package com.example.smartstudy.models;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Deck {
    private String name;
    private List<Card> cards;
    private List<Deck> subDecks;
    private String path;

    public Deck(String name, List<Card> cards, List<Deck> subDecks, String path) {
        this.name = name;
        this.cards = cards;
        this.subDecks = subDecks;
        this.path = path;
    }

    public Deck() {
    }

    public List<Card> returnAllCards() {
        Stream.Builder<Card> cardStream = Stream.builder();
        getCards().stream().filter(card -> !card.isPaused()).forEach(cardStream::add);
        List<Deck> subDecks = getSubDecks();
        if(subDecks != null) {
            getCardsOfSubDecks(subDecks, cardStream);
        }
        return cardStream.build().collect(Collectors.toList());
    }

    private void getCardsOfSubDecks(List<Deck> subDecks, Stream.Builder<Card> cardStream) {
        for (Deck subDeck : subDecks) {
            subDeck.getCards().stream().filter(card -> !card.isPaused()).forEach(cardStream::add);
            if(subDeck.getSubDecks() != null) {
                getCardsOfSubDecks(subDeck.getSubDecks(), cardStream);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<Deck> getSubDecks() {
        return subDecks;
    }

    public void setSubDecks(List<Deck> subDecks) {
        this.subDecks = subDecks;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addCard(Card card) {
        cards.add(card);
    }
}
