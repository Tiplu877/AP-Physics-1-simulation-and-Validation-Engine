package launcher.scenarios;

import launcher.Scenario;
import launcher.ScenarioParameter;
import launcher.SceneResult;
import math.Vector3;
import physics.PhysicsWorld;
import render.Camera;
import scene.GameObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TwoBallCollisionScenario implements Scenario {

    @Override
    public String getName() { return "Two-Ball Collision (Momentum)"; }

    @Override
    public String getDescription() {
        return "Two balls on a collision course. Watch each ball's individual Momentum change in the " +
                "Data tab, while System Momentum stays exactly constant through the collision -- that's " +
                "conservation of momentum. Adjust restitution to compare elastic (bouncy) vs inelastic " +
                "(sticky) collisions, and watch System Kinetic Energy: it stays constant only when elastic.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Mass A (kg)", "massA", 0.5, 10, 2, 0.5),
                new ScenarioParameter("Mass B (kg)", "massB", 0.5, 10, 2, 0.5),
                new ScenarioParameter("Speed A (m/s)", "speedA", -10, 10, 4, 0.5),
                new ScenarioParameter("Speed B (m/s)", "speedB", -10, 10, -4, 0.5),
                new ScenarioParameter("Restitution (0=stick, 1=bouncy)", "restitution", 0, 1, 1.0, 0.05)
        );
    }

    @Override
    public SceneResult build(Map<String, Double> values) {
        double massA = values.get("massA");
        double massB = values.get("massB");
        double speedA = values.get("speedA");
        double speedB = values.get("speedB");
        double restitution = values.get("restitution");

        PhysicsWorld world = new PhysicsWorld();
        world.getSurfaces().clear(); // isolated collision demo -- no ground/friction muddying the momentum check
        world.setRestitution(restitution);

        GameObject ballA = new GameObject(new Vector3(-4, 3, 10), new Vector3(speedA, 0, 0), massA, 0.4);
        GameObject ballB = new GameObject(new Vector3(4, 3, 10), new Vector3(speedB, 0, 0), massB, 0.4);

        List<GameObject> objects = new ArrayList<>();
        objects.add(ballA);
        objects.add(ballB);
        world.addObject(ballA);
        world.addObject(ballB);

        Camera camera = new Camera(new Vector3(0, 5, -3), new Vector3(0, 3, 10), 60);
        return new SceneResult(world, objects, camera, ballA, ballB);
    }
}