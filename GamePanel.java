import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * GamePanel - Das zentrale Spielfeld, wo die eigentlichen Übungen stattfinden.
 * 
 * Dieses Panel generiert Aufgaben basierend auf dem ausgewählten Modul:
 * - Zeigt die Aufgabenstellung visuell mit Matrizen an
 * - Ermöglicht Eingabe durch klickbare Zellen oder Textfelder
 * - Gibt sofortiges Feedback (richtig/falsch)
 * - Verfolgt Punkte und Streak
 * - Bietet Erklärungen bei falschen Antworten
 */
public class GamePanel extends JPanel {
    
    // Farbdefinitionen
    private static final Color BACKGROUND_COLOR = new Color(25, 25, 35);
    private static final Color PANEL_COLOR = new Color(40, 44, 52);
    private static final Color ACCENT_COLOR = new Color(97, 175, 239);
    private static final Color SUCCESS_COLOR = new Color(152, 195, 121);
    private static final Color ERROR_COLOR = new Color(224, 108, 117);
    private static final Color TEXT_COLOR = new Color(220, 223, 228);
    private static final Color HIGHLIGHT_COLOR = new Color(229, 192, 123);
    private static final Color MATRIX_CELL_COLOR = new Color(55, 60, 72);
    
    private MatrixGame game;
    private int moduleIndex;
    private Random random = new Random();
    
    // Aufgaben-Zustand
    private double[][] matrixA;
    private double[][] matrixB;
    private double[][] expectedResult;
    private double[][] userResult;
    private double scalar;
    private int currentQuestion = 0;
    private int totalQuestions = 5;
    private int correctAnswers = 0;
    
    // UI-Komponenten
    private JLabel questionLabel;
    private JLabel scoreLabel;
    private JLabel streakLabel;
    private JPanel matrixDisplayPanel;
    private JPanel inputPanel;
    private JButton submitButton;
    private JButton nextButton;
    private JButton hintButton;
    private JTextArea feedbackArea;
    private JTextField[][] inputFields;
    
    // Modul-Namen
    private final String[] MODULE_NAMES = {
        "Grundlagen", "Addition", "Subtraktion", "Skalar-Multiplikation",
        "Matrix-Multiplikation", "Transposition", "Determinante", 
        "Inverse Matrix", "Spezial-Matrizen", "Freies Üben"
    };
    
    public GamePanel(MatrixGame game, int moduleIndex) {
        this.game = game;
        this.moduleIndex = moduleIndex;
        
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        
        initializeUI();
        generateQuestion();
    }
    
    private void initializeUI() {
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Hauptbereich mit Matrizen und Eingabe
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Aufgaben-Anzeige
        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBackground(PANEL_COLOR);
        questionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 65, 77)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        questionLabel.setForeground(TEXT_COLOR);
        questionPanel.add(questionLabel, BorderLayout.CENTER);
        
        hintButton = new JButton("💡 Hinweis");
        styleButton(hintButton, HIGHLIGHT_COLOR);
        hintButton.addActionListener(e -> showHint());
        questionPanel.add(hintButton, BorderLayout.EAST);
        
        mainContent.add(questionPanel, BorderLayout.NORTH);
        
        // Matrix-Anzeige und Eingabe
        JPanel workArea = new JPanel(new GridLayout(1, 2, 30, 0));
        workArea.setBackground(BACKGROUND_COLOR);
        
        // Linke Seite: Matrizen anzeigen
        matrixDisplayPanel = new JPanel();
        matrixDisplayPanel.setBackground(BACKGROUND_COLOR);
        matrixDisplayPanel.setLayout(new BoxLayout(matrixDisplayPanel, BoxLayout.Y_AXIS));
        
        // Rechte Seite: Eingabebereich
        inputPanel = new JPanel();
        inputPanel.setBackground(BACKGROUND_COLOR);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        
        workArea.add(matrixDisplayPanel);
        workArea.add(inputPanel);
        
        mainContent.add(workArea, BorderLayout.CENTER);
        
        // Feedback-Bereich
        feedbackArea = new JTextArea(3, 40);
        feedbackArea.setEditable(false);
        feedbackArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        feedbackArea.setBackground(PANEL_COLOR);
        feedbackArea.setForeground(TEXT_COLOR);
        feedbackArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        
        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
        feedbackScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 65, 77)),
            "Feedback",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            TEXT_COLOR
        ));
        feedbackScroll.setPreferredSize(new Dimension(0, 100));
        
        mainContent.add(feedbackScroll, BorderLayout.SOUTH);
        
        add(mainContent, BorderLayout.CENTER);
        
        // Buttons unten
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Zurück-Button
        JButton backBtn = new JButton("← Module");
        backBtn.setForeground(TEXT_COLOR);
        backBtn.setBackground(PANEL_COLOR);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> game.showMainMenu());
        
        // Modul-Titel
        JLabel titleLabel = new JLabel(MODULE_NAMES[moduleIndex] + " - Frage " + 
                                       (currentQuestion + 1) + "/" + totalQuestions);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Punkte und Streak
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setBackground(PANEL_COLOR);
        
        streakLabel = new JLabel("🔥 Streak: " + game.getStreak());
        streakLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        streakLabel.setForeground(ERROR_COLOR);
        
        scoreLabel = new JLabel("⭐ Punkte: " + game.getScore());
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        scoreLabel.setForeground(HIGHLIGHT_COLOR);
        
        statsPanel.add(streakLabel);
        statsPanel.add(scoreLabel);
        
        panel.add(backBtn, BorderLayout.WEST);
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(statsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(BACKGROUND_COLOR);
        
        submitButton = new JButton("✓ Prüfen");
        styleButton(submitButton, ACCENT_COLOR);
        submitButton.addActionListener(e -> checkAnswer());
        
        nextButton = new JButton("Weiter →");
        styleButton(nextButton, SUCCESS_COLOR);
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> {
            currentQuestion++;
            if (currentQuestion < totalQuestions) {
                generateQuestion();
            } else {
                showResults();
            }
        });
        
        panel.add(submitButton);
        panel.add(nextButton);
        
        return panel;
    }
    
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setForeground(TEXT_COLOR);
        button.setBackground(color);
        button.setPreferredSize(new Dimension(140, 45));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }
    
    /**
     * Generiert eine neue Frage basierend auf dem aktuellen Modul
     */
    private void generateQuestion() {
        // Reset UI
        nextButton.setEnabled(false);
        submitButton.setEnabled(true);
        feedbackArea.setText("");
        
        // Generiere Aufgabe basierend auf Modul
        switch (moduleIndex) {
            case 0 -> generateBasicsQuestion();
            case 1 -> generateAdditionQuestion();
            case 2 -> generateSubtractionQuestion();
            case 3 -> generateScalarMultiplicationQuestion();
            case 4 -> generateMatrixMultiplicationQuestion();
            case 5 -> generateTranspositionQuestion();
            case 6 -> generateDeterminantQuestion();
            case 7 -> generateInverseQuestion();
            case 8 -> generateSpecialMatrixQuestion();
            case 9 -> generateRandomQuestion();
        }
        
        // Update Header
        updateHeader();
    }
    
    private void updateHeader() {
        Component[] comps = ((JPanel)getComponent(0)).getComponents();
        for (Component c : comps) {
            if (c instanceof JLabel label && label.getHorizontalAlignment() == SwingConstants.CENTER) {
                label.setText(MODULE_NAMES[moduleIndex] + " - Frage " + 
                             (currentQuestion + 1) + "/" + totalQuestions);
            }
        }
    }
    
    // ==================== Aufgaben-Generatoren ====================
    
    private void generateBasicsQuestion() {
        int rows = 2 + random.nextInt(2);
        int cols = 2 + random.nextInt(2);
        matrixA = generateRandomMatrix(rows, cols, 1, 10);
        
        // Verschiedene Fragetypen
        int questionType = random.nextInt(4);
        
        matrixDisplayPanel.removeAll();
        inputPanel.removeAll();
        
        switch (questionType) {
            case 0 -> {
                // Frage nach Dimension
                questionLabel.setText("Welche Dimension hat diese Matrix?");
                matrixDisplayPanel.add(createMatrixPanel(matrixA, "A"));
                
                JPanel dimInput = new JPanel(new FlowLayout(FlowLayout.CENTER));
                dimInput.setBackground(BACKGROUND_COLOR);
                
                JTextField rowField = new JTextField(3);
                JTextField colField = new JTextField(3);
                styleTextField(rowField);
                styleTextField(colField);
                
                dimInput.add(new JLabel("<html><font color='white'>Zeilen:</font></html>"));
                dimInput.add(rowField);
                dimInput.add(new JLabel("<html><font color='white'> × Spalten:</font></html>"));
                dimInput.add(colField);
                
                inputPanel.add(Box.createVerticalGlue());
                inputPanel.add(dimInput);
                inputPanel.add(Box.createVerticalGlue());
                
                // Speichere erwartete Antwort
                expectedResult = new double[][]{{rows, cols}};
                inputFields = new JTextField[][]{{rowField, colField}};
            }
            case 1 -> {
                // Frage nach einem bestimmten Element
                int targetRow = random.nextInt(rows);
                int targetCol = random.nextInt(cols);
                questionLabel.setText(String.format("Was ist der Wert von a%d%d (Zeile %d, Spalte %d)?",
                    targetRow + 1, targetCol + 1, targetRow + 1, targetCol + 1));
                
                matrixDisplayPanel.add(createMatrixPanel(matrixA, "A"));
                
                JPanel elementInput = new JPanel(new FlowLayout(FlowLayout.CENTER));
                elementInput.setBackground(BACKGROUND_COLOR);
                
                JTextField valueField = new JTextField(5);
                styleTextField(valueField);
                
                elementInput.add(new JLabel(String.format("<html><font color='white'>a%d%d = </font></html>", 
                    targetRow + 1, targetCol + 1)));
                elementInput.add(valueField);
                
                inputPanel.add(Box.createVerticalGlue());
                inputPanel.add(elementInput);
                inputPanel.add(Box.createVerticalGlue());
                
                expectedResult = new double[][]{{matrixA[targetRow][targetCol]}};
                inputFields = new JTextField[][]{{valueField}};
            }
            case 2 -> {
                // Frage: Ist es quadratisch?
                questionLabel.setText("Ist diese Matrix quadratisch?");
                matrixDisplayPanel.add(createMatrixPanel(matrixA, "A"));
                
                JPanel boolInput = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
                boolInput.setBackground(BACKGROUND_COLOR);
                
                JButton yesBtn = new JButton("Ja ✓");
                JButton noBtn = new JButton("Nein ✗");
                styleButton(yesBtn, SUCCESS_COLOR);
                styleButton(noBtn, ERROR_COLOR);
                
                yesBtn.addActionListener(e -> {
                    userResult = new double[][]{{1}};
                    checkAnswer();
                });
                noBtn.addActionListener(e -> {
                    userResult = new double[][]{{0}};
                    checkAnswer();
                });
                
                boolInput.add(yesBtn);
                boolInput.add(noBtn);
                
                inputPanel.add(Box.createVerticalGlue());
                inputPanel.add(boolInput);
                inputPanel.add(Box.createVerticalGlue());
                
                expectedResult = new double[][]{{rows == cols ? 1 : 0}};
                inputFields = null; // Buttons statt Textfelder
            }
            default -> {
                // Zähle Elemente
                questionLabel.setText("Wie viele Elemente enthält diese Matrix?");
                matrixDisplayPanel.add(createMatrixPanel(matrixA, "A"));
                
                JPanel countInput = new JPanel(new FlowLayout(FlowLayout.CENTER));
                countInput.setBackground(BACKGROUND_COLOR);
                
                JTextField countField = new JTextField(5);
                styleTextField(countField);
                
                countInput.add(new JLabel("<html><font color='white'>Anzahl Elemente: </font></html>"));
                countInput.add(countField);
                
                inputPanel.add(Box.createVerticalGlue());
                inputPanel.add(countInput);
                inputPanel.add(Box.createVerticalGlue());
                
                expectedResult = new double[][]{{rows * cols}};
                inputFields = new JTextField[][]{{countField}};
            }
        }
        
        matrixDisplayPanel.revalidate();
        matrixDisplayPanel.repaint();
        inputPanel.revalidate();
        inputPanel.repaint();
    }
    
    private void generateAdditionQuestion() {
        int rows = 2 + random.nextInt(2);
        int cols = 2 + random.nextInt(2);
        matrixA = generateRandomMatrix(rows, cols, 1, 9);
        matrixB = generateRandomMatrix(rows, cols, 1, 9);
        
        questionLabel.setText("Berechne A + B");
        
        // Berechne erwartetes Ergebnis
        expectedResult = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                expectedResult[i][j] = matrixA[i][j] + matrixB[i][j];
            }
        }
        
        setupMatrixOperationUI("+");
    }
    
    private void generateSubtractionQuestion() {
        int rows = 2 + random.nextInt(2);
        int cols = 2 + random.nextInt(2);
        matrixA = generateRandomMatrix(rows, cols, 1, 15);
        matrixB = generateRandomMatrix(rows, cols, 1, 10);
        
        questionLabel.setText("Berechne A - B");
        
        expectedResult = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                expectedResult[i][j] = matrixA[i][j] - matrixB[i][j];
            }
        }
        
        setupMatrixOperationUI("-");
    }
    
    private void generateScalarMultiplicationQuestion() {
        int rows = 2 + random.nextInt(2);
        int cols = 2 + random.nextInt(2);
        matrixA = generateRandomMatrix(rows, cols, 1, 10);
        scalar = 2 + random.nextInt(5);
        
        questionLabel.setText(String.format("Berechne %.0f · A", scalar));
        
        expectedResult = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                expectedResult[i][j] = scalar * matrixA[i][j];
            }
        }
        
        matrixDisplayPanel.removeAll();
        inputPanel.removeAll();
        
        // Zeige Skalar und Matrix
        JPanel displayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        displayPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel scalarLabel = new JLabel(String.format("%.0f  ×", scalar));
        scalarLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        scalarLabel.setForeground(HIGHLIGHT_COLOR);
        
        displayPanel.add(scalarLabel);
        displayPanel.add(createMatrixPanel(matrixA, "A"));
        
        matrixDisplayPanel.add(Box.createVerticalGlue());
        matrixDisplayPanel.add(displayPanel);
        matrixDisplayPanel.add(Box.createVerticalGlue());
        
        // Eingabefelder
        createInputMatrix(rows, cols);
        
        matrixDisplayPanel.revalidate();
        inputPanel.revalidate();
    }
    
    private void generateMatrixMultiplicationQuestion() {
        // Für Multiplikation: A(m×n) * B(n×p) = C(m×p)
        int m = 2;
        int n = 2;
        int p = 2;
        
        matrixA = generateRandomMatrix(m, n, 1, 5);
        matrixB = generateRandomMatrix(n, p, 1, 5);
        
        questionLabel.setText("Berechne A · B (Zeile × Spalte!)");
        
        // Matrix-Multiplikation berechnen
        expectedResult = new double[m][p];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                expectedResult[i][j] = 0;
                for (int k = 0; k < n; k++) {
                    expectedResult[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        
        setupMatrixOperationUI("·");
    }
    
    private void generateTranspositionQuestion() {
        int rows = 2 + random.nextInt(2);
        int cols = 2 + random.nextInt(2);
        matrixA = generateRandomMatrix(rows, cols, 1, 10);
        
        questionLabel.setText("Berechne Aᵀ (Transponierte von A)");
        
        // Transponierte berechnen (Zeilen und Spalten vertauschen)
        expectedResult = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                expectedResult[j][i] = matrixA[i][j];
            }
        }
        
        matrixDisplayPanel.removeAll();
        inputPanel.removeAll();
        
        matrixDisplayPanel.add(Box.createVerticalGlue());
        matrixDisplayPanel.add(createMatrixPanel(matrixA, "A"));
        matrixDisplayPanel.add(Box.createVerticalGlue());
        
        createInputMatrix(cols, rows);
        
        matrixDisplayPanel.revalidate();
        inputPanel.revalidate();
    }
    
    private void generateDeterminantQuestion() {
        // 2x2 oder 3x3 Matrix
        int size = random.nextBoolean() ? 2 : 3;
        matrixA = generateRandomMatrix(size, size, -5, 10);
        
        questionLabel.setText("Berechne die Determinante det(A)");
        
        // Determinante berechnen
        double det;
        if (size == 2) {
            det = matrixA[0][0] * matrixA[1][1] - matrixA[0][1] * matrixA[1][0];
        } else {
            // 3x3 Determinante (Regel von Sarrus)
            det = matrixA[0][0] * matrixA[1][1] * matrixA[2][2]
                + matrixA[0][1] * matrixA[1][2] * matrixA[2][0]
                + matrixA[0][2] * matrixA[1][0] * matrixA[2][1]
                - matrixA[0][2] * matrixA[1][1] * matrixA[2][0]
                - matrixA[0][0] * matrixA[1][2] * matrixA[2][1]
                - matrixA[0][1] * matrixA[1][0] * matrixA[2][2];
        }
        
        expectedResult = new double[][]{{det}};
        
        matrixDisplayPanel.removeAll();
        inputPanel.removeAll();
        
        matrixDisplayPanel.add(Box.createVerticalGlue());
        matrixDisplayPanel.add(createMatrixPanel(matrixA, "A"));
        matrixDisplayPanel.add(Box.createVerticalGlue());
        
        // Einzelnes Eingabefeld
        JPanel detInput = new JPanel(new FlowLayout(FlowLayout.CENTER));
        detInput.setBackground(BACKGROUND_COLOR);
        
        JTextField detField = new JTextField(8);
        styleTextField(detField);
        
        detInput.add(new JLabel("<html><font color='white' size='+1'>det(A) = </font></html>"));
        detInput.add(detField);
        
        inputPanel.add(Box.createVerticalGlue());
        inputPanel.add(detInput);
        
        // Formel-Hinweis
        if (size == 2) {
            JLabel formulaLabel = new JLabel("<html><font color='#61AFEF' size='-1'>" +
                "Formel: ad - bc für [[a,b],[c,d]]</font></html>");
            formulaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            inputPanel.add(Box.createVerticalStrut(20));
            inputPanel.add(formulaLabel);
        }
        
        inputPanel.add(Box.createVerticalGlue());
        
        inputFields = new JTextField[][]{{detField}};
        
        matrixDisplayPanel.revalidate();
        inputPanel.revalidate();
    }
    
    private void generateInverseQuestion() {
        // Einfache 2x2 Inverse mit ganzzahligen Ergebnissen
        // Generiere Matrix mit det != 0 und schönen Werten
        do {
            matrixA = generateRandomMatrix(2, 2, 1, 5);
        } while (Math.abs(matrixA[0][0] * matrixA[1][1] - matrixA[0][1] * matrixA[1][0]) < 0.001);
        
        double det = matrixA[0][0] * matrixA[1][1] - matrixA[0][1] * matrixA[1][0];
        
        questionLabel.setText("Berechne A⁻¹ (gerundet auf ganze Zahlen)");
        
        // Inverse berechnen: 1/det * [[d, -b], [-c, a]]
        expectedResult = new double[2][2];
        expectedResult[0][0] = Math.round(matrixA[1][1] / det);
        expectedResult[0][1] = Math.round(-matrixA[0][1] / det);
        expectedResult[1][0] = Math.round(-matrixA[1][0] / det);
        expectedResult[1][1] = Math.round(matrixA[0][0] / det);
        
        matrixDisplayPanel.removeAll();
        inputPanel.removeAll();
        
        matrixDisplayPanel.add(Box.createVerticalGlue());
        matrixDisplayPanel.add(createMatrixPanel(matrixA, "A"));
        
        // Formel-Hinweis
        JLabel formulaLabel = new JLabel("<html><font color='#61AFEF' size='-1'>" +
            "Formel: A⁻¹ = (1/det) · [[d,-b],[-c,a]]</font></html>");
        matrixDisplayPanel.add(Box.createVerticalStrut(15));
        matrixDisplayPanel.add(formulaLabel);
        matrixDisplayPanel.add(Box.createVerticalGlue());
        
        createInputMatrix(2, 2);
        
        matrixDisplayPanel.revalidate();
        inputPanel.revalidate();
    }
    
    private void generateSpecialMatrixQuestion() {
        int size = 2 + random.nextInt(2);
        int questionType = random.nextInt(4);
        
        matrixDisplayPanel.removeAll();
        inputPanel.removeAll();
        
        switch (questionType) {
            case 0 -> {
                // Erkenne den Typ
                int matrixType = random.nextInt(4);
                switch (matrixType) {
                    case 0 -> matrixA = createIdentityMatrix(size);
                    case 1 -> matrixA = new double[size][size]; // Nullmatrix
                    case 2 -> matrixA = createDiagonalMatrix(size);
                    case 3 -> matrixA = createSymmetricMatrix(size);
                }
                
                questionLabel.setText("Welcher Typ ist diese Matrix?");
                matrixDisplayPanel.add(createMatrixPanel(matrixA, "A"));
                
                JPanel typePanel = new JPanel(new GridLayout(2, 2, 10, 10));
                typePanel.setBackground(BACKGROUND_COLOR);
                
                String[] types = {"Einheitsmatrix", "Nullmatrix", "Diagonalmatrix", "Symmetrisch"};
                
                for (int i = 0; i < 4; i++) {
                    final int type = i;
                    JButton btn = new JButton(types[i]);
                    styleButton(btn, PANEL_COLOR);
                    btn.addActionListener(e -> {
                        userResult = new double[][]{{type}};
                        checkAnswer();
                    });
                    typePanel.add(btn);
                }
                
                inputPanel.add(Box.createVerticalGlue());
                inputPanel.add(typePanel);
                inputPanel.add(Box.createVerticalGlue());
                
                expectedResult = new double[][]{{matrixType}};
                inputFields = null;
            }
            case 1 -> {
                // Erstelle Einheitsmatrix
                questionLabel.setText(String.format("Gib die %dx%d Einheitsmatrix ein", size, size));
                expectedResult = createIdentityMatrix(size);
                createInputMatrix(size, size);
            }
            case 2 -> {
                // Ergänze zur symmetrischen Matrix
                matrixA = createSymmetricMatrix(size);
                // Verstecke einige Elemente
                double[][] display = copyMatrix(matrixA);
                display[0][size-1] = Double.NaN;
                if (size > 2) display[1][size-1] = Double.NaN;
                
                questionLabel.setText("Ergänze die fehlenden Werte (symmetrische Matrix)");
                // Vereinfacht: Zeige einfach die Matrix
                matrixDisplayPanel.add(createMatrixPanel(matrixA, "A"));
                
                JPanel symInput = new JPanel(new FlowLayout(FlowLayout.CENTER));
                symInput.setBackground(BACKGROUND_COLOR);
                JTextField symField = new JTextField(5);
                styleTextField(symField);
                symInput.add(new JLabel(String.format("<html><font color='white'>a%d%d = </font></html>", 
                    1, size)));
                symInput.add(symField);
                
                inputPanel.add(Box.createVerticalGlue());
                inputPanel.add(symInput);
                inputPanel.add(Box.createVerticalGlue());
                
                expectedResult = new double[][]{{matrixA[0][size-1]}};
                inputFields = new JTextField[][]{{symField}};
            }
            default -> {
                // Ist sie diagonal?
                boolean isDiagonal = random.nextBoolean();
                matrixA = isDiagonal ? createDiagonalMatrix(size) : generateRandomMatrix(size, size, 1, 10);
                
                questionLabel.setText("Ist diese Matrix eine Diagonalmatrix?");
                matrixDisplayPanel.add(createMatrixPanel(matrixA, "A"));
                
                JPanel boolInput = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
                boolInput.setBackground(BACKGROUND_COLOR);
                
                JButton yesBtn = new JButton("Ja ✓");
                JButton noBtn = new JButton("Nein ✗");
                styleButton(yesBtn, SUCCESS_COLOR);
                styleButton(noBtn, ERROR_COLOR);
                
                yesBtn.addActionListener(e -> {
                    userResult = new double[][]{{1}};
                    checkAnswer();
                });
                noBtn.addActionListener(e -> {
                    userResult = new double[][]{{0}};
                    checkAnswer();
                });
                
                boolInput.add(yesBtn);
                boolInput.add(noBtn);
                
                inputPanel.add(Box.createVerticalGlue());
                inputPanel.add(boolInput);
                inputPanel.add(Box.createVerticalGlue());
                
                expectedResult = new double[][]{{isDiagonal ? 1 : 0}};
                inputFields = null;
            }
        }
        
        matrixDisplayPanel.revalidate();
        inputPanel.revalidate();
    }
    
    private void generateRandomQuestion() {
        // Wähle zufällig ein Modul (außer diesem und Grundlagen)
        int randomModule = 1 + random.nextInt(8);
        switch (randomModule) {
            case 1 -> generateAdditionQuestion();
            case 2 -> generateSubtractionQuestion();
            case 3 -> generateScalarMultiplicationQuestion();
            case 4 -> generateMatrixMultiplicationQuestion();
            case 5 -> generateTranspositionQuestion();
            case 6 -> generateDeterminantQuestion();
            case 7 -> generateInverseQuestion();
            default -> generateSpecialMatrixQuestion();
        }
    }
    
    // ==================== Hilfsmethoden ====================
    
    private void setupMatrixOperationUI(String operator) {
        matrixDisplayPanel.removeAll();
        inputPanel.removeAll();
        
        JPanel displayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        displayPanel.setBackground(BACKGROUND_COLOR);
        
        displayPanel.add(createMatrixPanel(matrixA, "A"));
        
        JLabel opLabel = new JLabel(operator);
        opLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        opLabel.setForeground(ACCENT_COLOR);
        displayPanel.add(opLabel);
        
        displayPanel.add(createMatrixPanel(matrixB, "B"));
        
        JLabel eqLabel = new JLabel("=");
        eqLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        eqLabel.setForeground(TEXT_COLOR);
        displayPanel.add(eqLabel);
        
        matrixDisplayPanel.add(Box.createVerticalGlue());
        matrixDisplayPanel.add(displayPanel);
        matrixDisplayPanel.add(Box.createVerticalGlue());
        
        createInputMatrix(expectedResult.length, expectedResult[0].length);
        
        matrixDisplayPanel.revalidate();
        inputPanel.revalidate();
    }
    
    private JPanel createMatrixPanel(double[][] matrix, String label) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Label
        JLabel matrixLabel = new JLabel(label, SwingConstants.CENTER);
        matrixLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        matrixLabel.setForeground(HIGHLIGHT_COLOR);
        panel.add(matrixLabel, BorderLayout.NORTH);
        
        // Matrix-Zellen
        JPanel gridPanel = new JPanel(new GridLayout(matrix.length, matrix[0].length, 3, 3));
        gridPanel.setBackground(BACKGROUND_COLOR);
        gridPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatrixBracketBorder(ACCENT_COLOR),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                JLabel cellLabel = new JLabel(formatValue(matrix[i][j]), SwingConstants.CENTER);
                cellLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
                cellLabel.setForeground(TEXT_COLOR);
                cellLabel.setOpaque(true);
                cellLabel.setBackground(MATRIX_CELL_COLOR);
                cellLabel.setPreferredSize(new Dimension(45, 35));
                cellLabel.setBorder(BorderFactory.createLineBorder(new Color(70, 75, 87)));
                gridPanel.add(cellLabel);
            }
        }
        
        panel.add(gridPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void createInputMatrix(int rows, int cols) {
        inputFields = new JTextField[rows][cols];
        
        JPanel inputWrapper = new JPanel(new BorderLayout(5, 5));
        inputWrapper.setBackground(BACKGROUND_COLOR);
        
        JLabel resultLabel = new JLabel("Ergebnis:", SwingConstants.CENTER);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        resultLabel.setForeground(SUCCESS_COLOR);
        inputWrapper.add(resultLabel, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 3, 3));
        gridPanel.setBackground(BACKGROUND_COLOR);
        gridPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatrixBracketBorder(SUCCESS_COLOR),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JTextField field = new JTextField(4);
                styleTextField(field);
                inputFields[i][j] = field;
                gridPanel.add(field);
            }
        }
        
        inputWrapper.add(gridPanel, BorderLayout.CENTER);
        
        inputPanel.add(Box.createVerticalGlue());
        inputPanel.add(inputWrapper);
        inputPanel.add(Box.createVerticalGlue());
    }
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Monospaced", Font.BOLD, 16));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBackground(MATRIX_CELL_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
    
    private double[][] generateRandomMatrix(int rows, int cols, int min, int max) {
        double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = min + random.nextInt(max - min + 1);
            }
        }
        return matrix;
    }
    
    private double[][] createIdentityMatrix(int size) {
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            matrix[i][i] = 1;
        }
        return matrix;
    }
    
    private double[][] createDiagonalMatrix(int size) {
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            matrix[i][i] = 1 + random.nextInt(9);
        }
        return matrix;
    }
    
    private double[][] createSymmetricMatrix(int size) {
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                matrix[i][j] = 1 + random.nextInt(9);
                matrix[j][i] = matrix[i][j];
            }
        }
        return matrix;
    }
    
    private double[][] copyMatrix(double[][] matrix) {
        double[][] copy = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, matrix[i].length);
        }
        return copy;
    }
    
    private String formatValue(double value) {
        if (Double.isNaN(value)) return "?";
        if (value == (int) value) {
            return String.valueOf((int) value);
        }
        return String.format("%.1f", value);
    }
    
    // ==================== Antwort-Prüfung ====================
    
    private void checkAnswer() {
        // Lese Benutzereingabe
        if (inputFields != null) {
            userResult = new double[inputFields.length][inputFields[0].length];
            try {
                for (int i = 0; i < inputFields.length; i++) {
                    for (int j = 0; j < inputFields[i].length; j++) {
                        String text = inputFields[i][j].getText().trim().replace(",", ".");
                        userResult[i][j] = Double.parseDouble(text);
                    }
                }
            } catch (NumberFormatException e) {
                feedbackArea.setText("⚠️ Bitte gib gültige Zahlen ein!");
                feedbackArea.setForeground(ERROR_COLOR);
                return;
            }
        }
        
        // Vergleiche mit erwartetem Ergebnis
        boolean correct = compareMatrices(userResult, expectedResult);
        
        if (correct) {
            handleCorrectAnswer();
        } else {
            handleWrongAnswer();
        }
        
        // UI aktualisieren
        submitButton.setEnabled(false);
        nextButton.setEnabled(true);
        
        scoreLabel.setText("⭐ Punkte: " + game.getScore());
        streakLabel.setText("🔥 Streak: " + game.getStreak());
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
    
    private void handleCorrectAnswer() {
        correctAnswers++;
        int points = 10 + game.getStreak() * 2;
        game.addScore(points);
        
        feedbackArea.setForeground(SUCCESS_COLOR);
        feedbackArea.setText(String.format(
            "✅ Richtig! +%d Punkte\n\n" +
            "Streak: %d 🔥 - Weiter so!",
            points, game.getStreak()
        ));
        
        // Highlight korrekte Eingaben
        if (inputFields != null) {
            for (JTextField[] row : inputFields) {
                for (JTextField field : row) {
                    field.setBackground(SUCCESS_COLOR.darker());
                }
            }
        }
    }
    
    private void handleWrongAnswer() {
        game.resetStreak();
        
        feedbackArea.setForeground(ERROR_COLOR);
        StringBuilder sb = new StringBuilder("❌ Leider falsch.\n\n");
        sb.append("Richtige Antwort: ");
        
        if (expectedResult.length == 1 && expectedResult[0].length == 1) {
            sb.append(formatValue(expectedResult[0][0]));
        } else {
            sb.append("\n");
            for (double[] row : expectedResult) {
                sb.append("[");
                for (int j = 0; j < row.length; j++) {
                    sb.append(formatValue(row[j]));
                    if (j < row.length - 1) sb.append(", ");
                }
                sb.append("]\n");
            }
        }
        
        feedbackArea.setText(sb.toString());
        
        // Highlight falsche Eingaben
        if (inputFields != null) {
            for (int i = 0; i < inputFields.length; i++) {
                for (int j = 0; j < inputFields[i].length; j++) {
                    try {
                        double val = Double.parseDouble(inputFields[i][j].getText().trim().replace(",", "."));
                        if (Math.abs(val - expectedResult[i][j]) > 0.001) {
                            inputFields[i][j].setBackground(ERROR_COLOR.darker());
                        } else {
                            inputFields[i][j].setBackground(SUCCESS_COLOR.darker());
                        }
                    } catch (NumberFormatException e) {
                        inputFields[i][j].setBackground(ERROR_COLOR.darker());
                    }
                }
            }
        }
    }
    
    private void showHint() {
        String hint = switch (moduleIndex) {
            case 0 -> "Grundlagen: Zähle die Zeilen (horizontal) und Spalten (vertikal) sorgfältig!";
            case 1 -> "Addition: Addiere die Elemente an der gleichen Position: c_ij = a_ij + b_ij";
            case 2 -> "Subtraktion: Subtrahiere die Elemente an der gleichen Position: c_ij = a_ij - b_ij";
            case 3 -> "Skalar-Multiplikation: Multipliziere JEDEN Eintrag mit dem Skalar!";
            case 4 -> "Matrix-Multiplikation: c_ij = Zeile i von A · Spalte j von B (Skalarprodukt)";
            case 5 -> "Transposition: Die Zeilen werden zu Spalten! a_ij wird zu a_ji";
            case 6 -> "Determinante 2x2: det = ad - bc. Für 3x3: Regel von Sarrus";
            case 7 -> "Inverse 2x2: A⁻¹ = (1/det) · [[d,-b],[-c,a]]";
            case 8 -> "Spezialmatrizen: Einheitsmatrix hat 1en auf der Diagonale, 0en sonst";
            default -> "Denke an die Grundregeln der Matrix-Operationen!";
        };
        
        JOptionPane.showMessageDialog(this, 
            hint, 
            "💡 Hinweis", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showResults() {
        double percentage = (double) correctAnswers / totalQuestions * 100;
        String grade = percentage >= 90 ? "⭐ Ausgezeichnet!" :
                       percentage >= 70 ? "👍 Gut gemacht!" :
                       percentage >= 50 ? "📚 Weiter üben!" : "💪 Nicht aufgeben!";
        
        String message = String.format(
            "Modul abgeschlossen!\n\n" +
            "Richtig: %d von %d (%.0f%%)\n" +
            "Gesamtpunkte: %d\n\n%s",
            correctAnswers, totalQuestions, percentage, game.getScore(), grade
        );
        
        JOptionPane.showMessageDialog(this, message, "Ergebnisse", JOptionPane.INFORMATION_MESSAGE);
        game.showMainMenu();
    }
    
    /**
     * Benutzerdefinierte Border für Matrix-Klammern
     */
    static class MatrixBracketBorder implements javax.swing.border.Border {
        private Color color;
        
        public MatrixBracketBorder(Color color) {
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(2));
            
            int bracketWidth = 8;
            
            // Linke Klammer
            g2d.drawLine(x, y, x + bracketWidth, y);
            g2d.drawLine(x, y, x, y + height - 1);
            g2d.drawLine(x, y + height - 1, x + bracketWidth, y + height - 1);
            
            // Rechte Klammer
            g2d.drawLine(x + width - bracketWidth, y, x + width - 1, y);
            g2d.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
            g2d.drawLine(x + width - bracketWidth, y + height - 1, x + width - 1, y + height - 1);
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(3, 10, 3, 10);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
