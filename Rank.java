public enum Rank {
    Ace(1, "A", "ace"),
    Two(2, "2", "2"),
    Three(3, "3", "3"),
    Four(4, "4", "4"),
    Five(5, "5", "5"),
    Six(6, "6", "6"),
    Seven(7, "7", "7"),
    Eight(8, "8", "8"),
    Nine(9, "9", "9"),
    Ten(10, "10", "10"),
    Jack(10, "J", "jack"),
    Queen(10, "Q", "queen"),
    King(10, "K", "king");

    private final int numericValue;
    private final String printValue;
    private final String imageFileNameComponent;

    private Rank(final int numericValue,
                 final String printValue,
                 final String imageFileNameComponent) {
        this.numericValue = numericValue;
        this.printValue = printValue;
        this.imageFileNameComponent = imageFileNameComponent;
    }

    public int getNumericValue() {
        return numericValue;
    }

    public String getImageFileName() {
        return imageFileNameComponent;
    }

    @Override
    public String toString() {
        return printValue;
    }
}
