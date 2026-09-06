package launcher.handlers;

import launcher.AIProblemHandler;
import launcher.SceneResult;
import launcher.scenarios.ProjectileScenario;
import solver.KinematicsSolver;
import solver.SolutionResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AngledProjectileHandler implements AIProblemHandler {

    @Override
    public String getProblemTypeKey() { return "angledProjectile"; }

    @Override
    public String getDescription() {
        return "A projectile launched at an angle, landing at the same height it launched from. " +
                "Needs: speed (m/s), angle (degrees above horizontal).";
    }

    @Override
    public List<String> getRequiredKeys() {
        return Arrays.asList("speed", "angle");
    }

    @Override
    public SolutionResult solve(Map<String, Double> values) {
        double g = values.getOrDefault("g", 9.81);
        return KinematicsSolver.solveAngledProjectileWithSteps(values.get("speed"), values.get("angle"), g);
    }

    @Override
    public SceneResult buildScenario(Map<String, Double> values) {
        Map<String, Double> scenarioValues = new HashMap<>();
        scenarioValues.put("speed", values.get("speed"));
        scenarioValues.put("angle", values.get("angle"));
        return new ProjectileScenario().build(scenarioValues);
    }
}
