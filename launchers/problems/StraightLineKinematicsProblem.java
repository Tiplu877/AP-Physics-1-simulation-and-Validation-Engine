package launcher.problems;

import launcher.SolvableProblem;
import solver.KinematicsSolver;
import solver.SolutionResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StraightLineKinematicsProblem implements SolvableProblem {

    @Override
    public String getName() { return "Straight-Line Kinematics (SUVAT)"; }

    @Override
    public String getDescription() {
        return "Given initial velocity, acceleration, and time, find final velocity and displacement.";
    }

    @Override
    public List<String> getInputLabels() {
        return Arrays.asList("Initial Velocity (m/s)", "Acceleration (m/s^2)", "Time (s)");
    }

    @Override
    public List<String> getInputKeys() {
        return Arrays.asList("v0", "a", "t");
    }

    @Override
    public SolutionResult solve(Map<String, Double> values) {
        return KinematicsSolver.solveStraightLineWithSteps(
                values.get("v0"), values.get("a"), values.get("t"));
    }
}