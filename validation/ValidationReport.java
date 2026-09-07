package validation;

import launcher.ScenarioParameter;
import validation.cases.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationReport {

    public static List<ValidationCase> buildCases() {
        return Arrays.asList(
                new AtwoodMachineValidation(),
                new PendulumPeriodValidation(),
                new CarOnCurveThresholdValidation(),
                new RollingRaceRatioValidation(),
                new ProjectileRangeValidation(),
                new SpringPeriodValidation()
        );
    }

    public static String buildMarkdown(List<ValidationOutcome> outcomes) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# Physics Engine Validation Report\n\n");
        markdown.append("Each test compares a value measured directly from the running simulation ");
        markdown.append("against the closed-form theoretical prediction for that scenario.\n\n");
        markdown.append("| Test | Theoretical | Measured | % Error | Result |\n");
        markdown.append("|------|-------------|----------|---------|--------|\n");

        int passCount = 0;
        for (ValidationOutcome outcome : outcomes) {
            if (outcome.passed) passCount++;
            String resultSymbol = outcome.passed ? "PASS" : "FAIL";
            markdown.append(String.format("| %s | %.4f %s | %.4f %s | %.2f%% | %s |\n",
                    outcome.testName, outcome.theoreticalValue, outcome.unit,
                    outcome.measuredValue, outcome.unit, outcome.percentError(), resultSymbol));
        }
        markdown.append(String.format("\n**%d / %d tests passed.**\n", passCount, outcomes.size()));
        return markdown.toString();
    }

    // Standalone console version -- still works exactly as before, for anyone who
    // prefers running it directly rather than through the GUI.
    public static void main(String[] args) throws IOException {
        List<ValidationCase> cases = buildCases();
        List<ValidationOutcome> outcomes = new java.util.ArrayList<>();

        for (ValidationCase testCase : cases) {
            Map<String, Double> defaults = new HashMap<>();
            for (ScenarioParameter param : testCase.getParameters()) {
                defaults.put(param.key, param.defaultValue);
            }
            ValidationOutcome outcome = testCase.run(defaults);
            outcomes.add(outcome);
            System.out.printf("%-45s theoretical=%.4f %s | measured=%.4f %s | error=%.2f%% | %s%n",
                    outcome.testName, outcome.theoreticalValue, outcome.unit,
                    outcome.measuredValue, outcome.unit, outcome.percentError(),
                    outcome.passed ? "PASS" : "FAIL");
        }

        String markdown = buildMarkdown(outcomes);
        try (FileWriter writer = new FileWriter("validation_report.md")) {
            writer.write(markdown);
        }
        System.out.println("\nReport written to validation_report.md");
    }
}