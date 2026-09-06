package ai;

import java.util.HashMap;
import java.util.Map;

public class ProblemClassification {
    public String problemType;
    public Map<String, Double> knowns = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static ProblemClassification fromJson(String rawResponseText) {
        String jsonBlock = SimpleJson.extractJsonObject(rawResponseText);
        Map<String, Object> parsed = SimpleJson.parseObject(jsonBlock);

        ProblemClassification result = new ProblemClassification();
        result.problemType = (String) parsed.get("problemType");

        Map<String, Object> knownsRaw = (Map<String, Object>) parsed.get("knowns");
        if (knownsRaw != null) {
            for (Map.Entry<String, Object> entry : knownsRaw.entrySet()) {
                result.knowns.put(entry.getKey(), (Double) entry.getValue());
            }
        }
        return result;
    }
}
