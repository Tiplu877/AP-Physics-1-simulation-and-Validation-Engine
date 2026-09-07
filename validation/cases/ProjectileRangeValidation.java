package validation.cases;

import launcher.SceneResult;
import launcher.ScenarioParameter;
import launcher.scenarios.ProjectileScenario;
import solver.KinematicsSolver;
import validation.ValidationCase;
import validation.ValidationOutcome;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectileRangeValidation implements ValidationCase {

    @Override
    public ValidationOutcome run() {
        return null;
    }

    @Override
    public String getName() { return "Projectile Range"; }

    @Override
    public String getDescription() {
        return "Compares measured landing range against range = vx * timeOfFlight. Try a large dt to see numerical integration error grow.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Speed (m/s)", "speed", 10, 40, 25, 1),
                new ScenarioParameter("Angle (deg)", "angle", 10, 80, 40, 1),
                new ScenarioParameter("Timestep dt (s)", "dt", 0.0005, 0.05, 0.0005, 0.0005)
        );
    }

    @Override
    public ValidationOutcome run(Map<String, Double> values) {
        double speed = values.get("speed");
        double angle = values.get("angle");
        double dt = values.get("dt");
        double g = 9.81;

        Map<String, Double> sceneValues = new HashMap<>();
        sceneValues.put("speed", speed);
        sceneValues.put("angle", angle);
        SceneResult scene = new ProjectileScenario().build(sceneValues);

        double startX = scene.tracked.position.x;
        double launchHeight = scene.tracked.position.y;

        boolean hasLeftGround = false;
        double measuredRange = Double.NaN;

        for (int i = 0; i < 20000; i++) {
            scene.world.update(dt);
            if (!hasLeftGround && scene.tracked.position.y > launchHeight + 0.1) {
                hasLeftGround = true;
            }
            if (hasLeftGround && scene.tracked.position.y <= launchHeight && scene.tracked.velocity.y < 0) {
                measuredRange = scene.tracked.position.x - startX;
                break;
            }
        }

        double theoreticalRange = KinematicsSolver.range(speed, angle, g);

        ValidationOutcome outcome = new ValidationOutcome();
        outcome.testName = getName();
        outcome.description = String.format("v=%.0fm/s @ %.0fdeg, dt=%.4fs", speed, angle, dt);
        outcome.theoreticalValue = theoreticalRange;
        outcome.measuredValue = measuredRange;
        outcome.unit = "m";
        outcome.passed = !Double.isNaN(measuredRange) && outcome.percentError() < 2.0;
        outcome.notes = "Larger dt increases integration error and landing-detection error -- try dt=0.02 or higher to see it fail.";
        return outcome;
    }
}