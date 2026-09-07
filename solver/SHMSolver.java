package solver;

public class SHMSolver {

    // Period of a mass-spring system
    public static double springPeriod(double mass, double springConstant) {
        return 2 * Math.PI * Math.sqrt(mass / springConstant);
    }

    // Period of a simple pendulum (small-angle approximation)
    public static double pendulumPeriod(double length, double g) {
        return 2 * Math.PI * Math.sqrt(length / g);
    }

    public static double angularFrequency(double period) {
        return 2 * Math.PI / period;
    }
    public static SolutionResult solvePendulumPeriodWithSteps(double length, double g) {
        SolutionResult result = new SolutionResult();

        double period = pendulumPeriod(length, g);
        result.addStep(
                "Small-angle pendulum period formula",
                "T = 2*pi*sqrt(L/g)",
                String.format("T = 2*pi*sqrt(%.2f/%.2f)", length, g),
                period, "s"
        );

        result.finalAnswer = period;
        result.finalUnit = "s";
        return result;
    }

    public static double angularFrequencyFromSpring(double mass, double springConstant) {
        return Math.sqrt(springConstant / mass);
    }

    public static double angularFrequencyFromPendulum(double length, double g) {
        return Math.sqrt(g / length);
    }

    // Max speed during oscillation, at the equilibrium point (x=0)
    public static double maxVelocity(double amplitude, double angularFrequency) {
        return amplitude * angularFrequency;
    }

    // Max acceleration during oscillation, at the extremes (x=+-amplitude)
    public static double maxAcceleration(double amplitude, double angularFrequency) {
        return amplitude * angularFrequency * angularFrequency;
    }

    // Position at time t, given amplitude, angular frequency, and phase offset (radians).
    // phase=0 means it starts at maximum displacement (x=A) with zero velocity.
    public static double position(double amplitude, double angularFrequency, double time, double phase) {
        return amplitude * Math.cos(angularFrequency * time + phase);
    }

    public static double velocity(double amplitude, double angularFrequency, double time, double phase) {
        return -amplitude * angularFrequency * Math.sin(angularFrequency * time + phase);
    }
}