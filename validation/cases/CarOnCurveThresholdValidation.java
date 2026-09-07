package validation.cases;

import launcher.SceneResult;
import launcher.ScenarioParameter;
import launcher.scenarios.CarOnCurveScenario;
import solver.CircularMotionSolver;
import validation.ValidationCase;
import validation.ValidationOutcome;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarOnCurveThresholdValidation implements ValidationCase {

    @Override
    public ValidationOutcome run() {
        return null;
    }

    @Override
    public String getName() { return "Car on Curve -- Skid Threshold"; }

    @Override
    public String getDescription() {
        return "Checks that the car does NOT skid below the theoretical max safe speed, and DOES skid above it.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Radius (m)", "radius", 3, 30, 10, 1),
                new ScenarioParameter("Static Friction", "staticFriction", 0.1, 1.2, 0.6, 0.05),
                new ScenarioParameter("Car Mass (kg)", "mass", 500, 2500, 1200, 100)
        );
    }

    @Override
    public ValidationOutcome run(Map<String, Double> values) {
        double radius = values.get("radius");
        double staticFriction = values.get("staticFriction");
        double mass = values.get("mass");
        double g = 9.81;
        double maxSafeSpeed = CircularMotionSolver.maxSafeSpeedOnFlatCurve(staticFriction, g, radius);

        boolean belowSkids = runAndCheckSkid(radius, staticFriction, mass, maxSafeSpeed * 0.9);
        boolean aboveSkids = runAndCheckSkid(radius, staticFriction, mass, maxSafeSpeed * 1.1);
        boolean matches = !belowSkids && aboveSkids;

        ValidationOutcome outcome = new ValidationOutcome();
        outcome.testName = getName();
        outcome.description = String.format("r=%.1fm, mu=%.2f, mass=%.0fkg -- max safe speed = %.2f m/s",
                radius, staticFriction, mass, maxSafeSpeed);
        outcome.theoreticalValue = maxSafeSpeed;
        outcome.measuredValue = maxSafeSpeed;
        outcome.unit = "m/s";
        outcome.passed = matches;
        outcome.notes = String.format("At 90%% of threshold: skidded=%b (expected false). At 110%%: skidded=%b (expected true).",
                belowSkids, aboveSkids);
        return outcome;
    }

    private boolean runAndCheckSkid(double radius, double staticFriction, double mass, double speed) {
        Map<String, Double> sceneValues = new HashMap<>();
        sceneValues.put("radius", radius);
        sceneValues.put("speed", speed);
        sceneValues.put("staticFriction", staticFriction);
        sceneValues.put("mass", mass);
        SceneResult scene = new CarOnCurveScenario().build(sceneValues);

        double dt = 0.001;
        for (int i = 0; i < 2000; i++) {
            scene.world.update(dt);
        }
        return scene.world.getHorizontalCircularMotionConstraints().get(0).isSkidding();
    }
}