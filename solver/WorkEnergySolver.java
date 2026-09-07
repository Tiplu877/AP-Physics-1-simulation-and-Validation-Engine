package solver;

import math.Vector3;

public class WorkEnergySolver {

    // Classic form: W = F * d * cos(theta), where theta is the angle between
    // the force and the direction of displacement.
    public static double work(double forceMagnitude, double displacement, double angleDegrees) {
        return forceMagnitude * displacement * Math.cos(Math.toRadians(angleDegrees));
    }

    // Vector form -- same thing, but when you already have both as Vector3s
    // rather than a magnitude + angle.
    public static double work(Vector3 force, Vector3 displacement) {
        return force.dot(displacement);
    }

    public static double kineticEnergy(double mass, double speed) {
        return 0.5 * mass * speed * speed;
    }

    // Solves the work-energy theorem for final speed, given net work done and starting speed.
    // Throws if the math implies negative KE (net work more negative than the object's
    // starting KE allows -- not physically possible, means the inputs are inconsistent).
    public static double finalSpeedFromWork(double mass, double initialSpeed, double netWork) {
        double initialKE = kineticEnergy(mass, initialSpeed);
        double finalKE = initialKE + netWork;
        if (finalKE < 0) {
            throw new IllegalArgumentException("Net work implies negative kinetic energy -- inconsistent inputs");
        }
        return Math.sqrt(2 * finalKE / mass);
    }
}