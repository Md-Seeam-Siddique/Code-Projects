import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WinnerGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel winnerScreenMainPanel;
	private JLabel winnerLabel;
	private JLabel firstPointLabel, secondPointLabel, thirdPointLabel, fourthPointLabel;
	private GameManager gameManager;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WinnerGUI frame = new WinnerGUI();
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
	public WinnerGUI() {


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);
		
		// MAIN PANEL OF THE WINNER SCREEN GUI
		winnerScreenMainPanel = new JPanel();
		winnerScreenMainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(winnerScreenMainPanel);
		winnerScreenMainPanel.setLayout(new BorderLayout(0, 70));
		
		//Winner declaration label(North)
		winnerLabel = new JLabel("Player # WON !!! :) ");
		winnerLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 70));
		winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		winnerScreenMainPanel.add(winnerLabel, BorderLayout.NORTH);
		
		//Panel which contains the players and their respective points (Centre)
		JPanel pointsLabel = new JPanel();
		winnerScreenMainPanel.add(pointsLabel, BorderLayout.CENTER);
		pointsLabel.setLayout(new GridLayout(4, 0, 0, 20));
		
		//Label of the player with the most points
		firstPointLabel = new JLabel("1: Player # , # points");
		firstPointLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 40));
		firstPointLabel.setHorizontalAlignment(SwingConstants.CENTER);
		pointsLabel.add(firstPointLabel);
		
		//Label of the player with the second most points
		secondPointLabel = new JLabel("2: Player # , # points");
		secondPointLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 40));
		secondPointLabel.setHorizontalAlignment(SwingConstants.CENTER);
		pointsLabel.add(secondPointLabel);
		
		//Label of the player with the third most points
		thirdPointLabel = new JLabel("3: Player # , # points");
		thirdPointLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 40));
		thirdPointLabel.setHorizontalAlignment(SwingConstants.CENTER);
		pointsLabel.add(thirdPointLabel);
		
		//Label of the player with the least points
		fourthPointLabel = new JLabel("4: Player # , # points");
		fourthPointLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 40));
		fourthPointLabel.setHorizontalAlignment(SwingConstants.CENTER);
		pointsLabel.add(fourthPointLabel);
		
		//Panel which contains the go home button (South)
		JPanel buttonPanel = new JPanel();
		winnerScreenMainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		//When this button is pressed the user is taken to the Welcome Screen
		JButton goHomeButton = new JButton("GO TO HOME");
		goHomeButton.setBackground(new Color(0, 153, 255));
		goHomeButton.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 40));
		goHomeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				System.out.println("we reached here just fyi");
//				dispose();
//				new WelcomeScreen(gameManager.makeNewInstance(4, null)).setVisible(true);
			}
		});
		buttonPanel.add(goHomeButton);
	}
	
	// Constructor that accepts sorted player array
	public WinnerGUI(Player[] sortedPlayers, GameManager gameManager) {


		this.gameManager = gameManager;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);
		
		// MAIN PANEL OF THE WINNER SCREEN GUI
		winnerScreenMainPanel = new JPanel();
		winnerScreenMainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(winnerScreenMainPanel);
		winnerScreenMainPanel.setLayout(new BorderLayout(0, 70));
		
		// Determine the winner
		Player winner = sortedPlayers[0];
		
		//Winner declaration label(North)
		winnerLabel = new JLabel("Player " + winner.getPlayerNumber() + " WON !!! :) ");
		winnerLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 70));
		winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		winnerScreenMainPanel.add(winnerLabel, BorderLayout.NORTH);
		
		//Panel which contains the players and their respective points (Centre)
		JPanel pointsLabel = new JPanel();
		winnerScreenMainPanel.add(pointsLabel, BorderLayout.CENTER);
		pointsLabel.setLayout(new GridLayout(4, 0, 0, 20));
		
		// Add each player's score
		firstPointLabel = new JLabel("1: Player " + sortedPlayers[0].getPlayerNumber() + ", " + sortedPlayers[0].getScore() + " points");
		firstPointLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 40));
		firstPointLabel.setHorizontalAlignment(SwingConstants.CENTER);
		pointsLabel.add(firstPointLabel);
		
		// Check if player 2 exists
		if (sortedPlayers.length > 1 && sortedPlayers[1] != null) {
			secondPointLabel = new JLabel("2: Player " + sortedPlayers[1].getPlayerNumber() + ", " + sortedPlayers[1].getScore() + " points");
		} else {
			secondPointLabel = new JLabel("2: --");
		}
		secondPointLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 40));
		secondPointLabel.setHorizontalAlignment(SwingConstants.CENTER);
		pointsLabel.add(secondPointLabel);
		
		// Check if player 3 exists
		if (sortedPlayers.length > 2 && sortedPlayers[2] != null) {
			thirdPointLabel = new JLabel("3: Player " + sortedPlayers[2].getPlayerNumber() + ", " + sortedPlayers[2].getScore() + " points");
		} else {
			thirdPointLabel = new JLabel("3: --");
		}
		thirdPointLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 40));
		thirdPointLabel.setHorizontalAlignment(SwingConstants.CENTER);
		pointsLabel.add(thirdPointLabel);
		
		// Check if player 4 exists
		if (sortedPlayers.length > 3 && sortedPlayers[3] != null) {
			fourthPointLabel = new JLabel("4: Player " + sortedPlayers[3].getPlayerNumber() + ", " + sortedPlayers[3].getScore() + " points");
		} else {
			fourthPointLabel = new JLabel("4: --");
		}
		fourthPointLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 40));
		fourthPointLabel.setHorizontalAlignment(SwingConstants.CENTER);
		pointsLabel.add(fourthPointLabel);
		
		//Panel which contains the go home button (South)
		JPanel buttonPanel = new JPanel();
		winnerScreenMainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		//When this button is pressed the user is taken to the Welcome Screen
		JButton goHomeButton = new JButton("GO TO HOME");
		goHomeButton.setBackground(new Color(0, 153, 255));
		goHomeButton.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 40));
		goHomeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				new WelcomeScreen(gameManager.makeNewInstance(4, null)).setVisible(true);
			}
		});
		buttonPanel.add(goHomeButton);
	}
	
	// Add setter methods for labels
	public void setWinnerLabel(String text) {
		winnerLabel.setText(text);
	}
	
	public void setFirstPointLabel(String text) {
		firstPointLabel.setText(text);
	}
	
	public void setSecondPointLabel(String text) {
		secondPointLabel.setText(text);
	}
	
	public void setThirdPointLabel(String text) {
		thirdPointLabel.setText(text);
	}
	
	public void setFourthPointLabel(String text) {
		fourthPointLabel.setText(text);
	}

	
}