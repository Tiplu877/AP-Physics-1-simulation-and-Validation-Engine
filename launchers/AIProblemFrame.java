package launcher;

import ai.AISolvedProblem;
import ai.GeminiClient;
import render.Render;
import render.SolutionStepPanel;
import solver.SolutionResult;

import javax.swing.*;
import java.awt.*;

public class AIProblemFrame extends JFrame {

    private static final Color BACKGROUND = new Color(24, 24, 24);
    private static final Color INPUT_BG = new Color(32, 32, 34);
    private static final Color TEXT = new Color(220, 220, 220);
    private static final Color PLACEHOLDER = new Color(120, 120, 125);
    private static final Color ACCENT = new Color(90, 180, 255);
    private static final Color STATUS_COLOR = new Color(255, 200, 120);
    private static final Color ERROR_COLOR = new Color(255, 130, 110);
    private static final Font LABEL_FONT = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 20);

    private final AIProblemRegistry registry;
    private final GeminiClient geminiClient = new GeminiClient();
    private final JTextArea problemInput;
    private final JPanel resultContainer;
    private final JLabel statusLabel;

    private SceneResult lastScenario;

    public AIProblemFrame(AIProblemRegistry registry) {
        this.registry = registry;

        setTitle("AI Problem Solver");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(680, 780);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("AI Problem Solver");
        title.setFont(TITLE_FONT);
        title.setForeground(ACCENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 4, 0));
        add(title, BorderLayout.NORTH);

        JLabel subtitle = new JLabel("Paste any AP Physics 1 word problem", SwingConstants.CENTER);
        subtitle.setFont(LABEL_FONT);
        subtitle.setForeground(new Color(150, 150, 150));

        problemInput = new JTextArea();
        problemInput.setLineWrap(true);
        problemInput.setWrapStyleWord(true);
        problemInput.setFont(LABEL_FONT);
        problemInput.setBackground(INPUT_BG);
        problemInput.setForeground(TEXT);
        problemInput.setCaretColor(TEXT);
        problemInput.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        installPlaceholder();

        JScrollPane inputScroll = new JScrollPane(problemInput);
        inputScroll.setPreferredSize(new Dimension(0, 110));
        inputScroll.setBorder(BorderFactory.createLineBorder(new Color(48, 48, 52), 1));

        JButton solveButton = new JButton("Solve with AI");
        solveButton.setFont(LABEL_FONT.deriveFont(Font.BOLD, 15f));
        solveButton.setBackground(ACCENT);
        solveButton.setForeground(Color.BLACK);
        solveButton.setFocusPainted(false);
        solveButton.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        solveButton.addActionListener(e -> solveWithAI());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(LABEL_FONT);
        statusLabel.setForeground(STATUS_COLOR);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel buttonRow = new JPanel();
        buttonRow.setBackground(BACKGROUND);
        buttonRow.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        buttonRow.add(solveButton);

        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setBackground(BACKGROUND);
        topWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topWrapper.add(subtitle);
        topWrapper.add(Box.createVerticalStrut(10));
        topWrapper.add(inputScroll);
        topWrapper.add(buttonRow);
        topWrapper.add(statusLabel);

        add(topWrapper, BorderLayout.NORTH);

        resultContainer = new JPanel();
        resultContainer.setLayout(new BoxLayout(resultContainer, BoxLayout.Y_AXIS));
        resultContainer.setBackground(BACKGROUND);
        JScrollPane resultScroll = new JScrollPane(resultContainer);
        resultScroll.setBorder(BorderFactory.createEmptyBorder());
        resultScroll.getViewport().setBackground(BACKGROUND);
        resultScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(resultScroll, BorderLayout.CENTER);
    }

    // Simple placeholder-text behavior -- Swing has no built-in equivalent to HTML's
    // placeholder attribute, so this fakes it with focus listeners and a color swap.
    private void installPlaceholder() {
        problemInput.setText(PLACEHOLDER_TEXT);
        problemInput.setForeground(PLACEHOLDER);

        problemInput.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (problemInput.getText().equals(PLACEHOLDER_TEXT)) {
                    problemInput.setText("");
                    problemInput.setForeground(TEXT);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (problemInput.getText().isBlank()) {
                    problemInput.setText(PLACEHOLDER_TEXT);
                    problemInput.setForeground(PLACEHOLDER);
                }
            }
        });
    }

    private void solveWithAI() {
        String problemText = problemInput.getText().trim();
        if (problemText.isEmpty() || problemText.equals(PLACEHOLDER_TEXT)) return;

        statusLabel.setForeground(STATUS_COLOR);
        statusLabel.setText("Thinking...");
        resultContainer.removeAll();
        resultContainer.revalidate();
        resultContainer.repaint();

        new SwingWorker<Void, Void>() {
            String errorMessage = null;
            SolutionResult solution = null;
            SceneResult scenario = null;

            @Override
            protected Void doInBackground() {
                try {
                    String prompt = registry.buildSolvePrompt(problemText);
                    String rawResponse = geminiClient.generate(prompt);
                    AISolvedProblem solved = AISolvedProblem.fromJson(rawResponse);

                    solution = solved.solution;

                    if (solved.matchedScenarioType != null) {
                        AIProblemHandler handler = registry.get(solved.matchedScenarioType);
                        if (handler != null) {
                            boolean hasAllKeys = handler.getRequiredKeys().stream()
                                    .allMatch(solved.knowns::containsKey);
                            if (hasAllKeys) {
                                scenario = handler.buildScenario(solved.knowns);
                            }
                        }
                    }
                } catch (Exception e) {
                    errorMessage = "Error: " + e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                statusLabel.setText(" ");

                if (errorMessage != null) {
                    statusLabel.setForeground(ERROR_COLOR);
                    statusLabel.setText(errorMessage);
                    return;
                }

                lastScenario = scenario;
                resultContainer.add(new SolutionStepPanel(solution));

                JPanel footer = new JPanel();
                footer.setBackground(BACKGROUND);
                footer.setBorder(BorderFactory.createEmptyBorder(6, 10, 20, 10));

                if (scenario != null) {
                    JButton viewSimButton = new JButton("View Simulation of This Problem");
                    viewSimButton.setFont(LABEL_FONT);
                    viewSimButton.setFocusPainted(false);
                    viewSimButton.addActionListener(e -> launchSimulation());
                    footer.add(viewSimButton);
                } else {
                    JLabel noSimNote = new JLabel("No matching simulation available for this problem type.");
                    noSimNote.setForeground(new Color(130, 130, 130));
                    noSimNote.setFont(LABEL_FONT);
                    footer.add(noSimNote);
                }

                resultContainer.add(footer);
                resultContainer.revalidate();
                resultContainer.repaint();
            }
        }.execute();
    }

    private void launchSimulation() {
        if (lastScenario == null) return;

        Render render = new Render(lastScenario.objects, lastScenario.world.getSurfaces(),
                lastScenario.world.getRopeConstraints(), lastScenario.camera, lastScenario.tracked,
                lastScenario.relativeTo, lastScenario.world.getRestitution(),
                lastScenario.trackedSpring, lastScenario.periodTracker);

        Thread simulationThread = new Thread(() -> {
            long lastTime = System.nanoTime();
            while (true) {
                long now = System.nanoTime();
                double dt = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                lastScenario.world.update(dt);
                SwingUtilities.invokeLater(render::repaint);

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        simulationThread.setDaemon(true);
        simulationThread.start();
    }
}
