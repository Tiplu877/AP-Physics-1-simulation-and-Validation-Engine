package launcher.scenarios;

import launcher.Scenario;
import launcher.ScenarioParameter;
import launcher.SceneResult;
import math.Vector3;
import physics.PhysicsWorld;
import render.Camera;
import scene.GameObject;
import solver.KinematicsSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProjectileScenario implements Scenario {

    @Override
    public String getName() { return "Angled Projectile"; }

    @Override
    public String getDescription() {
        return "A ball launched at an angle. Check the Data tab and compare the simulated arc to the analytical solver's predicted range and max height.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Launch Speed (m/s)", "speed", 1, 40, 25, 1),
                new ScenarioParameter("Launch Angle (deg)", "angle", 5, 85, 40, 1)
        );
    }

    @Override
    public SceneResult build(Map<String, Double> values) {
        double speed = values.get("speed");
        double angle = values.get("angle");

        PhysicsWorld world = new PhysicsWorld();
        List<GameObject> objects = new ArrayList<>();

        double vx = KinematicsSolver.horizontalVelocityComponent(speed, angle);
        double vy = KinematicsSolver.verticalVelocityComponent(speed, angle);
        GameObject ball = new GameObject(new Vector3(-10, 0.5, 10), new Vector3(vx, vy, 0), 1.0, 0.5);
        ball.kineticFriction = 0.4;
        objects.add(ball);
        world.addObject(ball);

        Camera camera = new Camera(new Vector3(0, 5, -10), new Vector3(0, 1, 10), 60);
        return new SceneResult(world, objects, camera, ball, null);
    }
}