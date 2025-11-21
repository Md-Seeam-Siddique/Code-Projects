/*
 * @author Md Seeam Siddique
 * @version 1.0
 * @date 05/09/2025
 */
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class SnakeGame extends JFrame {

    private static final String CARD_HOME = "card_home";
    private static final String CARD_GAME = "card_game";

    private ColorScheme currentScheme = ColorScheme.NEON_NIBBLES;

    // Encapsulates the shared color palettes for the home screen and live gameplay.
    private enum ColorScheme {
        NEON_NIBBLES(
                "Neon Nibbles",
                new Color(38, 61, 89),
                new Color(18, 29, 45),
                new Color(244, 248, 255),
                new Color(186, 198, 214),
                new Color(244, 248, 255),
                new Color(63, 183, 132),
                new Color(48, 165, 118),
                new Color(88, 133, 237),
                new Color(73, 118, 222),
                new Color(242, 173, 78),
                new Color(225, 156, 62),
                new Color(199, 93, 209),
                new Color(181, 75, 191),
                new Color(232, 102, 130),
                new Color(214, 84, 112),
                new Color(16, 24, 34),
                new Color(42, 56, 71),
                new Color(108, 224, 171),
                new Color(255, 219, 124),
                new Color(255, 111, 135),
                new Color(232, 242, 255),
                new Color(12, 18, 26, 200),
                new Color(255, 255, 255, 60)
        ),
        BUBBLEGUM_BREEZE(
                "Bubblegum Breeze",
                new Color(255, 138, 196),
                new Color(170, 66, 197),
                new Color(255, 255, 255),
                new Color(255, 230, 245),
                new Color(255, 255, 255),
                new Color(255, 203, 107),
                new Color(255, 187, 82),
                new Color(144, 153, 255),
                new Color(120, 130, 245),
                new Color(255, 112, 184),
                new Color(240, 95, 165),
                new Color(120, 225, 255),
                new Color(95, 210, 240),
                new Color(255, 105, 150),
                new Color(235, 85, 125),
                new Color(48, 16, 69),
                new Color(80, 40, 110),
                new Color(255, 150, 220),
                new Color(255, 240, 190),
                new Color(120, 220, 255),
                new Color(255, 240, 255),
                new Color(60, 20, 90, 200),
                new Color(255, 255, 255, 80)
        ),
        JUNGLE_JIVE(
                "Jungle Jive",
                new Color(32, 94, 60),
                new Color(12, 46, 32),
                new Color(236, 255, 238),
                new Color(196, 230, 206),
                new Color(236, 255, 238),
                new Color(115, 201, 97),
                new Color(93, 179, 78),
                new Color(83, 181, 201),
                new Color(64, 160, 180),
                new Color(240, 211, 113),
                new Color(225, 195, 90),
                new Color(171, 118, 221),
                new Color(156, 104, 204),
                new Color(241, 119, 128),
                new Color(222, 98, 108),
                new Color(18, 52, 32),
                new Color(48, 96, 66),
                new Color(120, 214, 88),
                new Color(255, 236, 120),
                new Color(255, 103, 92),
                new Color(225, 245, 226),
                new Color(12, 40, 24, 200),
                new Color(255, 255, 255, 70)
        );

        final String displayName;
        final Color homeGradientTop;
        final Color homeGradientBottom;
        final Color titleColor;
        final Color subtitleColor;
        final Color buttonTextColor;
        final Color startBase;
        final Color startHover;
        final Color scoresBase;
        final Color scoresHover;
        final Color instructionsBase;
        final Color instructionsHover;
        final Color paletteBase;
        final Color paletteHover;
        final Color exitBase;
        final Color exitHover;
        final Color boardBackground;
        final Color boardGrid;
        final Color boardSnakeBody;
        final Color boardSnakeHead;
        final Color boardApple;
        final Color boardScoreText;
        final Color overlayFill;
        final Color overlayStroke;

        ColorScheme(
                String displayName,
                Color homeGradientTop,
                Color homeGradientBottom,
                Color titleColor,
                Color subtitleColor,
                Color buttonTextColor,
                Color startBase,
                Color startHover,
                Color scoresBase,
                Color scoresHover,
                Color instructionsBase,
                Color instructionsHover,
                Color paletteBase,
                Color paletteHover,
                Color exitBase,
                Color exitHover,
                Color boardBackground,
                Color boardGrid,
                Color boardSnakeBody,
                Color boardSnakeHead,
                Color boardApple,
                Color boardScoreText,
                Color overlayFill,
                Color overlayStroke) {
            this.displayName = displayName;
            this.homeGradientTop = homeGradientTop;
            this.homeGradientBottom = homeGradientBottom;
            this.titleColor = titleColor;
            this.subtitleColor = subtitleColor;
            this.buttonTextColor = buttonTextColor;
            this.startBase = startBase;
            this.startHover = startHover;
            this.scoresBase = scoresBase;
            this.scoresHover = scoresHover;
            this.instructionsBase = instructionsBase;
            this.instructionsHover = instructionsHover;
            this.paletteBase = paletteBase;
            this.paletteHover = paletteHover;
            this.exitBase = exitBase;
            this.exitHover = exitHover;
            this.boardBackground = boardBackground;
            this.boardGrid = boardGrid;
            this.boardSnakeBody = boardSnakeBody;
            this.boardSnakeHead = boardSnakeHead;
            this.boardApple = boardApple;
            this.boardScoreText = boardScoreText;
            this.overlayFill = overlayFill;
            this.overlayStroke = overlayStroke;
        }
    }

    private final HighScoreManager highScoreManager = new HighScoreManager();
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final GamePanel gamePanel;
    private final HomePanel homePanel;

    public SnakeGame() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        gamePanel = new GamePanel();
        homePanel = new HomePanel();
        homePanel.setPreferredSize(gamePanel.getPreferredSize());

        cardPanel.setOpaque(true);
        cardPanel.add(homePanel, CARD_HOME);
        cardPanel.add(gamePanel, CARD_GAME);

        add(cardPanel);
        applyColorSchemeToComponents();
        pack();
        setLocationRelativeTo(null);
        showHomeScreen();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeGame::new);
    }

    private void showHomeScreen() {
        cardLayout.show(cardPanel, CARD_HOME);
        SwingUtilities.invokeLater(homePanel::requestInitialFocus);
    }

    private void startGameFromHome() {
        cardLayout.show(cardPanel, CARD_GAME);
        gamePanel.startNewGame();
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }

    private void returnToHomeScreen() {
        gamePanel.stopGame();
        cardLayout.show(cardPanel, CARD_HOME);
        SwingUtilities.invokeLater(homePanel::requestInitialFocus);
    }

    private void applyColorSchemeToComponents() {
        if (cardPanel != null) {
            cardPanel.setBackground(currentScheme.homeGradientBottom);
            cardPanel.repaint();
        }
        if (homePanel != null) {
            homePanel.applyColorScheme(currentScheme);
        }
        if (gamePanel != null) {
            gamePanel.applyColorScheme(currentScheme);
        }
    }

    private void changeColorScheme(ColorScheme newScheme) {
        if (newScheme == null || newScheme == currentScheme) {
            return;
        }
        currentScheme = newScheme;
        applyColorSchemeToComponents();
    }

    private void showColorCarnivalDialog() {
        ColorScheme[] schemes = ColorScheme.values();
        String[] options = new String[schemes.length];
        for (int i = 0; i < schemes.length; i++) {
            options[i] = schemes[i].displayName;
        }
        int choice = JOptionPane.showOptionDialog(
                this,
                "Pick a fresh color look for your snake world!",
                "Color Carnival",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[currentScheme.ordinal()]);
        if (choice >= 0 && choice < schemes.length) {
            changeColorScheme(schemes[choice]);
        }
    }

    private void showHighScoresDialog() {
        while (true) {
            List<Integer> scores = highScoreManager.getHighScores();
            String message;
            if (scores.isEmpty()) {
                message = "No high scores yet. Finish a game to add one.";
            } else {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < scores.size(); i++) {
                    builder.append(i + 1)
                            .append(". ")
                            .append(scores.get(i))
                            .append(" points");
                    if (i < scores.size() - 1) {
                        builder.append(System.lineSeparator());
                    }
                }
                message = builder.toString();
            }

            Object[] options = scores.isEmpty()
                    ? new Object[]{"Close"}
                    : new Object[]{"Close", "Erase All"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    message,
                    "High Scores",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (options.length > 1 && choice == 1) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Erase all saved high scores?",
                        "Confirm Erase",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    highScoreManager.clearScores();
                }
                continue;
            }
            break;
        }
    }

    private void showInstructionsDialog() {
        String instructions = "Goal:\n"
                + "- Eat the apples to grow the snake and increase your score.\n"
                + "\n"
                + "Controls:\n"
                + "- Arrow Keys or WASD to steer the snake.\n"
                + "- P to pause or resume play.\n"
                + "- R to restart after a game over.\n"
                + "- Esc to return to the home screen.\n"
                + "\n"
                + "Tips:\n"
                + "- Avoid running into walls or your own tail.";
        JOptionPane.showMessageDialog(this, instructions, "How to Play", JOptionPane.INFORMATION_MESSAGE);
    }

    // Renders the animated home screen and menu controls.
    private final class HomePanel extends JPanel {

        private final JLabel titleLabel;
        private final JLabel subtitleLabel;
        private final RoundedButton startButton;
        private final RoundedButton scoresButton;
        private final RoundedButton instructionsButton;
        private final RoundedButton colorsButton;
        private final RoundedButton exitButton;

        HomePanel() {
            setOpaque(false);
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.weightx = 1.0;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;

            gbc.gridy = 0;
            gbc.weighty = 1.0;
            add(new JLabel(""), gbc);

            gbc.gridy++;
            gbc.weighty = 0.0;
            gbc.insets = new Insets(0, 0, 12, 0);
            titleLabel = new JLabel("Snake Game");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 52));
            add(titleLabel, gbc);

            gbc.gridy++;
            gbc.insets = new Insets(0, 0, 32, 0);
            subtitleLabel = new JLabel("Slither and Score!");
            subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
            add(subtitleLabel, gbc);

            gbc.insets = new Insets(10, 120, 10, 120);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            startButton = createMenuButton(
                    "Start New Game",
                    currentScheme.startBase,
                    currentScheme.startHover);
            startButton.addActionListener(e -> startGameFromHome());
            gbc.gridy++;
            add(startButton, gbc);

            scoresButton = createMenuButton(
                    "View High Scores",
                    currentScheme.scoresBase,
                    currentScheme.scoresHover);
            scoresButton.addActionListener(e -> showHighScoresDialog());
            gbc.gridy++;
            add(scoresButton, gbc);

            instructionsButton = createMenuButton(
                    "How to Play",
                    currentScheme.instructionsBase,
                    currentScheme.instructionsHover);
            instructionsButton.addActionListener(e -> showInstructionsDialog());
            gbc.gridy++;
            add(instructionsButton, gbc);

            colorsButton = createMenuButton(
                    "Color Carnival",
                    currentScheme.paletteBase,
                    currentScheme.paletteHover);
            colorsButton.addActionListener(e -> showColorCarnivalDialog());
            gbc.gridy++;
            add(colorsButton, gbc);

            exitButton = createMenuButton(
                    "Exit",
                    currentScheme.exitBase,
                    currentScheme.exitHover);
            exitButton.addActionListener(e -> dispose());
            gbc.gridy++;
            add(exitButton, gbc);

            gbc.gridy++;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(0, 0, 0, 0);
            add(new JLabel(""), gbc);
        }

        // Provides consistent styling for each menu button instance.
        private RoundedButton createMenuButton(String text, Color base, Color hover) {
            RoundedButton button = new RoundedButton(text, base, hover, currentScheme.buttonTextColor);
            button.setFont(new Font("SansSerif", Font.BOLD, 20));
            return button;
        }

        void applyColorScheme(ColorScheme scheme) {
            titleLabel.setForeground(scheme.titleColor);
            subtitleLabel.setForeground(scheme.subtitleColor);
            startButton.updateColors(scheme.startBase, scheme.startHover, scheme.buttonTextColor);
            scoresButton.updateColors(scheme.scoresBase, scheme.scoresHover, scheme.buttonTextColor);
            instructionsButton.updateColors(scheme.instructionsBase, scheme.instructionsHover, scheme.buttonTextColor);
            colorsButton.updateColors(scheme.paletteBase, scheme.paletteHover, scheme.buttonTextColor);
            exitButton.updateColors(scheme.exitBase, scheme.exitHover, scheme.buttonTextColor);
            repaint();
        }

        void requestInitialFocus() {
            startButton.requestFocusInWindow();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gradient = new GradientPaint(
                    0, 0, SnakeGame.this.currentScheme.homeGradientTop,
                    0, getHeight(), SnakeGame.this.currentScheme.homeGradientBottom);
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Custom JButton that draws a rounded pill with hover/press feedback.
    private final class RoundedButton extends JButton {

        private Color baseColor;
        private Color hoverColor;
        private boolean hovered;
        private boolean pressed;

        RoundedButton(String text, Color baseColor, Color hoverColor, Color textColor) {
            super(text);
            updateColors(baseColor, hoverColor, textColor);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(14, 32, 14, 32));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    pressed = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        pressed = true;
                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        // Allow the theme to refresh button colors when the palette changes.
        void updateColors(Color baseColor, Color hoverColor, Color textColor) {
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            setForeground(textColor);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            // Draw a rounded pill background because the default L&F cannot produce this shape.
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = 32;
            Color fill = baseColor;
            if (pressed) {
                fill = hoverColor.darker();
            } else if (hovered) {
                fill = hoverColor;
            }
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
            g2.setColor(new Color(255, 255, 255, 70));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, arc, arc));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            // Border rendered in paintComponent.
        }
    }

    // Owns the main game loop, state, and rendering for the playfield.
    private final class GamePanel extends JPanel implements ActionListener {

        private static final int SCREEN_SIZE = 600;
        private static final int UNIT_SIZE = 20;
        private static final int GRID_TILES = SCREEN_SIZE / UNIT_SIZE;
        private static final int BASE_DELAY = 110;
        private static final int DELAY_STEP = 10;
        private static final int MIN_DELAY = 50;

        private final Deque<Point> snake = new ArrayDeque<>();
        private final Random random = new Random();
        private final Timer timer;

        private Point apple = new Point();
        private Direction currentDirection = Direction.RIGHT;
        private Direction nextDirection = Direction.RIGHT;
        private boolean running = false;
        private boolean paused = false;
        private int score = 0;

        GamePanel() {
            setPreferredSize(new Dimension(SCREEN_SIZE, SCREEN_SIZE));
            setBackground(SnakeGame.this.currentScheme.boardBackground);
            setFocusable(true);
            setFocusTraversalKeysEnabled(false);
            setDoubleBuffered(true);

            timer = new Timer(BASE_DELAY, this);

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    handleKeyPress(e);
                }
            });
        }

        void applyColorScheme(ColorScheme scheme) {
            setBackground(scheme.boardBackground);
            repaint();
        }

        @Override
        public void addNotify() {
            super.addNotify();
            requestFocusInWindow();
        }

        // --- Game state management ---------------------------------------------------------

        private void startGame() {
            snake.clear();
            int startX = GRID_TILES / 2;
            int startY = GRID_TILES / 2;
            snake.addLast(new Point(startX, startY));
            snake.addLast(new Point(startX - 1, startY));
            snake.addLast(new Point(startX - 2, startY));

            currentDirection = Direction.RIGHT;
            nextDirection = Direction.RIGHT;
            score = 0;
            paused = false;
            running = true;

            spawnApple();

            timer.setDelay(BASE_DELAY);
            timer.setInitialDelay(BASE_DELAY);
            timer.start();
            repaint();
        }

        void startNewGame() {
            startGame();
        }

        void stopGame() {
            timer.stop();
            running = false;
            paused = false;
            repaint();
        }

        private void restartGame() {
            startGame();
        }

        private void gameOver() {
            running = false;
            paused = false;
            timer.stop();
            SnakeGame.this.highScoreManager.recordScore(score);
        }

        private void togglePause() {
            if (!running) {
                return;
            }
            paused = !paused;
        }

        private void spawnApple() {
            // Keep sampling positions until we find a tile that is not covered by the snake.
            int x;
            int y;
            do {
                x = random.nextInt(GRID_TILES);
                y = random.nextInt(GRID_TILES);
            } while (isOccupying(x, y));
            apple = new Point(x, y);
        }

        private boolean isOccupying(int x, int y) {
            for (Point segment : snake) {
                if (segment.x == x && segment.y == y) {
                    return true;
                }
            }
            return false;
        }

        // --- Input handling ---------------------------------------------------------------

        private void handleKeyPress(KeyEvent e) {
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> attemptDirection(Direction.LEFT);
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> attemptDirection(Direction.RIGHT);
                case KeyEvent.VK_UP, KeyEvent.VK_W -> attemptDirection(Direction.UP);
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> attemptDirection(Direction.DOWN);
                case KeyEvent.VK_P -> togglePause();
                case KeyEvent.VK_R -> attemptRestart();
                case KeyEvent.VK_ESCAPE -> attemptExitToMenu();
                default -> {
                }
            }
        }

        private void attemptDirection(Direction candidate) {
            if (!running) {
                return;
            }
            if (!candidate.isOpposite(currentDirection)) {
                // Queue the new direction so we never reverse into ourselves mid-tick.
                nextDirection = candidate;
            }
        }

        // Pause the loop, confirm with the player, and restart without recreating the panel.
        private void attemptRestart() {
            if (!running) {
                restartGame();
                requestFocusInWindow();
                return;
            }

            boolean wasPaused = paused;
            boolean timerWasRunning = timer.isRunning();

            paused = true;
            if (timerWasRunning) {
                timer.stop();
            }
            repaint();

            int choice = JOptionPane.showConfirmDialog(
                    SnakeGame.this,
                    "Restart the current game?\nYour progress will be lost.",
                    "Confirm Restart",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                paused = wasPaused;
                if (timerWasRunning) {
                    timer.start();
                }
            }
            repaint();
            requestFocusInWindow();
        }

        // Give the player a chance to cancel before tearing down the active game.
        private void attemptExitToMenu() {
            if (!running) {
                SnakeGame.this.returnToHomeScreen();
                return;
            }

            boolean wasPaused = paused;
            boolean timerWasRunning = timer.isRunning();

            paused = true;
            if (timerWasRunning) {
                timer.stop();
            }
            repaint();

            int choice = JOptionPane.showConfirmDialog(
                    SnakeGame.this,
                    "Exit to the home screen?\nYour current game will be lost.",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                SnakeGame.this.returnToHomeScreen();
                return;
            }

            paused = wasPaused;
            if (timerWasRunning) {
                timer.start();
            }
            repaint();
            requestFocusInWindow();
        }

        // --- Game loop --------------------------------------------------------------------

        @Override
        public void actionPerformed(ActionEvent e) {
            if (running && !paused) {
                move();
            }
            repaint();
        }

        private void move() {
            currentDirection = nextDirection;
            Point head = snake.peekFirst();
            int newX = head.x + currentDirection.dx;
            int newY = head.y + currentDirection.dy;

            if (newX < 0 || newX >= GRID_TILES || newY < 0 || newY >= GRID_TILES) {
                gameOver();
                return;
            }

            boolean grows = (newX == apple.x && newY == apple.y);
            // Detect self-collisions using the target coordinates before mutating the deque.
            if (collidesWithSelf(newX, newY, grows)) {
                gameOver();
                return;
            }

            Point newHead = new Point(newX, newY);
            // Push the new head forward; we only drop the tail if we are not consuming an apple.
            snake.addFirst(newHead);

            if (grows) {
                score++;
                adjustSpeed();
                spawnApple();
            } else {
                snake.removeLast();
            }
        }

        private boolean collidesWithSelf(int x, int y, boolean grows) {
            Point tail = snake.peekLast();
            for (Point segment : snake) {
                if (!grows && segment == tail) {
                    // The tail will move this tick, so allow the head to occupy its current tile.
                    continue;
                }
                if (segment.x == x && segment.y == y) {
                    return true;
                }
            }
            return false;
        }

        // Gradually increase the timer speed as the score climbs to ramp up difficulty.
        private void adjustSpeed() {
            int level = score / 5;
            int targetDelay = Math.max(MIN_DELAY, BASE_DELAY - (level * DELAY_STEP));
            if (timer.getDelay() != targetDelay) {
                timer.setDelay(targetDelay);
                timer.setInitialDelay(targetDelay);
            }
        }

        // --- Rendering --------------------------------------------------------------------

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGrid(g);
            drawApple(g);
            drawSnake(g);
            drawScore(g);

            if (!running) {
                drawGameOver(g);
            } else if (paused) {
                drawPaused(g);
            }
        }

        private void drawGrid(Graphics g) {
            g.setColor(SnakeGame.this.currentScheme.boardGrid);
            for (int i = 0; i <= GRID_TILES; i++) {
                int pos = i * UNIT_SIZE;
                g.drawLine(pos, 0, pos, SCREEN_SIZE);
                g.drawLine(0, pos, SCREEN_SIZE, pos);
            }
        }

        private void drawApple(Graphics g) {
            g.setColor(SnakeGame.this.currentScheme.boardApple);
            g.fillOval(apple.x * UNIT_SIZE, apple.y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
        }

        private void drawSnake(Graphics g) {
            ColorScheme scheme = SnakeGame.this.currentScheme;
            Point head = snake.peekFirst();
            for (Point segment : snake) {
                if (segment == head) {
                    g.setColor(scheme.boardSnakeHead);
                } else {
                    g.setColor(scheme.boardSnakeBody);
                }
                g.fillRect(segment.x * UNIT_SIZE, segment.y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
            }
        }

        private void drawScore(Graphics g) {
            g.setColor(SnakeGame.this.currentScheme.boardScoreText);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 22);
        }

        private void drawPaused(Graphics g) {
            // Use a translucent overlay to keep the board visible while paused.
            String text = "Paused";
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(new Font("SansSerif", Font.BOLD, 24));
            FontMetrics metrics = g2.getFontMetrics();
            ColorScheme scheme = SnakeGame.this.currentScheme;

            int textWidth = metrics.stringWidth(text);
            int textHeight = metrics.getHeight();
            int paddingX = 36;
            int paddingY = 18;
            int boxWidth = textWidth + paddingX;
            int boxHeight = textHeight + paddingY;
            int boxX = (SCREEN_SIZE - boxWidth) / 2;
            int boxY = (SCREEN_SIZE - boxHeight) / 2;

            Color overlayFill = scheme.overlayFill;
            int strongerAlpha = Math.min(255, overlayFill.getAlpha() + 20);
            Color gameOverFill = new Color(
                    overlayFill.getRed(),
                    overlayFill.getGreen(),
                    overlayFill.getBlue(),
                    strongerAlpha);
            g2.setColor(gameOverFill);
            g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 28, 28);
            g2.setColor(scheme.overlayStroke);
            g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 28, 28);

            g2.setColor(scheme.boardScoreText);
            int textX = (SCREEN_SIZE - textWidth) / 2;
            int textY = (SCREEN_SIZE - textHeight) / 2 + metrics.getAscent();
            g2.drawString(text, textX, textY);
            g2.dispose();
        }

        private void drawGameOver(Graphics g) {
            // Reuse the overlay styling so the game-over state matches the pause treatment.
            String line1 = "Game Over | Score: " + score;
            String line2 = "Press R to Restart or Esc for Menu";
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(new Font("SansSerif", Font.BOLD, 24));
            FontMetrics metrics = g2.getFontMetrics();
            int lineHeight = metrics.getHeight();
            int totalHeight = lineHeight * 2;
            int startY = (SCREEN_SIZE - totalHeight) / 2 + metrics.getAscent();
            ColorScheme scheme = SnakeGame.this.currentScheme;

            int x1 = (SCREEN_SIZE - metrics.stringWidth(line1)) / 2;
            int x2 = (SCREEN_SIZE - metrics.stringWidth(line2)) / 2;

            int boxPaddingX = 48;
            int boxPaddingY = 28;
            int boxWidth = Math.max(metrics.stringWidth(line1), metrics.stringWidth(line2)) + boxPaddingX;
            int boxHeight = totalHeight + boxPaddingY;
            int boxX = (SCREEN_SIZE - boxWidth) / 2;
            int boxY = (SCREEN_SIZE - boxHeight) / 2;

            g2.setColor(scheme.overlayFill);
            g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 30, 30);
            g2.setColor(scheme.overlayStroke);
            g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 30, 30);

            g2.setColor(scheme.boardScoreText);
            g2.drawString(line1, x1, startY);
            g2.drawString(line2, x2, startY + lineHeight);
            g2.dispose();
        }

        // --- Direction enum ---------------------------------------------------------------

        private enum Direction {
            UP(0, -1),
            DOWN(0, 1),
            LEFT(-1, 0),
            RIGHT(1, 0);

            final int dx;
            final int dy;

            Direction(int dx, int dy) {
                this.dx = dx;
                this.dy = dy;
            }

            boolean isOpposite(Direction other) {
                return dx + other.dx == 0 && dy + other.dy == 0;
            }
        }
    }

    // Handles persistence of the local high score table with simple file-based storage.
    private static final class HighScoreManager {

        private static final int MAX_ENTRIES = 5;
        private final Path scoreFile;

        HighScoreManager() {
            scoreFile = Paths.get("").toAbsolutePath().resolve("highscore.txt");
        }

        synchronized List<Integer> getHighScores() {
            return Collections.unmodifiableList(readScores());
        }

        synchronized void recordScore(int newScore) {
            if (newScore <= 0) {
                return;
            }
            List<Integer> scores = new ArrayList<>(readScores());
            scores.add(newScore);
            List<Integer> normalized = normalizeScores(scores);
            writeScores(normalized);
        }

        synchronized void clearScores() {
            writeScores(Collections.emptyList());
        }

        private List<Integer> readScores() {
            List<Integer> scores = new ArrayList<>();
            if (Files.exists(scoreFile)) {
                try {
                    List<String> lines = Files.readAllLines(scoreFile);
                    for (String line : lines) {
                        String trimmed = line.trim();
                        if (trimmed.isEmpty()) {
                            continue;
                        }
                        try {
                            int value = Integer.parseInt(trimmed);
                            if (value >= 0) {
                                scores.add(value);
                            }
                        } catch (NumberFormatException ignored) {
                            // Ignore malformed entries and continue.
                        }
                    }
                } catch (IOException ignored) {
                    return new ArrayList<>();
                }
            }
            return normalizeScores(scores);
        }

        private void writeScores(List<Integer> scores) {
            try {
                Path parent = scoreFile.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                List<Integer> normalized = normalizeScores(scores);
                List<String> lines = new ArrayList<>();
                for (Integer score : normalized) {
                    lines.add(Integer.toString(score));
                }
                Files.write(scoreFile, lines);
            } catch (IOException ignored) {
                // Persistence failures should not break the game loop.
            }
        }

        private List<Integer> normalizeScores(List<Integer> scores) {
            List<Integer> working = new ArrayList<>(scores);
            working.sort(Collections.reverseOrder());

            List<Integer> unique = new ArrayList<>();
            HashSet<Integer> seen = new HashSet<>();
            // Remove duplicates while preserving descending order and the top N entries.
            for (Integer score : working) {
                if (score == null) {
                    continue;
                }
                if (seen.add(score)) {
                    unique.add(score);
                    if (unique.size() == MAX_ENTRIES) {
                        break;
                    }
                }
            }
            return unique;
        }
    }
}
