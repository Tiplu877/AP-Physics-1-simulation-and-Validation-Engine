package render;

import scene.MotionSample;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

public class GraphPanel extends JPanel {

    private static final Color BACKGROUND = new Color(24, 24, 24);
    private static final Color GRID_COLOR = new Color(50, 50, 50);
    private static final Color AXIS_COLOR = new Color(110, 110, 110);
    private static final Color TEXT_COLOR = new Color(190, 190, 190);
    private static final Font LABEL_FONT = new Font("Monospaced", Font.PLAIN, 12);
    private static final int MARGIN = 45;

    private final List<MotionSample> history;
    private final Function<MotionSample, Double> valueExtractor;
    private final String label;
    private final Color lineColor;

    public GraphPanel(List<MotionSample> history, Function<MotionSample, Double> valueExtractor,
                      String label, Color lineColor) {
        this.history = history;
        this.valueExtractor = valueExtractor;
        this.label = label;
        this.lineColor = lineColor;
        setBackground(BACKGROUND);
        setPreferredSize(new Dimension(500, 180));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(LABEL_FONT);

        if (history == null || history.size() < 2) {
            g2.setColor(TEXT_COLOR);
            g2.drawString(label + " (waiting for data...)", MARGIN, MARGIN);
            return;
        }

        int width = getWidth();
        int height = getHeight();

        double minTime = history.get(0).time;
        double maxTime = history.get(history.size() - 1).time;

        double minValue = Double.MAX_VALUE;
        double maxValue = -Double.MAX_VALUE;
        for (MotionSample sample : history) {
            double v = valueExtractor.apply(sample);
            minValue = Math.min(minValue, v);
            maxValue = Math.max(maxValue, v);
        }
        if (minValue == maxValue) {
            minValue -= 1;
            maxValue += 1;
        }

        g2.setColor(GRID_COLOR);
        int gridLines = 4;
        for (int i = 1; i < gridLines; i++) {
            int y = MARGIN + i * (height - 2 * MARGIN) / gridLines;
            g2.drawLine(MARGIN, y, width - MARGIN, y);
        }

        g2.setColor(AXIS_COLOR);
        g2.drawLine(MARGIN, height - MARGIN, width - MARGIN, height - MARGIN);
        g2.drawLine(MARGIN, MARGIN, MARGIN, height - MARGIN);

        g2.setColor(TEXT_COLOR);
        g2.drawString(label, MARGIN, MARGIN - 15);
        g2.drawString("0s", MARGIN - 10, height - MARGIN + 18);
        g2.drawString(String.format("%.1fs", maxTime), width - MARGIN - 25, height - MARGIN + 18);
        g2.drawString(String.format("%.1f", maxValue), 5, MARGIN + 5);
        g2.drawString(String.format("%.1f", minValue), 5, height - MARGIN);

        g2.setColor(lineColor);
        g2.setStroke(new BasicStroke(2f));
        int prevX = -1, prevY = -1;
        for (MotionSample sample : history) {
            double tFraction = (sample.time - minTime) / (maxTime - minTime);
            double vFraction = (valueExtractor.apply(sample) - minValue) / (maxValue - minValue);

            int x = MARGIN + (int) (tFraction * (width - 2 * MARGIN));
            int y = height - MARGIN - (int) (vFraction * (height - 2 * MARGIN));

            if (prevX != -1) g2.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
    }
}