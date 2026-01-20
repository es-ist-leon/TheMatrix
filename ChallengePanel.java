import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * ChallengePanel - Der Herausforderungsmodus mit Zeitdruck.
 * 
 * In diesem Modus hat der Spieler begrenzte Zeit, um möglichst viele
 * Aufgaben aus allen Kategorien zu lösen. Je schneller und korrekter
 * die Antworten, desto mehr Punkte gibt es!
 * 
 * Features:
 * - Countdown-Timer
 * - Zufällige Aufgaben aus allen Modulen
 * - Kombo-System für aufeinanderfolgende richtige Antworten
 * - Highscore-Tracking
 */
public class ChallengePanel extends JPanel {
    
    // Farben
    private static final Color BACKGROUND_COLOR = new Color(25, 25, 35);
    private static final Color PANEL_COLOR = new Color(40, 44, 52);
    private static final Color ACCENT_COLOR = new Color(97, 175, 239);
    private static final Color SUCCESS_COLOR = new Color(152, 195, 121);
    private static final Color ERROR_COLOR = new Color(224, 108, 117);
    private static final Color TEXT_COLOR = new Color(220, 223, 228);
    private static final Color HIGHLIGHT_COLOR = new Color(229, 192, 123);
    private static final Color MATRIX_CELL_COLOR = new Color(55, 60, 72);
    private static final Color WARNING_COLOR = new Color(224, 108, 117);
    
    private MatrixGame game;
    private Random random = new Random();
    
    // Spielzustand
    private int timeRemaining = 120; // 2 Minuten
    private int challengeScore = 0;
    private int combo = 0;
    private int questionsAnswered = 0;
    private int correctAnswers = 0;
    private boolean gameActive = false;
    private boolean gameStarted = false;
    
    // Aktuelle Aufgabe
    private double[][] matrixA;
    private double[][] matrixB;
    private double[][] expectedResult;
    private double scalar;
    private String currentOperation;
    private int currentQuestionType;
    
    // Timer
    private Timer gameTimer;
    
    // UI-Komponenten
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private JLabel comboLabel;
    private JLabel questionLabel;
    private JPanel matrixPanel;
    private JPanel inputArea;
    private JTextField[][] inputFields;
    private JButton submitButton;
    private JTextArea feedbackArea;
    private JProgressBar timeBar;
    
    public ChallengePanel(MatrixGame game) {
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        
        showStartScreen();
    }
    
    /**
     * Zeigt den Startbildschirm vor Spielbeginn
     */
    private void showStartScreen() {
        removeAll();
        
        JPanel startPanel = new JPanel();
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        startPanel.setBackground(BACKGROUND_COLOR);
        startPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Titel
        JLabel titleLabel = new JLabel("⚡ HERAUSFORDERUNG ⚡");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
        titleLabel.setForeground(HIGHLIGHT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Beschreibung
        JLabel descLabel = new JLabel("<html><center>" +
            "<p style='font-size: 16px; color: #DCE0E8; margin: 20px;'>" +
            "Löse so viele Matrix-Aufgaben wie möglich in <b style='color: #E5C07B;'>2 Minuten</b>!</p>" +
            "<p style='font-size: 14px; color: #98C379;'>• Schnelle Antworten = Mehr Punkte</p>" +
            "<p style='font-size: 14px; color: #98C379;'>• Kombo-Bonus für richtige Serien</p>" +
            "<p style='font-size: 14px; color: #98C379;'>• Aufgaben aus allen Kategorien</p>" +
            "</center></html>");
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Schwierigkeitsauswahl
        JPanel difficultyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        difficultyPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel diffLabel = new JLabel("Schwierigkeit: ");
        diffLabel.setForeground(TEXT_COLOR);
        diffLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        String[] difficulties = {"Anfänger (3 min)", "Normal (2 min)", "Experte (90 sec)"};
        JComboBox<String> diffBox = new JComboBox<>(difficulties);
        diffBox.setSelectedIndex(1);
        diffBox.setBackground(PANEL_COLOR);
        diffBox.setForeground(TEXT_COLOR);
        diffBox.addActionListener(e -> {
            timeRemaining = switch (diffBox.getSelectedIndex()) {
                case 0 -> 180;
                case 1 -> 120;
                case 2 -> 90;
                default -> 120;
            };
        });
        
        difficultyPanel.add(diffLabel);
        difficultyPanel.add(diffBox);
        
        // Start-Button
        JButton startBtn = new JButton("🎮 START!");
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 24));
        startBtn.setForeground(TEXT_COLOR);
        startBtn.setBackground(SUCCESS_COLOR);
        startBtn.setPreferredSize(new Dimension(200, 60));
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.setFocusPainted(false);
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startBtn.addActionListener(e -> startGame());
        
        // Zurück-Button
        JButton backBtn = new JButton("← Zurück zum Menü");
        backBtn.setForeground(TEXT_COLOR);
        backBtn.setBackground(PANEL_COLOR);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> game.showMainMenu());
        
        // Layout zusammenbauen
        startPanel.add(Box.createVerticalGlue());
        startPanel.add(titleLabel);
        startPanel.add(Box.createVerticalStrut(30));
        startPanel.add(descLabel);
        startPanel.add(Box.createVerticalStrut(30));
        startPanel.add(difficultyPanel);
        startPanel.add(Box.createVerticalStrut(40));
        startPanel.add(startBtn);
        startPanel.add(Box.createVerticalStrut(20));
        startPanel.add(backBtn);
        startPanel.add(Box.createVerticalGlue());
        
        add(startPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    /**
     * Startet das Spiel
     */
    private void startGame() {
        gameActive = true;
        gameStarted = true;
        challengeScore = 0;
        combo = 0;
        questionsAnswered = 0;
        correctAnswers = 0;
        
        initializeGameUI();
        generateQuestion();
        startTimer();
    }
    
    /**
     * Initialisiert die Spiel-UI
     */
    private void initializeGameUI() {
        removeAll();
        setLayout(new BorderLayout());
        
        // Header mit Timer und Punkten
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Timer
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timerPanel.setBackground(PANEL_COLOR);
        
        timerLabel = new JLabel("⏱️ " + formatTime(timeRemaining));
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
        timerLabel.setForeground(ACCENT_COLOR);
        
        timeBar = new JProgressBar(0, timeRemaining);
        timeBar.setValue(timeRemaining);
        timeBar.setPreferredSize(new Dimension(150, 15));
        timeBar.setForeground(ACCENT_COLOR);
        timeBar.setBackground(BACKGROUND_COLOR);
        timeBar.setBorderPainted(false);
        
        timerPanel.add(timerLabel);
        timerPanel.add(Box.createHorizontalStrut(15));
        timerPanel.add(timeBar);
        
        // Statistiken
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statsPanel.setBackground(PANEL_COLOR);
        
        comboLabel = new JLabel("🔥 x" + combo);
        comboLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        comboLabel.setForeground(ERROR_COLOR);
        
        scoreLabel = new JLabel("⭐ " + challengeScore);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        scoreLabel.setForeground(HIGHLIGHT_COLOR);
        
        statsPanel.add(comboLabel);
        statsPanel.add(scoreLabel);
        
        headerPanel.add(timerPanel, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Hauptbereich
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Frage
        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBackground(PANEL_COLOR);
        questionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 65, 77)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        questionLabel.setForeground(TEXT_COLOR);
        questionPanel.add(questionLabel, BorderLayout.CENTER);
        
        mainPanel.add(questionPanel, BorderLayout.NORTH);
        
        // Matrix und Eingabe
        JPanel workPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        workPanel.setBackground(BACKGROUND_COLOR);
        
        matrixPanel = new JPanel();
        matrixPanel.setBackground(BACKGROUND_COLOR);
        matrixPanel.setLayout(new BoxLayout(matrixPanel, BoxLayout.Y_AXIS));
        
        inputArea = new JPanel();
        inputArea.setBackground(BACKGROUND_COLOR);
        inputArea.setLayout(new BoxLayout(inputArea, BoxLayout.Y_AXIS));
        
        workPanel.add(matrixPanel);
        workPanel.add(inputArea);
        
        mainPanel.add(workPanel, BorderLayout.CENTER);
        
        // Feedback und Submit
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        
        feedbackArea = new JTextArea(2, 40);
        feedbackArea.setEditable(false);
        feedbackArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        feedbackArea.setBackground(PANEL_COLOR);
        feedbackArea.setForeground(TEXT_COLOR);
        feedbackArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        submitButton = new JButton("✓ Antworten");
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        submitButton.setForeground(TEXT_COLOR);
        submitButton.setBackground(ACCENT_COLOR);
        submitButton.setPreferredSize(new Dimension(150, 50));
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> checkAnswer());
        
        bottomPanel.add(new JScrollPane(feedbackArea), BorderLayout.CENTER);
        bottomPanel.add(submitButton, BorderLayout.EAST);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    /**
     * Startet den Countdown-Timer
     */
    private void startTimer() {
        int initialTime = timeRemaining;
        
        gameTimer = new Timer(1000, e -> {
            timeRemaining--;
            timerLabel.setText("⏱️ " + formatTime(timeRemaining));
            timeBar.setValue(timeRemaining);
            
            // Warnung bei wenig Zeit
            if (timeRemaining <= 30) {
                timerLabel.setForeground(WARNING_COLOR);
                timeBar.setForeground(WARNING_COLOR);
            }
            if (timeRemaining <= 10) {
                // Blinken bei sehr wenig Zeit
                timerLabel.setForeground(timeRemaining % 2 == 0 ? WARNING_COLOR : TEXT_COLOR);
            }
            
            if (timeRemaining <= 0) {
                endGame();
            }
        });
        gameTimer.start();
    }
    
    /**
     * Formatiert die Zeit als MM:SS
     */
    private String formatTime(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }
    
    /**
     * Generiert eine zufällige Aufgabe
     */
    private void generateQuestion() {
        matrixPanel.removeAll();
        inputArea.removeAll();
        feedbackArea.setText("");
        
        // Wähle zufälligen Aufgabentyp
        currentQuestionType = random.nextInt(6);
        
        switch (currentQuestionType) {
            case 0 -> generateAdditionQuestion();
            case 1 -> generateSubtractionQuestion();
            case 2 -> generateScalarQuestion();
            case 3 -> generateTransposeQuestion();
            case 4 -> generateDeterminantQuestion();
            case 5 -> generateElementQuestion();
        }
        
        matrixPanel.revalidate();
        matrixPanel.repaint();
        inputArea.revalidate();
        inputArea.repaint();
    }
    
    private void generateAdditionQuestion() {
        int size = 2;
        matrixA = generateMatrix(size, size, 1, 10);
        matrixB = generateMatrix(size, size, 1, 10);
        
        expectedResult = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                expectedResult[i][j] = matrixA[i][j] + matrixB[i][j];
            }
        }
        
        currentOperation = "Addition";
        questionLabel.setText("Berechne A + B");
        
        displayMatrixOperation("+");
        createInputGrid(size, size);
    }
    
    private void generateSubtractionQuestion() {
        int size = 2;
        matrixA = generateMatrix(size, size, 5, 15);
        matrixB = generateMatrix(size, size, 1, 10);
        
        expectedResult = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                expectedResult[i][j] = matrixA[i][j] - matrixB[i][j];
            }
        }
        
        currentOperation = "Subtraktion";
        questionLabel.setText("Berechne A - B");
        
        displayMatrixOperation("-");
        createInputGrid(size, size);
    }
    
    private void generateScalarQuestion() {
        int size = 2;
        matrixA = generateMatrix(size, size, 1, 10);
        scalar = 2 + random.nextInt(5);
        
        expectedResult = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                expectedResult[i][j] = scalar * matrixA[i][j];
            }
        }
        
        currentOperation = "Skalar";
        questionLabel.setText(String.format("Berechne %.0f · A", scalar));
        
        // Zeige Skalar und Matrix
        JPanel displayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        displayPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel scalarLabel = new JLabel(String.format("%.0f ×", scalar));
        scalarLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        scalarLabel.setForeground(HIGHLIGHT_COLOR);
        
        displayPanel.add(scalarLabel);
        displayPanel.add(createMatrixPanel(matrixA, "A"));
        
        matrixPanel.add(Box.createVerticalGlue());
        matrixPanel.add(displayPanel);
        matrixPanel.add(Box.createVerticalGlue());
        
        createInputGrid(size, size);
    }
    
    private void generateTransposeQuestion() {
        int rows = 2 + random.nextInt(2);
        int cols = 2 + random.nextInt(2);
        matrixA = generateMatrix(rows, cols, 1, 10);
        
        expectedResult = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                expectedResult[j][i] = matrixA[i][j];
            }
        }
        
        currentOperation = "Transposition";
        questionLabel.setText("Berechne Aᵀ");
        
        matrixPanel.add(Box.createVerticalGlue());
        matrixPanel.add(createMatrixPanel(matrixA, "A"));
        matrixPanel.add(Box.createVerticalGlue());
        
        createInputGrid(cols, rows);
    }
    
    private void generateDeterminantQuestion() {
        matrixA = generateMatrix(2, 2, 1, 8);
        
        double det = matrixA[0][0] * matrixA[1][1] - matrixA[0][1] * matrixA[1][0];
        expectedResult = new double[][]{{det}};
        
        currentOperation = "Determinante";
        questionLabel.setText("Berechne det(A)");
        
        matrixPanel.add(Box.createVerticalGlue());
        matrixPanel.add(createMatrixPanel(matrixA, "A"));
        matrixPanel.add(Box.createVerticalGlue());
        
        // Einzelnes Eingabefeld
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(BACKGROUND_COLOR);
        
        inputFields = new JTextField[1][1];
        inputFields[0][0] = new JTextField(6);
        styleTextField(inputFields[0][0]);
        
        inputPanel.add(new JLabel("<html><font color='white' size='+1'>det(A) = </font></html>"));
        inputPanel.add(inputFields[0][0]);
        
        inputArea.add(Box.createVerticalGlue());
        inputArea.add(inputPanel);
        inputArea.add(Box.createVerticalGlue());
    }
    
    private void generateElementQuestion() {
        int rows = 2 + random.nextInt(2);
        int cols = 2 + random.nextInt(2);
        matrixA = generateMatrix(rows, cols, 1, 15);
        
        int targetRow = random.nextInt(rows);
        int targetCol = random.nextInt(cols);
        
        expectedResult = new double[][]{{matrixA[targetRow][targetCol]}};
        
        currentOperation = "Element";
        questionLabel.setText(String.format("Was ist a%d%d?", targetRow + 1, targetCol + 1));
        
        matrixPanel.add(Box.createVerticalGlue());
        matrixPanel.add(createMatrixPanel(matrixA, "A"));
        matrixPanel.add(Box.createVerticalGlue());
        
        // Einzelnes Eingabefeld
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(BACKGROUND_COLOR);
        
        inputFields = new JTextField[1][1];
        inputFields[0][0] = new JTextField(6);
        styleTextField(inputFields[0][0]);
        
        inputPanel.add(new JLabel(String.format("<html><font color='white' size='+1'>a%d%d = </font></html>",
            targetRow + 1, targetCol + 1)));
        inputPanel.add(inputFields[0][0]);
        
        inputArea.add(Box.createVerticalGlue());
        inputArea.add(inputPanel);
        inputArea.add(Box.createVerticalGlue());
    }
    
    private void displayMatrixOperation(String operator) {
        JPanel displayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        displayPanel.setBackground(BACKGROUND_COLOR);
        
        displayPanel.add(createMatrixPanel(matrixA, "A"));
        
        JLabel opLabel = new JLabel(operator);
        opLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        opLabel.setForeground(ACCENT_COLOR);
        displayPanel.add(opLabel);
        
        displayPanel.add(createMatrixPanel(matrixB, "B"));
        
        matrixPanel.add(Box.createVerticalGlue());
        matrixPanel.add(displayPanel);
        matrixPanel.add(Box.createVerticalGlue());
    }
    
    private JPanel createMatrixPanel(double[][] matrix, String label) {
        JPanel panel = new JPanel(new BorderLayout(3, 3));
        panel.setBackground(BACKGROUND_COLOR);
        
        JLabel matrixLabel = new JLabel(label, SwingConstants.CENTER);
        matrixLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        matrixLabel.setForeground(HIGHLIGHT_COLOR);
        panel.add(matrixLabel, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridLayout(matrix.length, matrix[0].length, 2, 2));
        gridPanel.setBackground(BACKGROUND_COLOR);
        gridPanel.setBorder(BorderFactory.createCompoundBorder(
            new GamePanel.MatrixBracketBorder(ACCENT_COLOR),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                JLabel cellLabel = new JLabel(formatValue(matrix[i][j]), SwingConstants.CENTER);
                cellLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
                cellLabel.setForeground(TEXT_COLOR);
                cellLabel.setOpaque(true);
                cellLabel.setBackground(MATRIX_CELL_COLOR);
                cellLabel.setPreferredSize(new Dimension(35, 28));
                gridPanel.add(cellLabel);
            }
        }
        
        panel.add(gridPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private void createInputGrid(int rows, int cols) {
        inputFields = new JTextField[rows][cols];
        
        JPanel inputWrapper = new JPanel(new BorderLayout(3, 3));
        inputWrapper.setBackground(BACKGROUND_COLOR);
        
        JLabel resultLabel = new JLabel("= ?", SwingConstants.CENTER);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        resultLabel.setForeground(SUCCESS_COLOR);
        inputWrapper.add(resultLabel, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 2, 2));
        gridPanel.setBackground(BACKGROUND_COLOR);
        gridPanel.setBorder(BorderFactory.createCompoundBorder(
            new GamePanel.MatrixBracketBorder(SUCCESS_COLOR),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JTextField field = new JTextField(3);
                styleTextField(field);
                field.setPreferredSize(new Dimension(35, 28));
                
                // Enter zum Absenden
                final int fi = i, fj = j;
                field.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            // Zum nächsten Feld oder Absenden
                            if (fi == rows - 1 && fj == cols - 1) {
                                checkAnswer();
                            } else if (fj < cols - 1) {
                                inputFields[fi][fj + 1].requestFocus();
                            } else {
                                inputFields[fi + 1][0].requestFocus();
                            }
                        }
                    }
                });
                
                inputFields[i][j] = field;
                gridPanel.add(field);
            }
        }
        
        inputWrapper.add(gridPanel, BorderLayout.CENTER);
        
        inputArea.add(Box.createVerticalGlue());
        inputArea.add(inputWrapper);
        inputArea.add(Box.createVerticalGlue());
        
        // Fokus auf erstes Feld
        SwingUtilities.invokeLater(() -> inputFields[0][0].requestFocus());
    }
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Monospaced", Font.BOLD, 14));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBackground(MATRIX_CELL_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
    }
    
    private double[][] generateMatrix(int rows, int cols, int min, int max) {
        double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = min + random.nextInt(max - min + 1);
            }
        }
        return matrix;
    }
    
    private String formatValue(double value) {
        if (value == (int) value) {
            return String.valueOf((int) value);
        }
        return String.format("%.1f", value);
    }
    
    /**
     * Prüft die aktuelle Antwort
     */
    private void checkAnswer() {
        if (!gameActive) return;
        
        double[][] userResult;
        try {
            userResult = new double[inputFields.length][inputFields[0].length];
            for (int i = 0; i < inputFields.length; i++) {
                for (int j = 0; j < inputFields[i].length; j++) {
                    String text = inputFields[i][j].getText().trim().replace(",", ".");
                    userResult[i][j] = Double.parseDouble(text);
                }
            }
        } catch (NumberFormatException e) {
            feedbackArea.setForeground(ERROR_COLOR);
            feedbackArea.setText("⚠️ Bitte gültige Zahlen eingeben!");
            return;
        }
        
        questionsAnswered++;
        boolean correct = compareMatrices(userResult, expectedResult);
        
        if (correct) {
            correctAnswers++;
            combo++;
            
            // Punkte basierend auf Schwierigkeit und Kombo
            int basePoints = switch (currentQuestionType) {
                case 0, 1 -> 10;  // Addition/Subtraktion
                case 2 -> 12;     // Skalar
                case 3 -> 15;     // Transposition
                case 4 -> 20;     // Determinante
                case 5 -> 8;      // Element
                default -> 10;
            };
            
            int comboBonus = Math.min(combo - 1, 5) * 5;
            int points = basePoints + comboBonus;
            
            challengeScore += points;
            
            feedbackArea.setForeground(SUCCESS_COLOR);
            feedbackArea.setText(String.format("✅ Richtig! +%d Punkte (Kombo x%d)", points, combo));
            
            // Update Labels
            scoreLabel.setText("⭐ " + challengeScore);
            comboLabel.setText("🔥 x" + combo);
            
            // Kurze Pause, dann nächste Frage
            Timer nextTimer = new Timer(500, e -> generateQuestion());
            nextTimer.setRepeats(false);
            nextTimer.start();
            
        } else {
            combo = 0;
            
            feedbackArea.setForeground(ERROR_COLOR);
            StringBuilder sb = new StringBuilder("❌ Falsch! Richtig: ");
            if (expectedResult.length == 1 && expectedResult[0].length == 1) {
                sb.append(formatValue(expectedResult[0][0]));
            } else {
                sb.append("Matrix");
            }
            feedbackArea.setText(sb.toString());
            
            comboLabel.setText("🔥 x0");
            
            // Markiere Fehler
            for (int i = 0; i < inputFields.length; i++) {
                for (int j = 0; j < inputFields[i].length; j++) {
                    try {
                        double val = Double.parseDouble(inputFields[i][j].getText().trim().replace(",", "."));
                        inputFields[i][j].setBackground(
                            Math.abs(val - expectedResult[i][j]) < 0.001 ? 
                            SUCCESS_COLOR.darker() : ERROR_COLOR.darker()
                        );
                    } catch (NumberFormatException e) {
                        inputFields[i][j].setBackground(ERROR_COLOR.darker());
                    }
                }
            }
            
            // Kurze Pause, dann nächste Frage
            Timer nextTimer = new Timer(1500, e -> generateQuestion());
            nextTimer.setRepeats(false);
            nextTimer.start();
        }
    }
    
    private boolean compareMatrices(double[][] a, double[][] b) {
        if (a.length != b.length || a[0].length != b[0].length) return false;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (Math.abs(a[i][j] - b[i][j]) > 0.001) return false;
            }
        }
        return true;
    }
    
    /**
     * Beendet das Spiel und zeigt die Ergebnisse
     */
    private void endGame() {
        gameActive = false;
        gameTimer.stop();
        
        // Punkte zum Gesamtscore hinzufügen
        game.addScore(challengeScore);
        
        double accuracy = questionsAnswered > 0 ? 
            (double) correctAnswers / questionsAnswered * 100 : 0;
        
        String grade = accuracy >= 90 ? "🏆 Meister!" :
                      accuracy >= 70 ? "⭐ Großartig!" :
                      accuracy >= 50 ? "👍 Gut!" : "💪 Weiter üben!";
        
        String message = String.format(
            "ZEIT ABGELAUFEN!\n\n" +
            "━━━━━━━━━━━━━━━━━━━━\n" +
            "Punkte: %d\n" +
            "Aufgaben: %d\n" +
            "Richtig: %d (%.0f%%)\n" +
            "━━━━━━━━━━━━━━━━━━━━\n\n" +
            "%s",
            challengeScore, questionsAnswered, correctAnswers, accuracy, grade
        );
        
        JOptionPane.showMessageDialog(this, message, "Herausforderung beendet!", 
            JOptionPane.INFORMATION_MESSAGE);
        
        game.showMainMenu();
    }
}
