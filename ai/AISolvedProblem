package ai;

import solver.SolutionResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AISolvedProblem {
    public SolutionResult solution;
    public String matchedScenarioType; // nullable -- null if this problem doesn't match a known simulation
    public Map<String, Double> knowns = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static AISolvedProblem fromJson(String rawResponseText) {
        String jsonBlock = SimpleJson.extractJsonObject(rawResponseText);
        Map<String, Object> parsed = SimpleJson.parseObject(jsonBlock);

        AISolvedProblem result = new AISolvedProblem();
        result.solution = new SolutionResult();

        List<Object> stepsRaw = (List<Object>) parsed.get("steps");
        for (Object stepObj : stepsRaw) {
            Map<String, Object> step = (Map<String, Object>) stepObj;
            result.solution.addStep(
                    (String) step.get("description"),
                    (String) step.get("formula"),
                    (String) step.get("substitution"),
                    (Double) step.get("result"),
                    (String) step.get("unit")
            );
        }

        result.solution.finalAnswer = (Double) parsed.get("finalAnswer");
        result.solution.finalUnit = (String) parsed.get("finalUnit");

        Object problemTypeRaw = parsed.get("problemType");
        result.matchedScenarioType = (problemTypeRaw instanceof String) ? (String) problemTypeRaw : null;

        Object knownsRaw = parsed.get("knowns");
        if (knownsRaw instanceof Map) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) knownsRaw).entrySet()) {
                if (entry.getValue() instanceof Double) {
                    result.knowns.put(entry.getKey(), (Double) entry.getValue());
                }
            }
        }

        return result;
    }
}
