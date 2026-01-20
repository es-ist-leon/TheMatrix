import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * MatrixLabPanel - Das Matrix-Labor für freies Experimentieren
 * 
 * Hier können Spieler ihre eigenen Matrizen eingeben und
 * verschiedene Operationen darauf ausführen. Das Labor bietet:
 * 
 * - Freie Eingabe von Matrizen beliebiger Größe
 * - Alle Matrix-Operationen zum Ausprobieren
 * - Schritt-für-Schritt Berechnungsanzeige
 * - Visualisierung der Operationen
 * 
 * Perfekt zum Verstehen und Experimentieren!
 */
public class MatrixLabPanel extends JPanel {
    
    // Farben
    private static final Color BACKGROUND_COLOR = new Color(25, 25, 35);
    private static final Color PANEL_COLOR = new Color(40, 44, 52);
    private static final Color ACCENT_COLOR = new Color(97, 175, 239);
    private static final Color SUCCESS_COLOR = new Color(152, 195, 121);
    private static final Color ERROR_COLOR = new Color(224, 108, 117);
    private static final Color TEXT_COLOR = new Color(220, 223, 228);
    private static final Color HIGHLIGHT_COLOR = new Color(229, 192, 123);
    private static final Color MATRIX_CELL_COLOR = new Color(55, 60, 72);
    
    private MatrixGame game;
    
    // Matrizen
    private double[][] matrixA;
    private double[][] matrixB;
    private double[][] resultMatrix;
    
    // Größen
    private int rowsA = 3, colsA = 3;
    private int rowsB = 3, colsB = 3;
    
    // UI-Komponenten
    private JTextField[][] fieldsA;
    private JTextField[][] fieldsB;
    private JPanel matrixAPanel;
    private JPanel matrixBPanel;
    private JPanel resultPanel;
    private JTextArea calculationSteps;
    private JComboBox<String> operationBox;
    private JTextField scalarField;
    
    public MatrixLabPanel(MatrixGame game) {
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        
        initializeUI();
    }
    
    private void initializeUI() {
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Hauptbereich mit drei Spalten
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Oberer Bereich: Matrizen eingeben
        JPanel inputSection = new JPanel(new GridLayout(1, 3, 15, 0));
        inputSection.setBackground(BACKGROUND_COLOR);
        
        // Matrix A
        JPanel matrixASection = createMatrixInputSection("Matrix A", true);
        
        // Operationen
        JPanel operationsSection = createOperationsPanel();
        
        // Matrix B
        JPanel matrixBSection = createMatrixInputSection("Matrix B", false);
        
        inputSection.add(matrixASection);
        inputSection.add(operationsSection);
        inputSection.add(matrixBSection);
        
        mainPanel.add(inputSection, BorderLayout.CENTER);
        
        // Unterer Bereich: Ergebnis und Schritte
        JPanel outputSection = new JPanel(new GridLayout(1, 2, 15, 0));
        outputSection.setBackground(BACKGROUND_COLOR);
        outputSection.setPreferredSize(new Dimension(0, 200));
        
        // Ergebnis-Matrix
        resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(PANEL_COLOR);
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SUCCESS_COLOR, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel resultTitle = new JLabel("📊 Ergebnis");
        resultTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        resultTitle.setForeground(SUCCESS_COLOR);
        resultPanel.add(resultTitle, BorderLayout.NORTH);
        
        JLabel placeholderLabel = new JLabel("Wähle eine Operation und klicke 'Berechnen'", SwingConstants.CENTER);
        placeholderLabel.setForeground(TEXT_COLOR);
        resultPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        // Berechnungsschritte
        JPanel stepsPanel = new JPanel(new BorderLayout());
        stepsPanel.setBackground(PANEL_COLOR);
        stepsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 65, 77)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel stepsTitle = new JLabel("📝 Berechnungsschritte");
        stepsTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        stepsTitle.setForeground(HIGHLIGHT_COLOR);
        stepsPanel.add(stepsTitle, BorderLayout.NORTH);
        
        calculationSteps = new JTextArea(8, 30);
        calculationSteps.setEditable(false);
        calculationSteps.setFont(new Font("Monospaced", Font.PLAIN, 12));
        calculationSteps.setBackground(PANEL_COLOR);
        calculationSteps.setForeground(TEXT_COLOR);
        calculationSteps.setText("Die Berechnungsschritte werden hier angezeigt...");
        
        JScrollPane stepsScroll = new JScrollPane(calculationSteps);
        stepsScroll.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        stepsScroll.getViewport().setBackground(PANEL_COLOR);
        stepsPanel.add(stepsScroll, BorderLayout.CENTER);
        
        outputSection.add(resultPanel);
        outputSection.add(stepsPanel);
        
        mainPanel.add(outputSection, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Initialisiere Matrizen
        initializeMatrices();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JButton backBtn = new JButton("← Zurück");
        backBtn.setForeground(TEXT_COLOR);
        backBtn.setBackground(PANEL_COLOR);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> game.showMainMenu());
        
        JLabel titleLabel = new JLabel("🔬 Matrix-Labor");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel infoLabel = new JLabel("Experimentiere frei mit Matrizen!");
        infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        infoLabel.setForeground(new Color(TEXT_COLOR.getRed(), TEXT_COLOR.getGreen(), 
                                          TEXT_COLOR.getBlue(), 180));
        
        panel.add(backBtn, BorderLayout.WEST);
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(infoLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Erstellt einen Eingabebereich für eine Matrix
     */
    private JPanel createMatrixInputSection(String title, boolean isMatrixA) {
        JPanel section = new JPanel(new BorderLayout(10, 10));
        section.setBackground(PANEL_COLOR);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(isMatrixA ? ACCENT_COLOR : new Color(198, 120, 221), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Titel und Größeneinstellung
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(isMatrixA ? ACCENT_COLOR : new Color(198, 120, 221));
        
        // Größeneinstellung
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        sizePanel.setBackground(PANEL_COLOR);
        
        JLabel sizeLabel = new JLabel("Größe:");
        sizeLabel.setForeground(TEXT_COLOR);
        
        SpinnerNumberModel rowModel = new SpinnerNumberModel(3, 1, 5, 1);
        SpinnerNumberModel colModel = new SpinnerNumberModel(3, 1, 5, 1);
        
        JSpinner rowSpinner = new JSpinner(rowModel);
        JSpinner colSpinner = new JSpinner(colModel);
        
        rowSpinner.setPreferredSize(new Dimension(50, 25));
        colSpinner.setPreferredSize(new Dimension(50, 25));
        
        rowSpinner.addChangeListener(e -> {
            if (isMatrixA) {
                rowsA = (int) rowSpinner.getValue();
                updateMatrixA();
            } else {
                rowsB = (int) rowSpinner.getValue();
                updateMatrixB();
            }
        });
        
        colSpinner.addChangeListener(e -> {
            if (isMatrixA) {
                colsA = (int) colSpinner.getValue();
                updateMatrixA();
            } else {
                colsB = (int) colSpinner.getValue();
                updateMatrixB();
            }
        });
        
        sizePanel.add(sizeLabel);
        sizePanel.add(rowSpinner);
        sizePanel.add(new JLabel("<html><font color='white'>×</font></html>"));
        sizePanel.add(colSpinner);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(sizePanel, BorderLayout.EAST);
        
        section.add(headerPanel, BorderLayout.NORTH);
        
        // Matrix-Eingabefelder
        JPanel matrixContainer = new JPanel(new GridBagLayout());
        matrixContainer.setBackground(PANEL_COLOR);
        
        if (isMatrixA) {
            matrixAPanel = matrixContainer;
        } else {
            matrixBPanel = matrixContainer;
        }
        
        section.add(matrixContainer, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(PANEL_COLOR);
        
        JButton randomBtn = new JButton("🎲 Zufällig");
        styleSmallButton(randomBtn, HIGHLIGHT_COLOR);
        randomBtn.addActionListener(e -> {
            if (isMatrixA) {
                fillRandomMatrix(fieldsA);
            } else {
                fillRandomMatrix(fieldsB);
            }
        });
        
        JButton clearBtn = new JButton("🗑️ Leeren");
        styleSmallButton(clearBtn, ERROR_COLOR);
        clearBtn.addActionListener(e -> {
            if (isMatrixA) {
                clearMatrix(fieldsA);
            } else {
                clearMatrix(fieldsB);
            }
        });
        
        JButton identityBtn = new JButton("I");
        identityBtn.setToolTipText("Einheitsmatrix");
        styleSmallButton(identityBtn, SUCCESS_COLOR);
        identityBtn.addActionListener(e -> {
            if (isMatrixA) {
                fillIdentityMatrix(fieldsA);
            } else {
                fillIdentityMatrix(fieldsB);
            }
        });
        
        buttonPanel.add(randomBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(identityBtn);
        
        section.add(buttonPanel, BorderLayout.SOUTH);
        
        return section;
    }
    
    /**
     * Erstellt das Operationen-Panel in der Mitte
     */
    private JPanel createOperationsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("⚙️ Operationen");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(HIGHLIGHT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Operations-Dropdown
        String[] operations = {
            "A + B (Addition)",
            "A - B (Subtraktion)",
            "k · A (Skalar-Mult.)",
            "A · B (Matrix-Mult.)",
            "Aᵀ (Transponieren)",
            "det(A) (Determinante)",
            "A⁻¹ (Inverse)"
        };
        
        operationBox = new JComboBox<>(operations);
        operationBox.setMaximumSize(new Dimension(200, 35));
        operationBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        operationBox.setBackground(MATRIX_CELL_COLOR);
        operationBox.setForeground(TEXT_COLOR);
        
        // Skalar-Eingabe
        JPanel scalarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        scalarPanel.setBackground(PANEL_COLOR);
        scalarPanel.setMaximumSize(new Dimension(200, 40));
        
        JLabel scalarLabel = new JLabel("k = ");
        scalarLabel.setForeground(TEXT_COLOR);
        
        scalarField = new JTextField("2", 4);
        scalarField.setFont(new Font("Monospaced", Font.BOLD, 14));
        scalarField.setHorizontalAlignment(JTextField.CENTER);
        scalarField.setBackground(MATRIX_CELL_COLOR);
        scalarField.setForeground(TEXT_COLOR);
        scalarField.setCaretColor(TEXT_COLOR);
        
        scalarPanel.add(scalarLabel);
        scalarPanel.add(scalarField);
        
        // Berechnen-Button
        JButton calculateBtn = new JButton("▶ Berechnen");
        calculateBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        calculateBtn.setForeground(TEXT_COLOR);
        calculateBtn.setBackground(SUCCESS_COLOR);
        calculateBtn.setMaximumSize(new Dimension(200, 45));
        calculateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateBtn.setFocusPainted(false);
        calculateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calculateBtn.addActionListener(e -> performCalculation());
        
        // Info-Text
        JLabel infoLabel = new JLabel("<html><center><font size='-1' color='#AAB1C0'>" +
            "Wähle eine Operation<br>und klicke Berechnen</font></center></html>");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(operationBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scalarPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(calculateBtn);
        panel.add(Box.createVerticalGlue());
        panel.add(infoLabel);
        
        return panel;
    }
    
    private void styleSmallButton(JButton button, Color color) {
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setForeground(TEXT_COLOR);
        button.setBackground(color.darker());
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    
    /**
     * Initialisiert die Matrix-Eingabefelder
     */
    private void initializeMatrices() {
        fieldsA = new JTextField[rowsA][colsA];
        fieldsB = new JTextField[rowsB][colsB];
        
        updateMatrixA();
        updateMatrixB();
    }
    
    private void updateMatrixA() {
        matrixAPanel.removeAll();
        fieldsA = new JTextField[rowsA][colsA];
        
        JPanel gridPanel = createMatrixGrid(fieldsA, rowsA, colsA);
        matrixAPanel.add(gridPanel);
        
        matrixAPanel.revalidate();
        matrixAPanel.repaint();
    }
    
    private void updateMatrixB() {
        matrixBPanel.removeAll();
        fieldsB = new JTextField[rowsB][colsB];
        
        JPanel gridPanel = createMatrixGrid(fieldsB, rowsB, colsB);
        matrixBPanel.add(gridPanel);
        
        matrixBPanel.revalidate();
        matrixBPanel.repaint();
    }
    
    private JPanel createMatrixGrid(JTextField[][] fields, int rows, int cols) {
        JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 3, 3));
        gridPanel.setBackground(PANEL_COLOR);
        gridPanel.setBorder(BorderFactory.createCompoundBorder(
            new GamePanel.MatrixBracketBorder(ACCENT_COLOR),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JTextField field = new JTextField("0", 3);
                field.setFont(new Font("Monospaced", Font.BOLD, 14));
                field.setHorizontalAlignment(JTextField.CENTER);
                field.setBackground(MATRIX_CELL_COLOR);
                field.setForeground(TEXT_COLOR);
                field.setCaretColor(TEXT_COLOR);
                field.setBorder(BorderFactory.createLineBorder(new Color(70, 75, 87)));
                field.setPreferredSize(new Dimension(45, 35));
                
                fields[i][j] = field;
                gridPanel.add(field);
            }
        }
        
        return gridPanel;
    }
    
    private void fillRandomMatrix(JTextField[][] fields) {
        Random rand = new Random();
        for (JTextField[] row : fields) {
            for (JTextField field : row) {
                field.setText(String.valueOf(rand.nextInt(19) - 9)); // -9 bis 9
            }
        }
    }
    
    private void clearMatrix(JTextField[][] fields) {
        for (JTextField[] row : fields) {
            for (JTextField field : row) {
                field.setText("0");
            }
        }
    }
    
    private void fillIdentityMatrix(JTextField[][] fields) {
        for (int i = 0; i < fields.length; i++) {
            for (int j = 0; j < fields[i].length; j++) {
                fields[i][j].setText(i == j ? "1" : "0");
            }
        }
    }
    
    private double[][] readMatrix(JTextField[][] fields) throws NumberFormatException {
        double[][] matrix = new double[fields.length][fields[0].length];
        for (int i = 0; i < fields.length; i++) {
            for (int j = 0; j < fields[i].length; j++) {
                String text = fields[i][j].getText().trim().replace(",", ".");
                matrix[i][j] = Double.parseDouble(text);
            }
        }
        return matrix;
    }
    
    /**
     * Führt die ausgewählte Berechnung durch
     */
    private void performCalculation() {
        try {
            matrixA = readMatrix(fieldsA);
            matrixB = readMatrix(fieldsB);
        } catch (NumberFormatException e) {
            showError("Bitte gib gültige Zahlen ein!");
            return;
        }
        
        int operationIndex = operationBox.getSelectedIndex();
        StringBuilder steps = new StringBuilder();
        
        try {
            switch (operationIndex) {
                case 0 -> calculateAddition(steps);
                case 1 -> calculateSubtraction(steps);
                case 2 -> calculateScalarMultiplication(steps);
                case 3 -> calculateMatrixMultiplication(steps);
                case 4 -> calculateTransposition(steps);
                case 5 -> calculateDeterminant(steps);
                case 6 -> calculateInverse(steps);
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
        }
        
        // Zeige Berechnungsschritte
        calculationSteps.setText(steps.toString());
        calculationSteps.setCaretPosition(0);
    }
    
    private void calculateAddition(StringBuilder steps) {
        if (rowsA != rowsB || colsA != colsB) {
            throw new IllegalArgumentException("Für Addition müssen beide Matrizen die gleiche Größe haben!");
        }
        
        steps.append("=== ADDITION A + B ===\n\n");
        steps.append("Regel: Addiere die Elemente an gleichen Positionen.\n");
        steps.append("c_ij = a_ij + b_ij\n\n");
        
        resultMatrix = new double[rowsA][colsA];
        
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsA; j++) {
                resultMatrix[i][j] = matrixA[i][j] + matrixB[i][j];
                steps.append(String.format("c%d%d = %.0f + %.0f = %.0f\n",
                    i + 1, j + 1, matrixA[i][j], matrixB[i][j], resultMatrix[i][j]));
            }
        }
        
        displayResult();
    }
    
    private void calculateSubtraction(StringBuilder steps) {
        if (rowsA != rowsB || colsA != colsB) {
            throw new IllegalArgumentException("Für Subtraktion müssen beide Matrizen die gleiche Größe haben!");
        }
        
        steps.append("=== SUBTRAKTION A - B ===\n\n");
        steps.append("Regel: Subtrahiere die Elemente an gleichen Positionen.\n");
        steps.append("c_ij = a_ij - b_ij\n\n");
        
        resultMatrix = new double[rowsA][colsA];
        
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsA; j++) {
                resultMatrix[i][j] = matrixA[i][j] - matrixB[i][j];
                steps.append(String.format("c%d%d = %.0f - %.0f = %.0f\n",
                    i + 1, j + 1, matrixA[i][j], matrixB[i][j], resultMatrix[i][j]));
            }
        }
        
        displayResult();
    }
    
    private void calculateScalarMultiplication(StringBuilder steps) {
        double k;
        try {
            k = Double.parseDouble(scalarField.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bitte gib einen gültigen Skalar k ein!");
        }
        
        steps.append(String.format("=== SKALAR-MULTIPLIKATION k · A (k = %.1f) ===\n\n", k));
        steps.append("Regel: Multipliziere jedes Element mit dem Skalar.\n");
        steps.append("c_ij = k · a_ij\n\n");
        
        resultMatrix = new double[rowsA][colsA];
        
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsA; j++) {
                resultMatrix[i][j] = k * matrixA[i][j];
                steps.append(String.format("c%d%d = %.1f · %.0f = %.1f\n",
                    i + 1, j + 1, k, matrixA[i][j], resultMatrix[i][j]));
            }
        }
        
        displayResult();
    }
    
    private void calculateMatrixMultiplication(StringBuilder steps) {
        if (colsA != rowsB) {
            throw new IllegalArgumentException(
                String.format("Matrix-Multiplikation nicht möglich!\n" +
                    "A ist %dx%d, B ist %dx%d.\n" +
                    "Spalten von A (%d) müssen gleich Zeilen von B (%d) sein!",
                    rowsA, colsA, rowsB, colsB, colsA, rowsB));
        }
        
        steps.append("=== MATRIX-MULTIPLIKATION A · B ===\n\n");
        steps.append("Regel: c_ij = Zeile i von A · Spalte j von B (Skalarprodukt)\n");
        steps.append(String.format("Ergebnis wird %dx%d Matrix\n\n", rowsA, colsB));
        
        resultMatrix = new double[rowsA][colsB];
        
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                steps.append(String.format("c%d%d = ", i + 1, j + 1));
                
                double sum = 0;
                StringBuilder products = new StringBuilder();
                
                for (int k = 0; k < colsA; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                    if (k > 0) products.append(" + ");
                    products.append(String.format("%.0f·%.0f", matrixA[i][k], matrixB[k][j]));
                }
                
                resultMatrix[i][j] = sum;
                steps.append(products.toString());
                steps.append(String.format(" = %.0f\n", sum));
            }
            steps.append("\n");
        }
        
        displayResult();
    }
    
    private void calculateTransposition(StringBuilder steps) {
        steps.append("=== TRANSPOSITION Aᵀ ===\n\n");
        steps.append("Regel: Zeilen und Spalten werden vertauscht.\n");
        steps.append("a_ij wird zu a_ji\n\n");
        steps.append(String.format("Original: %dx%d Matrix\n", rowsA, colsA));
        steps.append(String.format("Transponiert: %dx%d Matrix\n\n", colsA, rowsA));
        
        resultMatrix = new double[colsA][rowsA];
        
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsA; j++) {
                resultMatrix[j][i] = matrixA[i][j];
                steps.append(String.format("a%d%d = %.0f → Position (%d,%d)\n",
                    i + 1, j + 1, matrixA[i][j], j + 1, i + 1));
            }
        }
        
        displayResult();
    }
    
    private void calculateDeterminant(StringBuilder steps) {
        if (rowsA != colsA) {
            throw new IllegalArgumentException("Die Determinante existiert nur für quadratische Matrizen!");
        }
        
        steps.append("=== DETERMINANTE det(A) ===\n\n");
        
        double det;
        
        if (rowsA == 2) {
            // 2x2 Determinante
            det = matrixA[0][0] * matrixA[1][1] - matrixA[0][1] * matrixA[1][0];
            
            steps.append("Formel für 2×2: det = a·d - b·c\n\n");
            steps.append(String.format("⎡ %.0f  %.0f ⎤\n", matrixA[0][0], matrixA[0][1]));
            steps.append(String.format("⎣ %.0f  %.0f ⎦\n\n", matrixA[1][0], matrixA[1][1]));
            steps.append(String.format("det = %.0f · %.0f - %.0f · %.0f\n",
                matrixA[0][0], matrixA[1][1], matrixA[0][1], matrixA[1][0]));
            steps.append(String.format("det = %.0f - %.0f\n",
                matrixA[0][0] * matrixA[1][1], matrixA[0][1] * matrixA[1][0]));
            
        } else if (rowsA == 3) {
            // 3x3 Determinante (Sarrus)
            det = matrixA[0][0] * matrixA[1][1] * matrixA[2][2]
                + matrixA[0][1] * matrixA[1][2] * matrixA[2][0]
                + matrixA[0][2] * matrixA[1][0] * matrixA[2][1]
                - matrixA[0][2] * matrixA[1][1] * matrixA[2][0]
                - matrixA[0][0] * matrixA[1][2] * matrixA[2][1]
                - matrixA[0][1] * matrixA[1][0] * matrixA[2][2];
            
            steps.append("Regel von Sarrus für 3×3:\n\n");
            steps.append("+ Hauptdiagonalen (links→rechts→unten):\n");
            steps.append(String.format("  %.0f·%.0f·%.0f = %.0f\n",
                matrixA[0][0], matrixA[1][1], matrixA[2][2],
                matrixA[0][0] * matrixA[1][1] * matrixA[2][2]));
            steps.append(String.format("  %.0f·%.0f·%.0f = %.0f\n",
                matrixA[0][1], matrixA[1][2], matrixA[2][0],
                matrixA[0][1] * matrixA[1][2] * matrixA[2][0]));
            steps.append(String.format("  %.0f·%.0f·%.0f = %.0f\n\n",
                matrixA[0][2], matrixA[1][0], matrixA[2][1],
                matrixA[0][2] * matrixA[1][0] * matrixA[2][1]));
            
            steps.append("- Nebendiagonalen (rechts→links→unten):\n");
            steps.append(String.format("  %.0f·%.0f·%.0f = %.0f\n",
                matrixA[0][2], matrixA[1][1], matrixA[2][0],
                matrixA[0][2] * matrixA[1][1] * matrixA[2][0]));
            steps.append(String.format("  %.0f·%.0f·%.0f = %.0f\n",
                matrixA[0][0], matrixA[1][2], matrixA[2][1],
                matrixA[0][0] * matrixA[1][2] * matrixA[2][1]));
            steps.append(String.format("  %.0f·%.0f·%.0f = %.0f\n\n",
                matrixA[0][1], matrixA[1][0], matrixA[2][2],
                matrixA[0][1] * matrixA[1][0] * matrixA[2][2]));
            
        } else {
            throw new IllegalArgumentException("Determinante nur für 2×2 und 3×3 Matrizen implementiert!");
        }
        
        steps.append(String.format("\n══════════════════\ndet(A) = %.2f\n══════════════════", det));
        
        // Zeige Ergebnis als einzelne Zahl
        resultMatrix = new double[][]{{det}};
        displaySingleResult(det, "Determinante");
    }
    
    private void calculateInverse(StringBuilder steps) {
        if (rowsA != colsA) {
            throw new IllegalArgumentException("Die Inverse existiert nur für quadratische Matrizen!");
        }
        
        if (rowsA != 2) {
            throw new IllegalArgumentException("Inverse nur für 2×2 Matrizen implementiert!");
        }
        
        double det = matrixA[0][0] * matrixA[1][1] - matrixA[0][1] * matrixA[1][0];
        
        if (Math.abs(det) < 0.0001) {
            throw new IllegalArgumentException("Matrix ist singulär (det = 0)!\nKeine Inverse möglich.");
        }
        
        steps.append("=== INVERSE A⁻¹ ===\n\n");
        steps.append("Formel für 2×2:\n");
        steps.append("A⁻¹ = (1/det) · ⎡  d  -b ⎤\n");
        steps.append("                ⎣ -c   a ⎦\n\n");
        
        steps.append("Schritt 1: Determinante berechnen\n");
        steps.append(String.format("det = %.0f·%.0f - %.0f·%.0f = %.2f\n\n",
            matrixA[0][0], matrixA[1][1], matrixA[0][1], matrixA[1][0], det));
        
        steps.append("Schritt 2: Adjunkte bilden\n");
        steps.append(String.format("⎡  %.0f  %.0f ⎤\n", matrixA[1][1], -matrixA[0][1]));
        steps.append(String.format("⎣ %.0f   %.0f ⎦\n\n", -matrixA[1][0], matrixA[0][0]));
        
        steps.append(String.format("Schritt 3: Mit 1/det = %.4f multiplizieren\n\n", 1.0/det));
        
        resultMatrix = new double[2][2];
        resultMatrix[0][0] = matrixA[1][1] / det;
        resultMatrix[0][1] = -matrixA[0][1] / det;
        resultMatrix[1][0] = -matrixA[1][0] / det;
        resultMatrix[1][1] = matrixA[0][0] / det;
        
        steps.append("Ergebnis:\n");
        steps.append(String.format("⎡ %.3f  %.3f ⎤\n", resultMatrix[0][0], resultMatrix[0][1]));
        steps.append(String.format("⎣ %.3f  %.3f ⎦\n\n", resultMatrix[1][0], resultMatrix[1][1]));
        
        steps.append("Probe: A · A⁻¹ sollte I (Einheitsmatrix) ergeben!");
        
        displayResult();
    }
    
    /**
     * Zeigt das Ergebnis als Matrix an
     */
    private void displayResult() {
        resultPanel.removeAll();
        
        JLabel titleLabel = new JLabel("📊 Ergebnis");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(SUCCESS_COLOR);
        resultPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridLayout(resultMatrix.length, resultMatrix[0].length, 3, 3));
        gridPanel.setBackground(PANEL_COLOR);
        gridPanel.setBorder(BorderFactory.createCompoundBorder(
            new GamePanel.MatrixBracketBorder(SUCCESS_COLOR),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        for (double[] row : resultMatrix) {
            for (double val : row) {
                JLabel cellLabel = new JLabel(formatValue(val), SwingConstants.CENTER);
                cellLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
                cellLabel.setForeground(TEXT_COLOR);
                cellLabel.setOpaque(true);
                cellLabel.setBackground(MATRIX_CELL_COLOR);
                cellLabel.setPreferredSize(new Dimension(55, 35));
                cellLabel.setBorder(BorderFactory.createLineBorder(SUCCESS_COLOR.darker()));
                gridPanel.add(cellLabel);
            }
        }
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(PANEL_COLOR);
        centerPanel.add(gridPanel);
        
        resultPanel.add(centerPanel, BorderLayout.CENTER);
        
        resultPanel.revalidate();
        resultPanel.repaint();
    }
    
    /**
     * Zeigt ein einzelnes Ergebnis an (für Determinante)
     */
    private void displaySingleResult(double value, String label) {
        resultPanel.removeAll();
        
        JLabel titleLabel = new JLabel("📊 " + label);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(SUCCESS_COLOR);
        resultPanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(formatValue(value));
        valueLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        valueLabel.setForeground(HIGHLIGHT_COLOR);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        resultPanel.add(valueLabel, BorderLayout.CENTER);
        
        resultPanel.revalidate();
        resultPanel.repaint();
    }
    
    private void showError(String message) {
        calculationSteps.setForeground(ERROR_COLOR);
        calculationSteps.setText("❌ FEHLER:\n\n" + message);
        
        resultPanel.removeAll();
        JLabel errorLabel = new JLabel("<html><center><font color='#E06C75'>⚠️<br>" + 
            message.replace("\n", "<br>") + "</font></center></html>");
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultPanel.add(errorLabel, BorderLayout.CENTER);
        resultPanel.revalidate();
        resultPanel.repaint();
    }
    
    private String formatValue(double value) {
        if (Math.abs(value - Math.round(value)) < 0.001) {
            return String.valueOf((int) Math.round(value));
        }
        return String.format("%.2f", value);
    }
}
