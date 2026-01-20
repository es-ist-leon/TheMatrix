import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ModuleSelectionPanel - Übersicht über alle verfügbaren Lernmodule
 * 
 * Jedes Modul deckt einen bestimmten Aspekt der Matrizenrechnung ab.
 * Der Spieler kann Module in beliebiger Reihenfolge auswählen,
 * bekommt aber Empfehlungen basierend auf dem Fortschritt.
 */
public class ModuleSelectionPanel extends JPanel {
    
    // Farben
    private static final Color BACKGROUND_COLOR = new Color(25, 25, 35);
    private static final Color PANEL_COLOR = new Color(40, 44, 52);
    private static final Color ACCENT_COLOR = new Color(97, 175, 239);
    private static final Color SUCCESS_COLOR = new Color(152, 195, 121);
    private static final Color TEXT_COLOR = new Color(220, 223, 228);
    private static final Color HIGHLIGHT_COLOR = new Color(229, 192, 123);
    
    private MatrixGame game;
    private String[] modules;
    
    // Modul-Beschreibungen
    private final String[] MODULE_DESCRIPTIONS = {
        "Lerne die Grundlagen: Was ist eine Matrix? Zeilen, Spalten und Notation.",
        "Addiere zwei Matrizen element-weise. Voraussetzung: Gleiche Dimension!",
        "Subtrahiere Matrizen voneinander. Ähnlich wie Addition!",
        "Multipliziere jeden Eintrag einer Matrix mit einer Zahl (Skalar).",
        "Die wichtigste Operation: Zeilen × Spalten. Achtung bei den Dimensionen!",
        "Vertausche Zeilen und Spalten einer Matrix (A wird zu Aᵀ).",
        "Berechne die Determinante für 2×2 und 3×3 Matrizen.",
        "Finde die Inverse einer Matrix, sodass A · A⁻¹ = I.",
        "Lerne spezielle Matrizen: Einheits-, Null-, Diagonal- und symmetrische Matrizen.",
        "Übe alle Operationen mit zufälligen Aufgaben!"
    };
    
    // Modul-Icons (Emojis als einfache Lösung)
    private final String[] MODULE_ICONS = {
        "📖", "➕", "➖", "✖️", "🔢", "🔄", "📐", "↩️", "⭐", "🎯"
    };
    
    // Schwierigkeitsgrade
    private final int[] MODULE_DIFFICULTY = {1, 1, 1, 2, 3, 2, 3, 4, 2, 5};
    
    public ModuleSelectionPanel(MatrixGame game, String[] modules) {
        this.game = game;
        this.modules = modules;
        
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        
        initializeUI();
    }
    
    private void initializeUI() {
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Modul-Grid
        JPanel modulesContainer = new JPanel(new BorderLayout());
        modulesContainer.setBackground(BACKGROUND_COLOR);
        modulesContainer.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JPanel modulesGrid = new JPanel(new GridLayout(2, 5, 15, 15));
        modulesGrid.setBackground(BACKGROUND_COLOR);
        
        for (int i = 0; i < modules.length; i++) {
            JPanel moduleCard = createModuleCard(i);
            modulesGrid.add(moduleCard);
        }
        
        modulesContainer.add(modulesGrid, BorderLayout.CENTER);
        add(modulesContainer, BorderLayout.CENTER);
        
        // Info-Panel unten
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Zurück-Button
        JButton backBtn = new JButton("← Hauptmenü");
        backBtn.setForeground(TEXT_COLOR);
        backBtn.setBackground(PANEL_COLOR);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> game.showMainMenu());
        
        // Titel
        JLabel titleLabel = new JLabel("🎯 Wähle ein Modul");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Punktestand
        JLabel scoreLabel = new JLabel("Punkte: " + game.getScore());
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabel.setForeground(HIGHLIGHT_COLOR);
        
        panel.add(backBtn, BorderLayout.WEST);
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(scoreLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Erstellt eine Modul-Karte mit Hover-Effekt und Info
     */
    private JPanel createModuleCard(int index) {
        JPanel card = new JPanel() {
            private boolean hovered = false;
            
            {
                // Hover-Effekt
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        game.startModule(index);
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Hintergrund mit Hover-Effekt
                if (hovered) {
                    g2d.setColor(ACCENT_COLOR.darker());
                } else {
                    g2d.setColor(PANEL_COLOR);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Rahmen
                g2d.setColor(hovered ? ACCENT_COLOR : new Color(60, 65, 77));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 15, 15);
            }
        };
        
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Modul-Nummer
        JLabel numberLabel = new JLabel("Modul " + (index + 1));
        numberLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        numberLabel.setForeground(new Color(TEXT_COLOR.getRed(), TEXT_COLOR.getGreen(), 
                                            TEXT_COLOR.getBlue(), 150));
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Icon
        JLabel iconLabel = new JLabel(MODULE_ICONS[index]);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Titel
        JLabel titleLabel = new JLabel("<html><center>" + modules[index] + "</center></html>");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Schwierigkeit
        JPanel difficultyPanel = createDifficultyIndicator(MODULE_DIFFICULTY[index]);
        difficultyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(numberLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLabel);
        card.add(Box.createVerticalGlue());
        card.add(difficultyPanel);
        
        // Tooltip mit Beschreibung
        card.setToolTipText("<html><body style='width: 200px; padding: 5px;'>" + 
                           MODULE_DESCRIPTIONS[index] + "</body></html>");
        
        return card;
    }
    
    /**
     * Erstellt einen Schwierigkeitsindikator mit Sternen
     */
    private JPanel createDifficultyIndicator(int level) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panel.setOpaque(false);
        
        for (int i = 1; i <= 5; i++) {
            JLabel star = new JLabel(i <= level ? "★" : "☆");
            star.setFont(new Font("SansSerif", Font.PLAIN, 12));
            star.setForeground(i <= level ? HIGHLIGHT_COLOR : new Color(100, 100, 100));
            panel.add(star);
        }
        
        return panel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        JLabel infoLabel = new JLabel("💡 Tipp: Fahre mit der Maus über ein Modul für mehr Infos");
        infoLabel.setForeground(new Color(TEXT_COLOR.getRed(), TEXT_COLOR.getGreen(), 
                                          TEXT_COLOR.getBlue(), 150));
        infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        
        panel.add(infoLabel);
        
        return panel;
    }
}
