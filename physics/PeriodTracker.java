package physics;

import math.Vector3;
import scene.GameObject;

import java.util.ArrayList;
import java.util.List;

public class PeriodTracker {
    final GameObject object;
    private final Vector3 center;
    private Double previousTangentialVelocitySign = null; // null until the first frame with real motion
    private final List<Double> turningPointTimes = new ArrayList<>();

    public PeriodTracker(GameObject object, Vector3 center) {
        this.object = object;
        this.center = center;
    }

    // Call once per frame, after the object's position/velocity have been updated for this step.
    public void update(double elapsedTime) {
        Vector3 toObject = object.position.subtract(center);
        double distance = toObject.magnitude();
        if (distance < 1e-6) return;

        Vector3 radialDirection = toObject.divide(distance);
        // Tangential velocity: whatever's left of velocity after removing the radial component
        double radialSpeed = object.velocity.dot(radialDirection);
        Vector3 tangentialVelocity = object.velocity.subtract(radialDirection.multiply(radialSpeed));

        // Project onto a consistent reference direction (perpendicular to radial, in the
        // swing plane) to get a signed value we can watch for sign flips -- using x here
        // since these pendulum/loop scenarios swing in the x-y plane.
        double signedTangentialVelocity = tangentialVelocity.x;

        if (Math.abs(signedTangentialVelocity) < 1e-4) return; // too close to zero to trust the sign

        double currentSign = Math.signum(signedTangentialVelocity);

        if (previousTangentialVelocitySign != null && currentSign != previousTangentialVelocitySign) {
            turningPointTimes.add(elapsedTime);
        }
        previousTangentialVelocitySign = currentSign;
    }

    // Returns the most recently measured full period, or -1 if not enough data yet
    // (needs at least 3 turning points -- start, one side, back to the same side).
    public double getMeasuredPeriod() {
        int n = turningPointTimes.size();
        if (n < 3) return -1;
        // Time between the two most recent SAME-side turning points (skip one in between)
        return turningPointTimes.get(n - 1) - turningPointTimes.get(n - 3);
    }

    public int getTurningPointCount() {
        return turningPointTimes.size();
    }
}