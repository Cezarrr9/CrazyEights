# CrazyEights
This is an implementation of the CrazyEights game in Java.

# Rules

In this variation, the game is played with 52 standard cards of a playing deck. There are no jokers involved.

The game is played between one human player and the computer. At the start of the game, the deck is shuffled and each player is handed five cards. One card from the deck is then added to the discard pile. The players take turns discarding cards or drawing a card from the deck. Cards may only be discarded if they share the suit or the rank with the top card of the discard pile. That is, if the top card of the discard pile is a 5 of Clubs, the player may only play cards that are Clubs (e.g. the 4 of Clubs) or cards that have rank 5 (e.g. the 5 of Diamonds). The card that has been played becomes the new top card of the discard pile. If they cannot play any card, they must draw a card from the deck. The first player to discard all cards in their hand wins the game. 

In addition to the basic rules above, there are a few special cards:
- **Ace Reverse**: Whenever a player plays an Ace, the direction of play reverses.
- **Draw Two**: Whenever a player plays a 2, the next player must draw 2 cards before their turn starts. Note that in this variation of the game, it is not allowed to stack 2s.
- **Queen Skip**: Whenever a player plays a Queen, the next player skips their turn.
- **Crazy Eights**: Players may always play an 8, even if it does not match the suit or rank of the top card of the discard pile. If a player plays an 8, they choose a suit (in the case of the human player four eights representing the four suits will pop up on the screen and then the choice can be made). The 8 that is now on top of the discard pile is now treated as if it were of the chosen suit. For example, if a player plays the 8 of Hearts and chooses Diamonds, the next player can only play another 8, or any Diamonds card. They cannot play any Hearts card (unless it is the 8 of Hearts). 


