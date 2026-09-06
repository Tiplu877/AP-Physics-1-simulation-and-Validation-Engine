package launcher;

import launcher.handlers.AngledProjectileHandler;
import launcher.handlers.BalanceDistanceHandler;
import launcher.handlers.PendulumPeriodHandler;
import launcher.handlers.StraightLineKinematicsHandler;
import render.Render;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LauncherFrame extends JFrame {

    private static final Color BACKGROUND = new Color(24, 24, 24);
    private static final Color PANEL_BG = new Color(30, 30, 30);
    private static final Color TEXT = new Color(220, 220, 220);
    private static final Color ACCENT = new Color(90, 180, 255);
    private static final Font LABEL_FONT = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 18);

    private final List<Scenario> scenarios;
    private final List<SolvableProblem> problems;
    private final AIProblemRegistry aiRegistry;
    private final JComboBox<String> scenarioPicker;
    private final JPanel parameterPanel;
    private final JTextArea descriptionArea;
    private final Map<String, JSlider> sliders = new HashMap<>();
    private Scenario currentScenario;

    public LauncherFrame(List<Scenario> scenarios, List<SolvableProblem> problems) {
        this.scenarios = scenarios;
        this.problems = problems;
        this.aiRegistry = buildAIRegistry();

        setTitle("Physics Engine - Scenario Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(BACKGROUND);
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Choose a Scenario", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        String[] names = scenarios.stream().map(Scenario::getName).toArray(String[]::new);
        scenarioPicker = new JComboBox<>(names);
        scenarioPicker.setFont(LABEL_FONT);
        scenarioPicker.addActionListener(e -> onScenarioSelected());

        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(BACKGROUND);
        descriptionArea.setForeground(TEXT);
        descriptionArea.setFont(LABEL_FONT);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND);
        topPanel.add(scenarioPicker, BorderLayout.NORTH);
        topPanel.add(descriptionArea, BorderLayout.CENTER);

        parameterPanel = new JPanel();
        parameterPanel.setBackground(PANEL_BG);
        parameterPanel.setLayout(new BoxLayout(parameterPanel, BoxLayout.Y_AXIS));
        parameterPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JScrollPane parameterScroll = new JScrollPane(parameterPanel);
        parameterScroll.setBorder(BorderFactory.createEmptyBorder());
        parameterScroll.getViewport().setBackground(PANEL_BG);

        JPanel centerWrapper = new JPanel(new BorderLayout(0, 10));
        centerWrapper.setBackground(BACKGROUND);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        centerWrapper.add(topPanel, BorderLayout.NORTH);
        centerWrapper.add(parameterScroll, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        JButton runButton = new JButton("Run Simulation");
        runButton.setFont(TITLE_FONT.deriveFont(15f));
        runButton.setBackground(ACCENT);
        runButton.setFocusPainted(false);
        runButton.addActionListener(e -> runSelectedScenario());

        JButton solverButton = new JButton("Step-by-Step Solver");
        solverButton.setFont(LABEL_FONT);
        solverButton.addActionListener(e -> new SolverFrame(problems).setVisible(true));

        JButton aiButton = new JButton("AI Problem Solver");
        aiButton.setFont(LABEL_FONT);
        aiButton.addActionListener(e -> new AIProblemFrame(aiRegistry).setVisible(true));
        JButton validationButton = new JButton("Validation Report");
        validationButton.setFont(LABEL_FONT);
        validationButton.addActionListener(e -> new ValidationReportFrame().setVisible(true));



        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        bottomPanel.add(runButton);
        bottomPanel.add(solverButton);
        bottomPanel.add(aiButton);
        bottomPanel.add(validationButton);

        add(bottomPanel, BorderLayout.SOUTH);

        onScenarioSelected();
    }

    private AIProblemRegistry buildAIRegistry() {
        AIProblemRegistry registry = new AIProblemRegistry();
        registry.register(new AngledProjectileHandler());
        registry.register(new StraightLineKinematicsHandler());
        registry.register(new PendulumPeriodHandler());
        registry.register(new BalanceDistanceHandler());
        // registry.register(new launcher.handlers.AverageForceHandler());
        // registry.register(new launcher.handlers.MinSpeedAtTopHandler());
        return registry;
    }

    private void onScenarioSelected() {
        currentScenario = scenarios.get(scenarioPicker.getSelectedIndex());
        descriptionArea.setText(currentScenario.getDescription());

        parameterPanel.removeAll();
        sliders.clear();

        for (ScenarioParameter param : currentScenario.getParameters()) {
            parameterPanel.add(buildSliderRow(param));
            parameterPanel.add(Box.createVerticalStrut(8));
        }

        parameterPanel.revalidate();
        parameterPanel.repaint();
    }

    private JPanel buildSliderRow(ScenarioParameter param) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(PANEL_BG);

        JLabel label = new JLabel(param.label);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT);
        label.setPreferredSize(new Dimension(160, 20));

        int sliderMax = (int) Math.round((param.max - param.min) / param.step);
        int sliderDefault = (int) Math.round((param.defaultValue - param.min) / param.step);
        JSlider slider = new JSlider(0, sliderMax, sliderDefault);
        slider.setBackground(PANEL_BG);

        JLabel valueLabel = new JLabel(String.format("%.2f", param.defaultValue));
        valueLabel.setFont(LABEL_FONT);
        valueLabel.setForeground(ACCENT);
        valueLabel.setPreferredSize(new Dimension(50, 20));

        slider.addChangeListener(e -> {
            double actualValue = param.min + slider.getValue() * param.step;
            valueLabel.setText(String.format("%.2f", actualValue));
        });

        sliders.put(param.key, slider);

        row.add(label, BorderLayout.WEST);
        row.add(slider, BorderLayout.CENTER);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    private void runSelectedScenario() {
        Map<String, Double> values = new HashMap<>();
        for (ScenarioParameter param : currentScenario.getParameters()) {
            JSlider slider = sliders.get(param.key);
            values.put(param.key, param.min + slider.getValue() * param.step);
        }

        SceneResult scene = currentScenario.build(values);

        Render render = new Render(scene.objects, scene.world.getSurfaces(), scene.world.getRopeConstraints(),
                scene.camera, scene.tracked, scene.relativeTo, scene.world.getRestitution(),
                scene.trackedSpring, scene.periodTracker);

        Thread simulationThread = new Thread(() -> {
            long lastTime = System.nanoTime();
            while (true) {
                long now = System.nanoTime();
                double dt = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                scene.world.update(dt);
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