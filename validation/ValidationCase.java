package validation;

import launcher.ScenarioParameter;

import java.util.List;
import java.util.Map;

public interface ValidationCase {
    ValidationOutcome run();

    String getName();
    String getDescription();
    List<ScenarioParameter> getParameters();
    ValidationOutcome run(Map<String, Double> values);
}