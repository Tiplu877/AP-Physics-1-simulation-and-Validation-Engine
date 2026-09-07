package solver;

public class TorqueSolver {

    // Magnitude of torque: tau = r * F * sin(theta), where theta is the angle
    // between the lever arm and the force. A force applied straight along the
    // lever arm (theta=0) produces zero torque -- it only pushes/pulls, doesn't turn.
    public static double torque(double leverArmLength, double forceMagnitude, double angleDegrees) {
        return leverArmLength * forceMagnitude * Math.sin(Math.toRadians(angleDegrees));
    }

    // Rotational analog of F = ma: alpha = tau / I
    public static double angularAccelerationFromTorque(double torque, double momentOfInertia) {
        return torque / momentOfInertia;
    }

    public static double torqueFromAngularAcceleration(double momentOfInertia, double angularAcceleration) {
        return momentOfInertia * angularAcceleration;
    }
}