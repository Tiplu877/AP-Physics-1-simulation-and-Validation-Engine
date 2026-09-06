package launcher;

import solver.SolutionResult;

import java.util.List;
import java.util.Map;

public interface SolvableProblem {
    String getName();
    String getDescription();
    List<String> getInputLabels(); // e.g. ["Speed (m/s)", "Angle (deg)"]
    List<String> getInputKeys();   // e.g. ["speed", "angle"] -- same order as labels
    SolutionResult solve(Map<String, Double> values);
}