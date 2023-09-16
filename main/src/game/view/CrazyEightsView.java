package game.view;

import game.controller.ComputerPane;
import game.controller.GamePane;
import game.controller.PlayerPane;
import game.model.CrazyEights;
import game.model.RandomCrazyEightsPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * CrazyEightsView class puts together all the visual elements
 * of the entire interface.
 */
public class CrazyEightsView extends JFrame implements PropertyChangeListener {

    private CrazyEights model;
    private GamePane boardCenter;
    private PlayerPane playerPane;
    private ComputerPane computerPane;
    private JPanel endingPanel;


    public CrazyEightsView(CrazyEights model) {
        this.model = model;
        this.model.addListener(this);
        init();
    }

    /**
     * Initialize the frame and include all the panels.
     */
    private void init() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 1000);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        ImageIcon img = new ImageIcon("eightInFire.png");
        this.setIconImage(img.getImage());

        // set player's hand
        setPlayerPanel();

        // set computer's hand
        setComputerPanel();

        // set central game panel
        setCentralPanel();

        // set the players of the game
        setPlayers();

        // make the frame visible
        this.setVisible(true);
    }

    private void setPlayers() {
        model.addPlayer(playerPane);
        System.out.println("Computer:" + computerPane.getComputerPlayer() );

        model.addPlayer(computerPane.getComputerPlayer());
        System.out.println("Human:" + playerPane );
    }

    private void setPlayerPanel() {
        playerPane = new PlayerPane(model, this);
        playerPane.setBackground(new Color(0x088A4B));
        this.add(playerPane, BorderLayout.SOUTH);
    }

    private void setComputerPanel() {
        computerPane =  new ComputerPane(model);
        computerPane.setBackground(new Color(0x088A4B));
        this.add(computerPane, BorderLayout.NORTH);
    }

    private void setCentralPanel() {
        boardCenter = new GamePane(model, playerPane);
        boardCenter.setBackground(new Color(0x088A4B));
        this.add(boardCenter, BorderLayout.CENTER);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "hand":
                if (model.getCurrentPlayer() instanceof PlayerPane) {
                    playerPane.setHand(model.fromCardHandToString(model.getCurrentPlayer()));
                } else {
                    if (model.getCurrentPlayer() instanceof RandomCrazyEightsPlayer) {
                        computerPane.setHand(computerPane.getBackCardHand(model.hands.get(model.getCurrentPlayer())));
                    }
                }
                playerPane.repaint();
                computerPane.repaint();
                break;
            case "topCard":
                boardCenter.setTopCard(model.getTopCardOnDiscardPile().toString());
                boardCenter.repaint();
                break;
            case "suit":
                boardCenter.setTopCard("" + evt.getNewValue().toString() + "8");
                boardCenter.repaint();
                break;
            case "8":
                playerPane.setHand(new ArrayList<>(Arrays.asList("C8", "H8", "S8", "D8")));
                playerPane.repaint();
                break;
            case "end":
                // in the case that a player does not have cards anymore, that player wins
                // I created a panel for when the human player wins that shows the message "You Won!"
                // and a panel for when the human player losses that shows "Game Over"
                JLabel message = new JLabel(evt.getNewValue().toString());
                message.setFont(new Font("Arial", Font.BOLD, 100));
                message.setForeground(Color.WHITE);
                remove(boardCenter);
                remove(playerPane);
                remove(computerPane);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.fill = GridBagConstraints.CENTER;
                endingPanel = new JPanel(new GridBagLayout());
                endingPanel.add(message, gbc);
                endingPanel.setBackground(model.getCurrentPlayerName().equals("Computer") ?  Color.RED : new Color(0x088A4B));
                this.add(endingPanel, BorderLayout.CENTER);
                this.revalidate();
                endingPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        System.exit(0);
                    }
                });
                break;
        }
    }
}
