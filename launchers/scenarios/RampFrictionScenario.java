package launcher.scenarios;

import launcher.Scenario;
import launcher.ScenarioParameter;
import launcher.SceneResult;
import math.Vector3;
import physics.PhysicsWorld;
import physics.Surface;
import render.Camera;
import scene.GameObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RampFrictionScenario implements Scenario {

    @Override
    public String getName() { return "Ramp with Friction"; }

    @Override
    public String getDescription() {
        return "A ball on an inclined ramp. Adjust the angle and friction to see whether it slides or stays put — try to find the angle where it just barely starts sliding.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Ramp Angle (deg)", "angle", 5, 60, 30, 1),
                new ScenarioParameter("Static Friction", "staticFriction", 0, 1.2, 0.5, 0.05),
                new ScenarioParameter("Kinetic Friction", "kineticFriction", 0, 1.2, 0.3, 0.05),
                new ScenarioParameter("Ball Mass (kg)", "mass", 0.5, 10, 2, 0.5)
        );
    }

    @Override
    public SceneResult build(Map<String, Double> values) {
        double angle = values.get("angle");
        double staticFriction = values.get("staticFriction");
        double kineticFriction = values.get("kineticFriction");
        double mass = values.get("mass");

        PhysicsWorld world = new PhysicsWorld();
        List<GameObject> objects = new ArrayList<>();

        Surface ramp = Surface.incline(new Vector3(0, 0, 10), angle, 8, 4);
        world.addSurface(ramp);

        double rampAngleRad = Math.toRadians(angle);
        double distanceAlongSlope = 3;
        double radius = 0.5;
        Vector3 ballPos = new Vector3(
                distanceAlongSlope * Math.cos(rampAngleRad) + radius * Math.sin(rampAngleRad),
                distanceAlongSlope * Math.sin(rampAngleRad) + radius * Math.cos(rampAngleRad),
                10
        );
        GameObject ball = new GameObject(ballPos, new Vector3(0, 0, 0), mass, radius);
        ball.staticFriction = staticFriction;
        ball.kineticFriction = kineticFriction;
        objects.add(ball);
        world.addObject(ball);

        Camera camera = new Camera(new Vector3(-6, 5, 3), new Vector3(4, 1, 10), 60);
        return new SceneResult(world, objects, camera, ball, null);
    }
}