package game.controller;

import game.model.CrazyEights;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

/**
 * This class implements the mouse adapter responsible
 * for drawing cards from the top of the deck.
 */
public class DrawCardAdapter extends MouseAdapter {
    private GamePane centralPane;
    private PlayerPane human;
    private CrazyEights model;
    private boolean clicked = false;

    public DrawCardAdapter(GamePane centralPane, PlayerPane humanPlayer, CrazyEights model) {
        this.human = humanPlayer;
        this.centralPane = centralPane;
        this.model = model;
    }

    public void mouseClicked(MouseEvent e) {
        if(model.getCurrentPlayer() == human) {
            int cardWidth = (int)GamePane.cardSize.getWidth();
            int cardHeight = (int)GamePane.cardSize.getHeight();
            int x = e.getX();
            int y = e.getY();
            if (x >= centralPane.getWidth() / 2 + cardWidth / 2 + 10 &&
                    x <= centralPane.getWidth() / 2 + 3 * cardWidth / 2 + 10 &&
                    y <= centralPane.getHeight() / 2 + cardHeight / 2 &&
                    y >= centralPane.getHeight() / 2 - cardHeight / 2) {
                clicked = true;
                human.propertyChange(new PropertyChangeEvent(this,
                        "drawButtonClicked", false, true));
            }
        }
    }
}
