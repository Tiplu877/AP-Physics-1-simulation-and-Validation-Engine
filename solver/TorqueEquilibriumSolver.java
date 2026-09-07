package solver;

public class TorqueEquilibriumSolver {

    // Torque magnitude from a force applied at some distance (lever arm) from the pivot,
    // assuming the force is perpendicular to the lever arm (true for a horizontal beam
    // with straight-down weights): tau = F * d
    public static double torque(double force, double leverArmDistance) {
        return force * leverArmDistance;
    }

    // Net torque from a set of forces -- pass signed forces (positive = one side of the
    // pivot, negative = the other) to get a signed net result.
    public static double netTorque(double[] forces, double[] leverArms) {
        if (forces.length != leverArms.length) {
            throw new IllegalArgumentException("forces and leverArms must be the same length");
        }
        double net = 0;
        for (int i = 0; i < forces.length; i++) {
            net += forces[i] * leverArms[i];
        }
        return net;
    }
    public static SolutionResult solveBalanceDistanceWithSteps(double knownForce, double knownDistance, double otherForce) {
        SolutionResult result = new SolutionResult();

        double torque1 = torque(knownForce, knownDistance);
        result.addStep(
                "Find the torque from the known side",
                "tau1 = F1 * d1",
                String.format("tau1 = %.2f * %.2f", knownForce, knownDistance),
                torque1, "N*m"
        );

        double distance = balanceDistance(knownForce, knownDistance, otherForce);
        result.addStep(
                "For equilibrium, both torques must be equal -- solve for the unknown distance",
                "d2 = tau1 / F2",
                String.format("d2 = %.2f / %.2f", torque1, otherForce),
                distance, "m"
        );

        result.finalAnswer = distance;
        result.finalUnit = "m";
        return result;
    }

    // Classic "where do you place the second weight to balance" problem:
    // F1*d1 = F2*d2 -- solves for the unknown distance given both forces and one distance.
    public static double balanceDistance(double knownForce, double knownDistance, double otherForce) {
        if (otherForce == 0) throw new IllegalArgumentException("otherForce is zero -- balance distance undefined");
        return (knownForce * knownDistance) / otherForce;
    }

    // Solves for the unknown force instead, given both distances and one force.
    public static double balanceForce(double knownForce, double knownDistance, double otherDistance) {
        if (otherDistance == 0) throw new IllegalArgumentException("otherDistance is zero -- balance force undefined");
        return (knownForce * knownDistance) / otherDistance;
    }

    public static boolean isBalanced(double netTorque, double tolerance) {
        return Math.abs(netTorque) < tolerance;
    }
}