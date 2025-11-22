import java.util.Arrays;


public class GameLogic
 {
    // Fixed 7x7 grid layout 
    
    private static final String[] TILE_COMBINATIONS = 
    {
        "Two pairs", "Three of a kind", "Little straight", "Full house", "Four of a kind", "Large straight", "All even",
        "All odd", "12 or fewer", "30 or more", "Three pairs", "Two times three of a kind", "Four of a kind and a pair", "Two pairs",
        "Three of a kind", "Little straight", "Full house", "Four of a kind", "Large straight", "All even", "All odd",
        "12 or fewer", "30 or more", "Three pairs", "Two times three of a kind", "Four of a kind and a pair", "Two pairs", "Three of a kind",
        "Little straight", "Full house", "Four of a kind", "Large straight", "All even", "All odd", "12 or fewer",
        "30 or more", "Three pairs", "Two times three of a kind", "Four of a kind and a pair", "Two pairs", "Three of a kind", "Little straight",
        "Full house", "Four of a kind", "Large straight", "All even", "All odd", "12 or fewer", "30 or more"
    };

    private static final String[] TILE_COLORS =
    {
        "PINK", "BLACK", "WHITE", "PINK", "BLACK", "WHITE", "PINK",
        "BLACK", "WHITE", "PINK", "BLACK", "WHITE", "PINK", "BLACK",
        "WHITE", "PINK", "BLACK", "WHITE", "PINK", "BLACK", "WHITE",
        "PINK", "BLACK", "WHITE", "PINK", "BLACK", "WHITE", "PINK",
        "BLACK", "WHITE", "PINK", "BLACK", "WHITE", "PINK", "BLACK",
        "WHITE", "PINK", "BLACK", "WHITE", "PINK", "BLACK", "WHITE",
        "PINK", "BLACK", "WHITE", "PINK", "BLACK", "WHITE", "PINK"
    };

    private static final int[] TILE_POINTS =
    {
        3, 2, 1, 3, 2, 1, 3,
        2, 1, 3, 2, 1, 3, 2,
        1, 3, 2, 1, 3, 2, 1,
        3, 2, 1, 3, 2, 1, 3,
        2, 1, 3, 2, 1, 3, 2,
        1, 3, 2, 1, 3, 2, 1,
        3, 2, 1, 3, 2, 1, 3
    };

    //Properties of the tiles
    public static String getTileCombination(int x, int y) 
    {
        return TILE_COMBINATIONS[x * 7 + y];
    }

    public static String getTileColor(int x, int y) 
    {
        return TILE_COLORS[x * 7 + y];
    }

    public static int getTilePoints(int x, int y)
    {
        return TILE_POINTS[x * 7 + y];
    }

public static boolean isValidCombination(int[] diceValues, String combination) {
    
    boolean result = switch (combination) {
        case "Two pairs" -> hasTwoPairs(diceValues);
        case "Three of a kind" -> hasThreeOfAKind(diceValues);
        case "Little straight" -> hasLittleStraight(diceValues);
        case "Full house" -> hasFullHouse(diceValues);
        case "Four of a kind" -> hasFourOfAKind(diceValues);
        case "Large straight" -> hasLargeStraight(diceValues);
        case "All even" -> allEven(diceValues);
        case "All odd" -> allOdd(diceValues);
        case "12 or fewer" -> sumIs12OrFewer(diceValues);
        case "30 or more" -> sumIs30OrMore(diceValues);
        case "Three pairs" -> hasThreePairs(diceValues);
        case "Two times three of a kind" -> hasTwoTimesThreeOfAKind(diceValues);
        case "Four of a kind and a pair" -> hasFourOfAKindAndAPair(diceValues);
        default -> false;
    };

    return result;
}

    //Helper Methods for Combinations
    private static boolean hasTwoPairs(int[] dice) 
    {
        int[] counts = new int[6];
        for (int value : dice) counts[value - 1]++;
        int pairs = 0;
        for (int count : counts) if (count >= 2) pairs++;
        return pairs >= 2;
    }

    private static boolean hasThreeOfAKind(int[] dice) 
    {
        int[] counts = new int[6];
        for (int value : dice) counts[value - 1]++;
        for (int count : counts) if (count >= 3) return true;
        return false;
    }

    private static boolean hasLittleStraight(int[] dice) 
    {
        boolean[] exists = new boolean[7]; // Index 0 unused
        for (int d : dice) exists[d] = true;
        int currentStreak = 0;
        for (boolean b : exists) {
            currentStreak = b ? currentStreak + 1 : 0;
            if (currentStreak >= 4) return true;
        }
        return false;
    }

    private static boolean hasFullHouse(int[] dice)
    {
        int[] counts = new int[6];
        for (int value : dice) counts[value - 1]++;
        boolean hasThree = false, hasTwo = false;
        for (int count : counts) {
            if (count >= 3) hasThree = true;
            if (count >= 2) hasTwo = true;
        }
        return hasThree && hasTwo;
    }

    private static boolean hasFourOfAKind(int[] dice) 
    {
        int[] counts = new int[6];
        for (int value : dice) counts[value - 1]++;
        for (int count : counts) if (count >= 4) return true;
        return false;
    }

    private static boolean hasLargeStraight(int[] dice) 
    {
        boolean[] exists = new boolean[7]; // Index 0 unused
        for (int d : dice) exists[d] = true;
        int currentStreak = 0;
        for (boolean b : exists) {
            currentStreak = b ? currentStreak + 1 : 0;
            if (currentStreak >= 5) return true;
        }
        return false;
    }

    private static boolean allEven(int[] dice) 
    {
        for (int d : dice) if (d % 2 != 0) return false;
        return true;
    }

    private static boolean allOdd(int[] dice) 
    {
        for (int d : dice) if (d % 2 == 0) return false;
        return true;
    }

    private static boolean sumIs12OrFewer(int[] dice) 
    {
        int sum = 0;
        for (int d : dice) sum += d;
        return sum <= 12;
    }

    private static boolean sumIs30OrMore(int[] dice)
    {
        int sum = 0;
        for (int d : dice) sum += d;
        return sum >= 30;
    }

    private static boolean hasThreePairs(int[] dice)
    {
        int[] counts = new int[6];
        for (int value : dice) counts[value - 1]++;
        int pairs = 0;
        for (int count : counts) if (count == 2) pairs++;
        return pairs == 3;
    }

    private static boolean hasTwoTimesThreeOfAKind(int[] dice)
    {
        int[] counts = new int[6];
        for (int value : dice) counts[value - 1]++;
        int triplets = 0;
        for (int count : counts) if (count == 3) triplets++;
        return triplets == 2;
    }

    private static boolean hasFourOfAKindAndAPair(int[] dice) 
    {
        int[] counts = new int[6];
        for (int value : dice) counts[value - 1]++;
        boolean hasFour = false, hasTwo = false;
        for (int count : counts) {
            if (count == 4) hasFour = true;
            if (count == 2) hasTwo = true;
        }
        return hasFour && hasTwo;
    }
}