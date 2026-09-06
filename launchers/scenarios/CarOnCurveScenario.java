package launcher.scenarios;

import launcher.Scenario;
import launcher.ScenarioParameter;
import launcher.SceneResult;
import math.Vector3;
import physics.HorizontalCircularMotionConstraint;
import physics.PhysicsWorld;
import render.Camera;
import scene.GameObject;
import solver.CircularMotionSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CarOnCurveScenario implements Scenario {

    @Override
    public String getName() { return "Car on a Flat Curve"; }

    @Override
    public String getDescription() {
        return "A car turning on a flat, unbanked curve. Static friction alone supplies the centripetal " +
                "force. If the speed is too high for the radius/friction combo, the car skids outward -- " +
                "compare the speed here to sqrt(staticFriction * g * radius), the theoretical max safe speed.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Radius (m)", "radius", 3, 30, 10, 1),
                new ScenarioParameter("Speed (m/s)", "speed", 1, 20, 6, 0.5),
                new ScenarioParameter("Static Friction", "staticFriction", 0.1, 1.2, 0.6, 0.05),
                new ScenarioParameter("Car Mass (kg)", "mass", 500, 2500, 1200, 100)
        );
    }

    @Override
    public SceneResult build(Map<String, Double> values) {
        double radius = values.get("radius");
        double speed = values.get("speed");
        double staticFriction = values.get("staticFriction");
        double mass = values.get("mass");

        double g = 9.81;
        double maxSafeSpeed = CircularMotionSolver.maxSafeSpeedOnFlatCurve(staticFriction, g, radius);
        System.out.printf("Max safe speed for this radius/friction: %.2f m/s (car is going %.2f m/s)%n",
                maxSafeSpeed, speed);

        PhysicsWorld world = new PhysicsWorld();
        world.getSurfaces().clear(); // road height is handled directly by the constraint, not the Surface system

        Vector3 center = new Vector3(0, 0.5, 10);
        Vector3 startPosition = center.add(new Vector3(radius, 0, 0));

        GameObject car = new GameObject(startPosition, new Vector3(0, 0, speed), mass, 0.6);
        car.staticFriction = staticFriction;
        List<GameObject> objects = new ArrayList<>();
        objects.add(car);
        world.addObject(car);

        world.addHorizontalCircularMotionConstraint(new HorizontalCircularMotionConstraint(car, center));

        Camera camera = new Camera(new Vector3(0, radius * 1.5, -radius * 0.5), new Vector3(0, 0, 10), 60);
        return new SceneResult(world, objects, camera, car, null);
    }
}