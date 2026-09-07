package render;

import physics.SpringConstraint;
import scene.GameObject;

import javax.swing.*;
import java.awt.*;

public class EnergyGraphWindow {

    private final JPanel panel;
    private final GraphPanel kineticGraph;
    private final GraphPanel potentialGraph;
    private final GraphPanel totalGraph;
    private final GraphPanel springGraph; // null if this object has no spring attached

    public EnergyGraphWindow(GameObject object, double g, double referenceHeight, SpringConstraint spring) {
        double mass = object.mass; // captured once -- mass isn't expected to change mid-simulation

        panel = new JPanel(new GridLayout(spring != null ? 4 : 3, 1, 0, 8));
        panel.setBackground(new Color(18, 18, 18));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        kineticGraph = new GraphPanel(object.getHistory(),
                s -> 0.5 * mass * s.velocity.magnitude_square(),
                "Kinetic Energy vs Time", new Color(255, 110, 110));

        potentialGraph = new GraphPanel(object.getHistory(),
                s -> mass * g * (s.position.y - referenceHeight),
                "Potential Energy vs Time", new Color(90, 180, 255));

        totalGraph = new GraphPanel(object.getHistory(),
                s -> {
                    double ke = 0.5 * mass * s.velocity.magnitude_square();
                    double gravPE = mass * g * (s.position.y - referenceHeight);
                    double springPE = spring != null ? spring.getPotentialEnergyAt(s.position) : 0;
                    return ke + gravPE + springPE;
                },
                "Total Mechanical Energy vs Time", new Color(120, 255, 150));

        springGraph = spring != null ? new GraphPanel(object.getHistory(),
                s -> spring.getPotentialEnergyAt(s.position),
                "Spring Potential Energy vs Time", new Color(255, 200, 120)) : null;

        panel.add(kineticGraph);
        panel.add(potentialGraph);
        if (springGraph != null) {
            panel.add(springGraph);
        }
        panel.add(totalGraph);
    }

    public JPanel getPanel() {
        return panel;
    }

    public void repaint() {
        kineticGraph.repaint();
        potentialGraph.repaint();
        if (springGraph != null) {
            springGraph.repaint();
        }
        totalGraph.repaint();
    }
}