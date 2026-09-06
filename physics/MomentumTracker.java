package physics;

import math.Vector3;
import scene.GameObject;

import java.util.List;

public class MomentumTracker {

    public static Vector3 totalMomentum(List<GameObject> objects) {
        Vector3 total = new Vector3(0, 0, 0);
        for (GameObject obj : objects) {
            total = total.add(obj.getMomentum());
        }
        return total;
    }

    public static double totalKineticEnergy(List<GameObject> objects) {
        double total = 0;
        for (GameObject obj : objects) {
            total += obj.getKineticEnergy();
        }
        return total;
    }
    // Classifies a collision based on its restitution coefficient. This describes the
// COLLISION MODEL being used, not something detected after the fact from energy loss --
// e=1 means no KE is lost (elastic), e=0 means max KE is lost while momentum is still
// conserved (perfectly inelastic, e.g. objects stick together), anything between is
// partially inelastic. This is the common AP1 point of confusion worth making explicit:
// momentum is ALWAYS conserved in a collision, but kinetic energy usually isn't.
    public static String classifyCollision(double restitution) {
        if (restitution >= 0.99) return "Elastic (KE conserved)";
        if (restitution <= 0.01) return "Perfectly Inelastic (max KE lost)";
        return "Partially Inelastic (some KE lost)";
    }
}