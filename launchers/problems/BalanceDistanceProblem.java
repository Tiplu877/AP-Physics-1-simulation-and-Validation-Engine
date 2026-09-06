package launcher.problems;

import launcher.SolvableProblem;
import solver.SolutionResult;
import solver.TorqueEquilibriumSolver;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BalanceDistanceProblem implements SolvableProblem {

    @Override
    public String getName() { return "Balance Beam Distance"; }

    @Override
    public String getDescription() {
        return "Given one side's force and distance, plus the other side's force, find the distance needed to balance.";
    }

    @Override
    public List<String> getInputLabels() {
        return Arrays.asList("Known Force (N)", "Known Distance (m)", "Other Force (N)");
    }

    @Override
    public List<String> getInputKeys() {
        return Arrays.asList("knownForce", "knownDistance", "otherForce");
    }

    @Override
    public SolutionResult solve(Map<String, Double> values) {
        return TorqueEquilibriumSolver.solveBalanceDistanceWithSteps(
                values.get("knownForce"), values.get("knownDistance"), values.get("otherForce"));
    }
}