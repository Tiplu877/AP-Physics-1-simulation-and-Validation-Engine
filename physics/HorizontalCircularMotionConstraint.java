package physics;

import math.Vector3;
import scene.GameObject;

public class HorizontalCircularMotionConstraint {
    public GameObject object;
    public Vector3 center; // only x/z matter; y is held at the car's starting road height
    public double radius;
    private boolean skidding = false;

    public HorizontalCircularMotionConstraint(GameObject object, Vector3 center) {
        this.object = object;
        this.center = center;
        Vector3 flatOffset = new Vector3(object.position.x - center.x, 0, object.position.z - center.z);
        this.radius = flatOffset.magnitude();
    }

    public boolean isSkidding() {
        return skidding;
    }

    // Call after gravity has been added to forceAccumulator, before object.update(dt).
    public void apply() {
        // Treat the road as flat and rigid: cancel whatever vertical force is currently
        // acting (gravity), same role as a real road's normal force. This bypasses the
        // general Surface/friction pipeline entirely -- circular friction here needs to
        // point toward the center, not oppose velocity like straight-line kinetic friction does.
        double verticalForce = object.forceAccumulator.y;
        Vector3 normalForce = new Vector3(0, -verticalForce, 0);
        object.addForce(normalForce);
        object.lastNormalForce = normalForce;

        if (skidding) {
            object.lastCircularForce = new Vector3(0, 0, 0);
            return; // already lost grip -- car continues in a straight line from here on
        }

        Vector3 flatOffset = new Vector3(object.position.x - center.x, 0, object.position.z - center.z);
        double distance = flatOffset.magnitude();
        if (distance < 1e-6) return;
        Vector3 radialDirection = flatOffset.multiply(-1.0 / distance); // unit vector, points toward center

        Vector3 horizontalVelocity = new Vector3(object.velocity.x, 0, object.velocity.z);
        double speed = horizontalVelocity.magnitude();

        double requiredCentripetal = object.mass * speed * speed / radius;
        double normalMagnitude = normalForce.magnitude();
        double maxFriction = object.staticFriction * normalMagnitude;

        if (requiredCentripetal > maxFriction) {
            skidding = true; // not enough grip left to hold the curve -- skids outward from here
            object.lastCircularForce = new Vector3(0, 0, 0);
            return;
        }

        Vector3 frictionForce = radialDirection.multiply(requiredCentripetal);
        object.addForce(frictionForce);
        object.lastCircularForce = frictionForce;
    }

    // After integration: keep the car pinned to road height, and exactly on the circle
    // unless it's already skidding (in which case let it go straight).
    public void correct() {
        object.position = new Vector3(object.position.x, center.y, object.position.z);
        object.velocity = new Vector3(object.velocity.x, 0, object.velocity.z);

        if (skidding) return;

        Vector3 flatOffset = new Vector3(object.position.x - center.x, 0, object.position.z - center.z);
        double distance = flatOffset.magnitude();
        if (distance < 1e-6) return;
        Vector3 radialDirection = flatOffset.multiply(1.0 / distance);
        object.position = center.add(radialDirection.multiply(radius));
    }
}