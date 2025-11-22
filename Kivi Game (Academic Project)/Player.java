import javax.swing.*;

public class Player {
    private int playerNumber;
    private int score;
    private int stonesPlaced;
    private int rerollsLeft = 2;
    private int[] lastDiceRoll;
    protected boolean isAI;
    protected GameManager gameManager;

    // Constructor for human players
    public Player(int playerNumber) {
        this.playerNumber = playerNumber;
        this.isAI = false;
        this.score = 0;
        this.stonesPlaced = 0;
    }

    // Protected constructor for AI subclasses
    protected Player(int playerNumber, GameManager gameManager) {
        this(playerNumber);
        this.gameManager = gameManager;
    }

    // Common methods for all players
    public boolean isAI() {
        return isAI;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getScore() {
        return score;
    }

    public void updateScore(int score) {
        this.score = score;
    }

    public boolean placeStone(Tile tile, int[] diceValues) {
        if (!tile.isOccupied()) {
            tile.placeTile(playerNumber);
            stonesPlaced++;
            return true;
        }
        return false;
    }

    // Turn handling (override in AI)
    public void takeTurn() {
        // Base implementation does nothing for human players
    }
    // AI getter (override in AI)
    public String getDifficulty() {
        return "";
    }
    
    // AI setter (override in AI)
    public void setDifficulty(String d) {
        //
    }
    // Reroll management
    public int getRerollsLeft() {
        return rerollsLeft;
    }

    public void setRerollsLeft(int rerollsLeft) {
        this.rerollsLeft = Math.max(0, rerollsLeft);
    }

    public void resetRerolls() {
        this.rerollsLeft = 2;
    }

    // Dice management
    public void setLastDiceRoll(int[] diceValues) {
        this.lastDiceRoll = diceValues != null ? diceValues.clone() : null;
    }

    public int[] getLastDiceRoll() {
        return lastDiceRoll;
    }
    
    public int getStonesPlaced() {
        return this.stonesPlaced;
    }

    public void setIsAI(boolean ai) {
        this.isAI = ai;
    }
    @Override
    public String toString() {
        return "Player " + playerNumber + (isAI ? " (AI)" : " (Human)");
    }
    
}