package physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import math.Vector3;
import scene.GameObject;

public class PhysicsWorld {
    private ArrayList<GameObject> objects;
    private List<Surface> surfaces;
    private List<RopeConstraint> ropeConstraints;
    private List<CircularMotionConstraint> circularConstraints;
    private List<HorizontalCircularMotionConstraint> horizontalCircularConstraints;
    private List<SpringConstraint> springConstraints;
    private List<PeriodTracker> periodTrackers;
    private List<RollingConstraint> rollingConstraints;
    private List<BeamPivotConstraint> beamPivotConstraints;
    private final Set<GameObject> frozen = new HashSet<>();
    private Vector3 gravity = new Vector3(0, -9.81, 0);
    private double restitution = 0.6;
    private double gravitationalConstant = 1.0; // exaggerated for visualization -- NOT the real 6.674e-11
    private static final double CONTACT_EPSILON = 0.02;
    private static final double MIN_SLIDE_SPEED = 0.01;

    public PhysicsWorld() {
        objects = new ArrayList<>();
        surfaces = new ArrayList<>();
        ropeConstraints = new ArrayList<>();
        circularConstraints = new ArrayList<>();
        horizontalCircularConstraints = new ArrayList<>();
        springConstraints = new ArrayList<>();
        periodTrackers = new ArrayList<>();
        rollingConstraints = new ArrayList<>();
        surfaces.add(new Surface(new Vector3(0, 0, 0), new Vector3(0, 1, 0))); // default flat ground
        beamPivotConstraints = new ArrayList<>();
    }

    public void addObject(GameObject object) { objects.add(object); }
    public void addSurface(Surface surface) { surfaces.add(surface); }
    public void addRopeConstraint(RopeConstraint rope) { ropeConstraints.add(rope); }
    public void addCircularMotionConstraint(CircularMotionConstraint c) { circularConstraints.add(c); }
    public void addHorizontalCircularMotionConstraint(HorizontalCircularMotionConstraint c) { horizontalCircularConstraints.add(c); }
    public void addSpringConstraint(SpringConstraint spring) { springConstraints.add(spring); }
    public void addPeriodTracker(PeriodTracker tracker) { periodTrackers.add(tracker); }
    public void addRollingConstraint(RollingConstraint c) { rollingConstraints.add(c); }
    public void addBeamPivotConstraint(BeamPivotConstraint c) { beamPivotConstraints.add(c); }
    public List<BeamPivotConstraint> getBeamPivotConstraints() { return beamPivotConstraints; }
    public List<Surface> getSurfaces() { return surfaces; }
    public List<RopeConstraint> getRopeConstraints() { return ropeConstraints; }
    public List<CircularMotionConstraint> getCircularMotionConstraints() { return circularConstraints; }
    public List<HorizontalCircularMotionConstraint> getHorizontalCircularMotionConstraints() { return horizontalCircularConstraints; }
    public List<SpringConstraint> getSpringConstraints() { return springConstraints; }
    public List<PeriodTracker> getPeriodTrackers() { return periodTrackers; }
    public List<RollingConstraint> getRollingConstraints() { return rollingConstraints; }

    public void setGravitationalConstant(double G) { this.gravitationalConstant = G; }
    public double getGravitationalConstant() { return gravitationalConstant; }
    public double getRestitution() { return restitution; }
    public void setRestitution(double restitution) { this.restitution = restitution; }

    public void freeze(GameObject object) {
        frozen.add(object);
        object.velocity = new Vector3(0, 0, 0);
    }

    public boolean isFrozen(GameObject object) {
        return frozen.contains(object);
    }

    private boolean isRolling(GameObject object) {
        for (RollingConstraint c : rollingConstraints) {
            if (c.object == object) return true;
        }
        return false;
    }

    public void update(double dt) {
        Map<GameObject, Surface> contactSurfaces = new HashMap<>();

        // Pass 0: mutual (Newtonian) gravity between any objects that opt into it.
        for (GameObject object : objects) {
            if (object.useNewtonianGravity) {
                object.lastGravityForce = new Vector3(0, 0, 0);
            }
        }
        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                GameObject a = objects.get(i);
                GameObject b = objects.get(j);
                if (!a.useNewtonianGravity || !b.useNewtonianGravity) continue;
                if (isFrozen(a) || isFrozen(b)) continue;

                Vector3 delta = b.position.subtract(a.position);
                double distance = delta.magnitude();
                if (distance < 1e-6) continue;

                double forceMagnitude = gravitationalConstant * a.mass * b.mass / (distance * distance);
                Vector3 direction = delta.divide(distance);
                Vector3 forceOnA = direction.multiply(forceMagnitude);
                Vector3 forceOnB = direction.multiply(-forceMagnitude);

                a.addForce(forceOnA);
                b.addForce(forceOnB);
                a.lastGravityForce = a.lastGravityForce.add(forceOnA);
                b.lastGravityForce = b.lastGravityForce.add(forceOnB);
            }
        }

        // Pass 0.5: spring forces
        for (SpringConstraint spring : springConstraints) {
            if (isFrozen(spring.object)) continue;
            spring.apply();
        }

        // Pass 1: applied force, constant surface gravity, ground friction
        for (GameObject object : objects) {
            if (isFrozen(object)) continue;

            object.lastAppliedForceUsed = object.appliedForce;
            object.addForce(object.appliedForce);

            if (!object.useNewtonianGravity) {
                Vector3 gravityForce = gravity.multiply(object.mass);
                object.lastGravityForce = gravityForce;
                object.addForce(gravityForce);
            }

            if (isRolling(object)) {
                // RollingConstraint handles its own normal force/friction in Pass 2 --
                // skip the generic surface pipeline entirely so the two don't fight.
                contactSurfaces.put(object, null);
            } else {
                Surface contactSurface = findContactSurface(object);
                contactSurfaces.put(object, contactSurface);

                if (contactSurface != null) {
                    applyNormalAndFriction(object, contactSurface);
                } else {
                    object.lastNormalForce = new Vector3(0, 0, 0);
                    object.lastFrictionForce = new Vector3(0, 0, 0);
                }
            }
        }

        // Pass 2: rope tension, circular motion constraints, rolling constraints
        for (RopeConstraint rope : ropeConstraints) {
            boolean groundedA = contactSurfaces.get(rope.objectA) != null;
            boolean groundedB = contactSurfaces.get(rope.objectB) != null;
            rope.apply(groundedA, groundedB);
            if (rope.hasGoneSlack()) {
                freeze(rope.objectA);
                freeze(rope.objectB);
            }
        }
        for (CircularMotionConstraint c : circularConstraints) {
            c.apply();
        }
        for (HorizontalCircularMotionConstraint c : horizontalCircularConstraints) {
            c.apply();
        }
        for (RollingConstraint c : rollingConstraints) {
            c.apply();
        }
        for (BeamPivotConstraint c : beamPivotConstraints) {
            c.apply();
        }

        // Pass 3: integrate and resolve contacts
        for (GameObject object : objects) {
            if (isFrozen(object)) continue;

            Surface contactSurface = contactSurfaces.get(object);
            Vector3 tangentVelocityBefore = contactSurface != null
                    ? tangentComponent(object.velocity, contactSurface.normal) : null;

            object.update(dt);

            if (contactSurface != null) {
                resolveSurfaceContact(object, contactSurface);
                preventFrictionOvershoot(object, contactSurface, tangentVelocityBefore);
            }
        }

        for (CircularMotionConstraint c : circularConstraints) {
            c.correct();
        }
        for (HorizontalCircularMotionConstraint c : horizontalCircularConstraints) {
            c.correct();
        }
        for (RollingConstraint c : rollingConstraints) {
            c.correct();
        }
        for (BeamPivotConstraint c : beamPivotConstraints) {
            c.correct();
        }

        resolveObjectCollisions();

        for (PeriodTracker tracker : periodTrackers) {
            tracker.update(tracker.object.getElapsedTime());
        }
    }

    private Surface findContactSurface(GameObject object) {
        for (Surface surface : surfaces) {
            if (!surface.isWithinBounds(object.position)) continue;
            double distance = surface.distanceTo(object.position);
            if (distance <= object.radius + CONTACT_EPSILON) {
                return surface;
            }
        }
        return null;
    }

    private Vector3 tangentComponent(Vector3 vector, Vector3 normal) {
        double alongNormal = vector.dot(normal);
        return vector.subtract(normal.multiply(alongNormal));
    }

    private void applyNormalAndFriction(GameObject object, Surface surface) {
        double intoSurface = -object.forceAccumulator.dot(surface.normal);

        Vector3 normalForceVector;
        if (intoSurface > 0) {
            normalForceVector = surface.normal.multiply(intoSurface);
            object.addForce(normalForceVector);
        } else {
            normalForceVector = new Vector3(0, 0, 0);
        }
        object.lastNormalForce = normalForceVector;
        double normalMagnitude = normalForceVector.magnitude();

        Vector3 netTangentialForce = tangentComponent(object.forceAccumulator, surface.normal);
        double netTangentialMagnitude = netTangentialForce.magnitude();

        Vector3 tangentVelocity = tangentComponent(object.velocity, surface.normal);
        double tangentSpeed = tangentVelocity.magnitude();

        Vector3 frictionForce;
        if (tangentSpeed < MIN_SLIDE_SPEED) {
            double maxStatic = object.staticFriction * normalMagnitude;
            if (netTangentialMagnitude <= maxStatic) {
                frictionForce = netTangentialForce.multiply(-1);
            } else {
                double kineticMagnitude = object.kineticFriction * normalMagnitude;
                frictionForce = netTangentialForce.normalize().multiply(-kineticMagnitude);
            }
        } else {
            double kineticMagnitude = object.kineticFriction * normalMagnitude;
            frictionForce = tangentVelocity.normalize().multiply(-kineticMagnitude);
        }

        object.addForce(frictionForce);
        object.lastFrictionForce = frictionForce;
    }

    private void resolveSurfaceContact(GameObject object, Surface surface) {
        double distance = surface.distanceTo(object.position);
        double penetration = object.radius - distance;
        if (penetration > 0) {
            object.position = object.position.add(surface.normal.multiply(penetration));
        }

        double velocityAlongNormal = object.velocity.dot(surface.normal);
        if (velocityAlongNormal < 0) {
            Vector3 normalComponent = surface.normal.multiply(velocityAlongNormal);
            Vector3 tangentPart = object.velocity.subtract(normalComponent);
            object.velocity = tangentPart.add(normalComponent.multiply(-restitution));
        }
    }

    private void preventFrictionOvershoot(GameObject object, Surface surface, Vector3 tangentVelocityBefore) {
        Vector3 tangentVelocityAfter = tangentComponent(object.velocity, surface.normal);
        boolean wasMoving = tangentVelocityBefore.magnitude() > MIN_SLIDE_SPEED;
        boolean reversed = wasMoving && tangentVelocityBefore.dot(tangentVelocityAfter) < 0;

        if (reversed || tangentVelocityAfter.magnitude() < MIN_SLIDE_SPEED) {
            double normalSpeed = object.velocity.dot(surface.normal);
            object.velocity = surface.normal.multiply(normalSpeed);
        }
    }

    private void resolveObjectCollisions() {
        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                GameObject a = objects.get(i);
                GameObject b = objects.get(j);

                Vector3 delta = b.position.subtract(a.position);
                double distance = delta.magnitude();
                double minDistance = a.radius + b.radius;

                if (distance < minDistance && distance > 0) {
                    Vector3 normal = delta.normalize();
                    separateOverlap(a, b, normal, minDistance - distance);
                    applyCollisionImpulse(a, b, normal);
                }
            }
        }
    }

    private void separateOverlap(GameObject a, GameObject b, Vector3 normal, double overlap) {
        double totalMass = a.mass + b.mass;
        double aShare = overlap * (b.mass / totalMass);
        double bShare = overlap * (a.mass / totalMass);
        a.position = a.position.subtract(normal.multiply(aShare));
        b.position = b.position.add(normal.multiply(bShare));
    }

    private void applyCollisionImpulse(GameObject a, GameObject b, Vector3 normal) {
        Vector3 relativeVelocity = b.velocity.subtract(a.velocity);
        double velocityAlongNormal = relativeVelocity.dot(normal);
        if (velocityAlongNormal > 0) return;

        double e = restitution;
        double j = -(1 + e) * velocityAlongNormal / (1.0 / a.mass + 1.0 / b.mass);

        Vector3 impulse = normal.multiply(j);
        a.velocity = a.velocity.subtract(impulse.multiply(1.0 / a.mass));
        b.velocity = b.velocity.add(impulse.multiply(1.0 / b.mass));
    }

    public void debug() {
        for (GameObject object : objects) {
            object.debug();
        }
    }
}