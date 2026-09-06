package launcher.handlers;

import launcher.AIProblemHandler;
import launcher.SceneResult;
import launcher.scenarios.PendulumScenario;
import solver.SHMSolver;
import solver.SolutionResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendulumPeriodHandler implements AIProblemHandler {

    @Override
    public String getProblemTypeKey() { return "pendulumPeriod"; }

    @Override
    public String getDescription() {
        return "A simple pendulum's period, small-angle approximation. Needs: length (m), the pendulum's string/rod length.";
    }

    @Override
    public List<String> getRequiredKeys() {
        return Arrays.asList("length");
    }

    @Override
    public SolutionResult solve(Map<String, Double> values) {
        double g = values.getOrDefault("g", 9.81);
        return SHMSolver.solvePendulumPeriodWithSteps(values.get("length"), g);
    }

    @Override
    public SceneResult buildScenario(Map<String, Double> values) {
        Map<String, Double> scenarioValues = new HashMap<>();
        scenarioValues.put("length", values.get("length"));
        // The problem doesn't specify mass or release angle -- these don't affect the
        // period (mass cancels out of pendulum physics; angle only matters if large),
        // so reasonable fixed defaults are used purely for the visualization.
        scenarioValues.put("mass", 0.5);
        scenarioValues.put("angle", 15.0);
        return new PendulumScenario().build(scenarioValues);
    }
}