package game.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

public class CrazyEights {
    /**
     * The initial hand size of players
     */
    public static int INITIAL_HAND_SIZE = 5;

    /**
     * Tracks the state of the game. Used to prevent players from
     * executing cards.
     */
    public enum GameState {
        INACTIVE, PLAYING, WAITING_FOR_PLAYER_DECISION
    }

    /**
     * An array with all the property change listeners.
     */
    public static ArrayList<PropertyChangeListener> listeners = new ArrayList<>();

    /**
     * An array that contains all the players of the game. In our GUI implementation
     * there are just two players: a computer player and a human player.
     */
    private ArrayList<CrazyEightsPlayer> players = new ArrayList<>();

    /**
     * A hashmap with the hands of all the players of the game.
     */
    public Map<CrazyEightsPlayer, ArrayList<Card>> hands = new HashMap<>();
    private List<Card> deck = new ArrayList<>();
    private List<Card> discardPile = new ArrayList<>();
    private Card topCardOnDiscardPile;
    private CrazyEightsPlayer currentPlayer;
    private GameState state = GameState.INACTIVE;
    private String playerName; // either Human or Computer

    public CrazyEightsPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Takes a player's hand that contains card objects and returns a hand that contains
     * the string representations of the card objects.
     * @param player the player that holds the hand transformed
     * @return an arraylist of strings representing the cards
     */
    public ArrayList<String> fromCardHandToString(CrazyEightsPlayer player) {
        ArrayList<String> stringHand = new ArrayList<>();
        for(Card card : hands.get(player)) {
            stringHand.add(card.toString());
        }
        return stringHand;
    }

    public void addListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(PropertyChangeEvent e) {
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(e);
        }
    }

    /**
     * Builds a deck of cards, implementing the special rules
     * for special cards.
     */
    private void buildDeck() {
        deck.clear();
        discardPile.clear();
        for (CrazyEightsPlayer player : players) {
            hands.get(player).clear();
        }
        for (Card.Suit suit : Card.Suit.values()) {
            deck.add(new Card(suit, 1){
                /**
                 * An Ace changes reverses the order of players.
                 */
                @Override
                protected void execute() {
                    if (state == GameState.PLAYING) {
                        Collections.reverse(players);
                        moveToNextPlayer();
                    }
                }
            });
            deck.add(new Card(suit, 2){
                /**
                 * A two causes the next player to draw 2 cards
                 */
                @Override
                protected void execute() {
                    if (state == GameState.PLAYING) {
                        checkEndGame();
                        moveToNextPlayer();
                        drawCards(2);
                    }
                }
            });
            deck.add(new Card(suit, 12){
                /**
                 * A Queen causes the next player to skip their turn
                 */
                @Override
                protected void execute() {
                    if (state == GameState.PLAYING) {
                        moveToNextPlayer();
                        moveToNextPlayer();
                    }
                }
            });
            deck.add(new Card(suit, 8){
                /**
                 * The eight changes its suit to match the player's choice
                 */
                @Override
                protected void execute() {
                    if (state == GameState.PLAYING) {
                        selectSuit();
                        moveToNextPlayer();
                    }
                }

                /**
                 * An eight is wild, and can be played on top of any card
                 */
                @Override
                protected boolean isPlayableOn(Card other) {
                    return true;
                }
            });
            for (int i: new int[]{3,4,5,6,7,9,10,11,13}) {
                deck.add(new Card(suit, i){
                    /**
                     * Regular cards do nothing but
                     * pass the turn to the next player
                     */
                    @Override
                    protected void execute() {
                        if (state == GameState.PLAYING) {
                            moveToNextPlayer();
                        }
                    }
                });
            }
        }
    }

    /**
     * Allows the current player to select a suit. To simulate this,
     * a "virtual card" of the chosen suit and the original value
     * is placed on top of the discard pile.
     */
    private void selectSuit() {
        if (state == GameState.PLAYING) {
            state = GameState.WAITING_FOR_PLAYER_DECISION;
            notifyListeners(new PropertyChangeEvent(this, "8", null, null));
            Card.Suit suit = currentPlayer.chooseSuit(this);
            state = GameState.PLAYING;
            topCardOnDiscardPile = new Card(suit, topCardOnDiscardPile.getValue()) {
                @Override
                protected void execute() {
                }
            };

            notifyListeners(new PropertyChangeEvent(this, "suit", null,
                    getTopCardOnDiscardPile().toString().charAt(0)));
            notifyListeners(new PropertyChangeEvent(this, "hand",
                    null, fromCardHandToString(currentPlayer)));
            System.out.println(playerName + " changes color to " + suit);
            notifyListeners(new PropertyChangeEvent(this, "topCard",
                    null, getTopCardOnDiscardPile()));
        }
    }

    /**
     * Moves a given card to the top of the discard pile
     * @param card Card to be discarded
     */
    private void discard(Card card) {
        if (state == GameState.PLAYING) {
            topCardOnDiscardPile = card;
            discardPile.add(card);
        }
    }

    /**
     * Passes the turn to the next player
     */
    public void moveToNextPlayer() {
        if (state == GameState.PLAYING) {
            int currentPlayerId = players.indexOf(currentPlayer);
            currentPlayer = players.get((currentPlayerId + 1) % players.size());
        }
    }

    /**
     * Causes the current player to draw cards
     * @param n the number of cards to draw
     */
    private void drawCards(int n) {
        if (state == GameState.PLAYING) {
            for (int i = 0; i < n; i++) {
                drawCard();
            }
        }
    }

    /**
     * Causes the current player to draw a card. If the deck has
     * run out, the discard pile is shuffled into the deck.
     * @return Card that was drawn by the player
     */
    public Card drawCard() {
        if (state == GameState.PLAYING) {
            if (deck.size() <= 1) {
                while (discardPile.size() > 1) {
                    deck.add(discardPile.remove(1));
                }
                Collections.shuffle(deck);
            }
            Card cardDrawn = deck.remove(0);
            hands.get(currentPlayer).add(cardDrawn);
            playerName = currentPlayer instanceof RandomCrazyEightsPlayer ? "Computer" : "Human";
            System.out.println(playerName + " draws " + cardDrawn);
            notifyListeners(new PropertyChangeEvent(this, "hand",
                    null, fromCardHandToString(currentPlayer)));
            return cardDrawn;
        }
        return null;
    }

    /**
     * Adds a player to the game. Can only be performed when the game
     * is not currently being played.
     * @param player CardGamePlayer to add to the game
     */
    public void addPlayer(CrazyEightsPlayer player) {
        if (!isGameActive()) {
            players.add(player);
            hands.put(player, new ArrayList<>());
        }
    }

    /**
     * Determines whether the game is currently being played
     * @return true iff a game is in progress
     */
    public boolean isGameActive() {
        return (state != GameState.INACTIVE);
    }

    public String getCurrentPlayerName() {
        return this.playerName;
    }

    /**
     * Starts a new game
     */
    public void start() {
        if (!isGameActive()) {
            state = GameState.PLAYING;
            buildDeck();
            Collections.shuffle(deck);
            for (int i = 0; i < players.size(); i++) {
                List<Card> hand = hands.get(players.get(i));
                for (int j = 0; j < INITIAL_HAND_SIZE; j++) {
                    hand.add(deck.remove(0));
                }
                System.out.println(i + ":\t" + hand);
                currentPlayer = players.get(i);
                notifyListeners( new PropertyChangeEvent(this, "hand",
                        null, fromCardHandToString(currentPlayer)));
            }
            discard(deck.remove(0));
            System.out.println("Top card is " + topCardOnDiscardPile);
            notifyListeners(new PropertyChangeEvent(this, "topCard",
                    null, getTopCardOnDiscardPile()));
            currentPlayer = players.get(0);
            playRound();
        }
    }

    /**
     * Plays a single round of the game by
     * 1) Asking a player to choose an action
     * 2) Executing the action
     * 3) Ending the game if appropriate
     */
    private void playRound() {
        while (isGameActive()) {
            playerName = currentPlayer instanceof RandomCrazyEightsPlayer ? "Computer" : "Human";
            List<Card> hand = hands.get(currentPlayer);
            notifyListeners(new PropertyChangeEvent(this, "hand",
                    null, fromCardHandToString(currentPlayer)));
            notifyListeners(new PropertyChangeEvent(this, "topCard",
                    null, getTopCardOnDiscardPile()));
            List<Card> cards = new ArrayList<>();
            cards.addAll(hand);
            // By handing the player a copy of their hand,
            // players are unable to change the cards in
            // their actual hand
            Card cardPlayed;
            do {
                state = GameState.WAITING_FOR_PLAYER_DECISION;
                cardPlayed = currentPlayer.takeTurn(cards, this);
                state = GameState.PLAYING;
            } while (!(cardPlayed == null || (hand.contains(cardPlayed) && isPlayable(cardPlayed))));
            // The while loop above ensures that the current player
            // can actually play the card they have chosen
            System.out.println(cardPlayed);
            if (cardPlayed == null) {
                drawCard();
                notifyListeners(new PropertyChangeEvent(this, "hand",
                        null, fromCardHandToString(currentPlayer)));
                notifyListeners(new PropertyChangeEvent(this, "topCard",
                        null, getTopCardOnDiscardPile()));
                moveToNextPlayer();
            } else {
                System.out.println(playerName + " plays " + cardPlayed);
                hand.remove(cardPlayed);
                discard(cardPlayed);
                notifyListeners(new PropertyChangeEvent(this, "topCard",
                        null, getTopCardOnDiscardPile()));
                notifyListeners(new PropertyChangeEvent(this, "hand",
                        null, fromCardHandToString(currentPlayer)));
                cardPlayed.execute();
            }
            checkEndGame();
        }
    }

    /**
     * Determines whether the game has ended because some player
     * no longer has any cards
     */
    private void checkEndGame() {
        for (List<Card> hand : hands.values()) {
            if (hand.size() < 1) {
                System.out.println("The game has ended");
                notifyListeners(new PropertyChangeEvent(this, "end", null,
                        this.getCurrentPlayerName().equals("Computer") ? "GAME OVER!" : "YOU WON!"));
                state = GameState.INACTIVE;
                return;
            }
        }
    }

    /**
     * Retrieves the current card on top of the discard pile.
     * Since Cards are immutable, players are unable to change
     * the top card of the discard pile
     * @return Card on top of the discard pile
     */
    public Card getTopCardOnDiscardPile() {
        return topCardOnDiscardPile;
    }

    /**
     * Determines whether a card is playable in the current situation.
     * @param card Card held by the current player
     * @return true iff the current player can play the given Card
     */
    public boolean isPlayable(Card card) {
        return card.isPlayableOn(topCardOnDiscardPile);
    }
}
