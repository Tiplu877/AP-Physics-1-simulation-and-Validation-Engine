package validation.cases;

import launcher.SceneResult;
import launcher.ScenarioParameter;
import launcher.scenarios.PendulumScenario;
import solver.SHMSolver;
import validation.ValidationCase;
import validation.ValidationOutcome;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendulumPeriodValidation implements ValidationCase {

    @Override
    public ValidationOutcome run() {
        return null;
    }

    @Override
    public String getName() { return "Pendulum Period (Small Angle)"; }

    @Override
    public String getDescription() {
        return "Compares measured period against T = 2*pi*sqrt(L/g). Keep angle small (under ~15deg) for the approximation to hold.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Length (m)", "length", 0.5, 4, 1.5, 0.1),
                new ScenarioParameter("Release Angle (deg)", "angle", 3, 80, 10, 1)        );
    }

    @Override
    public ValidationOutcome run(Map<String, Double> values) {
        double length = values.get("length");
        double angle = values.get("angle");
        double g = 9.81;

        Map<String, Double> sceneValues = new HashMap<>();
        sceneValues.put("length", length);
        sceneValues.put("angle", angle);
        sceneValues.put("mass", 0.5);
        SceneResult scene = new PendulumScenario().build(sceneValues);

        double dt = 0.002;
        int steps = 4000; // 8s -- generous margin for longer pendulums to complete several swings
        for (int i = 0; i < steps; i++) {
            scene.world.update(dt);
        }

        double measuredPeriod = scene.periodTracker.getMeasuredPeriod();
        double theoreticalPeriod = SHMSolver.pendulumPeriod(length, g);

        ValidationOutcome outcome = new ValidationOutcome();
        outcome.testName = getName();
        outcome.description = String.format("L=%.2fm, released at %.0f deg", length, angle);
        outcome.theoreticalValue = theoreticalPeriod;
        outcome.measuredValue = measuredPeriod;
        outcome.unit = "s";
        outcome.passed = measuredPeriod > 0 && outcome.percentError() < 2.0;
        return outcome;
    }
}