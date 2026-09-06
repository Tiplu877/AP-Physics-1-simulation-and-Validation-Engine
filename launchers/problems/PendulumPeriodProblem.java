package launcher.problems;

import launcher.SolvableProblem;
import solver.SHMSolver;
import solver.SolutionResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PendulumPeriodProblem implements SolvableProblem {

    @Override
    public String getName() { return "Pendulum Period"; }

    @Override
    public String getDescription() {
        return "Given the string length, find the period (small-angle approximation).";
    }

    @Override
    public List<String> getInputLabels() {
        return Arrays.asList("Length (m)", "Gravity (m/s^2)");
    }

    @Override
    public List<String> getInputKeys() {
        return Arrays.asList("length", "g");
    }

    @Override
    public SolutionResult solve(Map<String, Double> values) {
        return SHMSolver.solvePendulumPeriodWithSteps(values.get("length"), values.get("g"));
    }
}