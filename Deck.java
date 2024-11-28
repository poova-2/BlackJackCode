import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Deck {
    private final List<Card> cards = new LinkedList<>();

    private final Deck backupDeck;

    public Deck() {
        this(true, null);
    }

    public Deck(final boolean emptyDeck, final Deck deck) {
        backupDeck = deck;

        if (emptyDeck) {
            return;
        }
        for (final Suite suite : Suite.values()) {
            for (final Rank rank : Rank.values()) {
                cards.add(new Card(rank, suite));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public int size() {
        return cards.size();
    }

    public Card take() {
        if (cards.isEmpty()) {
            if (backupDeck != null) {
                moveCards(backupDeck);
            }
        }
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    public Card peak() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(cards.size() - 1);
    }

    public void addCards(final List<Card> cards) {
        this.cards.addAll(cards);
    }

    public void moveCards(final Deck deck) {
        this.cards.addAll(deck.cards);
        deck.cards.clear();
        shuffle();
    }

    @Override
    public String toString() {
        if (cards.isEmpty()) {
            return "Empty Deck";
        }

        boolean first = true;
        final StringBuilder sb = new StringBuilder();
        for (final Card card : cards) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(card);
        }

        return sb.toString();
    }
}