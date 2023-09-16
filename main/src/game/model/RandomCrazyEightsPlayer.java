package game.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Player that plays Crazy Eights by randomly choosing
 * legal actions to play
 */
public class RandomCrazyEightsPlayer implements CrazyEightsPlayer {
    /**
     * Shuffles their hand and picks the first playable Card to
     * play. If no such Card exists, draws a card
     * @param hand List of Cards in the player's hand
     * @param game CardGame being played
     * @return
     */
    @Override
    public Card takeTurn(List<Card> hand, CrazyEights game) {
        Collections.shuffle(hand);
        for (Card card : hand) {
            if (game.isPlayable(card)) {
                return card;
            }
        }
        return null;
    }

    /**
     * Chooses a random suit.
     * @param game CardGame being played
     * @return
     */
    @Override
    public Card.Suit chooseSuit(CrazyEights game) {
        return Card.Suit.values()[new Random().nextInt(Card.Suit.values().length)];
    }
}
