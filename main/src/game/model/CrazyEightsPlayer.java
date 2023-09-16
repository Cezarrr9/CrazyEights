package game.model;

import java.util.List;

/**
 * Player functions for the Crazy Eights game
 */
public interface CrazyEightsPlayer {
    /**
     * Asks the player to select a card from their hand to play,
     * or null if they want to draw a card instead
     * @param hand List of Cards in the player's hand
     * @param game CardGame being played
     * @return Card to be played, or null to draw a card
     */
    Card takeTurn(List<Card> hand, CrazyEights game);

    /**
     * Asks the player to select a suit from the four possibilities
     * Clubs, Hearts, Spades, and Diamonds
     * @param game CardGame being played
     * @return Card.Suit chosen by the player
     */
    Card.Suit chooseSuit(CrazyEights game);
}