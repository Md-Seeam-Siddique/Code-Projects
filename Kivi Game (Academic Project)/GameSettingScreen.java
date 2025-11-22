//package GRPproject;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.Timer;

public class GameSettingScreen extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel GameSettingsMainPanel;
    private GameManager gameManager;

    private JComboBox<String> P2PlayerTypeComboBox, P3PlayerTypeComboBox, P4PlayerTypeComboBox;
    private JComboBox<String> P2DifficultyComboBox, P3DifficultyComboBox, P4DifficultyComboBox;
    private JLabel ChoosePlayerWarningLabel, ChooseAiDifficultyWarningLabel, ChooseWorldWarningLabel;
    private JComboBox<String> ChooseWorldComboBox;
    private JButton StartGameButton;
    private JLabel PlayerInfoLabel, AiInfoLabel, WorldInfoLabel;
    
    
    private String selectedWorld;
    

    

    public GameSettingScreen(GameManager gameManager) {
        this.gameManager = gameManager;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1270, 720);
        GameSettingsMainPanel = new JPanel();
        GameSettingsMainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(GameSettingsMainPanel);
        GameSettingsMainPanel.setLayout(new BorderLayout(0, 0));

        JPanel GameSettingsPanel = new JPanel();
        GameSettingsMainPanel.add(GameSettingsPanel, BorderLayout.CENTER);
        GameSettingsPanel.setLayout(new GridLayout(6, 4, 5, 5));

        JPanel PlayerNumPanel = new JPanel();
        GameSettingsPanel.add(PlayerNumPanel);
        PlayerNumPanel.setLayout(new GridLayout(0, 4, 0, 0));

        JLabel Player1Label = new JLabel("PLAYER 1");
        PlayerNumPanel.add(Player1Label);
        Player1Label.setHorizontalAlignment(SwingConstants.CENTER);
        Player1Label.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));

        JLabel Player2Label = new JLabel("PLAYER 2");
        PlayerNumPanel.add(Player2Label);
        Player2Label.setHorizontalAlignment(SwingConstants.CENTER);
        Player2Label.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));

        JLabel Player3Label = new JLabel("PLAYER 3");
        PlayerNumPanel.add(Player3Label);
        Player3Label.setHorizontalAlignment(SwingConstants.CENTER);
        Player3Label.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));

        JLabel Player4Label = new JLabel("PLAYER 4");
        PlayerNumPanel.add(Player4Label);
        Player4Label.setHorizontalAlignment(SwingConstants.CENTER);
        Player4Label.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));

        JPanel PlayerChoosePanel = new JPanel();
        GameSettingsPanel.add(PlayerChoosePanel);
        PlayerChoosePanel.setLayout(new GridLayout(0, 4, 0, 0));

        JLabel P1HumanLabel = new JLabel("HUMAN");
        PlayerChoosePanel.add(P1HumanLabel);
        P1HumanLabel.setHorizontalAlignment(SwingConstants.CENTER);
        P1HumanLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));

        
        P2PlayerTypeComboBox = new JComboBox<>();
        PlayerChoosePanel.add(P2PlayerTypeComboBox);
        P2PlayerTypeComboBox.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 15));
        P2PlayerTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"NONE", "HUMAN", "AI"}));
        P2PlayerTypeComboBox.setMaximumRowCount(3);
        P2PlayerTypeComboBox.setBackground(new Color(153, 153, 153));

        P2PlayerTypeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) P2PlayerTypeComboBox.getSelectedItem();
                P2DifficultyComboBox.setEnabled("AI".equals(selectedType));
                validateSettings();
                updateGameInfoLabels();
            }
        });

        P3PlayerTypeComboBox = new JComboBox<>();
        PlayerChoosePanel.add(P3PlayerTypeComboBox);
        P3PlayerTypeComboBox.setBackground(new Color(153, 153, 153));
        P3PlayerTypeComboBox.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 15));
        P3PlayerTypeComboBox.setMaximumRowCount(3);
        P3PlayerTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"NONE", "HUMAN", "AI"}));

        P3PlayerTypeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) P3PlayerTypeComboBox.getSelectedItem();
                P3DifficultyComboBox.setEnabled("AI".equals(selectedType));
                validateSettings();
                updateGameInfoLabels();
            }
        });

        P4PlayerTypeComboBox = new JComboBox<>();
        PlayerChoosePanel.add(P4PlayerTypeComboBox);
        P4PlayerTypeComboBox.setBackground(new Color(153, 153, 153));
        P4PlayerTypeComboBox.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 15));
        P4PlayerTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"NONE", "HUMAN", "AI"}));

        P4PlayerTypeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) P4PlayerTypeComboBox.getSelectedItem();
                P4DifficultyComboBox.setEnabled("AI".equals(selectedType));
                validateSettings();
                updateGameInfoLabels();
            }
        });
        
        

        ChoosePlayerWarningLabel = new JLabel("CHOOSE AT LEAST ANOTHER TYPE OF PLAYER");
        ChoosePlayerWarningLabel.setVerticalAlignment(SwingConstants.TOP);
        ChoosePlayerWarningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ChoosePlayerWarningLabel.setForeground(new Color(255, 0, 0));
        ChoosePlayerWarningLabel.setBackground(new Color(245, 255, 250));
        ChoosePlayerWarningLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 15));
        GameSettingsPanel.add(ChoosePlayerWarningLabel);

        JPanel AiDifficultyPanel = new JPanel();
        GameSettingsPanel.add(AiDifficultyPanel);
        AiDifficultyPanel.setLayout(new GridLayout(0, 4, 0, 0));

        JLabel AiDifficultyLabel = new JLabel("AI DIFFICULTY:");
        AiDifficultyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        AiDifficultyLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        AiDifficultyPanel.add(AiDifficultyLabel);

        P2DifficultyComboBox = new JComboBox<>();
        P2DifficultyComboBox.setBackground(new Color(153, 153, 153));
        P2DifficultyComboBox.setEnabled(false);
        P2DifficultyComboBox.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 15));
        P2DifficultyComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"EASY", "HARD"}));
        P2DifficultyComboBox.setMaximumRowCount(2);
        AiDifficultyPanel.add(P2DifficultyComboBox);

        P3DifficultyComboBox = new JComboBox<>();
        P3DifficultyComboBox.setBackground(new Color(153, 153, 153));
        P3DifficultyComboBox.setEnabled(false);
        P3DifficultyComboBox.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 15));
        P3DifficultyComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"EASY", "HARD"}));
        P3DifficultyComboBox.setMaximumRowCount(2);
        AiDifficultyPanel.add(P3DifficultyComboBox);

        P4DifficultyComboBox = new JComboBox<>();
        P4DifficultyComboBox.setBackground(new Color(153, 153, 153));
        P4DifficultyComboBox.setEnabled(false);
        P4DifficultyComboBox.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 15));
        P4DifficultyComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"EASY", "HARD"}));
        P4DifficultyComboBox.setMaximumRowCount(2);
        AiDifficultyPanel.add(P4DifficultyComboBox);

        ChooseAiDifficultyWarningLabel = new JLabel("CHOOSE AI DIFFICULTY FOR ALL THE AI PLAYERS");
        ChooseAiDifficultyWarningLabel.setVerticalAlignment(SwingConstants.TOP);
        ChooseAiDifficultyWarningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ChooseAiDifficultyWarningLabel.setForeground(new Color(255, 0, 0));
        ChooseAiDifficultyWarningLabel.setBackground(new Color(255, 255, 255));
        ChooseAiDifficultyWarningLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 15));
        GameSettingsPanel.add(ChooseAiDifficultyWarningLabel);

        JPanel ChooseWorldPanel = new JPanel();
        GameSettingsPanel.add(ChooseWorldPanel);
        ChooseWorldPanel.setLayout(new GridLayout(0, 3, 0, 0));

        JLabel ChooseWorldLabel = new JLabel("CHOOSE YOUR WORLD:");
        ChooseWorldLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ChooseWorldLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        ChooseWorldPanel.add(ChooseWorldLabel);

        ChooseWorldComboBox = new JComboBox<>();
        ChooseWorldComboBox.setBackground(new Color(153, 153, 153));
        ChooseWorldComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"DEFAULT", "RED", "BLUE", "GREEN"}));
        ChooseWorldComboBox.setMaximumRowCount(3);
        ChooseWorldComboBox.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 15));
        ChooseWorldPanel.add(ChooseWorldComboBox);

        ChooseWorldWarningLabel = new JLabel("CHOOSE A WORLD");
        ChooseWorldWarningLabel.setForeground(new Color(255, 0, 0));
        ChooseWorldWarningLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 15));
        ChooseWorldPanel.add(ChooseWorldWarningLabel);

        JLabel ChooseGameSettingsLabel = new JLabel("CHOOSE YOUR NEW GAME SETTINGS");
        ChooseGameSettingsLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 50));
        ChooseGameSettingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        GameSettingsMainPanel.add(ChooseGameSettingsLabel, BorderLayout.NORTH);

        JPanel GameInfoStartGamePanel = new JPanel();
        GameSettingsMainPanel.add(GameInfoStartGamePanel, BorderLayout.SOUTH);
        GameInfoStartGamePanel.setLayout(new GridLayout(0, 2, 0, 0));

        JPanel GameInfoPanel = new JPanel();
        GameInfoStartGamePanel.add(GameInfoPanel);
        GameInfoPanel.setLayout(new GridLayout(3, 4, 0, 0));

        PlayerInfoLabel = new JLabel("Players:");
        PlayerInfoLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        GameInfoPanel.add(PlayerInfoLabel);

        AiInfoLabel = new JLabel("AI Players:");
        AiInfoLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        GameInfoPanel.add(AiInfoLabel);

        WorldInfoLabel = new JLabel("World:");
        WorldInfoLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        GameInfoPanel.add(WorldInfoLabel);

        StartGameButton = new JButton("START GAME");
        StartGameButton.setBackground(new Color(51, 153, 255));
        StartGameButton.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 25));
        StartGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (validateSettings()) {
                    try {
                        String selectedWorld = (String) ChooseWorldComboBox.getSelectedItem();
                        int totalPlayers = getSelectedTotalPlayers();
                        GameManager gameManager = GameManager.getInstance(totalPlayers, selectedWorld);

                        // Initialize players
                        initializePlayers();
                        
                        dispose();
                        
                        // Initial dice values
                        int[] initialDice = new int[6];
                        // nick - changed this to 0's instead of random to get empty start of turn dice to roll
                        for (int i = 0; i < 6; i++) {
                            initialDice[i] = 0; // Generate random dice values
                        }
                        gameManager.setDice(initialDice);
                        
                        // Create and show the grid directly
                        Grid grid = new Grid(gameManager, selectedWorld);
                        gameManager.setGrid(grid);
                        gameManager.setFirstTurn(false); // Not the first turn anymore since we're generating dice
                        
                        // Show the grid
                        grid.setVisible(true);
                        grid.updateDice(); // Update the UI to show the dice values
                        grid.updateRerollsLabel();


                        if (gameManager.isCurrentPlayerAI()) {
                            Player ai = gameManager.getCurrentPlayerObject();
                            ai.takeTurn();
                        }
                        
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error starting game: " + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        GameInfoStartGamePanel.add(StartGameButton);
        
        // Initialize warning labels to be hidden
        ChoosePlayerWarningLabel.setVisible(false);
        ChooseAiDifficultyWarningLabel.setVisible(false);
        ChooseWorldWarningLabel.setVisible(false);
        
        // Initialize game info labels
        updateGameInfoLabels();
    }
    
    private void updateGameInfoLabels() {
        int humanPlayers = 1; // Player 1 is always human
        int aiPlayers = 0;
        
        // Count players from combo boxes
        if (P2PlayerTypeComboBox.getSelectedItem().equals("HUMAN")) humanPlayers++;
        if (P2PlayerTypeComboBox.getSelectedItem().equals("AI")) aiPlayers++;
        if (P3PlayerTypeComboBox.getSelectedItem().equals("HUMAN")) humanPlayers++;
        if (P3PlayerTypeComboBox.getSelectedItem().equals("AI")) aiPlayers++;
        if (P4PlayerTypeComboBox.getSelectedItem().equals("HUMAN")) humanPlayers++;
        if (P4PlayerTypeComboBox.getSelectedItem().equals("AI")) aiPlayers++;
        
        PlayerInfoLabel.setText("Players: " + (humanPlayers + aiPlayers));
        AiInfoLabel.setText("AI Players: " + aiPlayers);
        WorldInfoLabel.setText("World: " + ChooseWorldComboBox.getSelectedItem());
    }
    
    private boolean validateSettings() {
        boolean isValid = true;
        
        boolean p2Selected = !P2PlayerTypeComboBox.getSelectedItem().equals("NONE");
        boolean p3Selected = !P3PlayerTypeComboBox.getSelectedItem().equals("NONE");
        boolean p4Selected = !P4PlayerTypeComboBox.getSelectedItem().equals("NONE");
        ChoosePlayerWarningLabel.setText("CHOOSE AT LEAST ANOTHER TYPE OF PLAYER");

        // Check if at least one other player is selected
        boolean hasAnotherPlayer = p2Selected || p3Selected || p4Selected;

        // Enforce contiguous player selection (no gaps)
        if ((!p2Selected && (p3Selected || p4Selected)) || (!p3Selected && p4Selected)) {
            ChoosePlayerWarningLabel.setText("SELECT PLAYERS IN ORDER (NO GAPS)");
            ChoosePlayerWarningLabel.setVisible(true);
            isValid = false;
        } else if (!hasAnotherPlayer) {
        
            ChoosePlayerWarningLabel.setVisible(true);
            isValid = false;
        } else {
            ChoosePlayerWarningLabel.setVisible(false);
        }
        
        // Check if AI difficulty is selected for all AI players
        boolean hasAIDifficultySelected = true;
        
        if (P2PlayerTypeComboBox.getSelectedItem().equals("AI") && 
            P2DifficultyComboBox.getSelectedItem() == null) {
            hasAIDifficultySelected = false;
        }
        
        if (P3PlayerTypeComboBox.getSelectedItem().equals("AI") && 
            P3DifficultyComboBox.getSelectedItem() == null) {
            hasAIDifficultySelected = false;
        }
        
        if (P4PlayerTypeComboBox.getSelectedItem().equals("AI") && 
            P4DifficultyComboBox.getSelectedItem() == null) {
            hasAIDifficultySelected = false;
        }
        
        if (!hasAIDifficultySelected) {
            ChooseAiDifficultyWarningLabel.setVisible(true);
            isValid = false;
        } else {
            ChooseAiDifficultyWarningLabel.setVisible(false);
        }
        
        // Check if a world is selected
        if (ChooseWorldComboBox.getSelectedItem() == null) {
            ChooseWorldWarningLabel.setVisible(true);
            isValid = false;
        } else {
            ChooseWorldWarningLabel.setVisible(false);
        }
        
        return isValid;
    }
    
    private int getSelectedTotalPlayers() {
        int count = 1; // Player 1 is always included
        
        if (!P2PlayerTypeComboBox.getSelectedItem().equals("NONE")) count++;
        if (!P3PlayerTypeComboBox.getSelectedItem().equals("NONE")) count++;
        if (!P4PlayerTypeComboBox.getSelectedItem().equals("NONE")) count++;
        
        return count;
    }
    
    private void initializePlayers() {
        gameManager.clearPlayers();
        
        // Player 1 is always a human player
        Player player1 = new Player(1);
        gameManager.addPlayer(player1);
        
     // Handle Player 2
     String p2Type = (String) P2PlayerTypeComboBox.getSelectedItem();
     if (!"NONE".equals(p2Type)) {
         if ("HUMAN".equals(p2Type)) {
             gameManager.addPlayer(new Player(2));
         } else if ("AI".equals(p2Type)) {
             String difficulty = (String) P2DifficultyComboBox.getSelectedItem();
             AIPlayer aiPlayer = new AIPlayer(2, difficulty, gameManager);
             gameManager.addPlayer(aiPlayer);
             gameManager.addAIPlayer(aiPlayer);
         }
     }
 
        
     // Handle Player 3
     String p3Type = (String) P3PlayerTypeComboBox.getSelectedItem();
     if (!"NONE".equals(p3Type)) {
         if ("HUMAN".equals(p3Type)) {
             gameManager.addPlayer(new Player(3));
         } else if ("AI".equals(p3Type)) {
             String difficulty = (String) P3DifficultyComboBox.getSelectedItem();
             AIPlayer aiPlayer = new AIPlayer(3, difficulty, gameManager);
             gameManager.addPlayer(aiPlayer);
             gameManager.addAIPlayer(aiPlayer);
         }
     }
 
        // Handle Player 4
        String p4Type = (String) P4PlayerTypeComboBox.getSelectedItem();
        if (!"NONE".equals(p4Type)) {
            if ("HUMAN".equals(p4Type)) {
                gameManager.addPlayer(new Player(4));
            } else if ("AI".equals(p4Type)) {
                String difficulty = (String) P4DifficultyComboBox.getSelectedItem();
                AIPlayer aiPlayer = new AIPlayer(4, difficulty, gameManager);
                gameManager.addPlayer(aiPlayer);
                gameManager.addAIPlayer(aiPlayer);
            }
        }
        }
    }
