import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * MatrixGame - Ein visuelles, interaktives Lernspiel für Matrizen
 * 
 * Dieses Spiel vermittelt spielerisch alle wichtigen Konzepte der Matrizenrechnung:
 * - Grundlagen: Was ist eine Matrix? Zeilen, Spalten, Elemente
 * - Addition und Subtraktion von Matrizen
 * - Skalarmultiplikation
 * - Matrizenmultiplikation
 * - Transposition
 * - Determinante (2x2 und 3x3)
 * - Inverse Matrix
 * - Spezielle Matrizen (Einheitsmatrix, Nullmatrix, etc.)
 */
public class MatrixGame extends JFrame {
    
    // Farbschema für ein ansprechendes Design
    private static final Color BACKGROUND_COLOR = new Color(25, 25, 35);
    private static final Color PANEL_COLOR = new Color(40, 44, 52);
    private static final Color ACCENT_COLOR = new Color(97, 175, 239);
    private static final Color SUCCESS_COLOR = new Color(152, 195, 121);
    private static final Color ERROR_COLOR = new Color(224, 108, 117);
    private static final Color TEXT_COLOR = new Color(220, 223, 228);
    private static final Color MATRIX_CELL_COLOR = new Color(55, 60, 72);
    private static final Color HIGHLIGHT_COLOR = new Color(229, 192, 123);
    
    // Spielzustand
    private int currentLevel = 0;
    private int score = 0;
    private int streak = 0;
    private String playerName = "Spieler";
    
    // UI-Komponenten
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JLabel scoreLabel;
    private JLabel streakLabel;
    private JLabel levelLabel;
    
    // Spielmodule
    private final String[] MODULES = {
        "Grundlagen", "Addition", "Subtraktion", "Skalar-Multiplikation",
        "Matrix-Multiplikation", "Transposition", "Determinante", 
        "Inverse Matrix", "Spezial-Matrizen", "Freies Üben"
    };
    
    public MatrixGame() {
        setTitle("🎮 Matrix Meister - Das Matrizen-Lernspiel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        
        // Hauptlayout initialisieren
        initializeUI();
        
        // Startbildschirm anzeigen
        showStartScreen();
    }
    
    /**
     * Initialisiert die Benutzeroberfläche mit dem Hauptlayout
     */
    private void initializeUI() {
        // Hauptpanel mit CardLayout für verschiedene Ansichten
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        add(mainPanel);
    }
    
    /**
     * Zeigt den Startbildschirm mit Spieloptionen
     */
    private void showStartScreen() {
        JPanel startPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient-Hintergrund
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(25, 25, 35),
                    getWidth(), getHeight(), new Color(35, 40, 55)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Dekorative Matrizen im Hintergrund
                drawDecorativeMatrices(g2d);
            }
        };
        
        // Titel-Bereich
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(60, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("MATRIX MEISTER");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 56));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Lerne Matrizen spielerisch!");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(15));
        titlePanel.add(subtitleLabel);
        
        // Button-Bereich
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 50, 0));
        
        // Spielmodus-Buttons
        JButton tutorialBtn = createStyledButton("📚 Tutorial starten", ACCENT_COLOR);
        tutorialBtn.addActionListener(e -> showTutorial());
        
        JButton moduleBtn = createStyledButton("🎯 Module auswählen", SUCCESS_COLOR);
        moduleBtn.addActionListener(e -> showModuleSelection());
        
        JButton challengeBtn = createStyledButton("⚡ Herausforderung", HIGHLIGHT_COLOR);
        challengeBtn.addActionListener(e -> startChallenge());
        
        JButton sandboxBtn = createStyledButton("🔬 Matrix-Labor", new Color(198, 120, 221));
        sandboxBtn.addActionListener(e -> openMatrixLab());
        
        buttonPanel.add(tutorialBtn);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(moduleBtn);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(challengeBtn);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(sandboxBtn);
        
        // Info-Bereich unten
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setOpaque(false);
        JLabel infoLabel = new JLabel("Drücke eine Taste oder klicke auf einen Button zum Starten");
        infoLabel.setForeground(new Color(TEXT_COLOR.getRed(), TEXT_COLOR.getGreen(), TEXT_COLOR.getBlue(), 150));
        infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        infoPanel.add(infoLabel);
        
        startPanel.add(titlePanel, BorderLayout.NORTH);
        startPanel.add(buttonPanel, BorderLayout.CENTER);
        startPanel.add(infoPanel, BorderLayout.SOUTH);
        
        mainPanel.add(startPanel, "start");
        cardLayout.show(mainPanel, "start");
    }
    
    /**
     * Zeichnet dekorative Matrizen im Hintergrund für visuellen Effekt
     */
    private void drawDecorativeMatrices(Graphics2D g2d) {
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g2d.setColor(new Color(97, 175, 239, 30));
        
        Random rand = new Random(42); // Fester Seed für konsistente Anzeige
        for (int i = 0; i < 8; i++) {
            int x = rand.nextInt(1100);
            int y = rand.nextInt(700);
            int rows = 2 + rand.nextInt(3);
            int cols = 2 + rand.nextInt(3);
            
            drawMatrixOutline(g2d, x, y, rows, cols, 25);
        }
    }
    
    /**
     * Zeichnet eine Matrix-Umrandung (Klammern)
     */
    private void drawMatrixOutline(Graphics2D g2d, int x, int y, int rows, int cols, int cellSize) {
        int width = cols * cellSize;
        int height = rows * cellSize;
        
        // Linke Klammer
        g2d.drawLine(x, y, x + 10, y);
        g2d.drawLine(x, y, x, y + height);
        g2d.drawLine(x, y + height, x + 10, y + height);
        
        // Rechte Klammer
        g2d.drawLine(x + width - 10, y, x + width, y);
        g2d.drawLine(x + width, y, x + width, y + height);
        g2d.drawLine(x + width - 10, y + height, x + width, y + height);
        
        // Beispielwerte
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int val = (int)(Math.random() * 10);
                g2d.drawString(String.valueOf(val), x + 15 + j * cellSize, y + 18 + i * cellSize);
            }
        }
    }
    
    /**
     * Erstellt einen einheitlich gestylten Button
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
            }
        };
        
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(300, 55));
        button.setMaximumSize(new Dimension(300, 55));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * Zeigt das interaktive Tutorial an
     */
    private void showTutorial() {
        TutorialPanel tutorialPanel = new TutorialPanel(this);
        mainPanel.add(tutorialPanel, "tutorial");
        cardLayout.show(mainPanel, "tutorial");
    }
    
    /**
     * Zeigt die Modulauswahl
     */
    private void showModuleSelection() {
        ModuleSelectionPanel modulePanel = new ModuleSelectionPanel(this, MODULES);
        mainPanel.add(modulePanel, "modules");
        cardLayout.show(mainPanel, "modules");
    }
    
    /**
     * Startet den Herausforderungsmodus
     */
    private void startChallenge() {
        ChallengePanel challengePanel = new ChallengePanel(this);
        mainPanel.add(challengePanel, "challenge");
        cardLayout.show(mainPanel, "challenge");
    }
    
    /**
     * Öffnet das Matrix-Labor zum freien Experimentieren
     */
    private void openMatrixLab() {
        MatrixLabPanel labPanel = new MatrixLabPanel(this);
        mainPanel.add(labPanel, "lab");
        cardLayout.show(mainPanel, "lab");
    }
    
    /**
     * Startet ein bestimmtes Modul
     */
    public void startModule(int moduleIndex) {
        currentLevel = moduleIndex;
        GamePanel gamePanel = new GamePanel(this, moduleIndex);
        mainPanel.add(gamePanel, "game_" + moduleIndex);
        cardLayout.show(mainPanel, "game_" + moduleIndex);
    }
    
    /**
     * Zurück zum Startbildschirm
     */
    public void showMainMenu() {
        cardLayout.show(mainPanel, "start");
    }
    
    // Getter und Setter für Spielzustand
    public int getScore() { return score; }
    public void addScore(int points) { 
        score += points;
        streak++;
    }
    public void resetStreak() { streak = 0; }
    public int getStreak() { return streak; }
    public int getCurrentLevel() { return currentLevel; }
    
    public static void main(String[] args) {
        // Look and Feel setzen
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Spiel starten
        SwingUtilities.invokeLater(() -> {
            MatrixGame game = new MatrixGame();
            game.setVisible(true);
        });
    }
}
