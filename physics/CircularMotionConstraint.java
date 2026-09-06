package physics;

import math.Vector3;
import scene.GameObject;

public class CircularMotionConstraint {
    public GameObject object;
    public Vector3 center;
    public double radius;
    private boolean slack = false;

    public CircularMotionConstraint(GameObject object, Vector3 center) {
        this.object = object;
        this.center = center;
        this.radius = center.subtract(object.position).magnitude();
    }

    public boolean isSlack() {
        return slack;
    }

    // Call after gravity/applied/friction forces are already accumulated on the object,
    // but before object.update(dt) runs.
    public void apply() {
        if (slack) {
            object.lastCircularForce = new Vector3(0, 0, 0);
            return; // string already went slack earlier -- object flies off as a free projectile from here on
        }

        Vector3 toCenter = center.subtract(object.position);
        double distance = toCenter.magnitude();
        if (distance < 1e-6) {
            object.lastCircularForce = new Vector3(0, 0, 0);
            return;
        }
        Vector3 radialDirection = toCenter.divide(distance); // unit vector, points toward center

        // Speed component that's actually tangential (perpendicular to the string)
        double radialSpeed = object.velocity.dot(radialDirection);
        Vector3 tangentialVelocity = object.velocity.subtract(radialDirection.multiply(radialSpeed));
        double tangentialSpeed = tangentialVelocity.magnitude();

        double requiredCentripetal = object.mass * tangentialSpeed * tangentialSpeed / radius;

        // How much of gravity (already in forceAccumulator) is already pointing toward the center --
        // at the top of the loop this is positive (gravity helps), at the bottom it's negative (gravity fights it)
        double externalTowardCenter = object.forceAccumulator.dot(radialDirection);

        double tension = requiredCentripetal - externalTowardCenter;

        if (tension < 0) {
            // A string can only pull, never push. Not enough speed to need this much
            // (or this little) inward force -- the string goes slack right here.
            slack = true;
            object.lastCircularForce = new Vector3(0, 0, 0);
            return;
        }

        Vector3 tensionForce = radialDirection.multiply(tension);
        object.addForce(tensionForce);
        object.lastCircularForce = tensionForce;
    }

    // Call after object.update(dt) -- corrects small numerical drift so the object
    // stays exactly on the circle (the string is treated as perfectly rigid/inextensible).
    public void correct() {
        if (slack) return;

        Vector3 toCenter = center.subtract(object.position);
        double distance = toCenter.magnitude();
        if (distance < 1e-6) return;
        Vector3 radialDirection = toCenter.divide(distance);

        object.position = center.subtract(radialDirection.multiply(radius));

        double radialSpeed = object.velocity.dot(radialDirection);
        object.velocity = object.velocity.subtract(radialDirection.multiply(radialSpeed));
    }
}