package game.model;

/**
 * A class representing playing cards.
 * This class implement immutable cards, so that they
 * cannot be changed by unscrupulous players.
 */
public abstract class Card {
    /**
     * Using an enumeration limits our cards to
     * clubs, hearts, diamonds, and spades.
     */
    public enum Suit {
        CLUBS, HEARTS, DIAMONDS, SPADES
    }
    private final Suit suit;
    private final int value;

    /**
     * Generates a new card of the given suit and value
     * @param suit the suit of the card
     * @param value the value of the card
     */
    public Card(Suit suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    @Override
    public String toString() {
        String retVal = "";
        switch (suit) {
            case CLUBS:
                retVal = "C";
                break;
            case HEARTS:
                retVal = "H";
                break;
            case DIAMONDS:
                retVal = "D";
                break;
            case SPADES:
                retVal = "S";
                break;
        }
        if (value == 1) {
            return retVal + "1";
        } else if (value == 11) {
            return retVal + "11";
        } else if (value == 12) {
            return retVal + "12";
        } else if (value == 13) {
            return retVal + "13";
        }
        return retVal + value;
    }

    /**
     * Retrieves the suit of the card
     * @return the suit of the card
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Retrieves the value of the card
     * @return value of the card
     */
    public int getValue() {
        return value;
    }

    /**
     * Determines whether this card can be played on top of another card.
     * By default, cards can only be played on top of cards that match
     * their suit or value. This method should be overridden for wild cards.
     * @param other Card to be played on top of
     * @return true iff this card is legal to play on top of other
     */
    protected boolean isPlayableOn(Card other) {
        return other == null || other.suit.equals(this.suit) || other.value == this.value;
    }

    /**
     * Performs the action on the card. For most cards, this will be empty.
     */
    protected abstract void execute();

}
