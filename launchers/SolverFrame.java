package launcher;

import render.SolutionStepPanel;
import solver.SolutionResult;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolverFrame extends JFrame {

    private static final Color BACKGROUND = new Color(24, 24, 24);
    private static final Color PANEL_BG = new Color(30, 30, 30);
    private static final Color TEXT = new Color(220, 220, 220);
    private static final Color ACCENT = new Color(90, 180, 255);
    private static final Font LABEL_FONT = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 18);

    private final List<SolvableProblem> problems;
    private final JComboBox<String> problemPicker;
    private final JTextArea descriptionArea;
    private final JPanel inputPanel;
    private final JPanel resultContainer;
    private final Map<String, JTextField> fields = new HashMap<>();
    private SolvableProblem currentProblem;

    public SolverFrame(List<SolvableProblem> problems) {
        this.problems = problems;

        setTitle("Step-by-Step Solver");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Choose a Problem Type", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        String[] names = problems.stream().map(SolvableProblem::getName).toArray(String[]::new);
        problemPicker = new JComboBox<>(names);
        problemPicker.setFont(LABEL_FONT);
        problemPicker.addActionListener(e -> onProblemSelected());

        descriptionArea = new JTextArea(2, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(BACKGROUND);
        descriptionArea.setForeground(TEXT);
        descriptionArea.setFont(LABEL_FONT);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        inputPanel = new JPanel();
        inputPanel.setBackground(PANEL_BG);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton solveButton = new JButton("Solve");
        solveButton.setFont(TITLE_FONT.deriveFont(15f));
        solveButton.setBackground(ACCENT);
        solveButton.setFocusPainted(false);
        solveButton.addActionListener(e -> solveCurrentProblem());

        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setBackground(BACKGROUND);
        topWrapper.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        topWrapper.add(problemPicker);
        topWrapper.add(descriptionArea);
        topWrapper.add(inputPanel);
        topWrapper.add(Box.createVerticalStrut(10));

        JPanel solveButtonWrapper = new JPanel();
        solveButtonWrapper.setBackground(BACKGROUND);
        solveButtonWrapper.add(solveButton);
        topWrapper.add(solveButtonWrapper);

        add(topWrapper, BorderLayout.NORTH);

        resultContainer = new JPanel(new BorderLayout());
        resultContainer.setBackground(BACKGROUND);
        JScrollPane resultScroll = new JScrollPane(resultContainer);
        resultScroll.setBorder(BorderFactory.createEmptyBorder());
        resultScroll.getViewport().setBackground(BACKGROUND);
        add(resultScroll, BorderLayout.CENTER);

        onProblemSelected();
    }

    private void onProblemSelected() {
        currentProblem = problems.get(problemPicker.getSelectedIndex());
        descriptionArea.setText(currentProblem.getDescription());

        inputPanel.removeAll();
        fields.clear();

        List<String> labels = currentProblem.getInputLabels();
        List<String> keys = currentProblem.getInputKeys();

        for (int i = 0; i < labels.size(); i++) {
            inputPanel.add(buildFieldRow(labels.get(i), keys.get(i)));
            inputPanel.add(Box.createVerticalStrut(6));
        }

        resultContainer.removeAll();
        inputPanel.revalidate();
        inputPanel.repaint();
        resultContainer.revalidate();
        resultContainer.repaint();
    }

    private JPanel buildFieldRow(String label, String key) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(PANEL_BG);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(LABEL_FONT);
        labelComponent.setForeground(TEXT);
        labelComponent.setPreferredSize(new Dimension(220, 24));

        JTextField field = new JTextField();
        field.setFont(LABEL_FONT);
        fields.put(key, field);

        row.add(labelComponent, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private void solveCurrentProblem() {
        Map<String, Double> values = new HashMap<>();
        for (String key : currentProblem.getInputKeys()) {
            JTextField field = fields.get(key);
            try {
                values.put(key, Double.parseDouble(field.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number for every field.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        SolutionResult result = currentProblem.solve(values);

        resultContainer.removeAll();
        resultContainer.add(new SolutionStepPanel(result), BorderLayout.NORTH);
        resultContainer.revalidate();
        resultContainer.repaint();
    }
}