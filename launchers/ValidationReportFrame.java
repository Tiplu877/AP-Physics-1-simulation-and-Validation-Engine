package launcher;

import validation.ValidationCase;
import validation.ValidationOutcome;
import validation.ValidationReport;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationReportFrame extends JFrame {

    private static final Color BACKGROUND = new Color(24, 24, 24);
    private static final Color PANEL_BG = new Color(30, 30, 30);
    private static final Color TEXT = new Color(220, 220, 220);
    private static final Color ACCENT = new Color(90, 180, 255);
    private static final Color PASS_COLOR = new Color(120, 255, 150);
    private static final Color FAIL_COLOR = new Color(255, 110, 110);
    private static final Font LABEL_FONT = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 18);

    private final List<ValidationCase> cases;
    private final JComboBox<String> testPicker;
    private final JTextArea descriptionArea;
    private final JPanel parameterPanel;
    private final Map<String, JSlider> sliders = new HashMap<>();
    private ValidationCase currentCase;

    private final DefaultTableModel tableModel;
    private final java.util.List<ValidationOutcome> accumulatedResults = new java.util.ArrayList<>();

    public ValidationReportFrame() {
        this.cases = ValidationReport.buildCases();

        setTitle("Validation Report");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Simulation vs. Theory Validation", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        String[] names = cases.stream().map(ValidationCase::getName).toArray(String[]::new);
        testPicker = new JComboBox<>(names);
        testPicker.setFont(LABEL_FONT);
        testPicker.addActionListener(e -> onTestSelected());

        descriptionArea = new JTextArea(2, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(BACKGROUND);
        descriptionArea.setForeground(TEXT);
        descriptionArea.setFont(LABEL_FONT);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        parameterPanel = new JPanel();
        parameterPanel.setBackground(PANEL_BG);
        parameterPanel.setLayout(new BoxLayout(parameterPanel, BoxLayout.Y_AXIS));
        parameterPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton runButton = new JButton("Run This Test");
        runButton.setFont(LABEL_FONT.deriveFont(Font.BOLD, 14f));
        runButton.setBackground(ACCENT);
        runButton.setFocusPainted(false);
        runButton.addActionListener(e -> runSelectedTest());

        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(BACKGROUND);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 10));
        leftPanel.setPreferredSize(new Dimension(360, 0));

        JPanel leftTop = new JPanel(new BorderLayout());
        leftTop.setBackground(BACKGROUND);
        leftTop.add(testPicker, BorderLayout.NORTH);
        leftTop.add(descriptionArea, BorderLayout.CENTER);
        leftPanel.add(leftTop, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(parameterPanel), BorderLayout.CENTER);

        JPanel runButtonWrapper = new JPanel();
        runButtonWrapper.setBackground(BACKGROUND);
        runButtonWrapper.add(runButton);
        leftPanel.add(runButtonWrapper, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        String[] columns = {"Test", "Inputs", "Theoretical", "Measured", "% Error", "Result"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setFont(LABEL_FONT);
        table.setRowHeight(26);
        table.setBackground(new Color(30, 30, 30));
        table.setForeground(TEXT);
        table.setGridColor(new Color(50, 50, 50));
        table.getTableHeader().setFont(LABEL_FONT.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(new Color(40, 40, 40));
        table.getTableHeader().setForeground(TEXT);
        table.setDefaultRenderer(Object.class, new ResultCellRenderer());

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.getViewport().setBackground(new Color(30, 30, 30));
        tableScroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        add(tableScroll, BorderLayout.CENTER);

        JButton exportButton = new JButton("Export to Markdown");
        exportButton.setFont(LABEL_FONT);
        exportButton.addActionListener(e -> exportMarkdown());

        JButton clearButton = new JButton("Clear Table");
        clearButton.setFont(LABEL_FONT);
        clearButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            accumulatedResults.clear();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        bottomPanel.add(exportButton);
        bottomPanel.add(clearButton);
        add(bottomPanel, BorderLayout.SOUTH);

        onTestSelected();
    }

    private void onTestSelected() {
        currentCase = cases.get(testPicker.getSelectedIndex());
        descriptionArea.setText(currentCase.getDescription());

        parameterPanel.removeAll();
        sliders.clear();

        for (ScenarioParameter param : currentCase.getParameters()) {
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

    private void runSelectedTest() {
        Map<String, Double> values = new HashMap<>();
        for (ScenarioParameter param : currentCase.getParameters()) {
            JSlider slider = sliders.get(param.key);
            values.put(param.key, param.min + slider.getValue() * param.step);
        }

        ValidationOutcome outcome = currentCase.run(values);
        accumulatedResults.add(outcome);

        tableModel.addRow(new Object[]{
                outcome.testName,
                outcome.description,
                String.format("%.4f %s", outcome.theoreticalValue, outcome.unit),
                String.format("%.4f %s", outcome.measuredValue, outcome.unit),
                String.format("%.2f%%", outcome.percentError()),
                outcome.passed ? "PASS" : "FAIL"
        });
    }

    private void exportMarkdown() {
        if (accumulatedResults.isEmpty()) return;
        try (FileWriter writer = new FileWriter("validation_report.md")) {
            writer.write(ValidationReport.buildMarkdown(accumulatedResults));
        } catch (Exception ignored) {
        }
    }

    private class ResultCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(new Color(30, 30, 30));
            if (column == 5) {
                c.setForeground("PASS".equals(value) ? PASS_COLOR : FAIL_COLOR);
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                c.setForeground(TEXT);
                setFont(getFont().deriveFont(Font.PLAIN));
            }
            return c;
        }
    }
}