import javax.swing.*;

public class Card {
    private final Rank rank;
    private final Suite suite;
    private final ImageIcon icon;

    public Card(final Rank rank, final Suite suite) {
        this.rank = rank;
        this.suite = suite;
        this.icon = new ImageIcon(getClass().getResource("images/" + rank.getImageFileName() + "_of_" + suite.getImageFileName() + ".png"));
    }

    public Rank getRank() {
        return rank;
    }

    public Suite getSuite() {
        return suite;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return rank.toString() + suite.toString();
    }
}
