package solver;

public class MomentumSolver {

    public static double momentum(double mass, double velocity) {
        return mass * velocity;
    }

    // J = F * t
    public static double impulse(double force, double time) {
        return force * time;
    }

    // Impulse-momentum theorem: J = m(v_final - v_initial). Solves for the missing piece
    // depending on which overload you call.

    public static double finalVelocityFromImpulse(double mass, double initialVelocity, double impulse) {
        return initialVelocity + impulse / mass;
    }

    public static double forceFromImpulse(double mass, double initialVelocity, double finalVelocity, double time) {
        if (time == 0) throw new IllegalArgumentException("time is zero -- force undefined");
        double deltaP = mass * (finalVelocity - initialVelocity);
        return deltaP / time;
    }
    public static SolutionResult solveAverageForceWithSteps(double mass, double initialVelocity, double finalVelocity, double collisionTime) {
        SolutionResult result = new SolutionResult();

        double deltaV = finalVelocity - initialVelocity;
        result.addStep(
                "Find the change in velocity",
                "deltaV = v_final - v_initial",
                String.format("deltaV = %.2f - %.2f", finalVelocity, initialVelocity),
                deltaV, "m/s"
        );

        double deltaP = mass * deltaV;
        result.addStep(
                "Find the change in momentum (impulse)",
                "deltaP = m * deltaV",
                String.format("deltaP = %.2f * %.2f", mass, deltaV),
                deltaP, "kg*m/s"
        );

        double force = deltaP / collisionTime;
        result.addStep(
                "Find average force using impulse-momentum theorem",
                "F = deltaP / t",
                String.format("F = %.2f / %.2f", deltaP, collisionTime),
                force, "N"
        );

        result.finalAnswer = force;
        result.finalUnit = "N";
        return result;
    }

    public static double timeFromImpulse(double mass, double initialVelocity, double finalVelocity, double force) {
        if (force == 0) throw new IllegalArgumentException("force is zero -- time undefined (velocity never changes)");
        double deltaP = mass * (finalVelocity - initialVelocity);
        return deltaP / force;
    }

    // Common real-world framing: given how much an object's velocity changed and over
    // what time, what average force acted on it? (e.g. "a 0.5s collision")
    public static double averageForceDuringCollision(double mass, double initialVelocity, double finalVelocity, double collisionTime) {
        return forceFromImpulse(mass, initialVelocity, finalVelocity, collisionTime);
    }
}