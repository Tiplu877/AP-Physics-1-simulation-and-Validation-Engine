package solver;

public class RotationalKinematicsSolver {

    // omega = omega0 + alpha*t
    public static double finalAngularVelocity(double omega0, double alpha, double t) {
        return omega0 + alpha * t;
    }

    // theta = omega0*t + 0.5*alpha*t^2
    public static double angularDisplacement(double omega0, double alpha, double t) {
        return omega0 * t + 0.5 * alpha * t * t;
    }

    public static double angularAcceleration(double omega0, double omega, double t) {
        if (t == 0) throw new IllegalArgumentException("time is zero -- angular acceleration undefined");
        return (omega - omega0) / t;
    }

    public static double time(double omega0, double omega, double alpha) {
        if (alpha == 0) throw new IllegalArgumentException("angular acceleration is zero -- time undefined");
        return (omega - omega0) / alpha;
    }

    // omega^2 = omega0^2 + 2*alpha*deltaTheta -- same two-root caveat as the linear version:
    // the equation alone can't tell you which rotational direction it ends up spinning.
    public static double[] finalAngularVelocityFromDisplacement(double omega0, double alpha, double deltaTheta) {
        double omegaSquared = omega0 * omega0 + 2 * alpha * deltaTheta;
        if (omegaSquared < 0) {
            throw new IllegalArgumentException("No real solution -- omega^2 would be negative");
        }
        double root = Math.sqrt(omegaSquared);
        return new double[] { root, -root };
    }

    // --- Linear <-> angular conversions (v = omega*r, a_tangential = alpha*r) ---

    public static double tangentialVelocity(double angularVelocity, double radius) {
        return angularVelocity * radius;
    }

    public static double tangentialAcceleration(double angularAcceleration, double radius) {
        return angularAcceleration * radius;
    }

    public static double angularVelocityFromTangential(double tangentialVelocity, double radius) {
        return tangentialVelocity / radius;
    }

    // Revolutions per second <-> radians per second, useful since real-world problems
    // (RPM on a motor, etc.) are often given in revolutions, not radians.
    public static double angularVelocityFromRevolutionsPerSecond(double revolutionsPerSecond) {
        return revolutionsPerSecond * 2 * Math.PI;
    }
}