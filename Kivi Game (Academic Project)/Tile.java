import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Tile extends JButton {
    private int x, y; // Tile coordinates
    private boolean clicked; 
    private String tileType; 
    private Icon tileIcon; 
    private Image tileImage; 
    private boolean occupied = false; 
    private int playerNumber = 0; 
    private int scoreValue; 

    // Hardcoded tile positioning strings
    private static final String WHITE_TILES = "1001001001010010000010100010100000100101001001001";
    private static final String BLACK_TILES = "0000000010101000001000001000001000001010100000000";
    private static final String FULL_HOUSE_TILES = "0000000000000000000010100000000000000001000000001";
    private static final String FOUR_OF_KIND_TILES = "0000000000000001000000000000000010000000000010010";
    private static final String GREATER_THAN_THIRTY_TILES = "0000000000000100100000000000000001000000000100000";
    private static final String TWO_THREE_KIND_TILES = "0000000000001000000000001000000000001000000000000";
    private static final String THREE_PAIRS_TILES = "0000000000100000000000000000001000000000100000000";
    private static final String FOUR_PLUS_PAIR_TILES = "0000000010000000001000000000000000000010000000000";
    private static final String THREE_OF_KIND_TILES = "0001001001000000000000000000100000000000000000000";
    private static final String LESS_THAN_TWELVE_TILES = "0010000000000000000001000100000000000000010000000";
    private static final String STRAIGHT_TILES = "0000000000010010000000000000000000000100001000000";
    private static final String LARGE_STRAIGHT_TILES = "0100000000000000010000000001010000000000000000000";
    private static final String TWO_PAIR_TILES = "1000000000000000000000000010000000100000000001000";
    private static final String EVENS_ONLY_TILES = "0000010100000000000000010000000000000000000000000";
    private static final String ODDS_ONLY_TILES = "0000100000000000000100000000000100010000000000100";

    private String world; 

    public Tile(int x, int y, String world) {
        super();
        this.x = x;
        this.y = y;
        this.clicked = false;
        this.tileImage = null;
        this.tileType = GameLogic.getTileCombination(x, y);
        this.scoreValue = GameLogic.getTilePoints(x, y);
        this.world = world;
        
        setColor(world);
        setImage();
        
    }

    // Sets default color state of the game
    public void setColor(String world) {
        if (WHITE_TILES.charAt(x * 7 + y) == '1') {
            this.setBackground(Color.WHITE);
        } else if (BLACK_TILES.charAt(x * 7 + y) == '1') {
            this.setBackground(Color.BLACK);
        } else {
            this.setBackground(ColorDiff(world));
        }
    }

    // Sets the tile image based on its type
    public void setImage() {
        String imagePath = "";
        if (ODDS_ONLY_TILES.charAt(x * 7 + y) == '1') {
            imagePath = "Grid_Icons/odds_only.png";
            this.tileType = "All odd";
        } else if (EVENS_ONLY_TILES.charAt(x * 7 + y) == '1') {
            imagePath = "Grid_Icons/evens_only.png";
            this.tileType = "All even";
        } else if (TWO_PAIR_TILES.charAt(x * 7 + y) == '1') {
            imagePath = "Grid_Icons/two_pair.png";
            this.tileType = "Two pairs";
        } else if (LARGE_STRAIGHT_TILES.charAt(x * 7 + y) == '1') {
            imagePath = "Grid_Icons/large_straight.png";
            this.tileType = "Large straight";
        } else if (STRAIGHT_TILES.charAt(x * 7 + y) == '1') {
            imagePath = "Grid_Icons/straight.png";
            this.tileType = "Little straight";
        } else if (LESS_THAN_TWELVE_TILES.charAt(x * 7 + y) == '1') {
            imagePath = "Grid_Icons/less_than_equal_to_twelve.png";
            this.tileType = "12 or fewer";
        } else if (GREATER_THAN_THIRTY_TILES.charAt(x * 7 + y) == '1') {
            imagePath = "Grid_Icons/greater_than_equal_to_thirty.png";
            this.tileType = "30 or more";
        } else if (THREE_OF_KIND_TILES.charAt(x * 7 + y) == '1') {
            imagePath = "Grid_Icons/three_of_a_kind.png";
            this.tileType = "Three of a kind";
        } else if (FOUR_PLUS_PAIR_TILES.charAt(x * 7 + y) == '1') {
            imagePath = "Grid_Icons/four_of_a_kind_and_pair.png";
            this.tileType = "Four of a kind and a pair";
        } else if (THREE_PAIRS_TILES.charAt(x * 7 + y) == '1') {
            imagePath = "Grid_Icons/three_pairs.png";
            this.tileType = "Three pairs";
        } else if (TWO_THREE_KIND_TILES.charAt(x * 7 + y) == '1') {
            imagePath = "Grid_Icons/two_three_of_a_kind.png";
            this.tileType = "Two times three of a kind";
        } else if (FOUR_OF_KIND_TILES.charAt(x * 7 + y) == '1') {
            imagePath = "Grid_Icons/four_of_a_kind.png";
            this.tileType = "Four of a kind";
        } else if (FULL_HOUSE_TILES.charAt(x * 7 + y) == '1') {
            imagePath = "Grid_Icons/full_house.png";
            this.tileType = "Full house";
        }

        if (!imagePath.isEmpty()) {
            tileImage = Toolkit.getDefaultToolkit().getImage(imagePath);
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(tileImage, 0);
            try {
                tracker.waitForAll(); // Wait for the image to load
            } catch (InterruptedException e) {
                tileImage = null; // Set to null if loading fails
            }

            if (tracker.isErrorAny()) {
                tileImage = null; // Set to null if loading fails
            }
        }

        // Set the icon only if the image was loaded successfully
        if (tileImage != null) {
            tileIcon = new ImageIcon(tileImage);
        } else {
            tileIcon = null; // No image for this tile
        }

        setIcon(tileIcon);
    }

    // Places a stone on the tile for the given player
    public void placeTile(int currentPlayer) {
        this.playerNumber = currentPlayer;
        this.occupied = true;

        int tileImageCenterX = (tileImage != null) ? tileImage.getWidth(null) / 2 : 0;
        int tileImageCenterY = (tileImage != null) ? tileImage.getHeight(null) / 2 : 0;
        int tileCenterX = (this.getWidth() / 2);
        int tileCenterY = (this.getHeight() / 2);
        BufferedImage combinedImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D temp = combinedImage.createGraphics();

        if (tileImage != null) {
            temp.drawImage(tileImage, tileCenterX - tileImageCenterX, tileCenterY - tileImageCenterY, null);
        
        }

        temp.setStroke(new BasicStroke(2));

        switch (currentPlayer) {
            case 1 -> { // Player 1 - Circle
                temp.setColor(stoneColor(world,"1"));
                temp.fillOval(tileCenterX - 23, tileCenterY - 23, 45, 45);
                temp.setColor(Color.BLACK);
                temp.drawOval(tileCenterX - 23, tileCenterY - 23, 45, 45);
            }
            case 2 -> { // Player 2 - Square
                temp.setColor(stoneColor(world,"2"));
                temp.fillRect(tileCenterX - 23,tileCenterY - 23, 45, 45);
                temp.setColor(Color.BLACK);
                temp.drawRect(tileCenterX - 23,tileCenterY - 23, 45, 45);
            }
            case 3 -> { // Player 3 - Triangle
                int[] triX = {tileCenterX - 1, tileCenterX + 23, tileCenterX - 25};
                int[] triY = {tileCenterY - 24, tileCenterY + 24, tileCenterY + 24};
                temp.setColor(stoneColor(world,"3"));
                temp.fillPolygon(triX, triY, 3);
                temp.setColor(Color.BLACK);
                temp.drawPolygon(triX, triY, 3);
            }
            case 4 -> { // Player 4 - Diamond
                int[] diaX = { tileCenterX- 1, tileCenterX + 17, tileCenterX - 1, tileCenterX - 19 };
                int[] diaY = {tileCenterY - 30, tileCenterY, tileCenterY +30, tileCenterY};
                temp.setColor(stoneColor(world,"4"));
                temp.fillPolygon(diaX, diaY, 4);
                temp.setColor(Color.BLACK);
                temp.drawPolygon(diaX, diaY, 4);
            }
        }

        temp.dispose();
        setIcon(new ImageIcon(combinedImage));
        this.resetBorder();

        this.revalidate();
        this.repaint();

        System.out.println("Tile at " + x + "," + y + " placed for Player " + playerNumber);
    }

    // Resets the tile to its initial state
    public void resetTile() {
        this.playerNumber = 0;
        this.occupied = false;
        this.resetBorder();
        this.setIcon(tileIcon);
        this.revalidate();
        this.repaint();
    }

    // Resets the tile's border to default
    public void resetBorder() {
        this.setBorder(new JButton().getBorder());
    }

    // Getters and setters
    public String getTileType() {
        return tileType;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public int getBoardX() {
        return x;
    }

    public int getBoardY() {
        return y;
    }

    public void setClicked() {
        this.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
        this.clicked = true;
    }

    public Color ColorDiff(String world)
    {
    	
        Color Indigo = new Color(0, 0, 255);
        Color darkPink = new Color(204, 0, 102);
        Color yellow = Color.YELLOW;
        Color Pink = Color.PINK;

    	if (world.equals("RED")){return Indigo;} 
		
		else if (world.equals("BLUE")) { return darkPink;} // DARK PINK
		
		else if (world.equals("GREEN")) {  return yellow;} // YELLOW

        return Pink ; // Light Pink 

       
    }

    public Color stoneColor(String world, String pNum)
    {
    	Color Red = Color.RED;
	    Color Green = Color.GREEN;
	    Color DarkBlue = new Color(0, 0, 128);
	    Color Yellow = Color.YELLOW;
	    Color SkyBlue = new Color(153, 204, 255);
	    Color Pink = Color.PINK;
	    Color Brown = new Color(139, 69, 19);
	    Color LemonGreen = new Color(110, 140, 5);
        Color gray = Color.LIGHT_GRAY;

    	if (world.equals("RED"))
        {
            if(pNum.equals("1")){return SkyBlue;}
            else if(pNum.equals("2")){return LemonGreen;}
            else if(pNum.equals("3")){return DarkBlue;}
            else if(pNum.equals("4")){return Yellow;}
        } 
		
		else if (world.equals("BLUE")) 
        { 
            if(pNum.equals("1")){return Red;}
            else if(pNum.equals("2")){return SkyBlue;}
            else if(pNum.equals("3")){return Brown;}
            else if(pNum.equals("4")){return Pink;}
        } 
		
		else if (world.equals("GREEN")) 
        {  
            if(pNum.equals("1")){return LemonGreen;}
            else if(pNum.equals("2")){return SkyBlue;}
            else if(pNum.equals("3")){return DarkBlue;}
            else if(pNum.equals("4")){return Yellow;}
        } 

        else if (world.equals("DEFAULT")) 
        {  
            if(pNum.equals("1")){return Red;}
            else if(pNum.equals("2")){return Green;}
            else if(pNum.equals("3")){return DarkBlue;}
            else if(pNum.equals("4")){return Yellow;}
        }

        return gray;      
    }
}
