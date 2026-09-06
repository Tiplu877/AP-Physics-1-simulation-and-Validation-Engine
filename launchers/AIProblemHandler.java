package launcher;

import solver.SolutionResult;

import java.util.List;
import java.util.Map;

public interface AIProblemHandler {
    String getProblemTypeKey();     // e.g. "angledProjectile" -- must match what the AI is told to output
    String getDescription();        // shown to the AI in the classification prompt, and to the user
    List<String> getRequiredKeys(); // e.g. ["speed", "angle"] -- what the AI needs to extract
    SolutionResult solve(Map<String, Double> values);

    // Returns a running simulation for these values, or null if this problem type
    // doesn't have a matching scenario (e.g. some problems are pure algebra with
    // nothing physical to visualize in 3D).
    SceneResult buildScenario(Map<String, Double> values);
}