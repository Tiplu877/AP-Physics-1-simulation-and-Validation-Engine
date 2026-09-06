package launcher.problems;

import launcher.SolvableProblem;
import solver.KinematicsSolver;
import solver.SolutionResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FreeFallThrownUpProblem implements SolvableProblem {

    @Override
    public String getName() { return "Free Fall (Thrown Upward)"; }

    @Override
    public String getDescription() {
        return "Given an initial upward speed, find time to apex and max height reached.";
    }

    @Override
    public List<String> getInputLabels() {
        return Arrays.asList("Initial Upward Speed (m/s)", "Gravity (m/s^2)");
    }

    @Override
    public List<String> getInputKeys() {
        return Arrays.asList("v0", "g");
    }

    @Override
    public SolutionResult solve(Map<String, Double> values) {
        return KinematicsSolver.solveFreeFallThrownUpWithSteps(values.get("v0"), values.get("g"));
    }
}