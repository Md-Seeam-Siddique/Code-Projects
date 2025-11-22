import java.util.Random;

public class AIPlayer extends Player {
    private String difficulty;
    private static final Random rand = new Random();

    public AIPlayer(int playerNumber, String difficulty, GameManager gameManager) {
        super(playerNumber, gameManager);
        this.difficulty = difficulty;
        this.isAI = true;
    }

    @Override
    public void takeTurn() {
        System.out.println("[AI] Player " + getPlayerNumber() + " (" + difficulty + ") taking turn");
        
        int[] currentDice = rollDice();
        gameManager.setDice(currentDice);
        gameManager.updateGridDiceLabel();
        boolean placed = attemptPlacement(currentDice);
        System.out.println(getPlayerNumber());
        if ("Hard".equals(difficulty) && !placed && getRerollsLeft() > 0) {
            System.out.println("[AI] Hard difficulty rerolling...");
            setRerollsLeft(getRerollsLeft() - 1);
            currentDice = rollDice();
            gameManager.setDice(currentDice);
            gameManager.updateGridDiceLabel();
            gameManager.updateGridRerollLabel();
            placed = attemptPlacement(currentDice);            
        }
        gameManager.nextPlayer();
        
    }

    private int[] rollDice() {
        int[] dice = new int[6];
        for (int i = 0; i < 6; i++) {
            dice[i] = rand.nextInt(6) + 1;
        }
        setLastDiceRoll(dice);
        gameManager.setDice(dice);
        return dice;
    }

    private boolean attemptPlacement(int[] diceValues) {
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                if (gameManager.isValidPlacement(x, y, diceValues)) {
                    System.out.println("[AI] Placing at (" + x + "," + y + ")");
                    gameManager.placeStone(x, y, getPlayerNumber());
                    return true;
                }
            }
        }
        System.out.println("[AI] No valid placements found");
        return false;
    }
    @Override
    public String getDifficulty() {
        return difficulty;
    }

    @Override
    public void setDifficulty(String d) {
        this.difficulty = d;
    }
    @Override
    public String toString() {
        return super.toString() + " - " + difficulty;
    }
}
