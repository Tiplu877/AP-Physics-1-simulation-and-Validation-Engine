package render;

import scene.GameObject;

import javax.swing.*;
import java.awt.*;

public class GraphWindow {

    private final JPanel panel;
    private final GraphPanel positionGraph;
    private final GraphPanel velocityGraph;
    private final GraphPanel accelerationGraph;

    public GraphWindow(GameObject object) {
        panel = new JPanel(new GridLayout(3, 1, 0, 8));
        panel.setBackground(new Color(18, 18, 18));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        positionGraph = new GraphPanel(object.getHistory(), s -> s.position.y, "Position (y) vs Time", new Color(90, 180, 255));
        velocityGraph = new GraphPanel(object.getHistory(), s -> s.velocity.y, "Velocity (y) vs Time", new Color(255, 110, 110));
        accelerationGraph = new GraphPanel(object.getHistory(), s -> s.acceleration.y, "Acceleration (y) vs Time", new Color(110, 220, 140));

        panel.add(positionGraph);
        panel.add(velocityGraph);
        panel.add(accelerationGraph);
    }

    public JPanel getPanel() {
        return panel;
    }

    public void repaint() {
        positionGraph.repaint();
        velocityGraph.repaint();
        accelerationGraph.repaint();
    }
}