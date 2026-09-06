package physics;

import math.Vector3;
import scene.GameObject;

public class RopeConstraint {
    public GameObject objectA;
    public GameObject objectB;
    public Vector3 directionAwayFromPulleyA;
    public Vector3 directionAwayFromPulleyB;
    public Vector3 pulleyPosition;

    private boolean hasGoneSlack = false; // once true, stays true for the rest of the run

    public RopeConstraint(GameObject objectA, Vector3 directionAwayFromPulleyA,
                          GameObject objectB, Vector3 directionAwayFromPulleyB) {
        this(objectA, directionAwayFromPulleyA, objectB, directionAwayFromPulleyB, null);
    }

    public RopeConstraint(GameObject objectA, Vector3 directionAwayFromPulleyA,
                          GameObject objectB, Vector3 directionAwayFromPulleyB,
                          Vector3 pulleyPosition) {
        this.objectA = objectA;
        this.directionAwayFromPulleyA = directionAwayFromPulleyA.normalize();
        this.objectB = objectB;
        this.directionAwayFromPulleyB = directionAwayFromPulleyB.normalize();
        this.pulleyPosition = pulleyPosition;
    }

    public boolean hasGoneSlack() {
        return hasGoneSlack;
    }

    // groundedA/groundedB tell the constraint whether either end currently rests on a
    // surface. If either does, the rope can't meaningfully enforce "shared acceleration"
    // anymore -- tension goes to zero and stays zero (latched), so a bounce off the
    // ground doesn't re-tighten the rope on a later frame.
    public double apply(boolean groundedA, boolean groundedB) {
        if (hasGoneSlack || groundedA || groundedB) {
            hasGoneSlack = true;
            objectA.lastTensionForce = new Vector3(0, 0, 0);
            objectB.lastTensionForce = new Vector3(0, 0, 0);
            return 0;
        }

        double externalA = objectA.forceAccumulator.dot(directionAwayFromPulleyA);
        double externalB = objectB.forceAccumulator.dot(directionAwayFromPulleyB);

        double massA = objectA.mass;
        double massB = objectB.mass;

        double acceleration = (externalA - externalB) / (massA + massB);
        double tension = externalA - massA * acceleration;

        if (tension < 0) {
            hasGoneSlack = true;
            objectA.lastTensionForce = new Vector3(0, 0, 0);
            objectB.lastTensionForce = new Vector3(0, 0, 0);
            return 0;
        }

        objectA.lastTensionForce = directionAwayFromPulleyA.multiply(-tension);
        objectB.lastTensionForce = directionAwayFromPulleyB.multiply(-tension);

        objectA.addForce(objectA.lastTensionForce);
        objectB.addForce(objectB.lastTensionForce);

        return tension;
    }
}