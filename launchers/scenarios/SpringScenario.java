package launcher.scenarios;

import launcher.Scenario;
import launcher.ScenarioParameter;
import launcher.SceneResult;
import math.Vector3;
import physics.PhysicsWorld;
import physics.SpringConstraint;
import render.Camera;
import scene.GameObject;
import solver.SHMSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SpringScenario implements Scenario {

    @Override
    public String getName() { return "Mass on a Spring"; }

    @Override
    public String getDescription() {
        return "A mass hanging from a spring. It starts stretched further than equilibrium, so it " +
                "oscillates up and down. Watch the Energy tab: total mechanical energy should stay " +
                "constant (this spring has no damping) even as it repeatedly trades between kinetic, " +
                "gravitational potential, and spring potential energy.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Spring Constant k (N/m)", "k", 5, 100, 30, 1),
                new ScenarioParameter("Mass (kg)", "mass", 0.2, 5, 1.0, 0.1),
                new ScenarioParameter("Rest Length (m)", "restLength", 0.5, 3, 1.5, 0.1),
                new ScenarioParameter("Starting Stretch (m)", "startingStretch", 0, 2, 0.5, 0.05)
        );
    }

    @Override
    public SceneResult build(Map<String, Double> values) {
        double k = values.get("k");
        double mass = values.get("mass");
        double restLength = values.get("restLength");
        double startingStretch = values.get("startingStretch");

        PhysicsWorld world = new PhysicsWorld();
        world.getSurfaces().clear(); // hanging freely -- nothing to land on

        Vector3 anchor = new Vector3(0, 8, 10);
        // Start stretched further than rest length so it's out of equilibrium and oscillates
        Vector3 startPosition = anchor.subtract(new Vector3(0, restLength + startingStretch, 0));

        GameObject ball = new GameObject(startPosition, new Vector3(0, 0, 0), mass, 0.3);
        List<GameObject> objects = new ArrayList<>();
        objects.add(ball);
        world.addObject(ball);

        SpringConstraint springConstraint = new SpringConstraint(ball, anchor, restLength, k);
        world.addSpringConstraint(springConstraint);

        Camera camera = new Camera(new Vector3(0, 5, 3), new Vector3(0, 5, 10), 60);
        SceneResult result = new SceneResult(world, objects, camera, ball, null);
        result.trackedSpring = springConstraint;
        double g = 9.81;
        double predictedPeriod = SHMSolver.springPeriod(mass, k);
        System.out.printf("Predicted period: %.3f s (T = 2*pi*sqrt(m/k))%n", predictedPeriod);
        return result;
    }
}