package scene;

import math.Vector3;
import java.util.ArrayList;
import java.util.List;

public class GameObject {
    public Vector3 position;
    public Vector3 velocity;
    public double mass;
    public Vector3 forceAccumulator;
    public double radius;
    public double beamLength = 0; // 0 = rendered as a sphere (default); >0 = rendered as a rotating line/beam instead
    public double staticFriction = 0.6;
    public double kineticFriction = 0.4;
    public Vector3 lastGravityForce = new Vector3(0, 0, 0);
    public Vector3 lastNormalForce = new Vector3(0, 0, 0);
    public Vector3 lastFrictionForce = new Vector3(0, 0, 0);
    public Vector3 lastAppliedForceUsed = new Vector3(0, 0, 0);
    public Vector3 lastNetForce = new Vector3(0, 0, 0);
    public Vector3 lastTensionForce = new Vector3(0, 0, 0);
    public Vector3 lastCircularForce = new Vector3(0, 0, 0);
    public Vector3 lastSpringForce = new Vector3(0, 0, 0);
    public boolean useNewtonianGravity = false;
    public Vector3 appliedForce = new Vector3(0, 0, 0);

    private final Vector3 initialPosition;
    private final Vector3 initialVelocity;
    private double cumulativeWorkByNetForce = 0;
    private double distanceTraveled = 0;
    private double elapsedTime = 0;

    public double angularPosition = 0;      // theta, in radians
    public double angularVelocity = 0;      // omega, in radians/sec
    public double angularAcceleration = 0;  // alpha, in radians/sec^2 -- torque-driven via update()
    public double momentOfInertia = 1.0;    // set this based on the object's actual shape (see MomentOfInertiaSolver)
    public Vector3 torqueAccumulator = new Vector3(0, 0, 0);
    public Vector3 lastTorque = new Vector3(0, 0, 0); // for display/debugging, mirrors lastNetForce's role

    private Vector3 lastAcceleration = new Vector3(0, 0, 0);
    private final List<MotionSample> history = new ArrayList<>();

    private static final double EQUILIBRIUM_THRESHOLD = 0.05; // Newtons -- below this, treat net force as "zero"

    public GameObject(Vector3 position, Vector3 velocity, double mass) {
        this(position, velocity, mass, 0.5);
    }

    public GameObject(Vector3 position, Vector3 velocity, double mass, double radius) {
        if (mass <= 0) {
            throw new IllegalArgumentException("mass must be positive");
        }
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
        this.radius = radius;
        this.forceAccumulator = new Vector3(0, 0, 0);
        this.initialPosition = position;
        this.initialVelocity = velocity;
    }

    public void addForce(Vector3 force) {
        forceAccumulator = forceAccumulator.add(force);
    }

    public void addTorque(Vector3 torque) {
        torqueAccumulator = torqueAccumulator.add(torque);
    }

    // Applies a force at some offset from the rotation axis, converting it into torque
    // automatically via the cross product (tau = r x F) -- this is usually more convenient
    // than computing torque by hand when you already have a force and where it's applied.
    public void addTorqueFromForce(Vector3 leverArm, Vector3 force) {
        addTorque(leverArm.cross(force));
    }

    public void setAppliedForce(Vector3 force) {
        appliedForce = force;
    }

    public void clearAppliedForce() {
        appliedForce = new Vector3(0, 0, 0);
    }

    public void update(double dt) {
        Vector3 previousPosition = position;
        lastNetForce = forceAccumulator;

        Vector3 acceleration = forceAccumulator.multiply(1.0 / mass);
        velocity = velocity.add(acceleration.multiply(dt));
        position = position.add(velocity.multiply(dt));

        distanceTraveled += position.subtract(previousPosition).magnitude();
        cumulativeWorkByNetForce += lastNetForce.dot(position.subtract(previousPosition));
        elapsedTime += dt;
        lastAcceleration = acceleration;
        history.add(new MotionSample(elapsedTime, position, velocity, acceleration));

        lastTorque = torqueAccumulator;
        angularAcceleration = torqueAccumulator.z / momentOfInertia; // AP1 keeps rotation to a single (z) axis
        angularVelocity += angularAcceleration * dt;
        angularPosition += angularVelocity * dt;
        torqueAccumulator = new Vector3(0, 0, 0);

        forceAccumulator = new Vector3(0, 0, 0);
    }

    // Straight-line, direction-aware change from start (a vector -- can be "backwards")
    public Vector3 getDisplacement() {
        return position.subtract(initialPosition);
    }

    // Total path length traveled -- always >= |displacement|, only equal for straight-line motion
    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public Vector3 getAverageVelocity() {
        if (elapsedTime == 0) return new Vector3(0, 0, 0);
        return getDisplacement().divide(elapsedTime);
    }

    public Vector3 getAverageAcceleration() {
        if (elapsedTime == 0) return new Vector3(0, 0, 0);
        return velocity.subtract(initialVelocity).divide(elapsedTime);
    }

    public double getAngularPosition() {
        return angularPosition;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public double getAngularAcceleration() {
        return angularAcceleration;
    }

    // Tangential speed at the object's own radius, if it's rotating about its own axis --
    // this is the v = omega * r relationship connecting linear and angular motion.
    public double getTangentialSpeed() {
        return Math.abs(angularVelocity) * radius;
    }

    public Vector3 getInstantaneousAcceleration() {
        return lastAcceleration;
    }

    public List<MotionSample> getHistory() {
        return history;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    // Velocity of this object as observed from another object's reference frame.
    // E.g. if a passenger throws a ball forward on a moving train, the ball's
    // velocity relative to the ground differs from its velocity relative to the train.
    public Vector3 getVelocityRelativeTo(GameObject other) {
        return velocity.subtract(other.velocity);
    }

    // Same idea, but for an arbitrary reference frame velocity rather than another object
    // (e.g. "relative to the ground, which isn't itself a GameObject")
    public Vector3 getVelocityRelativeTo(Vector3 referenceFrameVelocity) {
        return velocity.subtract(referenceFrameVelocity);
    }

    // True if net force is approximately zero -- object isn't accelerating, whether it's
    // at rest (static equilibrium) or moving at constant velocity (dynamic equilibrium).
    public boolean isInEquilibrium() {
        return lastNetForce.magnitude() < EQUILIBRIUM_THRESHOLD;
    }

    public boolean isInStaticEquilibrium() {
        return isInEquilibrium() && velocity.magnitude() < 0.05;
    }

    public boolean isInDynamicEquilibrium() {
        return isInEquilibrium() && velocity.magnitude() >= 0.05;
    }

    public double getKineticEnergy() {
        return 0.5 * mass * velocity.magnitude_square();
    }

    // referenceHeight is whatever y-position counts as "zero" PE for this scenario
    // (usually the ground/floor the object is above, not necessarily y=0 globally)
    public double getPotentialEnergy(double g, double referenceHeight) {
        return mass * g * (position.y - referenceHeight);
    }

    public double getTotalMechanicalEnergy(double g, double referenceHeight) {
        return getKineticEnergy() + getPotentialEnergy(g, referenceHeight);
    }

    public double getCumulativeWorkByNetForce() {
        return cumulativeWorkByNetForce;
    }

    // Compares accumulated net work against actual change in KE since this object was
    // created. They should match closely -- this is the work-energy theorem holding true
    // numerically. A small gap is expected (semi-implicit Euler integration error), not a bug;
    // a LARGE gap would suggest something's wrong with how a force is being applied.
    public double getWorkEnergyDiscrepancy() {
        double currentKE = getKineticEnergy();
        double initialKE = 0.5 * mass * initialVelocity.magnitude_square();
        double actualDeltaKE = currentKE - initialKE;
        return cumulativeWorkByNetForce - actualDeltaKE;
    }

    // Instantaneous power delivered by the net force right now: P = F . v
    public double getInstantaneousPower() {
        return lastNetForce.dot(velocity);
    }

    // Average power since this object was created: total work done / total time elapsed
    public double getAveragePower() {
        if (elapsedTime == 0) return 0;
        return cumulativeWorkByNetForce / elapsedTime;
    }

    public Vector3 getMomentum() {
        return velocity.multiply(mass);
    }

    public void debug() {
        System.out.println("Position     : " + position);
        System.out.println("Velocity     : " + velocity);
        System.out.println("Acceleration : " + lastAcceleration);
        System.out.println("Displacement : " + getDisplacement());
        System.out.println("Distance     : " + String.format("%.2f", distanceTraveled));
        System.out.println("Avg Velocity : " + getAverageVelocity());
        System.out.println("Force        : " + forceAccumulator);
        System.out.println("Mass         : " + mass);
        System.out.println("----------------------------");
    }
}
