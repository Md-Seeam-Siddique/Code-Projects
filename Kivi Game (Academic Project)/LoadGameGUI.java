import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class LoadGameGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel loadingScreenMainPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoadGameGUI frame = new LoadGameGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoadGameGUI() 
	{

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);
		//MAIN PANEL OF THE GUI 
		loadingScreenMainPanel = new JPanel();
		loadingScreenMainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(loadingScreenMainPanel);
		loadingScreenMainPanel.setLayout(new BorderLayout(300, 5)); //BORDER LAYOUT

		//Saved File Label (North)
		JLabel savedFilesLabel = new JLabel("Saved Files");
		savedFilesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		savedFilesLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 72));
		loadingScreenMainPanel.add(savedFilesLabel, BorderLayout.NORTH);

		//Panel which contains the Saved Games List ComboBox (Centre)
		JPanel fileListPanel = new JPanel();
		loadingScreenMainPanel.add(fileListPanel);
		fileListPanel.setLayout(new GridLayout(5, 1, 0, 0)); //Layout is made in such a way to make the GUI look better

		//Empty Filler panel for visual puposes
		JPanel topFillerPanel = new JPanel();
		fileListPanel.add(topFillerPanel);

		//THE COMBO BOX CONTAINIG THE LIST OF SAVED GAMES
		JComboBox<ArrayList<String>> fileListComboBox = new JComboBox<>();
		fileListComboBox.setBackground(Color.LIGHT_GRAY);

		ArrayList<String> saveList = new ArrayList<String>();
		File dir = new File("saves");
		dir.mkdir();
		
		String[] numSaves = dir.list();
		saveList.add("");
		// Generating list of savefiles with empty string at index 0
		if (numSaves != null) {
			for (int i = 0; i < numSaves.length; i++) {
				saveList.add(numSaves[i]);
			}
		}
		
		
		fileListComboBox.setModel(new DefaultComboBoxModel(saveList.toArray())); //The First selection is empty as the user needs to choose one save file
		fileListComboBox.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 20));
		fileListPanel.add(fileListComboBox);

		//Panel that contains all the buttons
		JPanel buttonPanel = new JPanel();
		loadingScreenMainPanel.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new GridLayout(0, 3, 50, 0));

		//When pressed it goes the the Welcome Screen
		JButton homeButton = new JButton("GO TO HOME");
		homeButton.setBackground(new Color(0, 153, 255));
		homeButton.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 35));
		//Go to Home button functionality
		homeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GameManager gameManager = GameManager.getInstance(4, null);
				new WelcomeScreen(gameManager).setVisible(true);
				dispose();
			}
		});
		buttonPanel.add(homeButton);

		//When a saved game from the list is chosen and this button is pressed it take you to the board screen and you can continue that game
		JButton continueButton = new JButton("CONTINUE GAME");
		continueButton.setBackground(new Color(0, 153, 255));
		continueButton.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 35));
		buttonPanel.add(continueButton);

		//When a saved game from the list is chosen and this button is pressed it will delete that saved game file
		JButton deleteButton = new JButton("DELETE FILE");
		deleteButton.setBackground(new Color(0, 153, 255));
		deleteButton.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 35));
		buttonPanel.add(deleteButton);

		//Empty Filler panel for visual puposes (West) 
		JPanel leftFillerPanel = new JPanel();
		loadingScreenMainPanel.add(leftFillerPanel, BorderLayout.WEST);

		//Empty Filler panel for visual puposes (East)
		JPanel rightFillerPanel = new JPanel();
		loadingScreenMainPanel.add(rightFillerPanel, BorderLayout.EAST);

		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileNameString = (String) "saves\\" + fileListComboBox.getSelectedItem();
				File file = new File(fileNameString);
				//Deleting file and removing it from the combobox
				if (file.exists()) {
					file.delete();
					saveList.remove(fileListComboBox.getSelectedIndex());
					fileListComboBox.removeItem(fileListComboBox.getSelectedItem());
				}
			}
		});
        continueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Stops load attempt if no save file is selected
                Object selected = fileListComboBox.getSelectedItem();
                if (!(selected instanceof String) || ((String) selected).isEmpty()) {
                    return;
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
				* PLAYER TURNS -> space seperated ints for each player in the game state # # # #
                * PLAYER OBJECT DATA -> space seperated ints for each Player class variable # # # # # #
                * -PLAYER DATA IS THE EOF 1 LINE FOR EACH PLAYER
                */
                String fileNameString = "saves\\" + selected;
                File file = new File(fileNameString);//File 
                try (Scanner input = new Scanner(file)) {
                    //Filepath 
                    String gameBoardString = input.nextLine();//gameBoard 1d string
					String[] currentDice = input.nextLine().trim().split(" ");//Dice Line
                    int currentPlayer = Integer.parseInt(input.nextLine());//currentPlayer int(whose turn is it)
                    String startOfTurnString = input.nextLine(); //Grid.startOfFirstTurn
                    int totalPlayers = Integer.parseInt(input.nextLine());//gameManager.totalPlayers int
					String worldString = input.nextLine();//world selection string
					String firstTurnString = input.nextLine();//GameManager.firstTurn
					int rerollsLeft = Integer.parseInt(input.nextLine());//GameManager.rerollsLeft
					int roundsPlayed = Integer.parseInt(input.nextLine());//GameManager.roundsPlayed
					boolean confirmClicked = (Integer.parseInt(input.nextLine()) == 1) ? true : false;//Grid.confirmClicked converting from string->bool in one line
					String[] playerTurnsString = input.nextLine().trim().split(" ");//GameManager.playerTurns array
					//Turning String[] into List<integer> to be passed into new gamemanager later
					List<Integer> playerTurns = new ArrayList<>();
					//Turning playerTurnsString into playerTurns int List
					for (int i = 0; i < playerTurnsString.length; i++) {
						if (!playerTurnsString[i].isEmpty()) {
							playerTurns.add(Integer.parseInt(playerTurnsString[i]));
						}
					}

					//GameManager.playerList and aiList
					//Im not sure how these are used, but it seems to work when I populate both lists with the ai players
					List<Player> playerList = new ArrayList<>();
					List<Player> aiList = new ArrayList<>();
					GameManager gameManager = GameManager.makeNewInstance(totalPlayers, worldString);//New gameManager to load into
					while (input.hasNextLine()) {
						//PlayerNumber, isAI, Score, rerolls, difficulty(ai)
						String[] currentPlayerString = input.nextLine().trim().split(" ");//currentPlayerString(in the loop)
						
						int currPlayerNumber = Integer.parseInt(currentPlayerString[0]);//currentPlayerNumber
						boolean ai = (Integer.parseInt(currentPlayerString[1]) == 1) ? true : false;//isAI bool
						//Generating AIPlayer if the currentPlayer in the loop is an ai player
						if (ai) {
							String diff = currentPlayerString[4];//AI difficulty
							AIPlayer currPlayer = new AIPlayer(currPlayerNumber, diff, gameManager);//Making new AIPlayer

							//Setting all class variables
							currPlayer.setIsAI(ai);//isAI bool
							currPlayer.updateScore(Integer.parseInt(currentPlayerString[2]));// Setting Player.score
							currPlayer.setRerollsLeft(Integer.parseInt(currentPlayerString[3]));//Setting Player.rerollsLeft

							// Im not sure how these are used, but it seems to work when I populate both
							// lists with the ai players
							playerList.add(currPlayer);
							aiList.add(currPlayer);
						}
						//If currentPlayer in loop is a human player make new Player and add it to PlayerList
						else{
							Player currPlayer = new Player(currPlayerNumber);//new Player object
							// Setting all class variables
							currPlayer.setIsAI(ai);//isAI bool
							currPlayer.updateScore(Integer.parseInt(currentPlayerString[2]));//Setting Player.score
							currPlayer.setRerollsLeft(Integer.parseInt(currentPlayerString[3]));//Setting Player.rerollsLeft
							playerList.add(currPlayer);//Add player to playerList
						}
					}
					//PLACE TILES ON BOARD
					Grid grid = new Grid(gameManager, worldString);
					grid.setBoardState(gameBoardString);
					//Get dice array
					int[] d = new int[6];
					for (int i = 0; i < 6 && i < currentDice.length; i++) {
						if (!currentDice[i].isEmpty()) {
							d[i] = Integer.parseInt(currentDice[i]);
						}
					}
					boolean startOfTurn = (Integer.parseInt(startOfTurnString) == 1) ? true : false;
					//Set gameManager values
					boolean firstTurnBool = (Integer.parseInt(firstTurnString) == 1) ? true : false;
					//Setting gameManager state
					gameManager.setGrid(grid);//GameManger.grid Grid()
					gameManager.setDice(d);//GameManager.dice int[]
					gameManager.setPlayerList(playerList);//GameManger.playerList List<Player>
					gameManager.setAiPlayerList(aiList);// GameManger.aiList List<Player>
					gameManager.setFirstTurn(firstTurnBool);//GameManger.firstTurn bool
					gameManager.setRerollsLeft(rerollsLeft);//GameManager.rerollsLeft int
					gameManager.setRoundsPlayed(roundsPlayed);//GameManager.roundsPlayed int
					gameManager.setCurrentPlayer(currentPlayer);//GameManager.currentPlayer int
					gameManager.setPlayerTurns(playerTurns);//GameManager.playerTurns List<Integer>

					//Setting Grid state
					grid.updateCurrentPlayer();//Updating current player label ongrid
                    grid.updateDice();//Updating dice label
                    grid.updateRerollsLabel();//Updating rerolls label
                    grid.setConfirmClicked(confirmClicked);//Setting Grid.confirmClicked so player cant place another tile after load if already placed before save in same turn
                    grid.setStartOfTurn(startOfTurn);//Setting Grid.startOfTurn so the first reroll after a load takes a reroll away if it ISNT the first roll of a turn
                    grid.setVisible(true);//Setting grid visible
					
				} catch (Exception ex) {
					System.out.println("An error occurred while loading the save: " + ex.getMessage());
				}
				dispose();
			}
		});
	}
}
