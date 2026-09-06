package launcher.scenarios;

import launcher.Scenario;
import launcher.ScenarioParameter;
import launcher.SceneResult;
import math.Vector3;
import physics.PhysicsWorld;
import physics.RollingConstraint;
import physics.Surface;
import render.Camera;
import scene.GameObject;
import solver.MomentOfInertiaSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RollingRaceScenario implements Scenario {

    @Override
    public String getName() { return "Rolling Race (Sphere vs Hoop)"; }

    @Override
    public String getDescription() {
        return "A solid sphere and a hoop, same mass and radius, released together on the same ramp. " +
                "Despite identical mass, they don't tie -- the sphere wins every time, because less of " +
                "its mass is far from the rotation axis (lower moment of inertia), so less of the driving " +
                "force gets 'spent' spinning it up. Mass cancels out of the result; shape doesn't.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Ramp Angle (deg)", "angle", 5, 45, 25, 1),
                new ScenarioParameter("Mass (kg, both objects)", "mass", 0.2, 5, 1.0, 0.1),
                new ScenarioParameter("Radius (m, both objects)", "radius", 0.2, 1, 0.4, 0.05)
        );
    }

    @Override
    public SceneResult build(Map<String, Double> values) {
        double angle = values.get("angle");
        double mass = values.get("mass");
        double radius = values.get("radius");

        PhysicsWorld world = new PhysicsWorld();
        world.getSurfaces().clear();

        Surface ramp = Surface.incline(new Vector3(0, 0, 10), angle, 10, 5);
        world.addSurface(ramp);

        double rampAngleRad = Math.toRadians(angle);
        double distanceAlongSlope = 8; // near the top of the ramp

        GameObject sphere = new GameObject(rampPosition(ramp, rampAngleRad, distanceAlongSlope, radius, -1), new Vector3(0, 0, 0), mass, radius);
        sphere.momentOfInertia = MomentOfInertiaSolver.solidSphere(mass, radius);
        world.addObject(sphere);
        world.addRollingConstraint(new RollingConstraint(sphere, ramp));

        GameObject hoop = new GameObject(rampPosition(ramp, rampAngleRad, distanceAlongSlope, radius, 1), new Vector3(0, 0, 0), mass, radius);
        hoop.momentOfInertia = MomentOfInertiaSolver.hoop(mass, radius);
        world.addObject(hoop);
        world.addRollingConstraint(new RollingConstraint(hoop, ramp));

        List<GameObject> objects = new ArrayList<>();
        objects.add(sphere);
        objects.add(hoop);

        Camera camera = new Camera(new Vector3(-8, 6, 3), new Vector3(4, 1, 10), 60);
        return new SceneResult(world, objects, camera, sphere, hoop);
    }

    // Places an object on the ramp's surface at a given distance along the slope,
    // offset sideways (in z) so the two racing objects don't collide with each other.
    private Vector3 rampPosition(Surface ramp, double angleRad, double distanceAlongSlope, double radius, double sideOffset) {
        return new Vector3(
                ramp.point.x + distanceAlongSlope * Math.cos(angleRad) + radius * Math.sin(angleRad),
                ramp.point.y + distanceAlongSlope * Math.sin(angleRad) + radius * Math.cos(angleRad),
                ramp.point.z + sideOffset * (radius * 2.5)
        );
    }
}