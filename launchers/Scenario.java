package launcher;

import java.util.List;
import java.util.Map;

public interface Scenario {
    String getName();
    String getDescription();
    List<ScenarioParameter> getParameters();
    SceneResult build(Map<String, Double> values); // values keyed by each parameter's "key"
}