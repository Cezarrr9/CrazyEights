package game.controller;

import game.model.CrazyEights;
import game.model.Card;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class implements the mouse adapter responsible for
 * playing a card after it was selected by the player.
 */

public class PlayCardAdapter extends MouseAdapter {
    private PlayerPane playerPane;
    private CrazyEights model;
    private Card card = null;

    public Card getCard() {
        return card;
    }

    public PlayCardAdapter(PlayerPane playerPane, CrazyEights model) {
        this.playerPane = playerPane;
        this.model = model;
    }

    public void mouseClicked(MouseEvent e) {
        if(model.getCurrentPlayer() == playerPane) {
            // first we get the index of the selected card
            int selected = playerPane.getSelectedCard(e.getX());
            if (selected >= 0) {
                // if the index is valid, we verify if the card is playable
                if (model.isPlayable(model.hands.get(playerPane).get(selected))) {
                    card = model.hands.get(playerPane).get(selected);
                    playerPane.getHand().remove(selected);
                    playerPane.setHand(playerPane.getHand());
                    playerPane.revalidate();
                    playerPane.repaint();
                }
            }
        }
    }
}
