package launcher.problems;

import launcher.SolvableProblem;
import solver.MomentumSolver;
import solver.SolutionResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AverageForceProblem implements SolvableProblem {

    @Override
    public String getName() { return "Average Force During a Collision"; }

    @Override
    public String getDescription() {
        return "Given mass, velocity before/after, and collision time, find the average force (impulse-momentum theorem).";
    }

    @Override
    public List<String> getInputLabels() {
        return Arrays.asList("Mass (kg)", "Initial Velocity (m/s)", "Final Velocity (m/s)", "Collision Time (s)");
    }

    @Override
    public List<String> getInputKeys() {
        return Arrays.asList("mass", "v0", "v", "t");
    }

    @Override
    public SolutionResult solve(Map<String, Double> values) {
        return MomentumSolver.solveAverageForceWithSteps(
                values.get("mass"), values.get("v0"), values.get("v"), values.get("t"));
    }
}