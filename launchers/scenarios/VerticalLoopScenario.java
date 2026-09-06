package launcher.scenarios;

import launcher.Scenario;
import launcher.ScenarioParameter;
import launcher.SceneResult;
import math.Vector3;
import physics.CircularMotionConstraint;
import physics.PhysicsWorld;
import render.Camera;
import scene.GameObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class VerticalLoopScenario implements Scenario {

    @Override
    public String getName() { return "Vertical Loop (Ball on a String)"; }

    @Override
    public String getDescription() {
        return "A ball on a string, swung in a vertical circle. Launch it from the bottom and watch " +
                "whether the string stays taut all the way around, or goes slack near the top. " +
                "Check the Data tab's speed near the top of the loop against sqrt(g*r) -- " +
                "the theoretical minimum speed needed at the top for the string to stay taut.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Radius (m)", "radius", 0.5, 3, 1.0, 0.1),
                new ScenarioParameter("Speed at Bottom (m/s)", "speed", 1, 15, 6, 0.2),
                new ScenarioParameter("Ball Mass (kg)", "mass", 0.1, 3, 0.5, 0.1)
        );
    }

    @Override
    public SceneResult build(Map<String, Double> values) {
        double radius = values.get("radius");
        double speed = values.get("speed");
        double mass = values.get("mass");

        PhysicsWorld world = new PhysicsWorld();
        world.getSurfaces().clear(); // no ground -- this is a free-swinging string, not resting on anything

        Vector3 center = new Vector3(0, radius + 1.5, 10); // the "hand" holding the string
        Vector3 startPosition = center.add(new Vector3(0, -radius, 0)); // ball starts at the bottom

        GameObject ball = new GameObject(startPosition, new Vector3(speed, 0, 0), mass, 0.15);
        List<GameObject> objects = new ArrayList<>();
        objects.add(ball);
        world.addObject(ball);

        CircularMotionConstraint loop = new CircularMotionConstraint(ball, center);
        world.addCircularMotionConstraint(loop);

        Camera camera = new Camera(new Vector3(0, center.y, 3), new Vector3(0, center.y, 10), 60);
        return new SceneResult(world, objects, camera, ball, null);
    }
}