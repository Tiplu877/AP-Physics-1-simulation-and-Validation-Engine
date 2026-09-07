package solver;

public class PowerSolver {

    public static double averagePower(double work, double time) {
        if (time == 0) throw new IllegalArgumentException("time is zero -- power undefined");
        return work / time;
    }

    // P = F * v * cos(theta) -- instantaneous power when force and velocity aren't aligned
    public static double instantaneousPower(double forceMagnitude, double speed, double angleDegrees) {
        return forceMagnitude * speed * Math.cos(Math.toRadians(angleDegrees));
    }
}