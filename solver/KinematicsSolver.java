package solver;

public class KinematicsSolver {

    // v = v0 + a*t
    public static double finalVelocity(double v0, double a, double t) {
        return v0 + a * t;
    }

    // Δx = v0*t + 0.5*a*t^2
    public static double displacement(double v0, double a, double t) {
        return v0 * t + 0.5 * a * t * t;
    }

    // v0 = v - a*t   (rearranged from v = v0 + a*t)
    public static double initialVelocity(double v, double a, double t) {
        return v - a * t;
    }

    // a = (v - v0) / t   (rearranged from v = v0 + a*t)
    public static double acceleration(double v0, double v, double t) {
        if (t == 0) throw new IllegalArgumentException("time is zero — acceleration undefined");
        return (v - v0) / t;
    }

    // t = (v - v0) / a   (rearranged from v = v0 + a*t)
    public static double time(double v0, double v, double a) {
        if (a == 0) throw new IllegalArgumentException("acceleration is zero — time undefined (velocity never changes)");
        return (v - v0) / a;
    }

    // Δx = 0.5 * (v0 + v) * t   (average velocity form)
    public static double displacementFromAverageVelocity(double v0, double v, double t) {
        return 0.5 * (v0 + v) * t;
    }

    // v^2 = v0^2 + 2*a*Δx  -> solve for v.
    // Returns BOTH roots (+ and -) since the equation alone can't tell you which
    // direction the object ends up moving — you pick based on the problem's setup.
    public static double[] finalVelocityFromDisplacement(double v0, double a, double deltaX) {
        double vSquared = v0 * v0 + 2 * a * deltaX;
        if (vSquared < 0) {
            throw new IllegalArgumentException("No real solution — v^2 would be negative (" + vSquared + ")");
        }
        double root = Math.sqrt(vSquared);
        return new double[] { root, -root };
    }

    // --- Free fall special cases (a = -g, starting from rest or with initial velocity) ---

    // Time to reach the highest point, given initial upward velocity v0 (v = 0 at apex)
    public static double timeToApex(double v0, double g) {
        if (g == 0) throw new IllegalArgumentException("gravity cannot be zero");
        return v0 / g;
    }

    // Max height reached, given initial upward velocity v0
    public static double maxHeight(double v0, double g) {
        double t = timeToApex(v0, g);
        return displacement(v0, -g, t);
    }

    // Time to fall a given height, starting with downward speed v0 (0 if dropped from rest)
    public static double timeToFall(double height, double v0, double g) {
        double discriminant = v0 * v0 + 2 * g * height;
        if (discriminant < 0) {
            throw new IllegalArgumentException("No real solution for time to fall");
        }
        return (-v0 + Math.sqrt(discriminant)) / g;
    }
    // --- Angled projectile motion (launched from and landing at the same height) ---

    public static double horizontalVelocityComponent(double speed, double angleDegrees) {
        return speed * Math.cos(Math.toRadians(angleDegrees));
    }

    public static double verticalVelocityComponent(double speed, double angleDegrees) {
        return speed * Math.sin(Math.toRadians(angleDegrees));
    }

    // Total time in the air, assuming launch height == landing height
    public static double timeOfFlight(double speed, double angleDegrees, double g) {
        double vy = verticalVelocityComponent(speed, angleDegrees);
        return 2 * vy / g; // time up (vy/g) + time down (same, by symmetry)
    }

    // Horizontal distance traveled when it lands (same height as launch)
    public static double range(double speed, double angleDegrees, double g) {
        double vx = horizontalVelocityComponent(speed, angleDegrees);
        double t = timeOfFlight(speed, angleDegrees, g);
        return vx * t;
    }

    // Peak height reached during the arc
    public static double maxHeightAngled(double speed, double angleDegrees, double g) {
        double vy = verticalVelocityComponent(speed, angleDegrees);
        return maxHeight(vy, g); // reuses the existing straight-up maxHeight(v0, g) method
    }
    // --- Angled projectile motion with launch height != landing height ---
// heightDifference = landingHeight - launchHeight
//   negative -> landing below launch point (e.g. off a cliff)
//   positive -> landing above launch point (e.g. onto a platform)
//   zero     -> same as the simpler timeOfFlight()/range() methods above

    public static double timeOfFlightWithHeightDifference(double speed, double angleDegrees, double g, double heightDifference) {
        double vy = verticalVelocityComponent(speed, angleDegrees);
        double discriminant = vy * vy + 2 * g * (-heightDifference);

        if (discriminant < 0) {
            throw new IllegalArgumentException(
                    "No real solution — projectile can't reach a landing height difference of " + heightDifference);
        }

        // Only the '+' root is physically valid (gives a positive time); the '-' root
        // would correspond to a negative time, which doesn't make sense.
        return (vy + Math.sqrt(discriminant)) / g;
    }
    public static SolutionResult solveAngledProjectileWithSteps(double speed, double angleDegrees, double g) {
        SolutionResult result = new SolutionResult();

        double vy = verticalVelocityComponent(speed, angleDegrees);
        result.addStep(
                "Find the vertical velocity component",
                "vy = v * sin(angle)",
                String.format("vy = %.1f * sin(%.0f°)", speed, angleDegrees),
                vy, "m/s"
        );

        double vx = horizontalVelocityComponent(speed, angleDegrees);
        result.addStep(
                "Find the horizontal velocity component",
                "vx = v * cos(angle)",
                String.format("vx = %.1f * cos(%.0f°)", speed, angleDegrees),
                vx, "m/s"
        );

        double t = timeOfFlight(speed, angleDegrees, g);
        result.addStep(
                "Find total time of flight (time up + time down, symmetric for equal launch/landing height)",
                "t = 2*vy / g",
                String.format("t = 2*%.2f / %.2f", vy, g),
                t, "s"
        );

        double range = vx * t;
        result.addStep(
                "Find the range (horizontal distance traveled)",
                "range = vx * t",
                String.format("range = %.2f * %.2f", vx, t),
                range, "m"
        );

        result.finalAnswer = range;
        result.finalUnit = "m";
        return result;
    }
    public static SolutionResult solveStraightLineWithSteps(double v0, double a, double t) {
        SolutionResult result = new SolutionResult();

        double v = finalVelocity(v0, a, t);
        result.addStep(
                "Find final velocity",
                "v = v0 + a*t",
                String.format("v = %.2f + %.2f*%.2f", v0, a, t),
                v, "m/s"
        );

        double displacement = displacement(v0, a, t);
        result.addStep(
                "Find displacement",
                "displacement = v0*t + 0.5*a*t^2",
                String.format("displacement = %.2f*%.2f + 0.5*%.2f*%.2f^2", v0, t, a, t),
                displacement, "m"
        );

        result.finalAnswer = displacement;
        result.finalUnit = "m";
        return result;
    }
    public static SolutionResult solveFreeFallThrownUpWithSteps(double v0, double g) {
        SolutionResult result = new SolutionResult();

        double apexTime = timeToApex(v0, g);
        result.addStep(
                "Find time to reach the highest point (velocity = 0 at apex)",
                "t = v0 / g",
                String.format("t = %.2f / %.2f", v0, g),
                apexTime, "s"
        );

        double height = maxHeight(v0, g);
        result.addStep(
                "Find max height using that time",
                "height = v0*t - 0.5*g*t^2",
                String.format("height = %.2f*%.2f - 0.5*%.2f*%.2f^2", v0, apexTime, g, apexTime),
                height, "m"
        );

        result.finalAnswer = height;
        result.finalUnit = "m";
        return result;
    }

    public static double rangeWithHeightDifference(double speed, double angleDegrees, double g, double heightDifference) {
        double vx = horizontalVelocityComponent(speed, angleDegrees);
        double t = timeOfFlightWithHeightDifference(speed, angleDegrees, g, heightDifference);
        return vx * t;
    }
}