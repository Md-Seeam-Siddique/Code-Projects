import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Grid extends JFrame implements ActionListener {

    private static JPanel mainPanel, eastPanel, gridPanel, dicePanel;
    private JLabel topLabel, playerLabel, invalidTextArea, remainingRerollsLabel, nextTurnInfoLabel;
    private JButton saveButton, quitButton, confirmButton, nextButton, rerollButton;
    private static final int WINDOW_WIDTH = 1100;
    private static final int WINDOW_HEIGHT = 720;
    private boolean confirmClicked;
    private boolean startOfTurn; // Bool to check if it's the start of players turn for handling some dice and
                                 // reroll logic
    private Tile[][] grid;
    private Tile lastTile;
    private int currentPlayer;
    private JLabel[] diceValueLabels;
    private JCheckBox[] diceCheckBoxes;
    private GameManager gameManager;
    private int[] currentDiceValues;

    private static int GRID_WIDTH, GRID_HEIGHT;
    private String world;

    public Grid(GameManager gameManager, String world) {
        this.gameManager = gameManager;
        this.currentDiceValues = gameManager.getDice();
        this.currentPlayer = gameManager.getCurrentPlayer();
        GRID_WIDTH = 7;
        GRID_HEIGHT = 7;
        this.startOfTurn = true;
        this.world = world;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        // Main window panel
        mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
        setContentPane(mainPanel);
        lastTile = null;

        // Panel that handles dice rolling and displaying values in the game grid window
        dicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        dicePanel.setBackground(Color.WHITE);

        // Initializing the 6 dice checkboxes and labels
        diceValueLabels = new JLabel[6];
        diceCheckBoxes = new JCheckBox[6];
        // This is taken and adjusted from diceRollGUI
        for (int i = 0; i < 6; i++) {
            // New panel every iteration to represent one dice
            JPanel currentDice = new JPanel();
            currentDice.setLayout(new BoxLayout(currentDice, BoxLayout.Y_AXIS));
            currentDice.setBackground(Color.WHITE);
            currentDice.setAlignmentX(Component.CENTER_ALIGNMENT);
            // New label every iteration for each dice to display it's value
            diceValueLabels[i] = new JLabel(String.valueOf(this.currentDiceValues[i]), SwingConstants.CENTER);
            diceValueLabels[i].setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 24));
            diceValueLabels[i].setAlignmentX(Component.CENTER_ALIGNMENT);

            diceCheckBoxes[i] = new JCheckBox();
            diceCheckBoxes[i].setSelected(true);
            diceCheckBoxes[i].setBackground(Color.WHITE);
            diceCheckBoxes[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            diceCheckBoxes[i].setEnabled(false);

            currentDice.add(diceValueLabels[i]);
            currentDice.add(diceCheckBoxes[i]);
            dicePanel.add(currentDice);
        }

        // Panel that holds buttons and dice roll information on the right of the main
        // panel
        eastPanel = new JPanel();
        eastPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5); // Added padding
        c.fill = GridBagConstraints.HORIZONTAL;

        confirmButton = new JButton("Confirm Tile");
        confirmButton.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        confirmButton.setBackground(ColorDiff(world));/////////////////////////////////////////
        confirmButton.setFocusPainted(false);

        nextButton = new JButton("Next Player");
        nextButton.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        nextButton.setBackground(ColorDiff(world));///////////////////////////////////
        nextButton.setFocusPainted(false);

        rerollButton = new JButton("Roll Dice");
        rerollButton.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        rerollButton.setBackground(ColorDiff(world)); /////////////////////////////
        rerollButton.setFocusPainted(false);

        Player startingPlayer = gameManager.getCurrentPlayerObject();
        int rerollsLeft = startingPlayer != null ? startingPlayer.getRerollsLeft() : 2;
        rerollButton.setEnabled(rerollsLeft > 0);

        remainingRerollsLabel = new JLabel("Rerolls Left: " + rerollsLeft, SwingConstants.CENTER);
        remainingRerollsLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));

        playerLabel = new JLabel("Player " + currentPlayer + "'s Turn", SwingConstants.CENTER);
        playerLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 24));

        // Text telling player to move to next player
        nextTurnInfoLabel = new JLabel("<html>Press the Next Player button<br/>to go to the next player!</html>",
                SwingConstants.CENTER);
        nextTurnInfoLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 18));
        nextTurnInfoLabel.setVisible(false);

        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(7, 7, 2, 2));
        gridPanel.setPreferredSize(new Dimension(600, 600));

        invalidTextArea = new JLabel("Invalid Tile", SwingConstants.CENTER);
        invalidTextArea.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 24));
        invalidTextArea.setForeground(Color.RED);
        invalidTextArea.setVisible(false);

        saveButton = new JButton("Save Game");
        saveButton.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        saveButton.setBackground(ColorDiff(world));/////////////////////////////////

        quitButton = new JButton("Quit Game");
        quitButton.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        quitButton.setBackground(ColorDiff(world)); /////////////////////////

        topLabel = new JLabel("Choose your Tile", SwingConstants.CENTER);
        topLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 24));

        confirmButton.addActionListener(this);
        nextButton.addActionListener(this);
        saveButton.addActionListener(this);
        quitButton.addActionListener(this);
        rerollButton.addActionListener(this);

        // Generating game grid of Tile buttons
        grid = new Tile[GRID_WIDTH][GRID_HEIGHT];
        for (int col = 0; col < GRID_WIDTH; col++) {
            for (int row = 0; row < GRID_HEIGHT; row++) {
                grid[col][row] = new Tile(col, row, world);
                grid[col][row].setPreferredSize(new Dimension(80, 80));
                grid[col][row].setColor(world);
                grid[col][row].setImage();
                grid[col][row].setFocusPainted(false);
                grid[col][row].setOpaque(true);
                grid[col][row].addActionListener(this);
                gridPanel.add(grid[col][row]);
            }
        }

        gridPanel.setBackground(Color.WHITE);
        c.anchor = GridBagConstraints.CENTER;

        // Adding components to eastPanel in correct order
        c.gridy = 0;
        eastPanel.add(playerLabel, c);

        // Add invalidTextArea at gridy = 1
        c.gridy = 1;
        eastPanel.add(invalidTextArea, c);

        c.gridy = 2;
        eastPanel.add(dicePanel, c);

        c.gridy = 3;
        eastPanel.add(rerollButton, c);

        c.gridy = 4;
        eastPanel.add(remainingRerollsLabel, c);

        c.gridy = 5;
        eastPanel.add(confirmButton, c);

        c.gridy = 6;
        eastPanel.add(nextButton, c);

        c.gridy = 7;
        eastPanel.add(saveButton, c);

        c.gridy = 8;
        eastPanel.add(quitButton, c);

        c.gridy = 9;
        eastPanel.add(nextTurnInfoLabel, c);

        eastPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(topLabel, BorderLayout.NORTH);
        mainPanel.add(eastPanel, BorderLayout.EAST);
        mainPanel.add(gridPanel, BorderLayout.CENTER);

        setResizable(false);
        setVisible(true);
    }

    public void showNextPlayerText() {
        this.nextTurnInfoLabel.setVisible(true);
    }

    public void hideNextPlayerText() {
        this.nextTurnInfoLabel.setVisible(false);
    }

    public void resetSelection() {
        if (lastTile != null) {
            lastTile.resetBorder();
        }
        lastTile = null;
        confirmClicked = false;
        invalidTextArea.setVisible(false);
    }

    public void updateDice() {
        this.currentDiceValues = this.gameManager.getDice();
        for (int i = 0; i < 6; i++) {
            diceValueLabels[i].setText(String.valueOf(this.currentDiceValues[i]));
        }
    }

    public void rerollDice() {
        // Decrements player rerolls after their first roll of a turn
        if (!this.startOfTurn) {
            gameManager.getCurrentPlayerObject()
                    .setRerollsLeft(gameManager.getCurrentPlayerObject().getRerollsLeft() - 1);
        }
        for (int i = 0; i < 6; i++) {
            // When this passes it sets the checkboxes to be enabled for each dice to allow
            // for selective rerolling
            if (this.startOfTurn) {
                diceCheckBoxes[i].setEnabled(true);
            }
            // Rerolls only selected dice
            if (diceCheckBoxes[i].isSelected()) {
                currentDiceValues[i] = (int) (Math.random() * 6 + 1);
                diceValueLabels[i].setText(String.valueOf(currentDiceValues[i]));
            }
        }
        this.startOfTurn = false;
        gameManager.setDice(currentDiceValues.clone());
    }

    // Resets each dice checkbox to be disabled to force player to roll all their
    // dice at the start of their turn
    public void resetDiceCheckboxes() {
        for (int i = 0; i < 6; i++) {
            diceCheckBoxes[i].setSelected(true);
            diceCheckBoxes[i].setEnabled(false);
        }
    }

    public boolean getStartOfTurn() {
        return this.startOfTurn;
    }

    public void setStartOfTurn(boolean b) {
        this.startOfTurn = b;
    }

    public void updateCurrentPlayer() {
        this.currentPlayer = this.gameManager.getCurrentPlayer();
        this.playerLabel.setText("Player " + currentPlayer + "'s Turn");
        this.startOfTurn = true;
        resetDiceCheckboxes();
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length) {
            return grid[x][y];
        } else {
            return null;
        }
    }

    public void updateRerollsLabel() {
        Player curr = gameManager.getCurrentPlayerObject();
        int rerollsLeft = (curr != null) ? curr.getRerollsLeft() : 0;
        gameManager.setRerollsLeft(rerollsLeft);
        remainingRerollsLabel.setText("Rerolls Left: " + rerollsLeft);
        rerollButton.setEnabled(rerollsLeft > 0);
    }

    public boolean isConfirmClicked() {
        return confirmClicked;
    }

    public void setConfirmClicked(boolean confirmClicked) {
        this.confirmClicked = confirmClicked;
    }

    public Tile getLastTile() {
        return lastTile;
    }

    public void setLastTile(Tile lastTile) {
        this.lastTile = lastTile;
    }

    public JLabel getInvalidTextArea() {
        return invalidTextArea;
    }

    public void setInvalidTextArea(JLabel invalidTextArea) {
        this.invalidTextArea = invalidTextArea;
    }

    public Tile[][] getBoardState() {
        return this.grid;
    }

    public void setBoardState(String s) {
        if (s == null || s.length() < 49) {
            return;
        }
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 7; row++) {
                char val = s.charAt(col * 7 + row);
                if (val != '0') {
                    grid[col][row].placeTile(val - '0');
                }
            }
        }
    }

    public void actionPerformed(ActionEvent aevt) {
        if (aevt.getSource() == nextButton) {
            // Check if game is over before advancing to next player
            if (gameManager.isGameOver()) {
                setVisible(false);
                dispose();
                gameManager.announceWinner();
                return;
            }
            this.nextTurnInfoLabel.setVisible(false);
            gameManager.nextPlayer();
            // gameManager.takeTurn();
            // if (gameManager.isCurrentPlayerAI()) {
            // Player ai = gameManager.getCurrentPlayerObject();
            // System.out.println("AI Player " + ai.getPlayerNumber() + " is taking their
            // turn now...");
            // ai.takeTurn();
            // }
            //
            // this.updateCurrentPlayer();
            this.confirmClicked = false;

            // Check if the game is over after changing player (their 10th turn might have
            // just completed)
            if (gameManager.isGameOver()) {
                setVisible(false);
                dispose();
                gameManager.announceWinner();
                return;
            }
        } else if (aevt.getSource() instanceof Tile) {
            Tile tempTile = (Tile) aevt.getSource();

            if (lastTile != null) {
                lastTile.resetBorder();
            }
            tempTile.setClicked();
            lastTile = tempTile;
        } else if (aevt.getSource() == confirmButton) {
            if (getLastTile() == null) {
                getInvalidTextArea().setText("No tile selected!");
                getInvalidTextArea().setVisible(true);
                return;
            }

            Player curr = gameManager.getCurrentPlayerObject();
            if (isConfirmClicked() || (curr != null && curr.isAI())) {
                return;
            }

            // Get coordinates of the selected tile
            int tileX = getLastTile().getBoardX();
            int tileY = getLastTile().getBoardY();

            gameManager.setDice(currentDiceValues.clone());

            if (gameManager.isValidPlacement(tileX, tileY, this.currentDiceValues) &&
                gameManager.placeStone(tileX, tileY, currentPlayer)) {
                setConfirmClicked(true);
                getInvalidTextArea().setVisible(false);
                this.showNextPlayerText();
            } else {
                getInvalidTextArea().setText("Invalid Tile!");
                getInvalidTextArea().setVisible(true);
            }
        } else if (aevt.getSource() == rerollButton) {
            Player curr = gameManager.getCurrentPlayerObject();
            if (curr != null && !curr.isAI() && !this.isConfirmClicked()) {
                this.rerollDice();
                this.updateRerollsLabel();
            }
        } else if (aevt.getSource() == saveButton) {
            gameManager.saveGame();
        } else if (aevt.getSource() == quitButton) {
            System.exit(0);
        }

    }

    public Color ColorDiff(String world) {
        Color Indigo = new Color(0, 0, 255);
        Color darkPink = new Color(204, 0, 102);
        Color yellow = Color.YELLOW;
        Color blue = new Color(0, 153, 255);

        if (world.equals("RED")) {
            return Indigo;
        } else if (world.equals("BLUE")) {
            return darkPink; // DARK PINK
        } else if (world.equals("GREEN")) {
            return yellow; // YELLOW
        }
        return blue;
    }
}
