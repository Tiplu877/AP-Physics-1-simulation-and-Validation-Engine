package render;

import physics.RopeConstraint;
import physics.SpringConstraint;
import physics.Surface;
import scene.GameObject;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Render {

    private final JFrame frame;
    private final RenderPanel panel;
    private final GraphWindow graphs;
    private final DataPanel dataPanel;
    private final EnergyGraphWindow energyGraphs;

    public Render(List<GameObject> objects, List<Surface> surfaces, List<RopeConstraint> ropeConstraints,
                  Camera camera, GameObject trackedForGraphs, GameObject relativeTo, double restitution,
                  SpringConstraint trackedSpring, physics.PeriodTracker periodTracker) {
        frame = new JFrame("Physics Engine");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new RenderPanel(objects, surfaces, ropeConstraints, camera);
        graphs = new GraphWindow(trackedForGraphs);
        dataPanel = new DataPanel(trackedForGraphs, relativeTo, objects, restitution, periodTracker);        energyGraphs = new EnergyGraphWindow(trackedForGraphs, 9.81, 0, trackedSpring);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(24, 24, 24));
        tabs.setForeground(Color.WHITE);
        tabs.addTab("3D View", panel);
        tabs.addTab("Graphs", graphs.getPanel());
        tabs.addTab("Data", wrapScrollable(dataPanel));
        tabs.addTab("Energy", energyGraphs.getPanel());

        frame.getContentPane().setBackground(new Color(18, 18, 18));
        frame.add(tabs);
        frame.setVisible(true);
    }

    private JScrollPane wrapScrollable(JPanel content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(30, 30, 30));
        return scroll;
    }

    public DataPanel getDataPanel() {
        return dataPanel;
    }

    public void repaint() {
        panel.repaint();
        graphs.repaint();
        dataPanel.refresh();
        energyGraphs.repaint();
    }
}