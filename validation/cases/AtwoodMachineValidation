package validation.cases;

import launcher.SceneResult;
import launcher.ScenarioParameter;
import launcher.scenarios.AtwoodMachineScenario;
import validation.ValidationCase;
import validation.ValidationOutcome;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtwoodMachineValidation implements ValidationCase {

    @Override
    public ValidationOutcome run() {
        return null;
    }

    @Override
    public String getName() { return "Atwood Machine Acceleration"; }

    @Override
    public String getDescription() {
        return "Compares measured acceleration against a = g|m1-m2|/(m1+m2).";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Mass A (kg)", "massA", 0.05, 0.5, 0.25, 0.01),
                new ScenarioParameter("Mass B (kg)", "massB", 0.05, 0.5, 0.20, 0.01)
        );
    }

    @Override
    public ValidationOutcome run(Map<String, Double> values) {
        double massA = values.get("massA");
        double massB = values.get("massB");
        double g = 9.81;

        Map<String, Double> sceneValues = new HashMap<>();
        sceneValues.put("massA", massA);
        sceneValues.put("massB", massB);
        SceneResult scene = new AtwoodMachineScenario().build(sceneValues);

        double dt = 0.0005;
        int steps = 100;
        for (int i = 0; i < steps; i++) {
            scene.world.update(dt);
        }

        double elapsedTime = dt * steps;
        double measuredAcceleration = scene.tracked.velocity.magnitude() / elapsedTime;
        double theoreticalAcceleration = g * Math.abs(massA - massB) / (massA + massB);

        ValidationOutcome outcome = new ValidationOutcome();
        outcome.testName = getName();
        outcome.description = String.format("m1=%.3fkg, m2=%.3fkg", massA, massB);
        outcome.theoreticalValue = theoreticalAcceleration;
        outcome.measuredValue = measuredAcceleration;
        outcome.unit = "m/s^2";
        outcome.passed = outcome.percentError() < 1.0;
        return outcome;
    }
}
