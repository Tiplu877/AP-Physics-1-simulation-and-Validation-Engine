package launcher.problems;

import launcher.SolvableProblem;
import solver.CircularMotionSolver;
import solver.SolutionResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MinSpeedAtTopProblem implements SolvableProblem {

    @Override
    public String getName() { return "Min Speed at Top of a Vertical Loop"; }

    @Override
    public String getDescription() {
        return "Given the loop's radius, find the minimum speed needed at the top for a string/track to stay taut.";
    }

    @Override
    public List<String> getInputLabels() {
        return Arrays.asList("Radius (m)", "Gravity (m/s^2)");
    }

    @Override
    public List<String> getInputKeys() {
        return Arrays.asList("radius", "g");
    }

    @Override
    public SolutionResult solve(Map<String, Double> values) {
        return CircularMotionSolver.solveMinSpeedAtTopWithSteps(values.get("g"), values.get("radius"));
    }
}