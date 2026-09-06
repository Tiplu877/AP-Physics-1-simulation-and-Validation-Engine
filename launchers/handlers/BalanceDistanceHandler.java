package launcher.handlers;

import launcher.AIProblemHandler;
import launcher.SceneResult;
import launcher.scenarios.BalanceBeamScenario;
import solver.SolutionResult;
import solver.TorqueEquilibriumSolver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceDistanceHandler implements AIProblemHandler {

    @Override
    public String getProblemTypeKey() { return "balanceDistance"; }

    @Override
    public String getDescription() {
        return "A balance beam / seesaw problem: given one side's force, its distance from the pivot, " +
                "and the other side's force, find the distance needed to balance. " +
                "Needs: knownForce (N), knownDistance (m), otherForce (N).";
    }

    @Override
    public List<String> getRequiredKeys() {
        return Arrays.asList("knownForce", "knownDistance", "otherForce");
    }

    @Override
    public SolutionResult solve(Map<String, Double> values) {
        return TorqueEquilibriumSolver.solveBalanceDistanceWithSteps(
                values.get("knownForce"), values.get("knownDistance"), values.get("otherForce"));
    }

    @Override
    public SceneResult buildScenario(Map<String, Double> values) {
        double g = 9.81;
        Map<String, Double> scenarioValues = new HashMap<>();
        scenarioValues.put("mass1", values.get("knownForce") / g);
        scenarioValues.put("dist1", values.get("knownDistance"));
        scenarioValues.put("mass2", values.get("otherForce") / g);
        double balancedDist = TorqueEquilibriumSolver.balanceDistance(
                values.get("knownForce"), values.get("knownDistance"), values.get("otherForce"));
        scenarioValues.put("dist2", balancedDist); // shows it AT the balanced position
        scenarioValues.put("damping", 2.0);
        return new BalanceBeamScenario().build(scenarioValues);
    }
}