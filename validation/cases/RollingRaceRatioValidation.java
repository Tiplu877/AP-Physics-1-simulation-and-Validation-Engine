package validation.cases;

import launcher.SceneResult;
import launcher.ScenarioParameter;
import launcher.scenarios.RollingRaceScenario;
import solver.MomentOfInertiaSolver;
import validation.ValidationCase;
import validation.ValidationOutcome;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RollingRaceRatioValidation implements ValidationCase {

    @Override
    public ValidationOutcome run() {
        return null;
    }

    @Override
    public String getName() { return "Rolling Race Acceleration Ratio"; }

    @Override
    public String getDescription() {
        return "Checks the sphere/hoop speed ratio against theory. This ratio should stay the same regardless of angle/mass/radius, since only shape (moment of inertia) matters.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Ramp Angle (deg)", "angle", 5, 45, 25, 1),
                new ScenarioParameter("Mass (kg)", "mass", 0.2, 5, 1.0, 0.1),
                new ScenarioParameter("Radius (m)", "radius", 0.2, 1, 0.4, 0.05)
        );
    }

    @Override
    public ValidationOutcome run(Map<String, Double> values) {
        double angle = values.get("angle");
        double mass = values.get("mass");
        double radius = values.get("radius");

        Map<String, Double> sceneValues = new HashMap<>();
        sceneValues.put("angle", angle);
        sceneValues.put("mass", mass);
        sceneValues.put("radius", radius);
        SceneResult scene = new RollingRaceScenario().build(sceneValues);

        double dt = 0.001;
        for (int i = 0; i < 200; i++) {
            scene.world.update(dt);
        }

        double sphereSpeed = scene.tracked.velocity.magnitude();
        double hoopSpeed = scene.relativeTo.velocity.magnitude();
        double measuredRatio = sphereSpeed / hoopSpeed;

        double iSphereRatio = MomentOfInertiaSolver.solidSphere(mass, radius) / (mass * radius * radius);
        double iHoopRatio = MomentOfInertiaSolver.hoop(mass, radius) / (mass * radius * radius);
        double theoreticalRatio = (1 + iHoopRatio) / (1 + iSphereRatio);

        ValidationOutcome outcome = new ValidationOutcome();
        outcome.testName = getName();
        outcome.description = String.format("angle=%.0fdeg, mass=%.2fkg, radius=%.2fm", angle, mass, radius);
        outcome.theoreticalValue = theoreticalRatio;
        outcome.measuredValue = measuredRatio;
        outcome.unit = "(ratio)";
        outcome.passed = outcome.percentError() < 2.0;
        return outcome;
    }
}