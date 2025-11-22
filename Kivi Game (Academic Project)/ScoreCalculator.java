public class ScoreCalculator {
    public static int calculateScore(Tile[][] grid, int playerNumber) {
        int score = 0;
        int rows = grid.length;
        int cols = grid[0].length;
        
        System.out.println("Calculating score for Player " + playerNumber);
        
        // Count occupied tiles for debugging
        int totalOccupied = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] != null && grid[i][j].isOccupied() && grid[i][j].getPlayerNumber() == playerNumber) {
                    totalOccupied++;
                }
            }
        }
        System.out.println("Player " + playerNumber + " has " + totalOccupied + " occupied tiles");

        // Calculate score by rows
        for (int row = 0; row < rows; row++) {
            int rowScore = calculateRowScore(grid[row], playerNumber);
            score += rowScore;
            if (rowScore > 0) {
                System.out.println("Row " + row + " score: " + rowScore);
            }
        }

        // Calculate score by columns
        for (int col = 0; col < cols; col++) {
            int colScore = calculateColumnScore(grid, col, playerNumber);
            score += colScore;
            if (colScore > 0) {
                System.out.println("Column " + col + " score: " + colScore);
            }
        }

        System.out.println("Final score for Player " + playerNumber + ": " + score);
        return score;
    }

    private static int calculateRowScore(Tile[] row, int playerNumber) {
        return applyScoringRules(row, playerNumber);
    }

    private static int calculateColumnScore(Tile[][] grid, int col, int playerNumber) {
        Tile[] column = new Tile[grid.length];
        for (int i = 0; i < grid.length; i++) {
            column[i] = grid[i][col];
        }
        return applyScoringRules(column, playerNumber);
    }

    private static int applyScoringRules(Tile[] tiles, int playerNumber) {
        int score = 0;
        int streakPoints = 0;
        int streakLength = 0;

        for (Tile tile : tiles) {
            if (tile != null && tile.isOccupied() && tile.getPlayerNumber() == playerNumber) {
                streakPoints += tile.getScoreValue(); // 1, 2, or 3 based on tile color
                streakLength++;
            } else {
                if (streakLength > 0) {
                    score += streakPoints * streakLength;
                    streakPoints = 0;
                    streakLength = 0;
                }
            }
        }

        // Add final streak if needed
        if (streakLength > 0) {
            score += streakPoints * streakLength;
        }

        return score;
    }
}
