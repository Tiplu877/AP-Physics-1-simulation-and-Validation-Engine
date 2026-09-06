package launcher.scenarios;

import launcher.Scenario;
import launcher.ScenarioParameter;
import launcher.SceneResult;
import math.Vector3;
import physics.CircularMotionConstraint;
import physics.PeriodTracker;
import physics.PhysicsWorld;
import render.Camera;
import scene.GameObject;
import solver.SHMSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PendulumScenario implements Scenario {

    @Override
    public String getName() { return "Pendulum"; }

    @Override
    public String getDescription() {
        return "A mass on a string, released from an angle and swinging back and forth. This reuses " +
                "the same rigid circular-motion constraint from the vertical loop scenario -- a pendulum " +
                "is just a case where the string never needs to push (tension stays positive), so it never " +
                "goes slack. Check the Data tab's Measured Period against T = 2*pi*sqrt(L/g) for small angles.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("String Length (m)", "length", 0.5, 4, 1.5, 0.1),
                new ScenarioParameter("Release Angle (deg from vertical)", "angle", 5, 80, 20, 1),
                new ScenarioParameter("Mass (kg)", "mass", 0.1, 3, 0.5, 0.1)
        );
    }

    @Override
    public SceneResult build(Map<String, Double> values) {
        double length = values.get("length");
        double angle = values.get("angle");
        double mass = values.get("mass");

        PhysicsWorld world = new PhysicsWorld();
        world.getSurfaces().clear(); // hanging freely -- nothing to land on

        Vector3 pivot = new Vector3(0, 6, 10);

        double angleRad = Math.toRadians(angle);
        Vector3 offsetFromPivot = new Vector3(length * Math.sin(angleRad), -length * Math.cos(angleRad), 0);
        Vector3 startPosition = pivot.add(offsetFromPivot);

        GameObject bob = new GameObject(startPosition, new Vector3(0, 0, 0), mass, 0.25);
        List<GameObject> objects = new ArrayList<>();
        objects.add(bob);
        world.addObject(bob);

        CircularMotionConstraint constraint = new CircularMotionConstraint(bob, pivot);
        world.addCircularMotionConstraint(constraint);

        PeriodTracker periodTracker = new PeriodTracker(bob, pivot);
        world.addPeriodTracker(periodTracker);

        Camera camera = new Camera(new Vector3(0, pivot.y - 1, 4), new Vector3(0, pivot.y - 2, 10), 60);
        SceneResult result = new SceneResult(world, objects, camera, bob, null);
        result.periodTracker = periodTracker;
        double g = 9.81;
        double predictedPeriod = SHMSolver.pendulumPeriod(length, g);
        System.out.printf("Predicted period (small-angle): %.3f s (release angle: %.0f deg -- " +
                "expect noticeable drift from this at larger angles)%n", predictedPeriod, angle);

        return result;
    }
}