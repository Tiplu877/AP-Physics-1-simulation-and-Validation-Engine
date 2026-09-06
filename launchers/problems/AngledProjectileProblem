package launcher.problems;

import launcher.SolvableProblem;
import solver.KinematicsSolver;
import solver.SolutionResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AngledProjectileProblem implements SolvableProblem {

    @Override
    public String getName() { return "Angled Projectile Motion"; }

    @Override
    public String getDescription() {
        return "Given a launch speed and angle (launching and landing at the same height), find the range.";
    }

    @Override
    public List<String> getInputLabels() {
        return Arrays.asList("Launch Speed (m/s)", "Launch Angle (deg)", "Gravity (m/s^2)");
    }

    @Override
    public List<String> getInputKeys() {
        return Arrays.asList("speed", "angle", "g");
    }

    @Override
    public SolutionResult solve(Map<String, Double> values) {
        return KinematicsSolver.solveAngledProjectileWithSteps(
                values.get("speed"), values.get("angle"), values.get("g"));
    }
}
