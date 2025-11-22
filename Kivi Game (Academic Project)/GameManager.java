import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;
import java.io.*;

public class GameManager {
    private int currentPlayer;
    private int totalPlayers;
    // why different ai player and player list. AI player extends player - polymorphism
    // maybe make an arraylist of players, and dynamically add players based on input, instead of hardcoding 4 players and then
    // making a new instance. Arraylist has size() method, so it can be used for loops
    private List<Player> players;
    private List<Player> aiPlayers;
    private int roundsPlayed;
    private Grid grid;
    private int[] dice;
    private boolean firstTurn;
    private int rerollsLeft = 2; // Default value
    private List<Integer> playerTurns; // Array to track turns per player
    
    private String world;

    private static GameManager instance;

    public static GameManager getInstance(int totalPlayerCount, String world) {
        if (instance == null) {
            instance = new GameManager(totalPlayerCount, world);
        } else {
            instance.resetState(totalPlayerCount, world);
        }
        return instance;
    }

    public static GameManager makeNewInstance(int totalPlayerCount, String world) {
        instance = new GameManager(totalPlayerCount, world);
        return instance;
    }

    private GameManager(int totalPlayers, String world) {
        this.players = new ArrayList<>();
        this.aiPlayers = new ArrayList<>();
        resetState(totalPlayers, world);
    }

    private void initializePlayerTurns(int playerCount) {
        playerTurns = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            playerTurns.add(0);
        }
    }

    private void resetState(int totalPlayers, String world) {
        this.totalPlayers = totalPlayers;
        this.world = world;
        this.setDice(new int[6]);
        
        this.currentPlayer = 1;
        this.roundsPlayed = 0;
        this.firstTurn = true;
        this.rerollsLeft = 2;
        if (this.players == null) {
            this.players = new ArrayList<>();
        } else {
            this.players.clear();
        }
        if (this.aiPlayers == null) {
            this.aiPlayers = new ArrayList<>();
        } else {
            this.aiPlayers.clear();
        }
        initializePlayerTurns(totalPlayers);
        this.grid = null;
    }

    /*
     * SAVE FILE FORMAT
     * 
     * BOARD STATE 1d STRING tile state represented by playernumber, 0 if no one has
     * a tile placed
     * CURRENT DICE -> # # # # # #
     * CURRENT PLAYER -> #
     * CURRENT PLAYER Grid.startOfTurn -> #
     * TOTAL PLAYERS -> #
     * WORLD STRING -> STRING
     * FIRSTTURN BOOL -> 0/1
     * REROLLS LEFT -> #
     * ROUNDSPLAYED -> #
     * PLAYER TURNS -> space seperated ints for each player in the game state # # #
     * #
     * PLAYER OBJECT DATA -> space seperated ints for each Player class variable # #
     * # # # #
     * -PLAYER DATA IS THE EOF 1 LINE FOR EACH PLAYER
     */
    public void saveGame() {
        if (grid == null) {
            System.out.println("No active grid to save.");
            return;
        }
        try {
            //Make save directory if it doesn't exist
            File dir = new File("saves");
            dir.mkdir();
            //Get current number of saves 
            String[] files = dir.list();
            int saveCount = (files != null) ? files.length : 0;
            
            //THIS WONT SAVE ANYTHING IF THERE ARE ALREADY 3 SAVES
            //I DONT KNOW HOW WE WANT TO HANDLE THIS(display error, change max number, etc)
            //FOR NOW I JUST SET IT TO 3
            if (saveCount == 3) {
                System.out.print("Saves Full");
                return;
            }
            //Full file path string save# increment
            String fileName = "saves\\save" + (saveCount + 1) + ".txt";
            File save = new File(fileName);
            if (save.createNewFile()) {
                System.out.println("File created: " + save.getName());
            } else {
                System.out.println("File already exists.");
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            //Save grid state to file
            Tile[][] gameBoard = grid.getBoardState();
            StringBuilder boardString = new StringBuilder();
            for (int col = 0; col < 7; col++) {
                for (int row = 0; row < 7; row++) {
                    //each tile is represented by the playernumber that owns it. 0 if empty
                    boardString.append(gameBoard[col][row].getPlayerNumber());
                }
            }
            writer.write(boardString.toString() + "\n");

            //CurrentDice as string
            StringBuilder diceString = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                diceString.append(this.getDice()[i]).append(" ");
            }
            //Saving gameManager variables
            writer.write(diceString.toString().trim() + "\n"); //Current dice
            writer.write(this.getCurrentPlayer() + "\n");//currentplayer
            writer.write((grid.getStartOfTurn() ? 1: 0) + "\n");
            writer.write(this.totalPlayers + "\n");//totalPlayers
            writer.write(this.world + "\n");//world
            writer.write((this.getFirstTurn() ? 1 : 0) + "\n");//firstTurn bool
            Player current = getCurrentPlayerObject();
            int rerollsToSave = (current != null) ? current.getRerollsLeft() : this.rerollsLeft;
            this.rerollsLeft = rerollsToSave;
            writer.write(rerollsToSave + "\n");//rerollsLeft
            writer.write(this.roundsPlayed + "\n");//roundsPlayed
            writer.write((grid.isConfirmClicked() ? 1 : 0) + "\n");
            //Getting player turns array into string # # # # 
            StringBuilder playerTurnsString = new StringBuilder();
            for (int i = 0; i < this.playerTurns.size(); i++) {
                playerTurnsString.append(this.playerTurns.get(i)).append(" ");
            }
            writer.write(playerTurnsString.toString().trim() + "\n");

            //Saving Player object data
            for (int i = 0; i < this.players.size(); i++) {
                StringBuilder playerString = new StringBuilder();
                Player currentPlayer = this.players.get(i);//Getting player[i]
                playerString.append(currentPlayer.getPlayerNumber()).append(" ");//Getting playerNumber
                playerString.append(currentPlayer.isAI() ? 1 : 0).append(" ");//Geting isAI flag
                playerString.append(currentPlayer.getScore()).append(" ");//Getting playerScore
                playerString.append(currentPlayer.getRerollsLeft()).append(" ");//Getting rerollsLeft for currentplayer
                //If player is AI save difficulty as well
                if (currentPlayer.isAI()) {
                    playerString.append(currentPlayer.getDifficulty()).append(" ");
                }
                writer.write(playerString.toString().trim() + "\n");//Write string line to savefile
            }
            writer.close();//Close file 
        } 
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    
    
    public void addAIPlayer(Player aiPlayer) {
        if (aiPlayer.isAI()) {
            System.out.println("AI Player " + aiPlayer.getPlayerNumber() + " added.");
            aiPlayers.add(aiPlayer);
        } else {
            System.out.println("Tried to add non-AI player " + aiPlayer.getPlayerNumber() + " to aiPlayers.");
        }
    }
    

    public void startGame() {
        takeTurn(); // Directly start first turn
    }
    public void takeTurn() {
        Player currentPlayerObj = getCurrentPlayerObject();
        if (currentPlayerObj == null) {
            return;
        }
        System.out.println("Current player: " + currentPlayer);
        
        if (currentPlayerObj.isAI()) {
            currentPlayerObj.takeTurn();
        } else {
            if (getFirstTurn()) {
                int[] newDice = new int[6];
                for (int i = 0; i < 6; i++) {
                    // nick - changed this to 0's instead of random to get empty start of turn dice to roll
                    newDice[i] = 0;
                }
                setDice(newDice);
                setFirstTurn(false);
                if (grid != null) {
                    grid.updateDice();
                    grid.updateRerollsLabel();
                }
            }
        }
    }
    // this is unnecessary and can cause problems. Set a general getPlayer function instead.
    private Player getAIPlayer(int playerNumber) {
        for (Player aiPlayer : aiPlayers) {
            if (aiPlayer.getPlayerNumber() == playerNumber && aiPlayer.isAI()) {
                return aiPlayer;
            }
        }
        return null;
    }

    // Add methods to get/update player turns
    public void incrementPlayerTurn(int playerIndex) {
        if (playerTurns == null) {
            return;
        }
        if (playerIndex >= 0 && playerIndex < playerTurns.size()) {
            playerTurns.set(playerIndex, playerTurns.get(playerIndex) + 1);
        }
    }

    public int getPlayerTurns(int playerIndex) {
        return playerTurns.get(playerIndex);
    }

    //nick - removed the new diceRollGUI calls when integrating dice rolling to grid window
    public void nextPlayer() {
        incrementPlayerTurn(currentPlayer - 1);
        currentPlayer = (currentPlayer % totalPlayers) + 1;
        resetRerolls();
        this.setDice(new int[6]);
        
        // Update UI first
        if (grid != null) {
            grid.resetSelection();
            grid.updateCurrentPlayer();
            grid.updateDice();
            grid.updateRerollsLabel();
            grid.hideNextPlayerText();
        }

        if (isGameOver()) {
            if (grid != null) {
                grid.setVisible(false);
                grid.dispose();
            }
            announceWinner();
            return;
        }
        
        // Schedule AI turn after UI updates
        SwingUtilities.invokeLater(() -> {
            if (isCurrentPlayerAI()) {
                Player ai = getCurrentPlayerObject();
                ai.takeTurn(); // This will trigger AI logic
            }
        });
    }

    public boolean isCurrentPlayerAI() {
        Player current = getCurrentPlayerObject();
        return current != null && current.isAI();
    }

    public boolean isGameOver() {
        if (players == null || playerTurns == null) {
            return false;
        }
        // Check if any player has reached 10 turns
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) != null && i < playerTurns.size() && playerTurns.get(i) >= 10) {
                return true;
            }
        }
        return false;
    }

    public void setPlayerTurns(List<Integer> p) {
        if (p == null) {
            initializePlayerTurns(this.totalPlayers);
            return;
        }
        this.playerTurns = new ArrayList<>(p);
        while (this.playerTurns.size() < this.totalPlayers) {
            this.playerTurns.add(0);
        }
        while (this.playerTurns.size() > this.totalPlayers) {
            this.playerTurns.remove(this.playerTurns.size() - 1);
        }
    }
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int c) {
        this.currentPlayer = c;
    }
    public void incrementRoundsPlayed() {
        roundsPlayed++;
    }
  

    public void setRerollsLeft(int rerollsLeft) {
        this.rerollsLeft = rerollsLeft;
    }

    public void resetRerolls() {
        this.rerollsLeft = 2;
        Player curr = getCurrentPlayerObject();
        if (curr != null) {
            curr.setRerollsLeft(2);
        }
    }

    public int getWinner() {
        int maxScore = -1;
        int winner = -1;
        for (Player player : players) {
            if (player.getScore() > maxScore) {
                maxScore = player.getScore();
                winner = player.getPlayerNumber();
            }
        }
        return winner;
    }

    public void announceWinner() {
        // Calculate scores using ScoreCalculator
        Tile[][] gridTiles = new Tile[7][7];
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                gridTiles[x][y] = getTile(x, y);
            }
        }
        
        // Debug - Print the occupied tiles
        System.out.println("Occupied tiles at game end:");
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                Tile tile = gridTiles[i][j];
                if (tile != null && tile.isOccupied()) {
                    System.out.println("Tile at " + i + "," + j + " is occupied by Player " + tile.getPlayerNumber());
                }
            }
        }
        
        // Update each player's score
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) != null) {
                int playerNumber = players.get(i).getPlayerNumber();
                int calculatedScore = ScoreCalculator.calculateScore(gridTiles, playerNumber);
                players.get(i).updateScore(calculatedScore);
                System.out.println("Player " + playerNumber + " final score: " + calculatedScore);
            }
        }
        
        // Sort players by score (highest first)
//        Player[] sortedPlayers = Arrays.copyOf(players, players.length);
        Player[] sortedPlayers = players.toArray(new Player[players.size()]);
        Arrays.sort(sortedPlayers, (p1, p2) -> {
            if (p1 == null) return 1;
            if (p2 == null) return -1;
            return Integer.compare(p2.getScore(), p1.getScore());
        });
        
        // Create and display WinnerGUI
        SwingUtilities.invokeLater(() -> {
            try {
                // First make sure Grid is fully disposed
                if (grid != null) {
                    grid.setVisible(false);
                    grid.dispose();
                    grid = null;
                }
                
                // Create the WinnerGUI
                WinnerGUI winnerScreen = new WinnerGUI(sortedPlayers,this);
                
                // Set the winner label
                winnerScreen.setWinnerLabel("Player " + sortedPlayers[0].getPlayerNumber() + " WON !!! :) ");
                
                // Set the scores
                if (sortedPlayers.length > 0 && sortedPlayers[0] != null) {
                    winnerScreen.setFirstPointLabel("1: Player " + sortedPlayers[0].getPlayerNumber() + " , " + sortedPlayers[0].getScore() + " points");
                }
                
                if (sortedPlayers.length > 1 && sortedPlayers[1] != null) {
                    winnerScreen.setSecondPointLabel("2: Player " + sortedPlayers[1].getPlayerNumber() + " , " + sortedPlayers[1].getScore() + " points");
                }
                
                if (sortedPlayers.length > 2 && sortedPlayers[2] != null) {
                    winnerScreen.setThirdPointLabel("3: Player " + sortedPlayers[2].getPlayerNumber() + " , " + sortedPlayers[2].getScore() + " points");
                }
                
                if (sortedPlayers.length > 3 && sortedPlayers[3] != null) {
                    winnerScreen.setFourthPointLabel("4: Player " + sortedPlayers[3].getPlayerNumber() + " , " + sortedPlayers[3].getScore() + " points");
                }
                
            

                winnerScreen.setVisible(true);
            } catch (Exception e) {
                System.out.println("Error showing winner screen: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    public boolean placeStone(int x, int y, int playerNumber) {
        Tile tile = getTile(x, y);
        if (tile != null && !tile.isOccupied() && playerNumber >= 1 && playerNumber <= players.size()) {
            tile.placeTile(playerNumber);
            players.get(playerNumber - 1).placeStone(tile, dice); // Use the current dice values
            return true;
        }
        return false;
    }

    public boolean isValidPlacement(int x, int y, int[] diceValues) {
        if (diceValues == null || diceValues.length != 6) {
            return false;
        }
        for (int val : diceValues) {
            if (val < 1 || val > 6) {
                return false;
            }
        }
        Tile tile = getTile(x, y);
        
        // First check if tile exists
        if (tile == null) {
            return false;
        }
        
        // Then check if tile is already occupied - add explicit debug output
        if (tile.isOccupied()) {
            System.out.println("Tile at " + x + "," + y + " is already occupied by player " + tile.getPlayerNumber());
            return false;
        }
        
        // Finally check if the dice combination is valid for this tile
        boolean validCombo = GameLogic.isValidCombination(diceValues, tile.getTileType());
        return validCombo;
    }

    public int getTilePoints(int x, int y) {
        Tile tile = getTile(x, y);
        return (tile != null) ? tile.getScoreValue() : 0;
    }

    public void addPlayer(Player player) {
        int playerNumber = player.getPlayerNumber();
        if (playerNumber < 1 || playerNumber > 4) {
            throw new IllegalArgumentException("Invalid player number: " + playerNumber);
        }
        if (players.size() >= totalPlayers) {
            throw new IllegalStateException("Cannot add more players than the configured total.");
        }
        players.add(player);
    }

    public void clearPlayers() {
        this.players = new ArrayList<>();
        this.aiPlayers = new ArrayList<>();
        this.currentPlayer = 1;
        initializePlayerTurns(this.totalPlayers);
    }

    public void setPlayerList(List<Player> p) {
        this.players = p;
        this.totalPlayers = p.size();
    }
    
    public void setAiPlayerList(List<Player> p) {
        this.aiPlayers = p;
    }
    public Player getCurrentPlayerObject() {
        if (players == null || players.isEmpty()) {
            return null;
        }
        int index = currentPlayer - 1;
        if (index < 0 || index >= players.size()) {
            return null;
        }
        return players.get(index);
    }
    public Tile getTile(int x, int y) {
        if (grid == null) {
            return null;
        }
        return grid.getTile(x, y); // Use the method from Grid
    }

    public void setRoundsPlayed(int r){
        this.roundsPlayed = r;
    }

    public Grid getGrid() {
        return this.grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public int[] getDice() {
        return this.dice != null ? this.dice.clone() : new int[6];
    }

    public void setDice(int[] currDice) {
        this.dice = currDice != null ? currDice.clone() : new int[6];
    }

    public void updateGridDiceLabel() {
        if (this.grid != null) {
            this.grid.updateDice();
        }
    }

    public void updateGridRerollLabel() {
        if (this.grid != null) {
            this.grid.updateRerollsLabel();
        }
    }

    public void updateGridPlayerLabel() {
        if (this.grid != null) {
            this.grid.updateCurrentPlayer();
        }
    }

    public void updateNextPlayerText() {
        if (this.grid != null) {
            this.grid.showNextPlayerText();
        }
    }
    
    public boolean getFirstTurn() {
        return this.firstTurn;
    }

    public void setFirstTurn(boolean val) {
        this.firstTurn = val;
    }

}
