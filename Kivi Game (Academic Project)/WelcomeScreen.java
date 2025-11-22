//package GRPproject;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;

public class WelcomeScreen extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel WelcomeScreenMainPanel;
    private GameManager gameManager;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GameManager gameManager = GameManager.getInstance(4, null);
                    WelcomeScreen frame = new WelcomeScreen(gameManager);
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
    public WelcomeScreen(GameManager gameManager) {
        this.gameManager = gameManager;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1280, 720);
        WelcomeScreenMainPanel = new JPanel();
        WelcomeScreenMainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(WelcomeScreenMainPanel);
        WelcomeScreenMainPanel.setLayout(new BorderLayout(0, 0));
        
        JLabel WelcomeToKIVILabel = new JLabel("Welcome to KIVI");
        WelcomeToKIVILabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 99));
        WelcomeToKIVILabel.setHorizontalAlignment(SwingConstants.CENTER);
        WelcomeScreenMainPanel.add(WelcomeToKIVILabel, BorderLayout.NORTH);
        
        JPanel NewLoadBtnPanel = new JPanel();
        WelcomeScreenMainPanel.add(NewLoadBtnPanel, BorderLayout.CENTER);
        GridLayout gl_panel = new GridLayout(2, 1);
        gl_panel.setVgap(20);
        NewLoadBtnPanel.setLayout(gl_panel);
        
        JButton NewGameBtn = new JButton("NEW GAME");
        NewGameBtn.setForeground(new Color(0, 0, 0));
        NewGameBtn.setBackground(new Color(0, 153, 255));
        NewGameBtn.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 35));
        
        NewGameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                
                // Open the GameSettingScreen
                //GameManager gameManager = GameManager.getInstance(4);

                new GameSettingScreen(gameManager).setVisible(true);
            }
        });

        
        
        NewLoadBtnPanel.add(NewGameBtn);
        
        JButton LoadGameBtn = new JButton("LOAD GAME");
        LoadGameBtn.setBackground(new Color(0, 153, 255));
        LoadGameBtn.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 35));
        NewLoadBtnPanel.add(LoadGameBtn);


        LoadGameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                new LoadGameGUI().setVisible(true); 
                dispose(); 
            }
        });
        
    }
}