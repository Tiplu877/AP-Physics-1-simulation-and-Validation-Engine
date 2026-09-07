package render;

import physics.MomentumTracker;
import scene.GameObject;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DataPanel extends JPanel {

    private static final Color BACKGROUND = new Color(24, 24, 24);
    private static final Color LABEL_COLOR = new Color(150, 150, 150);
    private static final Color VALUE_COLOR = new Color(230, 230, 230);
    private static final Color ACCENT = new Color(90, 180, 255);
    private static final Font LABEL_FONT = new Font("Monospaced", Font.PLAIN, 14);
    private static final Font VALUE_FONT = new Font("Monospaced", Font.BOLD, 14);
    private static final Font HEADER_FONT = new Font("Monospaced", Font.BOLD, 16);

    private final GameObject tracked;
    private final GameObject relativeTo; // may be null if there's nothing to compare against
    private final List<GameObject> allObjects;
    private final double restitution;
    private final physics.PeriodTracker periodTracker;

    private JLabel positionValue, velocityValue, accelerationValue;
    private JLabel displacementValue, distanceValue, avgVelocityValue;
    private JLabel relativeVelocityValue, massValue, frictionValue;
    private JLabel equilibriumValue;
    private JLabel workEnergyValue;
    private JLabel powerValue;
    private JLabel momentumValue;
    private JLabel systemMomentumValue;
    private JLabel systemKineticEnergyValue;
    private JLabel collisionTypeValue;
    private JTextArea solverSummary;
    private JLabel measuredPeriodValue;

    public DataPanel(GameObject tracked, GameObject relativeTo, List<GameObject> allObjects, double restitution, physics.PeriodTracker periodTracker) {        this.tracked = tracked;
        this.relativeTo = relativeTo;
        this.allObjects = allObjects;
        this.restitution = restitution;
        this.periodTracker = periodTracker;

        setBackground(BACKGROUND);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 14, 6, 14);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(header("Live Object Data"), gbc);
        gbc.gridwidth = 1;

        positionValue = addRow("Position", row++, gbc);
        velocityValue = addRow("Velocity", row++, gbc);
        accelerationValue = addRow("Acceleration", row++, gbc);
        displacementValue = addRow("Displacement", row++, gbc);
        distanceValue = addRow("Distance Traveled", row++, gbc);
        avgVelocityValue = addRow("Average Velocity", row++, gbc);
        relativeVelocityValue = addRow("Velocity (relative)", row++, gbc);
        massValue = addRow("Mass", row++, gbc);
        frictionValue = addRow("Kinetic Friction", row++, gbc);
        equilibriumValue = addRow("Equilibrium", row++, gbc);
        workEnergyValue = addRow("Net Work Done", row++, gbc);
        powerValue = addRow("Power (inst / avg)", row++, gbc);
        momentumValue = addRow("Momentum", row++, gbc);
        systemMomentumValue = addRow("System Momentum", row++, gbc);
        systemKineticEnergyValue = addRow("System Kinetic Energy", row++, gbc);
        collisionTypeValue = addRow("Collision Type", row++, gbc);
        measuredPeriodValue = addRow("Measured Period", row++, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(header("Solver Reference (static, computed once at startup)"), gbc);

        solverSummary = new JTextArea();
        solverSummary.setEditable(false);
        solverSummary.setBackground(BACKGROUND);
        solverSummary.setForeground(VALUE_COLOR);
        solverSummary.setFont(LABEL_FONT);
        gbc.gridy = row++;
        add(solverSummary, gbc);

        refresh();
    }

    private JLabel header(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        label.setForeground(ACCENT);
        return label;
    }

    private JLabel addRow(String labelText, int row, GridBagConstraints gbc) {
        gbc.gridy = row;
        gbc.gridx = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(LABEL_COLOR);
        add(label, gbc);

        gbc.gridx = 1;
        JLabel value = new JLabel("-");
        value.setFont(VALUE_FONT);
        value.setForeground(VALUE_COLOR);
        add(value, gbc);

        return value;
    }

    public void setSolverSummary(String text) {
        solverSummary.setText(text);
    }

    public void refresh() {
        positionValue.setText(tracked.position.toString());
        velocityValue.setText(tracked.velocity.toString());
        accelerationValue.setText(tracked.getInstantaneousAcceleration().toString());
        displacementValue.setText(tracked.getDisplacement().toString());
        distanceValue.setText(String.format("%.2f m", tracked.getDistanceTraveled()));
        avgVelocityValue.setText(tracked.getAverageVelocity().toString());
        relativeVelocityValue.setText(
                relativeTo != null ? tracked.getVelocityRelativeTo(relativeTo).toString() : "n/a");
        massValue.setText(String.format("%.2f kg", tracked.mass));
        frictionValue.setText(String.format("%.2f", tracked.kineticFriction));

        if (tracked.isInStaticEquilibrium()) {
            equilibriumValue.setText("YES (static)");
            equilibriumValue.setForeground(new Color(120, 255, 150));
        } else if (tracked.isInDynamicEquilibrium()) {
            equilibriumValue.setText("YES (dynamic)");
            equilibriumValue.setForeground(new Color(120, 255, 150));
        } else {
            equilibriumValue.setText("NO");
            equilibriumValue.setForeground(new Color(255, 140, 60));
        }

        workEnergyValue.setText(String.format("%.2f J (discrepancy: %.4f)",
                tracked.getCumulativeWorkByNetForce(), tracked.getWorkEnergyDiscrepancy()));

        powerValue.setText(String.format("%.2f W / %.2f W",
                tracked.getInstantaneousPower(), tracked.getAveragePower()));

        momentumValue.setText(tracked.getMomentum().toString());

        systemMomentumValue.setText(MomentumTracker.totalMomentum(allObjects).toString());
        systemKineticEnergyValue.setText(String.format("%.2f J", MomentumTracker.totalKineticEnergy(allObjects)));
        collisionTypeValue.setText(MomentumTracker.classifyCollision(restitution));
        if (periodTracker != null && periodTracker.getMeasuredPeriod() > 0) {
            measuredPeriodValue.setText(String.format("%.3f s", periodTracker.getMeasuredPeriod()));
        } else {
            measuredPeriodValue.setText(periodTracker != null ? "measuring..." : "n/a");
        }
    }
}