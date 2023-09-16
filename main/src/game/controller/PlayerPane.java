package game.controller;

import game.model.Card;
import game.model.CrazyEights;
import game.model.CrazyEightsPlayer;
import game.view.CrazyEightsView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * PlayerPane is a visual class meant to show cards on a single row.
 * Spacing between cards is automatically adjusted for the number
 * of cards and the available width, and the height of cards is
 * scaled to the available height.
 *
 * When the mouse moves over a card, the corresponding card is
 * shown higher than the other cards.
 */
public class PlayerPane extends JPanel implements CrazyEightsPlayer, PropertyChangeListener {
    /**
     * A hash map with all the card images.
     */
    public static final Map<String, BufferedImage> cardImages = new HashMap<>(4);
    public static int CARDS_PER_SUIT = 13;
    public static int DEFAULT_SELECTION_DELTA = -40;
    /**
     * An array list that contains the string representations of the cards
     * from the hand of the human player.
     */
    private ArrayList<String> hand = new ArrayList<>();
    private int selectedCard = -1;
    protected static Dimension cardSize = new Dimension(120, 150);
    private int[] xCardPosition = {};
    private int selectionDelta = DEFAULT_SELECTION_DELTA;
    private CrazyEights model;
    private Card.Suit suitPicked = null;
    private boolean drawButtonClicked = false;
    private Card cardToVerify;
    private CrazyEightsView view;

    /**
     *
     * @return the hand of the human player
     */
    public ArrayList<String> getHand() {
        return this.hand;
    }

    /**
     * Preloads images
     */
    protected static void loadImages() {
        try {
            for (String suit : new String[]{"C", "S", "D", "H"}) {
                for (int i = 1; i <= CARDS_PER_SUIT; i++) {
                    cardImages.put(suit + "" + i,
                            ImageIO.read(PlayerPane.class.getResource("/" + suit + i + ".png")));
                }
            }
            cardImages.put("00", ImageIO.read(PlayerPane.class.getResource("/00.png")));
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
     * Sets up panel that contains the player's hand and mouse interactions.
     */
    public PlayerPane(CrazyEights model, CrazyEightsView view) {
        this.model = model;
        this.view = view;

        if (cardImages.size() < 1) {
            loadImages();
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                selectedCard = getSelectedCard(e.getX());
                repaint();
            }
        });
    }

    /**
     *
     * @param x the x coordinate of where it was clicked on the screen
     * @return the index of the card selected by the human player
     */
    protected int getSelectedCard(int x) {
        for (int i = hand.size() - 1; i >= 0; i--) {
            if (x > xCardPosition[i] && x < xCardPosition[i] + cardSize.getWidth()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Implementation of the invalidate method.
     */
    @Override
    public void invalidate() {
        super.invalidate();
        selectedCard = -1;
        xCardPosition = new int[hand.size()];
        int xDelta = (int)Math.floor(Math.min(cardSize.getWidth() * 1.1, (getWidth() - cardSize.getWidth())/(hand.size() - 1)));
        int xPos = (int) ((getWidth() - cardSize.getWidth() - (hand.size() - 1)*xDelta) / 2);
        for (int i = 0; i < hand.size() && i < xCardPosition.length; i++) {
            xCardPosition[i] = xPos;
            xPos += xDelta;
        }

    }

    /**
     * Draws the human player's cards in the interface.
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int x = (int) cardSize.getWidth();
        int y = (int) cardSize.getHeight();
        if(xCardPosition.length != 0) {
            for (int i = 0; i < hand.size() && i < xCardPosition.length; i++) {
                g2d.drawImage(cardImages.get(hand.get(i)),
                        xCardPosition[i], selectedCard == i ? Math.max(selectionDelta, 0) : Math.max(-selectionDelta, 0),
                        x, y, null);
            }
        }
        g2d.dispose();
    }

    /**
     * Sets the hand of cards and resets the view to match.
     * @param hand hand of cards, where every card is a character "C", "S", "D", or "H"
     *             followed by a number between 1 and 13, or 00 for a face-down card
     */
    public void setHand(ArrayList<String> hand) {
        this.hand = hand;
        revalidate();
    }

    /**
     * A method that returns the card selected by the human player to be played.
     * @param hand List of Cards in the player's hand
     * @param model CardGame being played
     * @return the card selected by the human player
     */
    public Card takeTurn(List<Card> hand, CrazyEights model) {
        PlayCardAdapter mouseAdapter = new PlayCardAdapter(this, this.model);
        addMouseListener(mouseAdapter);
        do {
            cardToVerify = mouseAdapter.getCard();
        } while (cardToVerify == null && !drawButtonClicked);
        removeMouseListener(mouseAdapter);
        drawButtonClicked = false;
        return cardToVerify;
    }

    /**
     * A method that lets the human player choose a suit after playing an 8.
     * @param game CardGame being played
     * @return the chosen suit
     */
    public Card.Suit chooseSuit(CrazyEights game) {
        suitPicked = null;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int selected = getSelectedCard(e.getX());
                if (selected >= 0) {
                    switch (selected) {
                        case 0: // Clubs
                            suitPicked = Card.Suit.CLUBS;
                            break;
                        case 1: // Hearts
                            suitPicked = Card.Suit.HEARTS;
                            break;
                        case 2: // Spades
                            suitPicked = Card.Suit.SPADES;
                            break;
                        case 3: // Diamonds
                            suitPicked = Card.Suit.DIAMONDS;
                            break;
                    }
                }
            }
        });
        while (suitPicked == null) {
            try {
                Thread.sleep(150); // Introduce a small delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return suitPicked;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("drawButtonClicked")) {
            drawButtonClicked = (boolean) evt.getNewValue();
        }
    }
}
