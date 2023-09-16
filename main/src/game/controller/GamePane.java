package game.controller;

import game.model.CrazyEights;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the visual component of the central panel:
 * the top card of the discard pile and the deck of cards.
 */
public class GamePane extends JPanel {
    /**
     * A hash map that contains all the images of the cards.
     */
    public static final Map<String, BufferedImage> cardImages = new HashMap(4);
    public static int CARDS_PER_SUIT = 13;
    private String topCard;
    private String backOfTheCard;
    private PlayerPane playerPane;
    private CrazyEights model;
    protected static Dimension cardSize = new Dimension(120, 150);


    /**
     * Preloads images
     */
    protected static void loadImages() {
        try {
            for (String suit : new String[]{"C", "S", "D", "H"}) {
                for (int i = 1; i <= CARDS_PER_SUIT; i++) {
                    cardImages.put(suit + "" + i,
                            ImageIO.read(GamePane.class.getResource("/" + suit + i + ".png")));
                }
            }
            cardImages.put("00", ImageIO.read(GamePane.class.getResource("/00.png")));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Sets up the central panel that contains the top card of the
     * discard pile and the deck of cards from where the players
     * can draw cards when they are playing.
     */
    public GamePane(CrazyEights model, PlayerPane playerPane) {
        this.model = model;
        this.playerPane = playerPane;
        if (cardImages.size() < 1) {
            loadImages();
        }
        addMouseListener(new DrawCardAdapter(this, this.playerPane, this.model));
    }

    /**
     * Draws the top card of the discard pile and the back of a card
     * which represents the deck.
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        int x = (int) cardSize.getWidth();
        int y = (int) cardSize.getHeight();

        g2d.drawImage(cardImages.get(topCard), getWidth()/2 - x/2, getHeight()/2 - y/2, x, y, null);
        g2d.drawImage(cardImages.get(backOfTheCard), getWidth()/2 + x/2 + 10, getHeight()/2 - y/2, x, y, null);

        g2d.dispose();
    }

    public void setTopCard(String topCard) {
        this.topCard = topCard;
        this.backOfTheCard = "00";
        revalidate();
    }

}
