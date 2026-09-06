package launcher.scenarios;

import launcher.Scenario;
import launcher.ScenarioParameter;
import launcher.SceneResult;
import math.Vector3;
import physics.PhysicsWorld;
import render.Camera;
import scene.GameObject;
import solver.CircularMotionSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OrbitScenario implements Scenario {

    @Override
    public String getName() { return "Orbit (Gravitation)"; }

    @Override
    public String getDescription() {
        return "A light 'moon' orbiting a much heavier 'planet' under real inverse-square gravity " +
                "(not the constant-g approximation used elsewhere). Note: G is exaggerated here for " +
                "visualization -- this is NOT to real astronomical scale. The moon's initial speed is " +
                "set to the theoretical circular-orbit velocity; try changing it to see an elliptical " +
                "orbit, or push it high enough to escape entirely.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Planet Mass", "planetMass", 100, 2000, 450, 50),
                new ScenarioParameter("Orbit Radius (m)", "radius", 2, 15, 5, 0.5),
                new ScenarioParameter("Speed Multiplier (1.0 = circular)", "speedMultiplier", 0.3, 2.0, 1.0, 0.05)
        );
    }

    @Override
    public SceneResult build(Map<String, Double> values) {
        double planetMass = values.get("planetMass");
        double radius = values.get("radius");
        double speedMultiplier = values.get("speedMultiplier");

        double G = 0.1; // exaggerated demo-scale constant -- see class-level note

        PhysicsWorld world = new PhysicsWorld();
        world.getSurfaces().clear(); // no ground -- this is deep space, not a surface demo
        world.setGravitationalConstant(G);

        Vector3 systemCenter = new Vector3(0, 5, 10);
        GameObject planet = new GameObject(systemCenter, new Vector3(0, 0, 0), planetMass, 0.8);
        planet.useNewtonianGravity = true;

        double circularSpeed = CircularMotionSolver.orbitalVelocity(G, planetMass, radius);
        double actualSpeed = circularSpeed * speedMultiplier;

        Vector3 moonPosition = systemCenter.add(new Vector3(radius, 0, 0));
        GameObject moon = new GameObject(moonPosition, new Vector3(0, 0, actualSpeed), 5.0, 0.3);
        moon.useNewtonianGravity = true;

        System.out.printf("Circular orbit speed: %.3f | Actual speed used: %.3f | Orbital period: %.2fs%n",
                circularSpeed, actualSpeed, CircularMotionSolver.orbitalPeriod(G, planetMass, radius));

        List<GameObject> objects = new ArrayList<>();
        objects.add(planet);
        objects.add(moon);
        world.addObject(planet);
        world.addObject(moon);

        Camera camera = new Camera(new Vector3(0, 15, -5), systemCenter, 60);
        return new SceneResult(world, objects, camera, moon, planet);
    }
}