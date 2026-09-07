package validation.cases;

import launcher.SceneResult;
import launcher.ScenarioParameter;
import launcher.scenarios.SpringScenario;
import solver.SHMSolver;
import validation.OscillationPeriodDetector;
import validation.ValidationCase;
import validation.ValidationOutcome;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpringPeriodValidation implements ValidationCase {

    @Override
    public ValidationOutcome run() {
        return null;
    }

    @Override
    public String getName() { return "Mass-Spring Period"; }

    @Override
    public String getDescription() {
        return "Compares measured oscillation period against T = 2*pi*sqrt(m/k).";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Spring Constant k (N/m)", "k", 5, 100, 30, 1),
                new ScenarioParameter("Mass (kg)", "mass", 0.2, 5, 1.0, 0.1),
                new ScenarioParameter("Rest Length (m)", "restLength", 0.5, 3, 1.5, 0.1),
                new ScenarioParameter("Starting Stretch (m)", "startingStretch", 0.1, 2, 0.5, 0.05)
        );
    }

    @Override
    public ValidationOutcome run(Map<String, Double> values) {
        double k = values.get("k");
        double mass = values.get("mass");
        double restLength = values.get("restLength");
        double startingStretch = values.get("startingStretch");

        Map<String, Double> sceneValues = new HashMap<>();
        sceneValues.put("k", k);
        sceneValues.put("mass", mass);
        sceneValues.put("restLength", restLength);
        sceneValues.put("startingStretch", startingStretch);
        SceneResult scene = new SpringScenario().build(sceneValues);

        OscillationPeriodDetector detector = new OscillationPeriodDetector();
        double dt = 0.001;
        double elapsedTime = 0;

        for (int i = 0; i < 10000; i++) {
            scene.world.update(dt);
            elapsedTime += dt;
            detector.sample(scene.tracked.velocity.y, elapsedTime);
        }

        double measuredPeriod = detector.getMeasuredPeriod();
        double theoreticalPeriod = SHMSolver.springPeriod(mass, k);

        ValidationOutcome outcome = new ValidationOutcome();
        outcome.testName = getName();
        outcome.description = String.format("m=%.2fkg, k=%.0fN/m, stretch=%.2fm", mass, k, startingStretch);
        outcome.theoreticalValue = theoreticalPeriod;
        outcome.measuredValue = measuredPeriod;
        outcome.unit = "s";
        outcome.passed = measuredPeriod > 0 && outcome.percentError() < 1.0;
        return outcome;
    }
}