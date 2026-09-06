package launcher.scenarios;

import launcher.Scenario;
import launcher.ScenarioParameter;
import launcher.SceneResult;
import math.Vector3;
import physics.BeamPivotConstraint;
import physics.PhysicsWorld;
import render.Camera;
import scene.GameObject;
import solver.MomentOfInertiaSolver;
import solver.TorqueEquilibriumSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BalanceBeamScenario implements Scenario {

    @Override
    public String getName() { return "Balance Beam (Torque Equilibrium)"; }

    @Override
    public String getDescription() {
        return "A seesaw-style beam pivoted at its center, with two weights at adjustable distances " +
                "from the pivot. If F1*d1 = F2*d2, net torque is zero and the beam stays level -- " +
                "otherwise it rotates toward whichever side has more torque. Check the console for the " +
                "exact distance that would balance it.";
    }

    @Override
    public List<ScenarioParameter> getParameters() {
        return Arrays.asList(
                new ScenarioParameter("Weight 1 Mass (kg)", "mass1", 0.5, 10, 4, 0.5),
                new ScenarioParameter("Weight 1 Distance (m)", "dist1", 0.2, 3, 1.5, 0.1),
                new ScenarioParameter("Weight 2 Mass (kg)", "mass2", 0.5, 10, 3, 0.5),
                new ScenarioParameter("Weight 2 Distance (m)", "dist2", 0.2, 3, 1.5, 0.1),
                new ScenarioParameter("Damping", "damping", 0, 10, 2.0, 0.5)
        );
    }

    @Override
    public SceneResult build(Map<String, Double> values) {
        double mass1 = values.get("mass1");
        double dist1 = values.get("dist1");
        double mass2 = values.get("mass2");
        double dist2 = values.get("dist2");
        double damping = values.get("damping");

        double g = 9.81;
        double force1 = mass1 * g;
        double force2 = mass2 * g;

        double net = TorqueEquilibriumSolver.netTorque(new double[]{force1, -force2}, new double[]{dist1, dist2});
        double balancedDist2 = TorqueEquilibriumSolver.balanceDistance(force1, dist1, force2);
        System.out.printf("Net torque: %.2f N*m (%s) | To balance exactly, Weight 2 distance should be %.2f m%n",
                net, TorqueEquilibriumSolver.isBalanced(net, 0.5) ? "balanced" : "NOT balanced", balancedDist2);

        PhysicsWorld world = new PhysicsWorld();
        world.getSurfaces().clear();

        Vector3 pivot = new Vector3(0, 6, 10);        double beamLength = 6;

        GameObject beam = new GameObject(pivot, new Vector3(0, 0, 0), mass1 + mass2, 0);
        beam.beamLength = beamLength;
        beam.momentOfInertia = MomentOfInertiaSolver.rodAboutCenter(mass1 + mass2, beamLength);
        world.addObject(beam);

        BeamPivotConstraint beamConstraint = new BeamPivotConstraint(beam, pivot);
        beamConstraint.dampingCoefficient = damping;
        GameObject marker1 = new GameObject(pivot, new Vector3(0, 0, 0), mass1, 0.2);
        GameObject marker2 = new GameObject(pivot, new Vector3(0, 0, 0), mass2, 0.2);
        // markers are NOT added via world.addObject() -- BeamPivotConstraint controls
        // their position directly every frame; they're render-only visual props.

        beamConstraint.addWeight(dist1, force1, marker1);
        beamConstraint.addWeight(-dist2, force2, marker2);
        world.addBeamPivotConstraint(beamConstraint);

        List<GameObject> objects = new ArrayList<>();
        objects.add(beam);
        objects.add(marker1);
        objects.add(marker2);

        Camera camera = new Camera(new Vector3(0, 3, 3), pivot, 60);
        return new SceneResult(world, objects, camera, beam, null);
    }
}