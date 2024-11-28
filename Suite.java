public enum Suite {
    // https://en.wikipedia.org/wiki/Playing_card#Symbols_in_Unicode
    HEART("\u2661", "hearts"),
    DIAMOND("\u2662", "diamonds"),
    CLUB("\u2667", "clubs"),
    SPADE("\u2664", "spades");

    private final String printValue;
    private final String imageFileNameComponent;

    Suite(final String printValue, final String imageFileNameComponent) {
        this.printValue = printValue;
        this.imageFileNameComponent = imageFileNameComponent;
    }

    public String getImageFileName() {
        return imageFileNameComponent;
    }

    @Override
    public String toString() {
        return printValue;
    }
}
