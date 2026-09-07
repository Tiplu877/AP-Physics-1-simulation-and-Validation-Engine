package solver;

public class CircularMotionSolver {

    public static double centripetalAcceleration(double speed, double radius) {
        return speed * speed / radius;
    }

    public static double centripetalForce(double mass, double speed, double radius) {
        return mass * centripetalAcceleration(speed, radius);
    }

    public static double period(double speed, double radius) {
        return 2 * Math.PI * radius / speed;
    }
    // The fastest a car can go around a flat (unbanked) curve of a given radius before
// static friction can no longer supply enough centripetal force and it skids.
    public static double maxSafeSpeedOnFlatCurve(double staticFriction, double g, double radius) {
        return Math.sqrt(staticFriction * g * radius);
    }
    public static double gravitationalForce(double G, double mass1, double mass2, double distance) {
        return G * mass1 * mass2 / (distance * distance);
    }

    // Speed needed for a perfectly circular orbit around a central mass at a given radius
    public static double orbitalVelocity(double G, double centralMass, double radius) {
        return Math.sqrt(G * centralMass / radius);
    }
    public static SolutionResult solveMinSpeedAtTopWithSteps(double g, double radius) {
        SolutionResult result = new SolutionResult();

        double minSpeed = minSpeedAtTopOfLoop(g, radius);
        result.addStep(
                "At minimum speed, gravity alone provides exactly the centripetal force needed (tension = 0)",
                "m*g = m*v^2/r  ->  v = sqrt(g*r)",
                String.format("v = sqrt(%.2f * %.2f)", g, radius),
                minSpeed, "m/s"
        );

        result.finalAnswer = minSpeed;
        result.finalUnit = "m/s";
        return result;
    }

    // Kepler's third law, for a circular orbit
    public static double orbitalPeriod(double G, double centralMass, double radius) {
        return 2 * Math.PI * Math.sqrt(radius * radius * radius / (G * centralMass));
    }

    // Minimum speed to escape a central mass's gravity entirely, from a given radius
    public static double escapeVelocity(double G, double centralMass, double radius) {
        return Math.sqrt(2 * G * centralMass / radius);
    }

    public static double speedFromPeriod(double radius, double period) {
        return 2 * Math.PI * radius / period;
    }

    // Minimum speed needed AT THE TOP of a vertical loop for tension to stay >= 0.
    // Below this speed, gravity alone exceeds the centripetal requirement and the
    // string/track can't push back out -- contact/tautness is lost.
    public static double minSpeedAtTopOfLoop(double g, double radius) {
        return Math.sqrt(g * radius);
    }
}
