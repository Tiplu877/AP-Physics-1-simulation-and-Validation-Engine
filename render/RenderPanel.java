package render;

import math.Vector3;
import physics.RopeConstraint;
import physics.Surface;
import scene.GameObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RenderPanel extends JPanel {

    private List<GameObject> objects;
    private List<Surface> surfaces;
    private List<RopeConstraint> ropeConstraints;
    private Camera camera;
    private static final int GRID_HALF_SIZE = 20;
    private static final int GRID_STEP = 2;
    private static final double FORCE_SCALE = 0.05;

    public RenderPanel(List<GameObject> objects, List<Surface> surfaces,
                       List<RopeConstraint> ropeConstraints, Camera camera) {
        this.objects = objects;
        this.surfaces = surfaces;
        this.ropeConstraints = ropeConstraints;
        this.camera = camera;
        setBackground(new Color(18, 18, 18));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double focalLength = (getHeight() / 2.0) / Math.tan(Math.toRadians(camera.fov / 2.0));

        drawGrid(g2, focalLength);
        drawInclines(g2, focalLength);
        drawRopes(g2, focalLength);
        drawObjects(g2, focalLength);
        drawForceVectors(g2, focalLength);
        drawLegend(g2);
    }

    private void drawGrid(Graphics2D g2, double focalLength) {
        g2.setColor(new Color(60, 60, 60));
        for (int i = -GRID_HALF_SIZE; i <= GRID_HALF_SIZE; i += GRID_STEP) {
            drawLine(g2, focalLength, new Vector3(-GRID_HALF_SIZE, 0, i), new Vector3(GRID_HALF_SIZE, 0, i));
            drawLine(g2, focalLength, new Vector3(i, 0, -GRID_HALF_SIZE), new Vector3(i, 0, GRID_HALF_SIZE));
        }
    }

    private void drawInclines(Graphics2D g2, double focalLength) {
        if (surfaces == null) return;
        for (Surface surface : surfaces) {
            if (surface.tangent == null || surface.length == null || surface.width == null) continue;

            Vector3 bottomCenter = surface.point;
            Vector3 topCenter = surface.point.add(surface.tangent.multiply(surface.length));
            Vector3 widthAxis = new Vector3(0, 0, 1).multiply(surface.width / 2.0);

            Vector3 bottomLeft = bottomCenter.subtract(widthAxis);
            Vector3 bottomRight = bottomCenter.add(widthAxis);
            Vector3 topLeft = topCenter.subtract(widthAxis);
            Vector3 topRight = topCenter.add(widthAxis);

            g2.setColor(new Color(100, 100, 115));
            drawLine(g2, focalLength, bottomLeft, bottomRight);
            drawLine(g2, focalLength, topLeft, topRight);
            drawLine(g2, focalLength, bottomLeft, topLeft);
            drawLine(g2, focalLength, bottomRight, topRight);
            drawLine(g2, focalLength, bottomLeft, topRight);
        }
    }

    private void drawRopes(Graphics2D g2, double focalLength) {
        if (ropeConstraints == null) return;
        g2.setColor(new Color(200, 200, 200));
        for (RopeConstraint rope : ropeConstraints) {
            if (rope.pulleyPosition == null) continue;
            drawLine(g2, focalLength, rope.objectA.position, rope.pulleyPosition);
            drawLine(g2, focalLength, rope.objectB.position, rope.pulleyPosition);
        }
    }

    private void drawLine(Graphics2D g2, double focalLength, Vector3 a, Vector3 b) {
        Point p1 = projectPoint(a, focalLength);
        Point p2 = projectPoint(b, focalLength);
        if (p1 == null || p2 == null) return;
        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    private void drawObjects(Graphics2D g2, double focalLength) {
        g2.setColor(Color.WHITE);
        List<Projected> projected = new ArrayList<>();
        for (GameObject obj : objects) {
            if (obj.beamLength > 0) {
                drawBeam(g2, focalLength, obj);
                continue;
            }
            Vector3 cam = camera.worldToCamera(obj.position);
            if (cam.z <= camera.nearPlane) continue;
            double scale = focalLength / cam.z;
            int screenX = (int) (getWidth() / 2.0 + cam.x * scale);
            int screenY = (int) (getHeight() / 2.0 - cam.y * scale);
            int screenRadius = (int) (obj.radius * scale);
            projected.add(new Projected(screenX, screenY, screenRadius, cam.z));
        }
        projected.sort((a, b) -> Double.compare(b.depth, a.depth));
        for (Projected p : projected) {
            g2.fillOval(p.x - p.radius, p.y - p.radius, p.radius * 2, p.radius * 2);
        }
    }

    private void drawBeam(Graphics2D g2, double focalLength, GameObject beam) {
        double angle = beam.angularPosition;
        Vector3 dir = new Vector3(Math.cos(angle), Math.sin(angle), 0);
        Vector3 end1 = beam.position.add(dir.multiply(beam.beamLength / 2.0));
        Vector3 end2 = beam.position.subtract(dir.multiply(beam.beamLength / 2.0));
        g2.setColor(new Color(180, 140, 100));
        g2.setStroke(new BasicStroke(4f));
        drawLine(g2, focalLength, end1, end2);
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawForceVectors(Graphics2D g2, double focalLength) {
        for (GameObject obj : objects) {
            drawForceVector(g2, focalLength, obj.position, obj.lastGravityForce, new Color(255, 220, 80));
            drawForceVector(g2, focalLength, obj.position, obj.lastNormalForce, new Color(80, 220, 255));
            drawForceVector(g2, focalLength, obj.position, obj.lastFrictionForce, new Color(255, 140, 60));
            drawForceVector(g2, focalLength, obj.position, obj.lastAppliedForceUsed, new Color(230, 90, 230));
            drawForceVector(g2, focalLength, obj.position, obj.lastTensionForce, new Color(120, 255, 150));
            drawForceVector(g2, focalLength, obj.position, obj.lastNetForce, Color.WHITE, 3f);
            drawForceVector(g2, focalLength, obj.position, obj.lastCircularForce, new Color(180, 120, 255));
            drawForceVector(g2, focalLength, obj.position, obj.lastSpringForce, new Color(255, 200, 120));
        }
    }

    private void drawForceVector(Graphics2D g2, double focalLength, Vector3 origin, Vector3 force, Color color) {
        drawForceVector(g2, focalLength, origin, force, color, 2f);
    }

    private void drawForceVector(Graphics2D g2, double focalLength, Vector3 origin, Vector3 force, Color color, float strokeWidth) {
        if (force.magnitude() < 0.01) return;

        Vector3 tip = origin.add(force.multiply(FORCE_SCALE));
        Point p1 = projectPoint(origin, focalLength);
        Point p2 = projectPoint(tip, focalLength);
        if (p1 == null || p2 == null) return;

        g2.setColor(color);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        drawArrowhead(g2, p1, p2);
    }

    private void drawArrowhead(Graphics2D g2, Point from, Point to) {
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        int arrowLength = 8;
        double arrowAngle = Math.toRadians(25);
        int x1 = (int) (to.x - arrowLength * Math.cos(angle - arrowAngle));
        int y1 = (int) (to.y - arrowLength * Math.sin(angle - arrowAngle));
        int x2 = (int) (to.x - arrowLength * Math.cos(angle + arrowAngle));
        int y2 = (int) (to.y - arrowLength * Math.sin(angle + arrowAngle));
        g2.drawLine(to.x, to.y, x1, y1);
        g2.drawLine(to.x, to.y, x2, y2);
    }

    private void drawLegend(Graphics2D g2) {
        g2.setFont(new Font("Monospaced", Font.PLAIN, 13));
        int x = 15, y = 25, spacing = 20;
        String[] labels = { "Gravity", "Normal", "Friction", "Applied", "Tension", "Centripetal", "Spring", "Net Force" };
        Color[] colors = {
                new Color(255, 220, 80), new Color(80, 220, 255),
                new Color(255, 140, 60), new Color(230, 90, 230),
                new Color(120, 255, 150), new Color(180, 120, 255),
                new Color(255, 200, 120), Color.WHITE
        };
        for (int i = 0; i < labels.length; i++) {
            g2.setColor(colors[i]);
            g2.fillRect(x, y + i * spacing - 10, 12, 12);
            g2.setColor(new Color(220, 220, 220));
            g2.drawString(labels[i], x + 20, y + i * spacing);
        }
    }

    private Point projectPoint(Vector3 world, double focalLength) {
        Vector3 cam = camera.worldToCamera(world);
        if (cam.z <= camera.nearPlane) return null;
        double scale = focalLength / cam.z;
        int screenX = (int) (getWidth() / 2.0 + cam.x * scale);
        int screenY = (int) (getHeight() / 2.0 - cam.y * scale);
        return new Point(screenX, screenY);
    }

    private static class Projected {
        int x, y, radius;
        double depth;
        Projected(int x, int y, int radius, double depth) {
            this.x = x; this.y = y; this.radius = radius; this.depth = depth;
        }
    }
}