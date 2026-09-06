package physics;

import math.Vector3;
import scene.GameObject;

public class RollingConstraint {
    public GameObject object;
    public Surface surface;

    public RollingConstraint(GameObject object, Surface surface) {
        this.object = object;
        this.surface = surface;
    }

    // Call during force accumulation (after gravity has been added), before object.update(dt).
    // Bypasses the general Surface friction pipeline entirely -- rolling friction plays a
    // fundamentally different role (enforcing a constraint, not opposing sliding) than the
    // kinetic/static friction model used elsewhere, same reasoning as the car-on-curve constraint.
    public void apply() {
        Vector3 normal = surface.normal;
        // Tangent direction in the surface's plane, derived directly from the normal --
        // matches Surface.incline()'s own tangent/normal convention exactly.
        Vector3 tangent = new Vector3(normal.y, -normal.x, 0).normalize();

        // Normal force: cancel whatever's currently pressing the ball into the ramp,
        // same role as the regular incline normal force.
        double intoSurface = -object.forceAccumulator.dot(normal);
        Vector3 normalForce = intoSurface > 0 ? normal.multiply(intoSurface) : new Vector3(0, 0, 0);
        object.addForce(normalForce);
        object.lastNormalForce = normalForce;

        // Net force already pushing the ball along the ramp (gravity's downslope component).
        double netTangentialForce = object.forceAccumulator.dot(tangent);
        double I = object.momentOfInertia;
        double m = object.mass;
        double r = object.radius;

        // Derived from solving F=ma and torque=I*alpha together under the constraint
        // a = alpha*r (see explanation above) -- this is the exact friction force that
        // makes both equations agree, not an approximation.
        double frictionMagnitude = -netTangentialForce * I / (m * r * r + I);

        Vector3 frictionForce = tangent.multiply(frictionMagnitude);
        object.addForce(frictionForce);
        object.lastFrictionForce = frictionForce;

        double torqueZ = r * frictionMagnitude;
        object.addTorque(new Vector3(0, 0, torqueZ));
    }

    // Call after object.update(dt) -- keeps the ball pinned exactly on the ramp's surface.
    // This models the ball as always staying in contact (no bouncing off, no leaving the
    // surface) -- a real ball could hop or lose contact on a bumpy/steep enough setup,
    // which this doesn't account for.
    public void correct() {
        double distance = surface.distanceTo(object.position);
        double correction = distance - object.radius;
        object.position = object.position.subtract(surface.normal.multiply(correction));

        double velocityIntoSurface = object.velocity.dot(surface.normal);
        object.velocity = object.velocity.subtract(surface.normal.multiply(velocityIntoSurface));
    }
}