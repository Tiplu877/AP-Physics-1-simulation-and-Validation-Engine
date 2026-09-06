package launcher.handlers;

import launcher.AIProblemHandler;
import launcher.SceneResult;
import solver.KinematicsSolver;
import solver.SolutionResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StraightLineKinematicsHandler implements AIProblemHandler {

    @Override
    public String getProblemTypeKey() { return "straightLineKinematics"; }

    @Override
    public String getDescription() {
        return "Straight-line motion with constant acceleration (SUVAT). " +
                "Needs: v0 (m/s, initial velocity), a (m/s^2, acceleration), t (s, time).";
    }

    @Override
    public List<String> getRequiredKeys() {
        return Arrays.asList("v0", "a", "t");
    }

    @Override
    public SolutionResult solve(Map<String, Double> values) {
        return KinematicsSolver.solveStraightLineWithSteps(values.get("v0"), values.get("a"), values.get("t"));
    }

    @Override
    public SceneResult buildScenario(Map<String, Double> values) {
        return null; // no matching visual scenario -- this is pure 1D algebra, nothing to render in 3D
    }
}