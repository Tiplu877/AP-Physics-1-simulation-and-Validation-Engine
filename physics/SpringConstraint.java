package physics;

import math.Vector3;
import scene.GameObject;

public class SpringConstraint {
    public GameObject object;
    public Vector3 anchor;      // fixed point the spring is attached to (a wall, ceiling, etc.)
    public double restLength;   // the spring's natural, unstretched length
    public double springConstant; // k, in N/m -- stiffness

    public SpringConstraint(GameObject object, Vector3 anchor, double restLength, double springConstant) {
        this.object = object;
        this.anchor = anchor;
        this.restLength = restLength;
        this.springConstant = springConstant;
    }

    // Call during force accumulation, alongside gravity/applied force -- a spring is
    // just another force, not a rigid constraint like the rope/circular ones, so there's
    // no separate correct() step needed afterward.
    public void apply() {
        Vector3 toObject = object.position.subtract(anchor);
        double currentLength = toObject.magnitude();

        if (currentLength < 1e-6) {
            object.lastSpringForce = new Vector3(0, 0, 0);
            return; // object is sitting exactly on the anchor -- direction is undefined
        }

        Vector3 directionAwayFromAnchor = toObject.divide(currentLength);
        double stretch = currentLength - restLength; // positive = stretched, negative = compressed

        // F = -k*x. If stretched (stretch > 0), force should pull back TOWARD the anchor,
        // i.e. opposite directionAwayFromAnchor -- hence the negative sign here.
        Vector3 springForce = directionAwayFromAnchor.multiply(-springConstant * stretch);

        object.addForce(springForce);
        object.lastSpringForce = springForce;
    }
    public double getPotentialEnergy() {
        return getPotentialEnergyAt(object.position);
    }

    // Computed from an arbitrary position rather than always object.position, so this
// same formula can be reused against historical MotionSample positions for graphing --
// anchor/restLength/springConstant don't change over time, only position does.
    public double getPotentialEnergyAt(Vector3 position) {
        double currentLength = position.subtract(anchor).magnitude();
        double stretch = currentLength - restLength;
        return 0.5 * springConstant * stretch * stretch;
    }
}