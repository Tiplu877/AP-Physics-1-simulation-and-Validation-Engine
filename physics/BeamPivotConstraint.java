package physics;

import math.Vector3;
import scene.GameObject;

import java.util.ArrayList;
import java.util.List;

public class BeamPivotConstraint {
    public GameObject beam;
    public Vector3 pivot;
    private final List<WeightSpec> weights = new ArrayList<>();
    public double dampingCoefficient = 5.0;
    public BeamPivotConstraint(GameObject beam, Vector3 pivot) {
        this.beam = beam;
        this.pivot = pivot;
    }

    // leverArm: signed distance from pivot along the beam's own axis (positive = one
    // side, negative = the other). weightForce: downward force (e.g. mass * g).
    // marker: a separate GameObject used purely to visualize the weight's position --
    // it must NOT be added via world.addObject(), since its position is fully
    // controlled here every frame, not by ordinary physics integration.
    public void addWeight(double leverArm, double weightForce, GameObject marker) {
        weights.add(new WeightSpec(leverArm, weightForce, marker));
    }

    // Call during force/torque accumulation, before beam.update(dt).
    public void apply() {
        for (WeightSpec w : weights) {
            double angle = beam.angularPosition;
            // Where this weight currently sits, given the beam's current tilt
            Vector3 r = new Vector3(w.leverArm * Math.cos(angle), w.leverArm * Math.sin(angle), 0);
            Vector3 force = new Vector3(0, -w.weightForce, 0);
            double torqueZ = r.x * force.y - r.y * force.x; // r x F, z-component only (2D rotation)
            beam.addTorque(new Vector3(0, 0, torqueZ));

            if (w.marker != null) {
                w.marker.position = pivot.add(r);
            }
        }
        double dampingTorque = -dampingCoefficient * beam.angularVelocity;
        beam.addTorque(new Vector3(0, 0, dampingTorque));
    }

    // Keeps the beam pinned at the pivot -- it only rotates, never translates.
    // Call after beam.update(dt).
    public void correct() {
        beam.position = pivot;
        beam.velocity = new Vector3(0, 0, 0);
    }

    private static class WeightSpec {
        double leverArm;
        double weightForce;
        GameObject marker;
        WeightSpec(double leverArm, double weightForce, GameObject marker) {
            this.leverArm = leverArm;
            this.weightForce = weightForce;
            this.marker = marker;
        }
    }
}
