import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TutorialPanel - Interaktives Tutorial, das Schritt für Schritt 
 * alle Grundlagen der Matrizenrechnung erklärt.
 * 
 * Das Tutorial ist wie eine "Guided Tour" aufgebaut:
 * 1. Was ist eine Matrix?
 * 2. Zeilen und Spalten verstehen
 * 3. Matrixnotation (m x n)
 * 4. Elemente ansprechen (a_ij)
 * 5. Erste einfache Operationen
 */
public class TutorialPanel extends JPanel {
    
    // Farbdefinitionen
    private static final Color BACKGROUND_COLOR = new Color(25, 25, 35);
    private static final Color PANEL_COLOR = new Color(40, 44, 52);
    private static final Color ACCENT_COLOR = new Color(97, 175, 239);
    private static final Color SUCCESS_COLOR = new Color(152, 195, 121);
    private static final Color TEXT_COLOR = new Color(220, 223, 228);
    private static final Color HIGHLIGHT_COLOR = new Color(229, 192, 123);
    private static final Color MATRIX_CELL_COLOR = new Color(55, 60, 72);
    
    private MatrixGame game;
    private int currentStep = 0;
    private int totalSteps;
    
    // Tutorial-Inhalt
    private List<TutorialStep> tutorialSteps;
    
    // UI-Komponenten
    private JPanel contentPanel;
    private JLabel stepLabel;
    private JProgressBar progressBar;
    private JButton nextButton;
    private JButton prevButton;
    private MatrixVisualizationPanel matrixVis;
    
    // Animation
    private Timer animationTimer;
    private int animationFrame = 0;
    private int highlightRow = -1;
    private int highlightCol = -1;
    
    public TutorialPanel(MatrixGame game) {
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        
        initializeTutorialSteps();
        initializeUI();
        showStep(0);
    }
    
    /**
     * Initialisiert alle Tutorial-Schritte mit Erklärungen und Visualisierungen
     */
    private void initializeTutorialSteps() {
        tutorialSteps = new ArrayList<>();
        
        // Schritt 1: Was ist eine Matrix?
        tutorialSteps.add(new TutorialStep(
            "Was ist eine Matrix?",
            """
            <html><body style='width: 450px; color: #DCE0E8; font-family: SansSerif;'>
            <h2 style='color: #61AFEF;'>Willkommen zur Matrix-Welt! 🎉</h2>
            <p style='font-size: 14px; line-height: 1.6;'>
            Eine <b style='color: #E5C07B;'>Matrix</b> ist eine rechteckige Anordnung von Zahlen, 
            die in <b style='color: #98C379;'>Zeilen</b> (horizontal) und <b style='color: #E06C75;'>Spalten</b> 
            (vertikal) organisiert sind.
            </p>
            <p style='font-size: 14px; line-height: 1.6;'>
            Du kannst dir eine Matrix wie eine <b>Tabelle</b> vorstellen, in der jedes Feld 
            einen Zahlenwert enthält. Matrizen sind in vielen Bereichen wichtig:
            </p>
            <ul style='font-size: 13px;'>
            <li>🎮 Computergrafik (3D-Transformationen)</li>
            <li>🤖 Künstliche Intelligenz & Machine Learning</li>
            <li>📊 Datenanalyse und Statistik</li>
            <li>🔬 Physik und Ingenieurwesen</li>
            </ul>
            <p style='font-size: 14px; color: #61AFEF;'>
            Schau dir rechts die animierte Matrix an!
            </p>
            </body></html>
            """,
            new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
            TutorialStep.VisualizationType.HIGHLIGHT_ALL
        ));
        
        // Schritt 2: Zeilen verstehen
        tutorialSteps.add(new TutorialStep(
            "Zeilen einer Matrix",
            """
            <html><body style='width: 450px; color: #DCE0E8; font-family: SansSerif;'>
            <h2 style='color: #98C379;'>Zeilen (Rows) 📏</h2>
            <p style='font-size: 14px; line-height: 1.6;'>
            Eine <b style='color: #98C379;'>Zeile</b> ist eine <b>horizontale</b> Reihe 
            von Zahlen in einer Matrix.
            </p>
            <p style='font-size: 14px; line-height: 1.6;'>
            Die Matrix rechts hat <b style='color: #E5C07B;'>3 Zeilen</b>:
            </p>
            <ul style='font-size: 14px; line-height: 1.8;'>
            <li><span style='color: #98C379;'>Zeile 1:</span> [1, 2, 3]</li>
            <li><span style='color: #98C379;'>Zeile 2:</span> [4, 5, 6]</li>
            <li><span style='color: #98C379;'>Zeile 3:</span> [7, 8, 9]</li>
            </ul>
            <p style='font-size: 14px; line-height: 1.6;'>
            Zeilen werden von <b>oben nach unten</b> nummeriert, 
            üblicherweise beginnend bei 1.
            </p>
            <p style='font-size: 13px; color: #61AFEF; font-style: italic;'>
            💡 Tipp: "Zeile" und "Row" beginnen beide mit einem Buchstaben, 
            der wie eine horizontale Linie aussieht!
            </p>
            </body></html>
            """,
            new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
            TutorialStep.VisualizationType.HIGHLIGHT_ROWS
        ));
        
        // Schritt 3: Spalten verstehen
        tutorialSteps.add(new TutorialStep(
            "Spalten einer Matrix",
            """
            <html><body style='width: 450px; color: #DCE0E8; font-family: SansSerif;'>
            <h2 style='color: #E06C75;'>Spalten (Columns) 📊</h2>
            <p style='font-size: 14px; line-height: 1.6;'>
            Eine <b style='color: #E06C75;'>Spalte</b> ist eine <b>vertikale</b> Reihe 
            von Zahlen in einer Matrix.
            </p>
            <p style='font-size: 14px; line-height: 1.6;'>
            Die Matrix rechts hat <b style='color: #E5C07B;'>3 Spalten</b>:
            </p>
            <ul style='font-size: 14px; line-height: 1.8;'>
            <li><span style='color: #E06C75;'>Spalte 1:</span> [1, 4, 7]</li>
            <li><span style='color: #E06C75;'>Spalte 2:</span> [2, 5, 8]</li>
            <li><span style='color: #E06C75;'>Spalte 3:</span> [3, 6, 9]</li>
            </ul>
            <p style='font-size: 14px; line-height: 1.6;'>
            Spalten werden von <b>links nach rechts</b> nummeriert.
            </p>
            <p style='font-size: 13px; color: #61AFEF; font-style: italic;'>
            💡 Merkhilfe: Eine "Spalte" steht aufrecht wie eine Säule!
            </p>
            </body></html>
            """,
            new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
            TutorialStep.VisualizationType.HIGHLIGHT_COLS
        ));
        
        // Schritt 4: Matrix-Dimension
        tutorialSteps.add(new TutorialStep(
            "Die Dimension einer Matrix",
            """
            <html><body style='width: 450px; color: #DCE0E8; font-family: SansSerif;'>
            <h2 style='color: #E5C07B;'>Dimension: m × n 📐</h2>
            <p style='font-size: 14px; line-height: 1.6;'>
            Die <b style='color: #E5C07B;'>Dimension</b> einer Matrix gibt an, 
            wie viele Zeilen und Spalten sie hat.
            </p>
            <p style='font-size: 14px; line-height: 1.6;'>
            Man schreibt: <b style='color: #C678DD; font-size: 16px;'>m × n</b> 
            (sprich: "m kreuz n" oder "m mal n")
            </p>
            <ul style='font-size: 14px; line-height: 1.8;'>
            <li><b style='color: #98C379;'>m</b> = Anzahl der <b>Zeilen</b></li>
            <li><b style='color: #E06C75;'>n</b> = Anzahl der <b>Spalten</b></li>
            </ul>
            <p style='font-size: 14px; line-height: 1.6;'>
            <b>Beispiele:</b><br/>
            • Eine 3×3 Matrix hat 3 Zeilen und 3 Spalten (quadratisch)<br/>
            • Eine 2×4 Matrix hat 2 Zeilen und 4 Spalten<br/>
            • Eine 4×1 Matrix ist ein <b>Spaltenvektor</b><br/>
            • Eine 1×4 Matrix ist ein <b>Zeilenvektor</b>
            </p>
            <p style='font-size: 13px; color: #E06C75; font-style: italic;'>
            ⚠️ Wichtig: Immer zuerst Zeilen, dann Spalten!
            </p>
            </body></html>
            """,
            new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
            TutorialStep.VisualizationType.SHOW_DIMENSION
        ));
        
        // Schritt 5: Elemente ansprechen
        tutorialSteps.add(new TutorialStep(
            "Elemente einer Matrix",
            """
            <html><body style='width: 450px; color: #DCE0E8; font-family: SansSerif;'>
            <h2 style='color: #C678DD;'>Element-Notation: a<sub>ij</sub> 🎯</h2>
            <p style='font-size: 14px; line-height: 1.6;'>
            Jede Zahl in einer Matrix nennen wir ein <b style='color: #C678DD;'>Element</b>.
            Um ein Element zu benennen, verwenden wir die Notation:
            </p>
            <p style='font-size: 20px; text-align: center; color: #E5C07B;'>
            <b>a<sub>ij</sub></b>
            </p>
            <ul style='font-size: 14px; line-height: 1.8;'>
            <li><b style='color: #98C379;'>i</b> = Zeilennummer</li>
            <li><b style='color: #E06C75;'>j</b> = Spaltennummer</li>
            </ul>
            <p style='font-size: 14px; line-height: 1.6;'>
            <b>Beispiel:</b> In unserer Matrix ist:<br/>
            • a<sub>11</sub> = 1 (Zeile 1, Spalte 1)<br/>
            • a<sub>23</sub> = 6 (Zeile 2, Spalte 3)<br/>
            • a<sub>32</sub> = 8 (Zeile 3, Spalte 2)
            </p>
            <p style='font-size: 13px; color: #61AFEF; font-style: italic;'>
            🎮 Klicke auf ein Element in der Matrix, um seine Position zu sehen!
            </p>
            </body></html>
            """,
            new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
            TutorialStep.VisualizationType.INTERACTIVE_ELEMENTS
        ));
        
        // Schritt 6: Gleichheit von Matrizen
        tutorialSteps.add(new TutorialStep(
            "Gleichheit von Matrizen",
            """
            <html><body style='width: 450px; color: #DCE0E8; font-family: SansSerif;'>
            <h2 style='color: #61AFEF;'>Wann sind zwei Matrizen gleich? 🤔</h2>
            <p style='font-size: 14px; line-height: 1.6;'>
            Zwei Matrizen <b>A</b> und <b>B</b> sind <b style='color: #98C379;'>gleich</b>, 
            wenn sie:
            </p>
            <ol style='font-size: 14px; line-height: 1.8;'>
            <li>Die <b>gleiche Dimension</b> haben (gleiche Anzahl Zeilen und Spalten)</li>
            <li><b>Alle</b> entsprechenden Elemente identisch sind</li>
            </ol>
            <p style='font-size: 14px; line-height: 1.6;'>
            <b>Mathematisch:</b><br/>
            A = B ⟺ a<sub>ij</sub> = b<sub>ij</sub> für alle i, j
            </p>
            <p style='font-size: 14px; line-height: 1.6;'>
            <b style='color: #E06C75;'>Beispiel für Ungleichheit:</b><br/>
            Wenn auch nur <b>ein</b> Element unterschiedlich ist, 
            sind die Matrizen ungleich!
            </p>
            </body></html>
            """,
            new double[][]{{1, 2}, {3, 4}},
            TutorialStep.VisualizationType.COMPARISON
        ));
        
        // Schritt 7: Spezielle Matrizen - Nullmatrix
        tutorialSteps.add(new TutorialStep(
            "Spezielle Matrizen: Nullmatrix",
            """
            <html><body style='width: 450px; color: #DCE0E8; font-family: SansSerif;'>
            <h2 style='color: #56B6C2;'>Die Nullmatrix 𝟎 ⭕</h2>
            <p style='font-size: 14px; line-height: 1.6;'>
            Eine <b style='color: #56B6C2;'>Nullmatrix</b> ist eine Matrix, 
            in der <b>alle Elemente 0</b> sind.
            </p>
            <p style='font-size: 14px; line-height: 1.6;'>
            Sie wird oft mit <b>O</b> oder <b>0</b> bezeichnet.
            </p>
            <p style='font-size: 14px; line-height: 1.6;'>
            <b>Wichtige Eigenschaft:</b><br/>
            A + O = A (Die Nullmatrix ist das neutrale Element der Addition)
            </p>
            <p style='font-size: 14px; line-height: 1.6;'>
            <b style='color: #E5C07B;'>Anwendung:</b><br/>
            Die Nullmatrix ist wie die "0" bei normalen Zahlen - 
            sie verändert nichts bei der Addition!
            </p>
            </body></html>
            """,
            new double[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            TutorialStep.VisualizationType.HIGHLIGHT_ALL
        ));
        
        // Schritt 8: Einheitsmatrix
        tutorialSteps.add(new TutorialStep(
            "Spezielle Matrizen: Einheitsmatrix",
            """
            <html><body style='width: 450px; color: #DCE0E8; font-family: SansSerif;'>
            <h2 style='color: #98C379;'>Die Einheitsmatrix I 🆔</h2>
            <p style='font-size: 14px; line-height: 1.6;'>
            Die <b style='color: #98C379;'>Einheitsmatrix</b> (auch Identitätsmatrix) ist eine 
            quadratische Matrix mit:
            </p>
            <ul style='font-size: 14px; line-height: 1.8;'>
            <li><b>1</b> auf der Hauptdiagonalen (von links oben nach rechts unten)</li>
            <li><b>0</b> überall sonst</li>
            </ul>
            <p style='font-size: 14px; line-height: 1.6;'>
            <b>Wichtige Eigenschaft:</b><br/>
            A · I = I · A = A<br/>
            (Die Einheitsmatrix ist das neutrale Element der Multiplikation)
            </p>
            <p style='font-size: 14px; line-height: 1.6;'>
            <b style='color: #E5C07B;'>Anwendung:</b><br/>
            Wie die "1" bei normalen Zahlen - multipliziert man damit, 
            bleibt alles unverändert!
            </p>
            </body></html>
            """,
            new double[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}},
            TutorialStep.VisualizationType.HIGHLIGHT_DIAGONAL
        ));
        
        // Schritt 9: Abschluss
        tutorialSteps.add(new TutorialStep(
            "Tutorial abgeschlossen!",
            """
            <html><body style='width: 450px; color: #DCE0E8; font-family: SansSerif;'>
            <h2 style='color: #98C379;'>🎉 Großartig gemacht! 🎉</h2>
            <p style='font-size: 14px; line-height: 1.6;'>
            Du hast die <b>Grundlagen der Matrizen</b> gemeistert! 
            Du weißt jetzt:
            </p>
            <ul style='font-size: 14px; line-height: 1.8;'>
            <li>✅ Was eine Matrix ist</li>
            <li>✅ Zeilen und Spalten zu unterscheiden</li>
            <li>✅ Die Dimension (m × n) zu bestimmen</li>
            <li>✅ Elemente mit a<sub>ij</sub> anzusprechen</li>
            <li>✅ Die Nullmatrix und Einheitsmatrix</li>
            </ul>
            <p style='font-size: 14px; line-height: 1.6;'>
            <b style='color: #61AFEF;'>Nächste Schritte:</b><br/>
            Probiere die verschiedenen Module aus, um Matrix-Operationen 
            wie Addition, Multiplikation und mehr zu lernen!
            </p>
            <p style='font-size: 16px; text-align: center; color: #E5C07B;'>
            <b>Bereit für mehr? Los geht's! 🚀</b>
            </p>
            </body></html>
            """,
            new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
            TutorialStep.VisualizationType.CELEBRATION
        ));
        
        totalSteps = tutorialSteps.size();
    }
    
    /**
     * Initialisiert die UI-Komponenten
     */
    private void initializeUI() {
        // Header mit Fortschritt
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Hauptinhalt
        contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        add(contentPanel, BorderLayout.CENTER);
        
        // Navigation
        JPanel navPanel = createNavigationPanel();
        add(navPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Erstellt das Header-Panel mit Titel und Fortschrittsanzeige
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Zurück-Button
        JButton backBtn = new JButton("← Zurück");
        backBtn.setForeground(TEXT_COLOR);
        backBtn.setBackground(PANEL_COLOR);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> game.showMainMenu());
        
        // Titel
        stepLabel = new JLabel("Tutorial");
        stepLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        stepLabel.setForeground(TEXT_COLOR);
        stepLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Fortschrittsbalken
        progressBar = new JProgressBar(0, totalSteps - 1);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(200, 8));
        progressBar.setForeground(ACCENT_COLOR);
        progressBar.setBackground(BACKGROUND_COLOR);
        progressBar.setBorderPainted(false);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(PANEL_COLOR);
        JLabel progressLabel = new JLabel("Fortschritt: ");
        progressLabel.setForeground(TEXT_COLOR);
        rightPanel.add(progressLabel);
        rightPanel.add(progressBar);
        
        panel.add(backBtn, BorderLayout.WEST);
        panel.add(stepLabel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Erstellt das Navigations-Panel mit Vor/Zurück-Buttons
     */
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        panel.setBackground(BACKGROUND_COLOR);
        
        prevButton = new JButton("◀ Zurück");
        styleNavigationButton(prevButton, false);
        prevButton.addActionListener(e -> showStep(currentStep - 1));
        
        nextButton = new JButton("Weiter ▶");
        styleNavigationButton(nextButton, true);
        nextButton.addActionListener(e -> {
            if (currentStep < totalSteps - 1) {
                showStep(currentStep + 1);
            } else {
                game.showMainMenu();
            }
        });
        
        panel.add(prevButton);
        panel.add(nextButton);
        
        return panel;
    }
    
    /**
     * Stylt einen Navigationsbutton
     */
    private void styleNavigationButton(JButton button, boolean isPrimary) {
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setForeground(TEXT_COLOR);
        button.setBackground(isPrimary ? ACCENT_COLOR : PANEL_COLOR);
        button.setPreferredSize(new Dimension(150, 45));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(
            isPrimary ? ACCENT_COLOR : new Color(60, 65, 77), 2));
    }
    
    /**
     * Zeigt einen bestimmten Tutorial-Schritt an
     */
    private void showStep(int step) {
        if (step < 0 || step >= totalSteps) return;
        
        currentStep = step;
        TutorialStep tutorialStep = tutorialSteps.get(step);
        
        // UI aktualisieren
        stepLabel.setText(tutorialStep.title);
        progressBar.setValue(step);
        
        // Buttons aktualisieren
        prevButton.setEnabled(step > 0);
        nextButton.setText(step < totalSteps - 1 ? "Weiter ▶" : "Fertig ✓");
        
        // Content aktualisieren
        contentPanel.removeAll();
        
        // Textbereich links
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(PANEL_COLOR);
        textPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 65, 77), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel contentLabel = new JLabel(tutorialStep.content);
        contentLabel.setVerticalAlignment(SwingConstants.TOP);
        textPanel.add(contentLabel, BorderLayout.CENTER);
        
        // Matrix-Visualisierung rechts
        matrixVis = new MatrixVisualizationPanel(
            tutorialStep.exampleMatrix, 
            tutorialStep.visualizationType
        );
        
        contentPanel.add(textPanel);
        contentPanel.add(matrixVis);
        
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Animation starten wenn nötig
        startVisualizationAnimation(tutorialStep.visualizationType);
    }
    
    /**
     * Startet die passende Animation für den Visualisierungstyp
     */
    private void startVisualizationAnimation(TutorialStep.VisualizationType type) {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        
        animationFrame = 0;
        
        if (type == TutorialStep.VisualizationType.HIGHLIGHT_ROWS ||
            type == TutorialStep.VisualizationType.HIGHLIGHT_COLS) {
            animationTimer = new Timer(1000, e -> {
                animationFrame = (animationFrame + 1) % 3;
                matrixVis.setHighlightIndex(animationFrame);
                matrixVis.repaint();
            });
            animationTimer.start();
        }
    }
    
    /**
     * Innere Klasse für einen Tutorial-Schritt
     */
    static class TutorialStep {
        String title;
        String content;
        double[][] exampleMatrix;
        VisualizationType visualizationType;
        
        enum VisualizationType {
            HIGHLIGHT_ALL, HIGHLIGHT_ROWS, HIGHLIGHT_COLS,
            SHOW_DIMENSION, INTERACTIVE_ELEMENTS, COMPARISON,
            HIGHLIGHT_DIAGONAL, CELEBRATION
        }
        
        TutorialStep(String title, String content, double[][] matrix, VisualizationType type) {
            this.title = title;
            this.content = content;
            this.exampleMatrix = matrix;
            this.visualizationType = type;
        }
    }
    
    /**
     * Panel zur Visualisierung von Matrizen
     */
    class MatrixVisualizationPanel extends JPanel {
        private double[][] matrix;
        private TutorialStep.VisualizationType type;
        private int highlightIndex = 0;
        private int selectedRow = -1;
        private int selectedCol = -1;
        private int cellSize = 60;
        
        public MatrixVisualizationPanel(double[][] matrix, TutorialStep.VisualizationType type) {
            this.matrix = matrix;
            this.type = type;
            setBackground(BACKGROUND_COLOR);
            setPreferredSize(new Dimension(400, 400));
            
            // Mausinteraktion für interaktive Elemente
            if (type == TutorialStep.VisualizationType.INTERACTIVE_ELEMENTS) {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleClick(e.getX(), e.getY());
                    }
                });
                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        handleHover(e.getX(), e.getY());
                    }
                });
            }
        }
        
        public void setHighlightIndex(int index) {
            this.highlightIndex = index;
        }
        
        private void handleClick(int x, int y) {
            int[] cell = getCellAt(x, y);
            if (cell != null) {
                selectedRow = cell[0];
                selectedCol = cell[1];
                repaint();
            }
        }
        
        private void handleHover(int x, int y) {
            // Optional: Hover-Effekt
        }
        
        private int[] getCellAt(int x, int y) {
            int startX = (getWidth() - matrix[0].length * cellSize) / 2;
            int startY = (getHeight() - matrix.length * cellSize) / 2;
            
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    int cellX = startX + j * cellSize;
                    int cellY = startY + i * cellSize;
                    if (x >= cellX && x < cellX + cellSize &&
                        y >= cellY && y < cellY + cellSize) {
                        return new int[]{i, j};
                    }
                }
            }
            return null;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int rows = matrix.length;
            int cols = matrix[0].length;
            int startX = (getWidth() - cols * cellSize) / 2;
            int startY = (getHeight() - rows * cellSize) / 2;
            
            // Matrix-Klammern zeichnen
            drawMatrixBrackets(g2d, startX, startY, rows, cols);
            
            // Zellen zeichnen
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    int x = startX + j * cellSize;
                    int y = startY + i * cellSize;
                    
                    // Hintergrundfarbe basierend auf Typ
                    Color cellColor = getCellColor(i, j);
                    
                    g2d.setColor(cellColor);
                    g2d.fillRoundRect(x + 3, y + 3, cellSize - 6, cellSize - 6, 8, 8);
                    
                    // Rahmen
                    g2d.setColor(new Color(70, 75, 87));
                    g2d.drawRoundRect(x + 3, y + 3, cellSize - 6, cellSize - 6, 8, 8);
                    
                    // Wert
                    g2d.setColor(TEXT_COLOR);
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
                    String value = formatValue(matrix[i][j]);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = x + (cellSize - fm.stringWidth(value)) / 2;
                    int textY = y + (cellSize + fm.getAscent() - fm.getDescent()) / 2;
                    g2d.drawString(value, textX, textY);
                }
            }
            
            // Zusätzliche Visualisierungen
            drawAdditionalVisuals(g2d, startX, startY, rows, cols);
        }
        
        private Color getCellColor(int row, int col) {
            switch (type) {
                case HIGHLIGHT_ROWS:
                    return row == highlightIndex ? SUCCESS_COLOR.darker() : MATRIX_CELL_COLOR;
                case HIGHLIGHT_COLS:
                    return col == highlightIndex ? new Color(224, 108, 117, 150) : MATRIX_CELL_COLOR;
                case HIGHLIGHT_DIAGONAL:
                    return row == col ? SUCCESS_COLOR.darker() : MATRIX_CELL_COLOR;
                case INTERACTIVE_ELEMENTS:
                    if (row == selectedRow && col == selectedCol) {
                        return HIGHLIGHT_COLOR;
                    }
                    return MATRIX_CELL_COLOR;
                case CELEBRATION:
                    // Regenbogeneffekt
                    float hue = (float)(row * matrix[0].length + col) / (matrix.length * matrix[0].length);
                    return Color.getHSBColor(hue, 0.5f, 0.7f);
                default:
                    return MATRIX_CELL_COLOR;
            }
        }
        
        private void drawMatrixBrackets(Graphics2D g2d, int startX, int startY, int rows, int cols) {
            g2d.setColor(ACCENT_COLOR);
            g2d.setStroke(new BasicStroke(3));
            
            int width = cols * cellSize;
            int height = rows * cellSize;
            int bracketWidth = 15;
            
            // Linke Klammer
            g2d.drawLine(startX - bracketWidth, startY, startX - 5, startY);
            g2d.drawLine(startX - bracketWidth, startY, startX - bracketWidth, startY + height);
            g2d.drawLine(startX - bracketWidth, startY + height, startX - 5, startY + height);
            
            // Rechte Klammer
            g2d.drawLine(startX + width + 5, startY, startX + width + bracketWidth, startY);
            g2d.drawLine(startX + width + bracketWidth, startY, startX + width + bracketWidth, startY + height);
            g2d.drawLine(startX + width + 5, startY + height, startX + width + bracketWidth, startY + height);
        }
        
        private void drawAdditionalVisuals(Graphics2D g2d, int startX, int startY, int rows, int cols) {
            switch (type) {
                case SHOW_DIMENSION:
                    // Dimensionsanzeige
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
                    g2d.setColor(HIGHLIGHT_COLOR);
                    String dimension = rows + " × " + cols;
                    g2d.drawString(dimension, startX + (cols * cellSize - 50) / 2, startY + rows * cellSize + 35);
                    
                    // Pfeile für Zeilen/Spalten
                    g2d.setColor(SUCCESS_COLOR);
                    g2d.drawString("m = " + rows, startX - 70, startY + rows * cellSize / 2);
                    g2d.setColor(new Color(224, 108, 117));
                    g2d.drawString("n = " + cols, startX + cols * cellSize / 2 - 15, startY - 15);
                    break;
                    
                case INTERACTIVE_ELEMENTS:
                    if (selectedRow >= 0 && selectedCol >= 0) {
                        g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
                        g2d.setColor(HIGHLIGHT_COLOR);
                        String notation = "a" + (selectedRow + 1) + (selectedCol + 1) + " = " + 
                                         formatValue(matrix[selectedRow][selectedCol]);
                        g2d.drawString(notation, startX, startY + rows * cellSize + 40);
                        
                        g2d.setColor(TEXT_COLOR);
                        g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
                        g2d.drawString("Zeile " + (selectedRow + 1) + ", Spalte " + (selectedCol + 1), 
                                      startX, startY + rows * cellSize + 65);
                    } else {
                        g2d.setColor(new Color(TEXT_COLOR.getRed(), TEXT_COLOR.getGreen(), 
                                              TEXT_COLOR.getBlue(), 150));
                        g2d.setFont(new Font("SansSerif", Font.ITALIC, 14));
                        g2d.drawString("Klicke auf ein Element!", 
                                      startX + 20, startY + rows * cellSize + 40);
                    }
                    break;
            }
        }
        
        private String formatValue(double value) {
            if (value == (int) value) {
                return String.valueOf((int) value);
            }
            return String.format("%.1f", value);
        }
    }
}
