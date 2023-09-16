package game.controller;

import game.model.CrazyEights;
import game.model.RandomCrazyEightsPlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the panel where the computer cards
 * are drawn in the interface.
 */
public class ComputerPane extends JPanel {
    /**
     * A hash map that contains the image of the back of a card.
     */
    public static final Map<String, BufferedImage> cardImages = new HashMap<>(4);
    public static int DEFAULT_SELECTION_DELTA = -40;
    /**
     * An array list that contains the string representation
     * of the cards in the computer player's hand.
     */
    private ArrayList<String> hand = new ArrayList<>();
    private Dimension cardSize = new Dimension(120, 150);
    private int[] xCardPosition = {};
    private int selectionDelta = DEFAULT_SELECTION_DELTA;
    private RandomCrazyEightsPlayer computerPlayer;
    private CrazyEights model;


    public RandomCrazyEightsPlayer getComputerPlayer() {
        return computerPlayer;
    }

    /**
     * Preloads images
     */
    protected static void loadImages() {
        try {
            cardImages.put("00", ImageIO.read(ComputerPane.class.getResource("/00.png")));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1000, (int)cardSize.getHeight() + Math.abs(selectionDelta));
    }

    /**
     * Sets up the computer panel that contains the cards of the computer
     * and defines a random crazy eight player as being the computer player.
     */
    public ComputerPane(CrazyEights model) {
        this.model = model;
        computerPlayer = new RandomCrazyEightsPlayer();
        if (cardImages.size() < 1) {
            loadImages();
        }
    }

    /**
     * Takes the hand of the computer player and returns a list
     * that contains the string representations of the images corresponding to
     * the back of the cards in the hand. This is done in order to not show the
     * cards of the computer on the interface and keep them hidden to the human player.
     * @param hand the computer player's hand
     * @return a list of strings representing the back of the card image
     */
    public ArrayList<String> getBackCardHand(ArrayList hand) {
        ArrayList<String> backCardHand = new ArrayList<>();
        for(int i = 0; i < hand.size(); i++) {
            backCardHand.add("00");
        }
        return backCardHand;
    }

    /**
     * Implementation of the invalidate method.
     */
    @Override
    public void invalidate() {
        super.invalidate();
        xCardPosition = new int[hand.size()];
        int xDelta = (int)Math.floor(Math.min(cardSize.getWidth() * 1.1, (getWidth() - cardSize.getWidth())/(hand.size() - 1)));
        int xPos = (int) ((getWidth() - cardSize.getWidth() - (hand.size() - 1)*xDelta) / 2);
        for (int i = 0; i < hand.size() && i < xCardPosition.length; i++) {
            xCardPosition[i] = xPos;
            xPos += xDelta;
        }
    }

    /**
     * This method draws the back of the cards of the
     * computer player in the interface.
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (xCardPosition.length != 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            for (int i = 0; i < hand.size() && i < xCardPosition.length; i++) {
                g2d.drawImage(cardImages.get(hand.get(i)),
                        xCardPosition[i], -selectionDelta,
                        (int) cardSize.getWidth(), (int) cardSize.getHeight(), null);

            }
            g2d.dispose();
        }
    }

    /**
     * Sets the hand of cards and resets the view to match.
     * @param hand hand of cards, where every card is a character "C", "S", "D", or "H"
     *             followed by a number between 1 and 13, or 00 for a face-down card
     */
    public void setHand(ArrayList hand) {
        this.hand = hand;
        revalidate();
    }
}
